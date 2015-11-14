/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.parser;

import java.util.ArrayList;
import java.util.HashMap;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;

import com.bng.core.bean.Exit;
import com.bng.core.bean.StopRecord;
import com.bng.core.bean.UnPatch;
import com.bng.core.coreCdrBean.CallCdr;
import com.bng.core.coreCdrBean.CallconfCdr;
import com.bng.core.coreCdrBean.DtmfCdr;
import com.bng.core.coreCdrBean.MediaCdr;
import com.bng.core.entity.Subscription;
import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Call;
import com.bng.core.jsonBean.DtmfGenerator;
import com.bng.core.jsonBean.Event;
import com.bng.core.memCache.MemCacheJSon;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.DBConnection;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

/**
 * Engine class implements MessageListener which consumes messages from msisdn queue. 
 * This class is responsible for getting current node and based on the events received from telephony will evaluate the next node.
 * When Event received from telephony, first engine will check the event and subEvent.
 * An instance of UserCallDetail class will be created for each call and will be stored in memCache having
 * key = vId_msisdn
 * 
 * if received event has E_OBD in event or E_CHSTATE as event and S_CALL_RINGING as subEvent, 
 * get the startNode of the service from memCache stored at the time of XML unMarshaling. startNodeKey is stored in memCache with keys as
 * startNodekey = vId_service_serviceName for OBD example:- 123456789_OBD_MV . 
 * startNodekey = ivrCode_service for IVR example:- 56060_IVR
 * 1.
 * If callType is Outgoing(OBD)/Incoming(IVR)
 * 
 * 2.
 * 	a)if(callType == OutGoing)
 * 		(i)	 E_OBD event will be received from scheduler for a normal OBD call. 
 * 			 Get StartNode from memCache and create userCallDetail and set currentState in it.
 * 		(ii) if event = E_ChState and subEvent = S_CallRinging, 
 * 			 Discard the event.
 * 		(iii)Rest all the event will be passed to the currentState node for execution.
 * 
 * 	b)if(callType == Incoming)
 * 		(i)   if event = E_ChState and sub_event = S_CallRinging,
 *            then its a start for new call. Get startNode from memCache and create userCallDetail and set currentState in it.  
 *      (ii)  if event = E_ChState and sub_event = s_CallPending
 *            get subEventCause
 * 				if(subEventCause = RLSSent)
 * 					Its a confirmation that call is released from telephony. Discard the event.
 * 				else, send hangUp event to telephony      
 * 		(iii) if event = E_ChState and sub_event = S_CallStandby
 * 				call is terminated and channel is free from telephony end. Free the resources from core(Delete userCalldetail). 
 *  
 *  c)get currentState from userCallDetail which is present in memCache and get the currentNode based on currentState.
 *  
 *  3. Get currentNode from memCache by passing current state. This task is done in while loop.
 *  4. Call execute method of currentNode and pass the event, userCallDetail and queueName to execute method.
 *  5. If nextStateFlag in userCallDetail is true, get getNextNode based on searchString returned by execute method.
 *  	else, set currentNode to nextNode is userCallDetail
 *  6. If executeCurrentNode is true in userCallDetail, keep on looping in while loop.
 *  	else, break from while loop. and wait for next event.
 *  
 */
public class Engine implements MessageListener
{
    private MemCacheJSon memCacheJSon = null;
    private Controller controller = null;
    private Core core = null;
    private String ivrCodeLength;
    private String loadServiceurl;
  
	public void setMemCacheJSon(MemCacheJSon memCacheJSon) {
		this.memCacheJSon = memCacheJSon;
	}

	public void setCore(Core core) {
        this.core = core;
    }

    public void setController(Controller controller) {
        this.controller = controller; 
        //this.controller.Unmarshaller("/home/richa/workspace/Sample-XML/MV_Swaziland.xml");
    }
    
    public void setIvrCodeLength(String ivrCodeLength) {
		this.ivrCodeLength = ivrCodeLength;
	}

	public void setLoadServiceurl(String loadServiceurl) {
		this.loadServiceurl = loadServiceurl;
	}

	public void onMessage(Message msg) 
    {        
        Event event = null;
        ArrayList<Object> currentnode = null;
        UserCallDetail userCallDetail = null;
        
        String searchstring = null;
        int nextState = 0;
        String key = "";
        int startNodeId;
        
        try
        {
        	Logger.sysLog(LogValues.info, this.getClass().getName(), "----New Event Received----");
        	Logger.sysLog(LogValues.info, this.getClass().getName(),"Event json = "+((TextMessage)msg).getText());
            try 
            {
                event = Utility.convertJsonStrToObject((((TextMessage)msg).getText()),Event.class);
                Logger.sysLog(LogValues.info, this.getClass().getName(), "event = "+event.getEvent()+" ,subEvent = "+event.getSubEvent()+" ,subEventCause = "+event.getSubEventCause()+" ,aPartyMsisdn = "+event.getaPartyMsisdn()+" ,bPartyMsisdn = "+event.getbPartyMsisdn()+" ,vId = "+event.getvId()+" ,callType = "+event.getCallType());
             //   if(event.getCallType() == CoreEnums.callType.Incoming.ordinal())
                //	event.setIvrCode(event.getIvrCode()+"9958557426");
            } 
            catch(Exception e)
            {
            	Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
            	return;
            } 
            
            if(event.getIvrCode().contains("#") || event.getIvrCode().contains("*") || event.getIvrCode().contains("%"))// || event.getaPartyMsisdn().contains("*") || event.getbPartyMsisdn().contains("*"))
    		{
            	if(event.getEvent() == CoreEnums.events.E_ChState.ordinal() && event.getSubEvent()== CoreEnums.subEvents.S_CallRinging.ordinal())
            	{
	    			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] IVR-code contains some special character. Discard the ringing event and hangup the call. IVRCode = "+event.getIvrCode()+", BParty = "+event.getbPartyMsisdn());
	    			Call call = new Call(CoreEnums.callOperations.hangup.ordinal(),event.getvId(),false,"","",event.getHardware(), ((Queue)msg.getJMSDestination()).getQueueName());
	    	        SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(call));
	    	        Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event Call added to queue = "+event.getCoreToTelephony());
	    	        Utility.createCdr(event,"Failure","ContainsSpecialChar");
	    	        return;
            	}
            	else
            	{
            		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Discard the event received for number containg some special char.");
            		return;
            	}
    		}            
            
            
            if((event.getCallType() != CoreEnums.callType.Incoming.ordinal()) && (event.getCallType() != CoreEnums.callType.Outgoing.ordinal()))
            {	
            	if(event.getEvent() == CoreEnums.events.E_ChState.ordinal() && event.getSubEvent()== CoreEnums.subEvents.S_CallRinging.ordinal())
            	{
	            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Calltype not valid = "+event.getCallType());
	            	Call call = new Call(CoreEnums.callOperations.hangup.ordinal(),event.getvId(),false,"","",event.getHardware(), ((Queue)msg.getJMSDestination()).getQueueName());
	            	SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(call));
		    	    Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event Call added to queue = "+event.getCoreToTelephony());
	            	Utility.createCdr(event,"Failure","InvalidCallType");
	            	return;
            	}
            	else
            	{
            		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Any other event for Invalid Calltype = "+event.getCallType()+", Do nothing");
            		return;
            	}
            }
            
            try
            {
            	if(event.getCallType() == CoreEnums.callType.Outgoing.ordinal())
	            {
	            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Going to find UCD(OBD) for VID : "+event.getvId()+" , bPartyMsisdn : "+event.getbPartyMsisdn());
	            	userCallDetail = (UserCallDetail) memCacheJSon.get(event.getvId()+"_"+event.getbPartyMsisdn());
	            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] UCD got from memCache = "+userCallDetail);
	            }
	            else
	            {
	            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Going to find UCD(IVR) for VID : "+event.getvId()+" , aPartyMsisdn : "+event.getaPartyMsisdn());
	            	userCallDetail = (UserCallDetail) memCacheJSon.get(event.getvId()+"_"+event.getaPartyMsisdn());
	            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] UCD got from memCache = "+userCallDetail);
	            }            	
            }
            catch(Exception e)
            {
            	Logger.sysLog(LogValues.error, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] UserCallDetail is null.\n"+coreException.GetStack(e));
            	userCallDetail = null;
            }
            
            if(event.getEvent() == CoreEnums.events.DEFAULT.ordinal() && event.getSubEvent() == CoreEnums.subEvents.RecordTimerExp.ordinal())
            {
            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Event recieved for record timer expiry.");
            	if(userCallDetail.isRecordTimerFlag())
            	{
            		//int timerInterval = Timer.stopTimer(Long.parseLong(userCallDetail.getRecordTimer()));
            		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Record Event timer interval");
            		StopRecord stopRecord = new StopRecord();
            		stopRecord.execute(event, ((Queue)msg.getJMSDestination()).getQueueName(), userCallDetail);
            		userCallDetail.setRecordTimerFlag(false);
            	}
            }
            else if(event.getEvent() == CoreEnums.events.DEFAULT.ordinal() && event.getSubEvent() == CoreEnums.subEvents.MasterTimerExp.ordinal())
            {
            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Exit timer expire. Jump on timer obj having cellId : "+userCallDetail.getMasterTimerCellId());
                Exit exit = new Exit();
                exit.execute(event,((Queue)msg.getJMSDestination()).getQueueName(),userCallDetail);
                exit = null;
                return;
            }
            else if(event.getEvent() == CoreEnums.events.DEFAULT.ordinal() && event.getSubEvent() == CoreEnums.subEvents.FreeMinTimerExp.ordinal())
            {
            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Free minutes timer exp. Jump on timer obj having cellId : "+userCallDetail.getFreeMinTimerCellId());
            	userCallDetail.setFreeMinTimerFlag(false);
            	userCallDetail.setRemainingFreeMinutes(0);
            	userCallDetail.setCurrentState(userCallDetail.getFreeMinTimerCellId());
            	if(event.getCallType() == CoreEnums.callType.Incoming.ordinal())
            	{
	            	Subscription subs = Utility.checkSubscription(Utility.numbercorrected(event.getaPartyMsisdn()), userCallDetail.getShortCode(), userCallDetail.getSubsServiceName());
	            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] subs in DB ="+subs);
	            	subs.setSubTimeLeft(0);
	            	Utility.updateSub(subs, event.getaPartyMsisdn());
            	}
            	else
            	{
            		Subscription subs = Utility.checkSubscription(Utility.numbercorrected(event.getbPartyMsisdn()), userCallDetail.getShortCode(), userCallDetail.getSubsServiceName());
            		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] subs in DB ="+subs);
            		subs.setSubTimeLeft(0);
	            	Utility.updateSub(subs, event.getbPartyMsisdn());
            	}
            }
            else if(event.getEvent() == CoreEnums.events.DEFAULT.ordinal() && event.getSubEvent() == CoreEnums.subEvents.UserDefinedTimerExp.ordinal())
            {
            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] User defined timer exp. Jump on timer obj having cellId : "+userCallDetail.getUserDefTimerCellId());
            	userCallDetail.setUserDefTimerFlag(false);
            	userCallDetail.setCurrentState(userCallDetail.getUserDefTimerCellId());
            }
            else if(event.getEvent() == CoreEnums.events.DEFAULT.ordinal() && event.getSubEvent() == CoreEnums.subEvents.CallPatched.ordinal())
            {
            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Self-gen event received for aParty callPatch.");
            	userCallDetail.setInPatchState(true);
            	userCallDetail.setCurrentState(event.getCurrentState());            	
            	userCallDetail.setPatchTime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
            	userCallDetail.setExecutePatch(true);
            }
            else if(event.getEvent() == CoreEnums.events.DEFAULT.ordinal() && event.getSubEvent() == CoreEnums.subEvents.CallPatchedWithCG.ordinal())
            {
            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Self-gen event received for callPatch with CG.");
            	userCallDetail.setInPatchWithCG(true);
            	userCallDetail.setCurrentState(event.getCurrentState());            	
            	userCallDetail.setPatchTime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
            	userCallDetail.setExecutePatch(true);
            	//userCallDetail.setAlreadyRandomGen(true);
            }
            else if(event.getEvent() == CoreEnums.events.E_GenerateDTMF.ordinal())
    		{	
            	DtmfGenerator dtmfgenerator ;
				if((userCallDetail.getCgVid() > 0) && (userCallDetail.getaPartyVId() >0))
					dtmfgenerator = new DtmfGenerator(userCallDetail.getCgVid(), userCallDetail.getaPartyVId(), event.getIp(), event.getHardware(), ((Queue)msg.getJMSDestination()).getQueueName(), userCallDetail.getDtmfgenerate());
				else
					dtmfgenerator = new DtmfGenerator(event.getvId(), event.getvId(), event.getIp(), event.getHardware(), ((Queue)msg.getJMSDestination()).getQueueName(), userCallDetail.getDtmfgenerate());
				SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(dtmfgenerator));
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event generate dtmf added to queue = "+event.getCoreToTelephony());
    			userCallDetail.setGenRandomDtmfTimer(false);
    			return;
    		}
            else if(event.getEvent() == CoreEnums.events.DEFAULT.ordinal() && event.getSubEvent() == CoreEnums.subEvents.DialoutTimerExp.ordinal())
            {
            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Timer exp for bParty ringing. Hangup the call.");
            	
            	Exit exit = new Exit();
            	exit.execute(event, ((Queue)msg.getJMSDestination()).getQueueName(), userCallDetail);
            	exit = null;
            	if(userCallDetail.isbPartyCallDetail())		//@gp
            	{		//@gp
            		Event aPartyEvent = new Event();
    				aPartyEvent.setaPartyMsisdn(userCallDetail.getaPartyMsisdn());
    				aPartyEvent.setvId(userCallDetail.getaPartyVId());
    				aPartyEvent.setCallType(CoreEnums.callType.Incoming.ordinal());			//@gp	//but here call is obd only if magic voice then add event otherwise only send hangup
    				aPartyEvent.setEvent(CoreEnums.events.DEFAULT.ordinal());
    				aPartyEvent.setSubEvent(CoreEnums.subEvents.BPartyCallFailed.ordinal());
    				aPartyEvent.setCoreToTelephony(event.getCoreToTelephony());
    				aPartyEvent.setIp(event.getIp());
    				aPartyEvent.setHardware(event.getHardware());
    				SendToTelephony.addMessageToQueue(((Queue)msg.getJMSDestination()).getQueueName(),Utility.convertObjectToJsonStr(aPartyEvent));
    		        Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"] Self-gen event for bParty Call failed due to ringing time-out added to queue = "+((Queue)msg.getJMSDestination()).getQueueName()); 
    		        
    		        userCallDetail.getCallCdr().getCallconf().setStatus("failure");
                	userCallDetail.getCallCdr().getCallconf().setReason("Ringingtimeout");
            	}		//@gp 
            	
            //	userCallDetail.setCallEndReceived(true); 
            	
            	userCallDetail.setDialStatus("failure");
				userCallDetail.setDialFailureReason("Ringingtimeout");
		        userCallDetail.setNextStateFlag(false);	  
		        userCallDetail.getCallCdr().setStatus("failure");
		        userCallDetail.getCallCdr().setReleasereason("Ringingtimeout");
		        userCallDetail.getCallCdr().setEnddatetime(userCallDetail.getCallEndTime());
		        if(userCallDetail.getObdTable() != null)	//@gp  DB was not updated for 
		        	DBConnection.insertObdStatus("update "+userCallDetail.getObdTable()+" set status = \"Failed\", reason = \"Ringingtimeout\" where msisdn = \""+event.getbPartyMsisdn()+"\"");
				
				memCacheJSon.set(userCallDetail.getbPartyVId()+"_"+event.getbPartyMsisdn(), 7200 ,userCallDetail);
            	return;
            }
            else if(event.getEvent() == CoreEnums.events.DEFAULT.ordinal() && event.getSubEvent() == CoreEnums.subEvents.BpartyHangupContinueWithAparty.ordinal())
            {	
            	//Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Self Gen event received BpartyHangupContinueWithAparty , close conf tag for key = "+userCallDetail.getbPartyMsisdn()+"_"+userCallDetail.getbPartyVId());
            	//userCallDetail.getCallCdr().removecallconfCdr(userCallDetail.getbPartyMsisdn()+"_"+userCallDetail.getbPartyVId());
            	userCallDetail.setExecuteCurrentState(true);
            	userCallDetail.setNextStateFlag(true);
            }
            else if(event.getEvent() == CoreEnums.events.DEFAULT.ordinal() && event.getSubEvent() == CoreEnums.subEvents.BpartyTalkingReceived.ordinal())
            {
            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Self Gen event received  for B party picked the call");
            	userCallDetail.setDialStatusbparty(false);
            	memCacheJSon.set(userCallDetail.getaPartyVId()+"_"+event.getaPartyMsisdn(), 7200 ,userCallDetail);
            	return;
            }
            /*else if(event.getEvent() == CoreEnums.events.DEFAULT.ordinal() && event.getSubEvent() == CoreEnums.subEvents.AlreadyPicked.ordinal())
            {
            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Self Gen event received for aparty AlreadyPicked");
            	userCallDetail.setAlreadypicked(true);
            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] already picked ="+userCallDetail.isAlreadypicked());
            	memCacheJSon.set(userCallDetail.getaPartyVId()+"_"+event.getaPartyMsisdn(), 7200 ,userCallDetail);
            	return;
            }*/
            else if(event.getCallType() == CoreEnums.callType.Outgoing.ordinal() && event.getEvent() == CoreEnums.events.E_OBD.ordinal())
            {
            	try
            	{
            		if(userCallDetail == null)
	                	userCallDetail = new UserCallDetail();
	                
	            	key = event.getIvrCode()+"_"+event.getService().toUpperCase()+"_"+event.getServiceName();//56060_OBD_MV
	            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Event is OBD start, IVR_Code = "+event.getIvrCode()+" , service = "+event.getServiceName()+" ,MSISDN = "+event.getaPartyMsisdn()+" ,VId = "+event.getvId()+" ,key = "+key+"_startNode");
	        		startNodeId = Integer.parseInt(memCacheJSon.get(key+"_startNode").toString());
	                
	        		currentnode= core.getCurrentNode(startNodeId,key); 
	        		userCallDetail.setShortCode(event.getIvrCode());	  
	        		userCallDetail.setCurrentState(startNodeId);
	                userCallDetail.setKey(key); 
	                userCallDetail.setObdjobtype(event.getJobType());
	                Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"] event.getJobtype() ="+event.getJobType()+" , userCallDetail.getObdjobtype() ="+userCallDetail.getObdjobtype());
	               
	                userCallDetail.setbPartyMsisdn(event.getbPartyMsisdn());
	                userCallDetail.setObdTable("obd_msisdn_"+event.getObdlist());
	                userCallDetail.setCallStartTime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
	               
	                //userCallDetail.setShortCode(event.getIvrCode());
	                Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] OBD startNodeId = "+startNodeId);
            	}
            	catch(Exception e)
            	{
            		Logger.sysLog(LogValues.error, this.getClass().getName(), "Caught exception while getting startNodeId for OBD service from memCache for shortCode = "+event.getIvrCode());
            		try
            		{
	            		loadServiceurl = loadServiceurl.replace("$servicename$", event.getServiceName()).replace("$shortcode$", event.getIvrCode()).replace("$calltype$", CoreEnums.callType.values[event.getCallType()].toString());
	        			String resp = Utility.callUrl(loadServiceurl);
	        			Logger.sysLog(LogValues.error, this.getClass().getName(),"["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] loadServiceurl = "+loadServiceurl+", response = "+resp);
	        			if(resp.equalsIgnoreCase("success"))
	        			{
	            			startNodeId = Integer.parseInt(memCacheJSon.get(key+"_startNode").toString());
	            			userCallDetail.setCurrentState(startNodeId);
	            			Logger.sysLog(LogValues.warn, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Service loaded again in cache. startNodeId = "+startNodeId);
	            		}
	        			else
	        				return;
            		}
            		catch(Exception E)
            		{
            			Logger.sysLog(LogValues.error, this.getClass().getName(), "Caught exception while hitting loadservice url = "+loadServiceurl+" , for" +key+"_startNode");
            			return;
            		}
            	}
            }
            else if(event.getEvent() == CoreEnums.events.E_DTMF.ordinal())
            {
            	String dtmfInput = "";
            	CallCdr callCdr = userCallDetail.getCallCdr();
            	if(event.getSubEvent()== 10)
            		dtmfInput = "*";
                else if(event.getSubEvent()== 11)
                	dtmfInput = "#";
                else if(event.getSubEvent()== 12)
                	dtmfInput = "NoInput";
                else 
                	dtmfInput = ""+event.getSubEvent();
            	DtmfCdr dtmf = new DtmfCdr(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"),dtmfInput);
            	callCdr.addDtmfList(dtmf);
            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Dtmf "+dtmfInput+" callCdr added to callCdr.");
            }
            else if((event.getEvent()== CoreEnums.events.E_PlayEnd.ordinal()) && 
            		(event.getSubEvent()== CoreEnums.playEndSubEvent.S_PlayStopByApp.ordinal()))
            {
            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] ForceFully playend event received for id : "+event.getTransId());
            	try
            	{
            		if(!(event.getTransId().equals("0")))
            		{
		            	MediaCdr media = userCallDetail.getCallCdr().getMediaCdr(event.getTransId());
		            	if(media != null)
		            	{
			        		media.setEndtime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
			        		userCallDetail.getCallCdr().addMediaMap(event.getTransId(), media);
			        		userCallDetail.getCallCdr().removeMedia(event.getTransId());
		            	}
		            	if(userCallDetail.isCallEndReceived())
		            	{
		            		if(userCallDetail.isSeek())
		            		{
		            			if(userCallDetail.isSinglebookmark())
		            				Utility.updateBookMark(event, userCallDetail);
		            			else
		            				Utility.addBookMark(event, userCallDetail);
		            		}
		            	}
		            	else if(userCallDetail.isSaveSeekFile()  && !(userCallDetail.isSinglebookmark()))
		            	{	
		            		// if(!userCallDetail.isSinglebookmark())
		            		 //{
		            			 userCallDetail.setSaveSeekFile(false);
				            		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Save bookmark when play end : "+event.getTransId());
				            		if(userCallDetail.isSeek())
				            		{
				            			Utility.addBookMark(event, userCallDetail);
				            			userCallDetail.setSeek(false);
					            	}
				        }
		            		 //}
		            		
		            }
		            	
            	}
            	catch(Exception e)
            	{
            		Logger.sysLog(LogValues.error, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Caught Exception while getting MediaCDR for transId = "+event.getTransId()+"\n"+coreException.GetStack(e));
            	}
            }
            else
            {
            	if(event.getEvent() == CoreEnums.events.E_ChState.ordinal())
                {
            		if(event.getSubEvent()== CoreEnums.subEvents.S_CallRinging.ordinal())
                	{
    	            	try
    	            	{     	            		
    	            		if(userCallDetail == null)
			                	userCallDetail = new UserCallDetail();    	            		
    	            		
    	            		String shortcode_key = Utility.getmaxmatch((HashMap<String, String>) memCacheJSon.get("ServiceMap"),event.getIvrCode());
    	            		HashMap serviceMap = (HashMap<String, String>) memCacheJSon.get("ServiceMap");
    	            	//	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Service Doesn't exist for shortcode = "+shortcode_key+" for ivr code = "+event.getIvrCode());
    	            		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Service Map = "+serviceMap);
    	            		if((shortcode_key != null) &&(!shortcode_key.equals(""))) 
    	            		{
	    	            		if(  event.getIvrCode().length() > shortcode_key.length())					//bparty exists after shortcode
		    	            		{	String shortcode = (String) serviceMap.get(shortcode_key);
			    	            		if(shortcode == null || shortcode.equals(""))
		    	            			{
		    	            				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Service Does not exist for shortcode = "+shortcode_key);
		    	            				Call call = new Call(CoreEnums.callOperations.hangup.ordinal(),event.getvId(),false,"","",event.getHardware(), ((Queue)msg.getJMSDestination()).getQueueName());
		        	    	    	        SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(call));
		        	    	    	        Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event Call added to queue = "+event.getCoreToTelephony());
		        	    	    	        Utility.createCdr(event, "Failure", "ServiceNotFound");
		        	    	    	        return;
		    	            			}
		    	            	
	    	            		
			    	            		String BPartyNo = event.getIvrCode().substring(shortcode_key.length(),event.getIvrCode().length());
			    	            	
			    	            		userCallDetail.setbPartyMsisdn(BPartyNo);
			    	            		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Bparty number found in start = "+BPartyNo);
			    	            		userCallDetail.setShortCode(shortcode);
				                    	userCallDetail.setDirectCall("true");
				                    	BPartyNo = null;
				                    }
	    	            		else
	    	            		{
	    	            			userCallDetail.setShortCode(event.getIvrCode());
	    	            			userCallDetail.setbPartyMsisdn("");
	    	            		}	
    	            		}
    	            		else
    	            		{
    	            			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] ivrcode = "+event.getIvrCode()+", received does not match with any existing shortcode ");
    	            			Call call = new Call(CoreEnums.callOperations.hangup.ordinal(),event.getvId(),false,"","",event.getHardware(), ((Queue)msg.getJMSDestination()).getQueueName());
    	    	    	        SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(call));
    	    	    	        Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event Call added to queue = "+event.getCoreToTelephony());
    	    	    	        Utility.createCdr(event, "Failure", "ServiceNotFound");
    	    	    	        return;
    	            		}
    	            		
    	            		
    	            	/*	if(event.getIvrCode().length() > Integer.parseInt(ivrCodeLength))
    	                    {	
    	            			String BPartyNo = Utility.numberCorrection(event.getIvrCode(),Integer.parseInt(ivrCodeLength));     
    	                    	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] IVR_Code = "+userCallDetail.getShortCode()+" , BPartyNo = "+BPartyNo);
    	                    	userCallDetail.setbPartyMsisdn(BPartyNo);
    	                    	userCallDetail.setShortCode(event.getIvrCode().substring(0, Integer.parseInt(ivrCodeLength)));
    	                    	BPartyNo = null;
    	                    	userCallDetail.setDirectCall("true");
    	                    }
    	            		else
    	            			userCallDetail.setShortCode(event.getIvrCode());
    	            		*/ 
    	            		
    	            		key = userCallDetail.getShortCode()+"_IVR";
	            			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Event is StartCall, IVR_Code = "+userCallDetail.getShortCode()+" , service = "+event.getServiceName()+" ,MSISDN = "+event.getaPartyMsisdn()+" ,VId = "+event.getvId());
		            		try
		            		{
		            			startNodeId = Integer.parseInt(memCacheJSon.get(key+"_startNode").toString());
		            			userCallDetail.setCurrentState(startNodeId);
		            			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] startNodeId = "+startNodeId);
		            		}
		            		catch(Exception e)
		            		{
		            			Logger.sysLog(LogValues.error, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Caught exception while getting startNodeId for IVR service from memCache for shortCode = "+userCallDetail.getShortCode());
		            			loadServiceurl = loadServiceurl.replace("$servicename$", event.getServiceName()).replace("$shortcode$", event.getIvrCode()).replace("$calltype$", CoreEnums.callType.values[event.getCallType()].toString());
		            			String resp = Utility.callUrl(loadServiceurl);
		            			Logger.sysLog(LogValues.info, this.getClass().getName(),"["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] loadServiceurl = "+loadServiceurl+", response = "+resp);
		            			if(resp.equalsIgnoreCase("success"))
		            			{
			            			startNodeId = Integer.parseInt(memCacheJSon.get(key+"_startNode").toString());
			            			userCallDetail.setCurrentState(startNodeId);
			            			Logger.sysLog(LogValues.warn, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Service loaded again in cache. startNodeId = "+startNodeId);
			            		}
		            			else
		            				return;
		            		}
			                
			                userCallDetail.setKey(key);  
			                userCallDetail.setCallStartTime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
			                userCallDetail.setaPartyMsisdn(event.getaPartyMsisdn());
			            }
    	            	catch(Exception e)
    	            	{
    	            		Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));    	            		
    	            		return;
    	            	}
                	}
                	else if(event.getSubEvent()== CoreEnums.subEvents.S_CallPending.ordinal())
                	{
                		if(userCallDetail == null)
            			{
            				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Hangup received for toll-free number. Discard the event.");
            				return;
            			}
                		if(event.getCallType() == CoreEnums.callType.Incoming.ordinal())
                		{
                			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] InPatchState = "+userCallDetail.isInPatchState()+" , IsAParty = "+userCallDetail.isaPartyCallDetail()+" , InPatchWithCG = "+userCallDetail.isInPatchWithCG());
                			if((event.getSubEventCause() == CoreEnums.subEventCause.RemoteHangupFirst.ordinal() || event.getSubEventCause() == CoreEnums.subEventCause.Misc.ordinal()))
                        	{
                    			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] In Pending state by telephony for IVR. Need to exit. msisdn = "+event.getaPartyMsisdn());
                        		
                    			if(userCallDetail.isDialStatusbparty())
                    			{
                    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Dialstatus for Bparty true (still dialing to bparty) ");
                    				UserCallDetail bPartyUCD = (UserCallDetail) memCacheJSon.get(userCallDetail.getbPartyVId()+"_"+userCallDetail.getbPartyMsisdn());
                    				if(bPartyUCD != null)
                    				{
	                    				bPartyUCD.setDialStatus("failure");
	                    				userCallDetail.getCallCdr().getCallconf().setStatus(bPartyUCD.getDialStatus());
	                					//userCallDetail.getCallCdr().getCallconf().setStartdatetime(userCallDetail.getPatchTime());
	                					//if(bPartyUCD.getDialStatus().equalsIgnoreCase("failure"))
	                						userCallDetail.getCallCdr().getCallconf().setReason(CoreEnums.subEventCause.RLCSent.toString());
	                					

	                					//bPartyUCD.setCallEndReceived(true);
	                					//bPartyUCD.setInPatchState(false);                  					

	                            		Exit bPartyExit = new Exit();
	                            		event.setvId(bPartyUCD.getbPartyVId());
	                            		bPartyExit.execute(event,((Queue)msg.getJMSDestination()).getQueueName(),bPartyUCD);
	                            		event.setvId(bPartyUCD.getaPartyVId());
	                                    memCacheJSon.set(userCallDetail.getbPartyVId()+"_"+userCallDetail.getbPartyMsisdn(),7200, bPartyUCD);
	                                    Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] BParty HangUp");
	                                    bPartyUCD = null;
	                                    bPartyExit =null;
	                                    userCallDetail.setDialStatusbparty(false);
                    				}                                    
                    			}
                    			
                    			
                    			if(userCallDetail.isInPatchState())
                                {
                                	UnPatch unPatch = new UnPatch();
                					unPatch.execute(event, ((Queue)msg.getJMSDestination()).getQueueName(), userCallDetail);
                					unPatch = null;
                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] AParty hangUp the call. Call unPatched. Going to hangUp bParty.");
                					userCallDetail.getCallCdr().getCallconf().setStartdatetime(userCallDetail.getPatchTime());
                					UserCallDetail bPartyUCD = (UserCallDetail) memCacheJSon.get(userCallDetail.getbPartyVId()+"_"+userCallDetail.getbPartyMsisdn());
                					if(bPartyUCD != null)
                					{
	                					userCallDetail.getCallCdr().getCallconf().setStatus(bPartyUCD.getDialStatus());	                					
	                					userCallDetail.getCallCdr().getCallconf().setReason(CoreEnums.subEventCause.RLCSent.toString());
	                    				
	                					//bPartyUCD.setCallEndReceived(true);

	                					bPartyUCD.setInPatchState(false);                  					
	                            		Exit bPartyExit = new Exit();
	                            		event.setvId(bPartyUCD.getbPartyVId());
	                            		bPartyExit.execute(event,((Queue)msg.getJMSDestination()).getQueueName(),bPartyUCD);
	                            		event.setvId(bPartyUCD.getaPartyVId());
	                                    memCacheJSon.set(userCallDetail.getbPartyVId()+"_"+userCallDetail.getbPartyMsisdn(),7200, bPartyUCD);
	                                    Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] BParty HangUp");
	                                    bPartyUCD = null;
	                                    bPartyExit =null;
                					}
                                }
                    			
                    			if(userCallDetail.isInPatchWithCG())
                                {
                                	UnPatch unPatchWithCG = new UnPatch();
                                	unPatchWithCG.execute(event, ((Queue)msg.getJMSDestination()).getQueueName(), userCallDetail);
                                	unPatchWithCG = null;
                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] AParty hangUp the call. Call unPatched. Going to hangUp CG.");
                					userCallDetail.setInPatchWithCG(false);
                					userCallDetail.getCallCdr().getCallconf().setStartdatetime(userCallDetail.getPatchTime());
                					UserCallDetail cgUCD = (UserCallDetail) memCacheJSon.get(userCallDetail.getCgVid()+"_"+userCallDetail.getCgMsisdn());
                					if(cgUCD != null)
                					{
	                					userCallDetail.getCallCdr().getCallconf().setStatus(cgUCD.getDialStatus());            					
	                					userCallDetail.getCallCdr().getCallconf().setReason(CoreEnums.subEventCause.RLCSent.toString());  // added on 15-04-14 as reson null in conf tag
	                    				
	                					//userCallDetail.getCallCdr().setReleasereason(releasereason);
	                					if(cgUCD.getDialStatus() != null)
	                					{
	                						if(cgUCD.getDialStatus().equalsIgnoreCase("failure"))
	                    						userCallDetail.getCallCdr().getCallconf().setReason(cgUCD.getDialFailureReason());
	                    					
	                					}
	                					
	                					//cgUCD.setCallEndReceived(true);
	                					cgUCD.setInPatchWithCG(false);                  					
	                            		Exit bPartyExit = new Exit();
	                            		event.setvId(cgUCD.getbPartyVId());
	                            		bPartyExit.execute(event,((Queue)msg.getJMSDestination()).getQueueName(),cgUCD);
	                            		event.setvId(cgUCD.getaPartyVId());
	                                    memCacheJSon.set(userCallDetail.getCgVid()+"_"+userCallDetail.getCgMsisdn(),7200, cgUCD);
	                                    Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CG HangUp");
	                                    cgUCD = null;
	                                    bPartyExit =null;	                                    
                					}
                                }
                    			userCallDetail.getCallCdr().setStatus("Success");
                         		userCallDetail.getCallCdr().setReason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
                         		
                        		//userCallDetail.setCallEndReceived(true);
                        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] 2.Setting CallEnd status.");
                        		Exit exit = new Exit();
                                exit.execute(event,((Queue)msg.getJMSDestination()).getQueueName(),userCallDetail);
                               
                                memCacheJSon.set(event.getvId()+"_"+event.getaPartyMsisdn(),7200, userCallDetail);
                                exit = null;  
                                return;                			
                        	}
                			else if((event.getSubEventCause() == CoreEnums.subEventCause.RLCSent.ordinal()) || 
                    				(event.getSubEventCause() == CoreEnums.subEventCause.ReleasingCall.ordinal()))
                        	{
                				if(!userCallDetail.isCallEndReceived())
                				{
                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] 1.Setting CallEnd status.");
                					userCallDetail.setCallEndReceived(true);
                					userCallDetail.getCallCdr().setStatus("Success");
                					userCallDetail.getCallCdr().setReason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
                				}
                				
                				if(userCallDetail.getCallCdr().getStatus() == null)
                					userCallDetail.getCallCdr().setStatus("Success");
                				
                				//if(userCallDetail.getbPartyMsisdn() != null && userCallDetail.getbPartyVId() >0)
                				if(userCallDetail.isDialStatusbparty())
                    			{
                    				UserCallDetail bPartyUCD = (UserCallDetail) memCacheJSon.get(userCallDetail.getbPartyVId()+"_"+userCallDetail.getbPartyMsisdn());
                    				if(bPartyUCD != null)
                    				{
	                    				bPartyUCD.setDialStatus("failure");
	                    				userCallDetail.getCallCdr().getCallconf().setStatus(bPartyUCD.getDialStatus());
	                					//userCallDetail.getCallCdr().getCallconf().setStartdatetime(userCallDetail.getPatchTime());
	                					if(bPartyUCD.getDialStatus().equalsIgnoreCase("failure"))
	                						userCallDetail.getCallCdr().getCallconf().setReason(bPartyUCD.getDialFailureReason());
	                					

	                					//bPartyUCD.setCallEndReceived(true);
	                					//bPartyUCD.setInPatchState(false);                  					

	                            		Exit bPartyExit = new Exit();
	                            		event.setvId(bPartyUCD.getbPartyVId());
	                            		bPartyExit.execute(event,((Queue)msg.getJMSDestination()).getQueueName(),bPartyUCD);
	                            		event.setvId(bPartyUCD.getaPartyVId());
	                                    memCacheJSon.set(userCallDetail.getbPartyVId()+"_"+userCallDetail.getbPartyMsisdn(),7200, bPartyUCD);
	                                    Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] BParty HangUp");
	                                    bPartyUCD = null;
	                                    bPartyExit =null;
	                                    userCallDetail.setDialStatusbparty(false);
                    				}                                    
                    			}
                				
                				if(userCallDetail.isaPartyCallDetail())
                				{
                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] 1.Setting CallEnd status for MV AParty.");
                					userCallDetail.getCallCdr().setStatus("Success");
                					if(userCallDetail.getCallCdr().getReason() ==null)
                						userCallDetail.getCallCdr().setReason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
                					
                					userCallDetail.getCallCdr().getCallconf().setStartdatetime(userCallDetail.getPatchTime());
                					if(userCallDetail.getCallCdr().getCallconf().getStatus() == null)
                					{
                						userCallDetail.getCallCdr().getCallconf().setStatus(userCallDetail.getDialStatus());
                						userCallDetail.getCallCdr().getCallconf().setReason(userCallDetail.getDialFailureReason());
                					}  
                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Dial status = "+userCallDetail.getDialStatus()+", Reason = "+userCallDetail.getDialFailureReason()+", ConfStatus = "+userCallDetail.getCallCdr().getCallconf().getStatus()+", ConfReason = "+userCallDetail.getCallCdr().getCallconf().getReason());
                				} 
                				if(userCallDetail.isInPatchWithCG())//(userCallDetail.getCgMsisdn() != null && userCallDetail.getCgVid() > 0)
                				{
                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] 2.Setting CallEnd status for AParty patched with CG.");
                					userCallDetail.getCallCdr().setStatus("Success");
                					userCallDetail.getCallCdr().setReason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
                					
                					userCallDetail.getCallCdr().getCallconf().setStartdatetime(userCallDetail.getPatchTime());
                					if(userCallDetail.getCallCdr().getCallconf().getStatus() == null)
                					{
                						userCallDetail.getCallCdr().getCallconf().setStatus(userCallDetail.getDialStatus());
                						userCallDetail.getCallCdr().getCallconf().setReason(userCallDetail.getDialFailureReason());
                					}  
                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Dial status = "+userCallDetail.getDialStatus()+", Reason = "+userCallDetail.getDialFailureReason()+", ConfStatus = "+userCallDetail.getCallCdr().getCallconf().getStatus()+", ConfReason = "+userCallDetail.getCallCdr().getCallconf().getReason());
                				}
                				memCacheJSon.set(event.getvId()+"_"+event.getaPartyMsisdn(),7200, userCallDetail);
                    			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Discard the event : event = "+event.getEvent()+", subEvent = "+event.getSubEvent()+", subEventCause = "+event.getSubEventCause());
            	            	return;
                        	}
                			else if((event.getSubEventCause() == CoreEnums.subEventCause.NoAnswer.ordinal()) || (event.getSubEventCause() == CoreEnums.subEventCause.E1Fluctuation.ordinal()))
                			{
                				if(event.getSubEventCause() == CoreEnums.subEventCause.NoAnswer.ordinal())
                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] EVENT RECEIVED FOR TIME UP BEFORE ANM : event = "+event.getEvent()+", subEvent = "+event.getSubEvent()+", subEventCause = "+event.getSubEventCause());
                				else
                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] EVENT RECEIVED DUR TO E1Fluctuation : event = "+event.getEvent()+", subEvent = "+event.getSubEvent()+", subEventCause = "+event.getSubEventCause());
                    		//	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] In Pending state by telephony for IVR. Need to exit. msisdn = "+event.getaPartyMsisdn());
                        		
                    			if(userCallDetail.isDialStatusbparty())
                    			{
                    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Dialstatus for Bparty true (still dialing to bparty) ");
                    				UserCallDetail bPartyUCD = (UserCallDetail) memCacheJSon.get(userCallDetail.getbPartyVId()+"_"+userCallDetail.getbPartyMsisdn());
                    				if(bPartyUCD != null)
                    				{
	                    				bPartyUCD.setDialStatus("failure");
	                    				userCallDetail.getCallCdr().getCallconf().setStatus(bPartyUCD.getDialStatus());
	                					//userCallDetail.getCallCdr().getCallconf().setStartdatetime(userCallDetail.getPatchTime());
	                					//if(bPartyUCD.getDialStatus().equalsIgnoreCase("failure"))
	                    				if(event.getSubEventCause() == CoreEnums.subEventCause.NoAnswer.ordinal())
	                						userCallDetail.getCallCdr().getCallconf().setReason(CoreEnums.subEventCause.RLCSent.toString());
	                    				else
	                    					userCallDetail.getCallCdr().getCallconf().setReason(CoreEnums.subEventCause.E1Fluctuation.toString());
	                					

	                					//bPartyUCD.setCallEndReceived(true);
	                					//bPartyUCD.setInPatchState(false);                  					

	                            		Exit bPartyExit = new Exit();
	                            		event.setvId(bPartyUCD.getbPartyVId());
	                            		bPartyExit.execute(event,((Queue)msg.getJMSDestination()).getQueueName(),bPartyUCD);
	                            		event.setvId(bPartyUCD.getaPartyVId());
	                                    memCacheJSon.set(userCallDetail.getbPartyVId()+"_"+userCallDetail.getbPartyMsisdn(),7200, bPartyUCD);
	                                    Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] BParty HangUp");
	                                    bPartyUCD = null;
	                                    bPartyExit =null;
	                                    userCallDetail.setDialStatusbparty(false);
                    				}                                    
                    			}
                    			
                    			
                    			if(userCallDetail.isInPatchState())
                                {
                                	UnPatch unPatch = new UnPatch();
                					unPatch.execute(event, ((Queue)msg.getJMSDestination()).getQueueName(), userCallDetail);
                					unPatch = null;
                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] AParty hangUp the call. Call unPatched. Going to hangUp bParty.");
                					userCallDetail.getCallCdr().getCallconf().setStartdatetime(userCallDetail.getPatchTime());
                					UserCallDetail bPartyUCD = (UserCallDetail) memCacheJSon.get(userCallDetail.getbPartyVId()+"_"+userCallDetail.getbPartyMsisdn());
                					if(bPartyUCD != null)
                					{
	                					userCallDetail.getCallCdr().getCallconf().setStatus(bPartyUCD.getDialStatus());	                					
	                					
	                					if(event.getSubEventCause() == CoreEnums.subEventCause.NoAnswer.ordinal())
	                						userCallDetail.getCallCdr().getCallconf().setReason(CoreEnums.subEventCause.RLCSent.toString());
	                    				else
	                    					userCallDetail.getCallCdr().getCallconf().setReason(CoreEnums.subEventCause.E1Fluctuation.toString());
	                    				
	                					//bPartyUCD.setCallEndReceived(true);

	                					bPartyUCD.setInPatchState(false);                  					
	                            		Exit bPartyExit = new Exit();
	                            		event.setvId(bPartyUCD.getbPartyVId());
	                            		bPartyExit.execute(event,((Queue)msg.getJMSDestination()).getQueueName(),bPartyUCD);
	                            		event.setvId(bPartyUCD.getaPartyVId());
	                                    memCacheJSon.set(userCallDetail.getbPartyVId()+"_"+userCallDetail.getbPartyMsisdn(),7200, bPartyUCD);
	                                    Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] BParty HangUp");
	                                    bPartyUCD = null;
	                                    bPartyExit =null;
                					}
                                }
                    			
                    			if(userCallDetail.isInPatchWithCG())
                                {
                                	UnPatch unPatchWithCG = new UnPatch();
                                	unPatchWithCG.execute(event, ((Queue)msg.getJMSDestination()).getQueueName(), userCallDetail);
                                	unPatchWithCG = null;
                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] AParty hangUp the call. Call unPatched. Going to hangUp CG.");
                					userCallDetail.setInPatchWithCG(false);
                					userCallDetail.getCallCdr().getCallconf().setStartdatetime(userCallDetail.getPatchTime());
                					UserCallDetail cgUCD = (UserCallDetail) memCacheJSon.get(userCallDetail.getCgVid()+"_"+userCallDetail.getCgMsisdn());
                					if(cgUCD != null)
                					{
	                					userCallDetail.getCallCdr().getCallconf().setStatus(cgUCD.getDialStatus());
	                					//userCallDetail.getCallCdr().setReleasereason(releasereason);
	                					if(cgUCD.getDialStatus() != null)
	                					{
	                						if(cgUCD.getDialStatus().equalsIgnoreCase("failure"))
	                    						userCallDetail.getCallCdr().getCallconf().setReason(cgUCD.getDialFailureReason());
	                    					
	                					}
	                					
	                					//cgUCD.setCallEndReceived(true);
	                					cgUCD.setInPatchWithCG(false);                  					
	                            		Exit bPartyExit = new Exit();
	                            		event.setvId(cgUCD.getbPartyVId());
	                            		bPartyExit.execute(event,((Queue)msg.getJMSDestination()).getQueueName(),cgUCD);
	                            		event.setvId(cgUCD.getaPartyVId());
	                                    memCacheJSon.set(userCallDetail.getCgVid()+"_"+userCallDetail.getCgMsisdn(),7200, cgUCD);
	                                    Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CG HangUp");
	                                    cgUCD = null;
	                                    bPartyExit =null;	                                    
                					}
                                }
                    			userCallDetail.getCallCdr().setStatus("Success");
                         		userCallDetail.getCallCdr().setReason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
                         		
                        		//userCallDetail.setCallEndReceived(true);
                        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] 2.Setting CallEnd status.");
                        		Exit exit = new Exit();
                                exit.execute(event,((Queue)msg.getJMSDestination()).getQueueName(),userCallDetail);
                               
                                memCacheJSon.set(event.getvId()+"_"+event.getaPartyMsisdn(),7200, userCallDetail);
                                exit = null;  
                                
                				return;
                			}
                			else
                    		{
                                Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Any other event for Pending_state(IVR Service) sub_eventCause = "+event.getSubEventCause()+" .stored current state = "+userCallDetail.getCurrentState());
                    		} 
                		}                		
                		else if((event.getCallType() == CoreEnums.callType.Outgoing.ordinal()))
                		{
                			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] InPatchState = "+userCallDetail.isInPatchState()+" , IsBParty = "+userCallDetail.isbPartyCallDetail()+" , IsCgCallDetail = "+userCallDetail.isCgCallDetail());
                			if((event.getSubEventCause() == CoreEnums.subEventCause.RemoteHangupFirst.ordinal()))
                			{
                				Exit exit = null;
                				//if((userCallDetail.getaPartyVId() >0 && userCallDetail.getaPartyMsisdn() != null) || userCallDetail.isCgCallDetail())
                				if(userCallDetail.isbPartyCallDetail() || userCallDetail.isCgCallDetail())
                				{
                					UnPatch unPatch = new UnPatch();
                					unPatch.execute(event, ((Queue)msg.getJMSDestination()).getQueueName(), userCallDetail);
                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] BParty hangUp the call. Call unPatched. Going to continue with aParty.APrty UCD-Key = "+userCallDetail.getaPartyVId()+"_"+userCallDetail.getaPartyMsisdn());
                					unPatch = null;
                					//userCallDetail.setInPatchState(false);
                					userCallDetail.setInPatchWithCG(false);
                					UserCallDetail aPartyUCD = (UserCallDetail) memCacheJSon.get(userCallDetail.getaPartyVId()+"_"+userCallDetail.getaPartyMsisdn());
                					if(aPartyUCD != null)
                					{
	                					//aPartyUCD.setCallEndReceived(true);
	                					aPartyUCD.setInPatchState(false);
	                					aPartyUCD.setInPatchWithCG(false);
	                					//aPartyUCD.getCallCdr().addCallConfList(userCallDetail.getCallCdr().getCallconf());
	                					aPartyUCD.getCallCdr().getCallconf().setStartdatetime(userCallDetail.getPatchTime());
	                					aPartyUCD.getCallCdr().getCallconf().setEnddatetime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
	                					aPartyUCD.getCallCdr().getCallconf().setStatus(userCallDetail.getDialStatus());
	                					if(userCallDetail.getDialStatus() !=null)
	                					{
		                					if(userCallDetail.getDialStatus().equalsIgnoreCase("failure"))
		                						aPartyUCD.getCallCdr().getCallconf().setReason(userCallDetail.getDialFailureReason());
		                					else
		                						aPartyUCD.getCallCdr().getCallconf().setReason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
	                					}
	                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] userCallDetail.getDialStatus() = "+userCallDetail.getDialStatus()+ " , CallConfCDR = "+userCallDetail.getCallCdr().getCallconf());
	                					//aPartyUCD.getCallCdr().getCallconf().setStatus(userCallDetail.getDialStatus());
	                					//aPartyUCD.getCallCdr().getCallconf().setReason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
	                					Event aPartyEvent = new Event();
	                					if(userCallDetail.isCgCallDetail())
	                					{
	                						
	                						aPartyEvent.setvId(userCallDetail.getaPartyVId());
	                						aPartyEvent.setCallType(userCallDetail.getCallTypePatchedWithCg());
	                						aPartyEvent.setaPartyMsisdn(userCallDetail.getaPartyMsisdn());
	                						aPartyEvent.setbPartyMsisdn(userCallDetail.getbPartyMsisdn());
	                						aPartyEvent.setEvent(event.getEvent());
	                						aPartyEvent.setSubEvent(event.getSubEvent());
	                						aPartyEvent.setCoreToTelephony(event.getCoreToTelephony());
		                            		exit = new Exit();
		                                    exit.execute(aPartyEvent,((Queue)msg.getJMSDestination()).getQueueName(),aPartyUCD);
		                                    memCacheJSon.set(userCallDetail.getaPartyVId()+"_"+userCallDetail.getaPartyMsisdn(),7200, aPartyUCD);
		                                    exit = null;   
		                                    Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] AParty HangUp");
		                                    aPartyUCD = null;
	                					}
	                					else
	                					{
		                					
		                					aPartyEvent.setaPartyMsisdn(userCallDetail.getaPartyMsisdn());
		                					aPartyEvent.setvId(userCallDetail.getaPartyVId());
		                					aPartyEvent.setCallType(CoreEnums.callType.Incoming.ordinal());
		                					aPartyEvent.setEvent(CoreEnums.events.DEFAULT.ordinal());
		                					aPartyEvent.setSubEvent(CoreEnums.subEvents.BpartyHangupContinueWithAparty.ordinal());
		                					aPartyEvent.setCoreToTelephony(event.getCoreToTelephony());
		                					SendToTelephony.addMessageToQueue(((Queue)msg.getJMSDestination()).getQueueName(),Utility.convertObjectToJsonStr(aPartyEvent));
		        	    	    	        Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Self-gen event for BpartyHangupContinueWithAparty added to queue");
		        	    	    	        memCacheJSon.set(userCallDetail.getaPartyVId()+"_"+userCallDetail.getaPartyMsisdn(),7200, aPartyUCD);
		        	    	    	        aPartyUCD = null;
	                					}
                					}
                					exit = new Exit();
                					exit.execute(event,((Queue)msg.getJMSDestination()).getQueueName(),userCallDetail);
                					exit = null;
                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] BParty HangUp");
                				}  
                				else if(userCallDetail.isInPatchWithCG())
                				{
                					UnPatch unPatchWithCG = new UnPatch();
                                	unPatchWithCG.execute(event, ((Queue)msg.getJMSDestination()).getQueueName(), userCallDetail);
                                	unPatchWithCG = null;
                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] AParty hangUp the call. Call unPatched. Going to hangUp CG.");
                					userCallDetail.setInPatchWithCG(false);
                					userCallDetail.getCallCdr().getCallconf().setStartdatetime(userCallDetail.getPatchTime());
                					UserCallDetail cgUCD = (UserCallDetail) memCacheJSon.get(userCallDetail.getCgVid()+"_"+userCallDetail.getCgMsisdn());
                					if(cgUCD != null)
                					{
	                					userCallDetail.getCallCdr().getCallconf().setStatus(cgUCD.getDialStatus());
	                					//userCallDetail.getCallCdr().setReleasereason(releasereason);
	                					if(cgUCD.getDialStatus() != null)
	                					{
	                						if(cgUCD.getDialStatus().equalsIgnoreCase("failure"))
	                    						userCallDetail.getCallCdr().getCallconf().setReason(cgUCD.getDialFailureReason());
	                						else
	                							userCallDetail.getCallCdr().setReleasereason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
	                    					
	                					}
	                					
	                					//	cgUCD.setCallEndReceived(true);
	                					cgUCD.setInPatchWithCG(false);                  					
	                            		Exit bPartyExit = new Exit();
	                            		event.setvId(cgUCD.getbPartyVId());
	                            		bPartyExit.execute(event,((Queue)msg.getJMSDestination()).getQueueName(),cgUCD);
	                            		event.setvId(cgUCD.getaPartyVId());
	                                    memCacheJSon.set(userCallDetail.getCgVid()+"_"+userCallDetail.getCgMsisdn(),7200, cgUCD);
	                                    Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CG HangUp");
	                                    cgUCD = null;
	                                    bPartyExit =null;	                                    
                					}
                					
                					exit = new Exit();
                					exit.execute(event,((Queue)msg.getJMSDestination()).getQueueName(),userCallDetail);
                					exit = null;
                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] AParty HangUp");
                                }
                				else
                				{
		                			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] In Pending state by telephony for OBD. Need to exit. msisdn = "+event.getaPartyMsisdn());
		                    		userCallDetail.getCallCdr().setStatus("Success");
		                    		userCallDetail.getCallCdr().setReason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
		                    		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] 3.Setting CallEnd status.");
		                    		//userCallDetail.setCallEndReceived(true);
		                    		exit = new Exit();
		                            exit.execute(event,((Queue)msg.getJMSDestination()).getQueueName(),userCallDetail);
		                            Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] userCallDetail key in OBD : "+userCallDetail.getbPartyVId());
		                            exit = null;
                				}
	        	            	memCacheJSon.set(userCallDetail.getbPartyVId()+"_"+userCallDetail.getbPartyMsisdn(),7200, userCallDetail);            	            
	                            return;  
                			}
                			else if((event.getSubEventCause() == CoreEnums.subEventCause.RLCSent.ordinal()) || 
                    				(event.getSubEventCause() == CoreEnums.subEventCause.ReleasingCall.ordinal()))
                			{
                				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Reason for callEnd = "+CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
                				if(!userCallDetail.isCallEndReceived())
                				{
                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] 4.Setting CallEnd status. Reason = "+CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
                					userCallDetail.getCallCdr().setReason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
                					userCallDetail.getCallCdr().setStatus("Success");
                					userCallDetail.setCallEndReceived(true);                					
                				}
                				
                				//if(userCallDetail.getaPartyMsisdn() != null && userCallDetail.getaPartyVId()>0)
                				if(userCallDetail.isbPartyCallDetail())
                				{
                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Release reason set for ConfCall.");
                					UserCallDetail aPartyUCD = (UserCallDetail) memCacheJSon.get(userCallDetail.getaPartyVId()+"_"+userCallDetail.getaPartyMsisdn());
                					if(aPartyUCD != null)
                					{
	                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] userCallDetail.getDialStatus() = "+userCallDetail.getDialStatus());
	                					aPartyUCD.getCallCdr().getCallconf().setStatus(userCallDetail.getDialStatus());
	                					if(userCallDetail.getPatchTime() != null)
	                						aPartyUCD.getCallCdr().getCallconf().setStartdatetime(userCallDetail.getPatchTime());
	                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]   userCallDetail.getDialStatus() = "+userCallDetail.getDialStatus()+" userCallDetail.getDialFailureReason() = "+userCallDetail.getDialFailureReason());
	                					if(userCallDetail.getDialStatus() != null)
	                					{
		                					if(userCallDetail.getDialStatus().equalsIgnoreCase("failure"))
		                					{
		                						if(aPartyUCD.getCallCdr().getCallconf().getReason() == null)
		                							aPartyUCD.getCallCdr().getCallconf().setReason(userCallDetail.getDialFailureReason());
		                					}
		                					else
		                					{
		                						if(aPartyUCD.getCallCdr().getCallconf().getReason() == null)
		                							aPartyUCD.getCallCdr().getCallconf().setReason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
		                					}	
		                				}
	                					else
	                					{
	                						
	                					}
	                					memCacheJSon.set(userCallDetail.getaPartyVId()+"_"+userCallDetail.getaPartyMsisdn(),7200, aPartyUCD);
	                					aPartyUCD = null;
                					}
                				}
                				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] userCallDetail key in OBD : "+userCallDetail.getbPartyVId()+"_"+userCallDetail.getbPartyMsisdn());
                				memCacheJSon.set(userCallDetail.getbPartyVId()+"_"+userCallDetail.getbPartyMsisdn(),7200, userCallDetail);
                				return;
            	            }
                			else if(event.getSubEventCause() == CoreEnums.subEventCause.E1Fluctuation.ordinal())
                			{
                				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] in pending due to reason : subEventCause =E1Fluctuation");
                				Exit exit = null;
                				
                				if(userCallDetail.isDialStatusbparty())
                				{
                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] dialstatus true");
                				}
                				else
                				{
                					//if((userCallDetail.getaPartyVId() >0 && userCallDetail.getaPartyMsisdn() != null) || userCallDetail.isCgCallDetail())
                    				if(userCallDetail.isbPartyCallDetail() || userCallDetail.isCgCallDetail())
                    				{
                    					UnPatch unPatch = new UnPatch();
                    					unPatch.execute(event, ((Queue)msg.getJMSDestination()).getQueueName(), userCallDetail);
                    					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] BParty hangUp the call. Call unPatched. Going to continue with aParty.APrty UCD-Key = "+userCallDetail.getaPartyVId()+"_"+userCallDetail.getaPartyMsisdn());
                    					unPatch = null;
                    					//userCallDetail.setInPatchState(false);
                    					userCallDetail.setInPatchWithCG(false);
                    					UserCallDetail aPartyUCD = (UserCallDetail) memCacheJSon.get(userCallDetail.getaPartyVId()+"_"+userCallDetail.getaPartyMsisdn());
                    					if(aPartyUCD != null)
                    					{
    	                					//aPartyUCD.setCallEndReceived(true);
    	                					aPartyUCD.setInPatchState(false);
    	                					aPartyUCD.setInPatchWithCG(false);
    	                					//aPartyUCD.getCallCdr().addCallConfList(userCallDetail.getCallCdr().getCallconf());
    	                					aPartyUCD.getCallCdr().getCallconf().setStartdatetime(userCallDetail.getPatchTime());
    	                					aPartyUCD.getCallCdr().getCallconf().setEnddatetime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
    	                					aPartyUCD.getCallCdr().getCallconf().setStatus(userCallDetail.getDialStatus());
    	                					if(userCallDetail.getDialStatus() !=null)
    	                					{
    		                					if(userCallDetail.getDialStatus().equalsIgnoreCase("failure"))
    		                						aPartyUCD.getCallCdr().getCallconf().setReason(userCallDetail.getDialFailureReason());
    		                					else
    		                						aPartyUCD.getCallCdr().getCallconf().setReason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
    	                					}
    	                					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] userCallDetail.getDialStatus() = "+userCallDetail.getDialStatus()+ " , CallConfCDR = "+userCallDetail.getCallCdr().getCallconf());
    	                					//aPartyUCD.getCallCdr().getCallconf().setStatus(userCallDetail.getDialStatus());
    	                					//aPartyUCD.getCallCdr().getCallconf().setReason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
    	                					Event aPartyEvent = new Event();
    	                					if(userCallDetail.isCgCallDetail())
    	                					{
    	                						
    	                						aPartyEvent.setvId(userCallDetail.getaPartyVId());
    	                						aPartyEvent.setCallType(userCallDetail.getCallTypePatchedWithCg());
    	                						aPartyEvent.setaPartyMsisdn(userCallDetail.getaPartyMsisdn());
    	                						aPartyEvent.setbPartyMsisdn(userCallDetail.getbPartyMsisdn());
    	                						aPartyEvent.setEvent(event.getEvent());
    	                						aPartyEvent.setSubEvent(event.getSubEvent());
    	                						aPartyEvent.setCoreToTelephony(event.getCoreToTelephony());
    		                            		exit = new Exit();
    		                                    exit.execute(aPartyEvent,((Queue)msg.getJMSDestination()).getQueueName(),aPartyUCD);
    		                                    memCacheJSon.set(userCallDetail.getaPartyVId()+"_"+userCallDetail.getaPartyMsisdn(),7200, aPartyUCD);
    		                                    exit = null;   
    		                                    Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] AParty HangUp");
    		                                    aPartyUCD = null;
    	                					}
    	                					else
    	                					{
    		                					
    		                					aPartyEvent.setaPartyMsisdn(userCallDetail.getaPartyMsisdn());
    		                					aPartyEvent.setvId(userCallDetail.getaPartyVId());
    		                					aPartyEvent.setCallType(CoreEnums.callType.Incoming.ordinal());
    		                					aPartyEvent.setEvent(CoreEnums.events.DEFAULT.ordinal());
    		                					aPartyEvent.setSubEvent(CoreEnums.subEvents.BpartyHangupContinueWithAparty.ordinal());
    		                					aPartyEvent.setCoreToTelephony(event.getCoreToTelephony());
    		                					SendToTelephony.addMessageToQueue(((Queue)msg.getJMSDestination()).getQueueName(),Utility.convertObjectToJsonStr(aPartyEvent));
    		        	    	    	        Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Self-gen event for BpartyHangupContinueWithAparty added to queue");
    		        	    	    	        memCacheJSon.set(userCallDetail.getaPartyVId()+"_"+userCallDetail.getaPartyMsisdn(),7200, aPartyUCD);
    		        	    	    	        aPartyUCD = null;
    	                					}
                    					}
                    					exit = new Exit();
                    					exit.execute(event,((Queue)msg.getJMSDestination()).getQueueName(),userCallDetail);
                    					exit = null;
                    					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] BParty HangUp");
                    				}  
                    				else if(userCallDetail.isInPatchWithCG())
                    				{
                    					UnPatch unPatchWithCG = new UnPatch();
                                    	unPatchWithCG.execute(event, ((Queue)msg.getJMSDestination()).getQueueName(), userCallDetail);
                                    	unPatchWithCG = null;
                    					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] AParty hangUp the call. Call unPatched. Going to hangUp CG.");
                    					userCallDetail.setInPatchWithCG(false);
                    					userCallDetail.getCallCdr().getCallconf().setStartdatetime(userCallDetail.getPatchTime());
                    					UserCallDetail cgUCD = (UserCallDetail) memCacheJSon.get(userCallDetail.getCgVid()+"_"+userCallDetail.getCgMsisdn());
                    					if(cgUCD != null)
                    					{
    	                					userCallDetail.getCallCdr().getCallconf().setStatus(cgUCD.getDialStatus());
    	                					//userCallDetail.getCallCdr().setReleasereason(releasereason);
    	                					if(cgUCD.getDialStatus() != null)
    	                					{
    	                						if(cgUCD.getDialStatus().equalsIgnoreCase("failure"))
    	                    						userCallDetail.getCallCdr().getCallconf().setReason(cgUCD.getDialFailureReason());
    	                						else
    	                							userCallDetail.getCallCdr().setReleasereason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
    	                    					
    	                					}
    	                					
    	                					//	cgUCD.setCallEndReceived(true);
    	                					cgUCD.setInPatchWithCG(false);                  					
    	                            		Exit bPartyExit = new Exit();
    	                            		event.setvId(cgUCD.getbPartyVId());
    	                            		bPartyExit.execute(event,((Queue)msg.getJMSDestination()).getQueueName(),cgUCD);
    	                            		event.setvId(cgUCD.getaPartyVId());
    	                                    memCacheJSon.set(userCallDetail.getCgVid()+"_"+userCallDetail.getCgMsisdn(),7200, cgUCD);
    	                                    Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CG HangUp");
    	                                    cgUCD = null;
    	                                    bPartyExit =null;	                                    
                    					}
                    					
                    					exit = new Exit();
                    					exit.execute(event,((Queue)msg.getJMSDestination()).getQueueName(),userCallDetail);
                    					exit = null;
                    					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] AParty HangUp");
                                    }
                    				else
                    				{
    		                			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] In Pending state by telephony for OBD. Need to exit. msisdn = "+event.getaPartyMsisdn());
    		                    		userCallDetail.getCallCdr().setStatus("Success");
    		                    		userCallDetail.getCallCdr().setReason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
    		                    		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] 3.Setting CallEnd status.");
    		                    		//userCallDetail.setCallEndReceived(true);
    		                    		exit = new Exit();
    		                            exit.execute(event,((Queue)msg.getJMSDestination()).getQueueName(),userCallDetail);
    		                            Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] userCallDetail key in OBD : "+userCallDetail.getbPartyVId());
    		                            exit = null;
                    				}
    	        	            	memCacheJSon.set(userCallDetail.getbPartyVId()+"_"+userCallDetail.getbPartyMsisdn(),7200, userCallDetail);            	            
    	                            return;  
                				}
                				
                			
                			}
                			else
                    		{
                                Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Any other event for Pending_state(OBD Service) subEventCause = "+event.getSubEventCause()+" .stored current state = "+userCallDetail.getCurrentState());
                    		} 
                		}
                		                   	
                	}
                	else if(event.getSubEvent()== CoreEnums.subEvents.S_CallStandby.ordinal())
                	{
                		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] In StandBy state. Free core resources.");
                		if(userCallDetail == null)
                		{
                			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] UCD doesnt exist in memory OR Already deleted.");
                			return;
                		}
                		if(userCallDetail.getCallCdr().getEnddatetime() == null || userCallDetail.getCallCdr().getEnddatetime().equals(""))
                		{
                			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] EndTime is null.");
                			if(userCallDetail.getCallEndTime() == null)
                				userCallDetail.getCallCdr().setEnddatetime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
                			else
                				userCallDetail.getCallCdr().setEnddatetime(userCallDetail.getCallEndTime());
                		}
                		if((userCallDetail.getCallCdr().getStatus() == null || userCallDetail.getCallCdr().getStatus().equals(""))
                				&& (userCallDetail.getCallCdr().getReleasereason() == null || userCallDetail.getCallCdr().getReleasereason().equals("")))
                		{
                			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Releasereason is null during standBy.");
                			userCallDetail.getCallCdr().setReleasereason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
                		}
                		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] bPartyCallDetail() = "+userCallDetail.isbPartyCallDetail()+" , aPartyCallDetail = "+userCallDetail.isaPartyCallDetail()+" , CgCallDetail "+userCallDetail.isCgCallDetail());
                		if(event.getCallType() == CoreEnums.callType.Outgoing.ordinal())
                		{
                			/*if((!userCallDetail.isbPartyCallDetail()) && (!userCallDetail.isCgCallDetail()))
                			{
                				userCallDetail.getCallCdr().finish(); 
                				memCacheJSon.delete(event.getvId()+"_"+event.getbPartyMsisdn());
                				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CDR created successfully. UCD deleted from memCache.");
                            }
                			else          
                			{
                				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Standby received for MV BParty or CG");
                				memCacheJSon.set(userCallDetail.getbPartyVId()+"_"+userCallDetail.getbPartyMsisdn(), 1800 ,userCallDetail);
                				
                			}*/
                			
                			if(userCallDetail.isbPartyCallDetail() || userCallDetail.isCgCallDetail() )
            				{
	            				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Standby received for MV BParty or CG");
	            				memCacheJSon.set(userCallDetail.getbPartyVId()+"_"+userCallDetail.getbPartyMsisdn(), 1800 ,userCallDetail);
            				}
            				else
            				{
            					userCallDetail.getCallCdr().finish(); 
                				memCacheJSon.delete(event.getvId()+"_"+event.getbPartyMsisdn());
                				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CDR created successfully. UCD deleted from memCache.");
                         	}
                		} 
                		else
                		{
                			userCallDetail.getCallCdr().finish();
                			memCacheJSon.delete(event.getvId()+"_"+event.getaPartyMsisdn());
                			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CDR created successfully. UCD deleted from memCache.");
                        }
                		userCallDetail = null;
                    	return;
                	}
                	else
                	{
                		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Any other event = "+event.getEvent()+", subEvent = "+event.getSubEvent()+" .stored current state = "+userCallDetail.getCurrentState());
                	}
                } 
                else
                {            	
                	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Any other event = "+event.getEvent()+", subEvent = "+event.getSubEvent()+" .stored current state = "+userCallDetail.getCurrentState());
                }
            }
            
            while(true)
            {
            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Going to find current node. key = "+userCallDetail.getKey()+" , playComplete = "+userCallDetail.isNpPlayComplete());
            	currentnode = core.getCurrentNode(userCallDetail.getCurrentState(),userCallDetail.getKey());
            	
                if(currentnode.size() >= 1)
                {
                	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Got current node. = "+currentnode);
                	Execute exeObj =(Execute)currentnode.get(0);
                	
                	searchstring = exeObj.execute(event, ((Queue)msg.getJMSDestination()).getQueueName(), userCallDetail);
                }
	            
                Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] nextStateFlag = "+userCallDetail.isNextStateFlag()+" , execute next state = "+userCallDetail.isExecuteCurrentState()+" , playComplete = "+userCallDetail.isNpPlayComplete());
	            
                if(userCallDetail.isNextStateFlag())
                {
                	String[] otherkeys = {"Any"};
                	
                	nextState = core.getNextNodeId(userCallDetail.getCurrentState(), searchstring, userCallDetail.getKey());
                	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] NextState found = "+nextState+", searcheString = "+searchstring+",currentState = "+userCallDetail.getCurrentState());
                	if(nextState == 0)
                	{
                		for(int i = 0;i<otherkeys.length;i++)
                		{
                			nextState = core.getNextNodeId(userCallDetail.getCurrentState(), otherkeys[i],userCallDetail.getKey());
                			if(nextState != 0)
                				break;
                			else
                			{
                				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] NextState found = "+nextState+", currentState = "+userCallDetail.getCurrentState());
                            	nextState = userCallDetail.getCurrentState(); 
                        		userCallDetail.setExecuteCurrentState(false);  
                			}
                				
                		}
                		   
                		
                	}
                	else
                	{
                		userCallDetail.setNumberOfFileInPlay(0);
            			userCallDetail.setNumberOfFilesPlayed(0);
                	}
                }
	            else     
	            	nextState = userCallDetail.getCurrentState();  
                
                userCallDetail.setCurrentState(nextState);
                Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] nextState = "+nextState);
                
                if(userCallDetail.isCopyCallDetail())
	            {
                	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Making copy of usercallDetail for bParty.");
	            	UserCallDetail userCallDetail2 = new UserCallDetail();
	            	CallCdr callCdr = new CallCdr();
	            	userCallDetail2.setCallCdr(callCdr);
	            	userCallDetail2.setService(userCallDetail.getService());
	            	userCallDetail2.setaPartyMsisdn(userCallDetail.getaPartyMsisdn());
	            	userCallDetail2.setaPartyVId(userCallDetail.getaPartyVId());
	            	userCallDetail2.setbPartyMsisdn(userCallDetail.getbPartyMsisdn());
	            	userCallDetail2.setbPartyVId(userCallDetail.getbPartyVId());
	            	userCallDetail2.setbPartyCallDetail(true);
	            	userCallDetail2.setCurrentState(userCallDetail.getCurrentState());
	            	userCallDetail2.setKey(userCallDetail.getKey());
	            	userCallDetail2.setCallCdr(userCallDetail.getCallCdr());
	            	userCallDetail2.setShortCode(userCallDetail.getShortCode());
	            	userCallDetail2.setDialTime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
	            	userCallDetail2.setVoiceEffectFrequency(userCallDetail.getVoiceEffectFrequency());
	            	userCallDetail2.setBackgroundFile(userCallDetail.getBackgroundFile());	       
	            	userCallDetail2.setLanguage(userCallDetail.getLanguage());
	            	userCallDetail2.setDialStatusbparty(userCallDetail.isDialStatusbparty());
	        /*gp*/	userCallDetail2.setRecordFileName(userCallDetail.getRecordFileName());
	            	//System.out.println("UCD2 aParty = "+userCallDetail2.isaPartyCallDetail()+", ucd bParty = "+userCallDetail.isbPartyCallDetail());
	            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Key for bParty UCD = "+userCallDetail.getbPartyVId()+"_"+userCallDetail.getbPartyMsisdn());
	            	memCacheJSon.set(userCallDetail.getbPartyVId()+"_"+userCallDetail.getbPartyMsisdn(), 7200 ,userCallDetail2);
	            	userCallDetail.setCopyCallDetail(false);
	            	userCallDetail.setUcd(userCallDetail2);
	            	userCallDetail2 = null;
	            	callCdr = null;
	            }
	            /*if(userCallDetail.isDeleteCopyCallDetail())
	            {
	            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Deleting copy of usercallDetail for bParty.");
	            	memCacheJSon.delete(""+userCallDetail.getbPartyVId());
	            }*/
                
                if(userCallDetail.isCreateCgUCD())
                {
                	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Making copy of usercallDetail for CG-dial.");
                	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] APartyMsisdn = "+userCallDetail.getaPartyMsisdn()+" , BPartyMsisdn = "+userCallDetail.getbPartyMsisdn());
                	UserCallDetail userCallDetail2 = new UserCallDetail();
	            	userCallDetail2.setService(userCallDetail.getService());
	            	userCallDetail2.setaPartyMsisdn(userCallDetail.getaPartyMsisdn());
            		userCallDetail2.setaPartyVId(userCallDetail.getaPartyVId());
	            	
	            	if(event.getCallType() == CoreEnums.callType.Outgoing.ordinal())
	            	{
	            		userCallDetail2.setaPartyMsisdn(userCallDetail.getbPartyMsisdn());
	            	}
	            	userCallDetail2.setbPartyMsisdn(userCallDetail.getCgMsisdn());
	            	userCallDetail2.setbPartyVId(userCallDetail.getCgVid());
	            	userCallDetail2.setCgCallDetail(true);
	            	userCallDetail2.setCgVid(userCallDetail.getCgVid());
	            	userCallDetail2.setCurrentState(userCallDetail.getCurrentState());
	            	userCallDetail2.setKey(userCallDetail.getKey());
	            	userCallDetail2.setCallCdr(userCallDetail.getCallCdr());
	            	userCallDetail2.setShortCode(userCallDetail.getShortCode());
	            	userCallDetail2.setDialTime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
	            	userCallDetail2.setLanguage(userCallDetail.getLanguage());
	            	userCallDetail2.setCallTypePatchedWithCg(userCallDetail.getCallTypePatchedWithCg());
	            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Key for bParty UCD = "+userCallDetail.getbPartyVId()+"_"+userCallDetail.getbPartyMsisdn());
	            	memCacheJSon.set(userCallDetail.getCgVid()+"_"+userCallDetail.getCgMsisdn(), 7200 ,userCallDetail2);
	            	userCallDetail.setCgCallDetail(false);
	            	userCallDetail.setUcd(userCallDetail2);
	            	userCallDetail.setCreateCgUCD(false);
	            	userCallDetail2 = null;
                }
	            if(userCallDetail.isDropAParty())
	            {
	            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Going to set dial failure status in AParty CDR.");
	            	UserCallDetail aPartyUcd = (UserCallDetail) memCacheJSon.get(userCallDetail.getaPartyVId()+"_"+userCallDetail.getaPartyMsisdn());
	            	
	            	Logger.sysLog(LogValues.info, this.getClass().getName(), "Dial status for bParty = "+userCallDetail.getDialStatus()+" , Reason = "+userCallDetail.getDialFailureReason());
	            	aPartyUcd.getCallCdr().getCallconf().setStatus(userCallDetail.getDialStatus());
	            	aPartyUcd.getCallCdr().getCallconf().setReason(userCallDetail.getDialFailureReason());
	            	memCacheJSon.set(userCallDetail.getaPartyVId()+"_"+userCallDetail.getaPartyMsisdn(),7200, aPartyUcd);
	            	aPartyUcd = null;
	            }
                
	            if(event.getCallType() == CoreEnums.callType.Outgoing.ordinal())
	            {
	            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] userCallDetail key in OBD : "+userCallDetail.getbPartyVId()+"_"+event.getbPartyMsisdn());
	            	memCacheJSon.set(userCallDetail.getbPartyVId()+"_"+event.getbPartyMsisdn(), 7200 ,userCallDetail);
	            }
	            else
	            {
	            	memCacheJSon.set(event.getvId()+"_"+event.getaPartyMsisdn(),7200, userCallDetail);
	            }
	            
	            Logger.sysLog(LogValues.info, Engine.class.getName(), "--------event processed---------next State ="+userCallDetail.getCurrentState()+" , userCallDetail.isExecuteCurrentState() = "+userCallDetail.isExecuteCurrentState());
	            
	            if(userCallDetail.isExecuteCurrentState())
	            {
	            	Logger.sysLog(LogValues.info, Engine.class.getName(), "Execute current state.");
	            	event.setEvent(CoreEnums.events.DEFAULT.ordinal());
	            	event.setSubEvent(CoreEnums.subEvents.DEFAULT.ordinal());
	            	userCallDetail.setExecuteCurrentState(false);
	            }
	            else
	            	break;
            }
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.error, this.getClass().getName(),coreException.GetStack(e));
        }
        finally
        {
            event = null;
            userCallDetail = null;
        }
    }
}

    

