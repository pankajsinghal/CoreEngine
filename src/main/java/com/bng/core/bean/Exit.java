/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.bean;

import java.util.Date;

import com.bng.core.entity.Subscription;
import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Call;
import com.bng.core.jsonBean.Event;
import com.bng.core.jsonBean.MagicParrot;
import com.bng.core.jsonBean.Record;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.queue.Timer;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.DBConnection;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

/**
 *
 * @author richa
 */
public class Exit implements Execute{
	
	private  String releasereason; 
	
	public String getReleasereason() {
		return releasereason;
	}

	public void setReleasereason(String releasereason) {
		this.releasereason = releasereason;
	}
	
	public Exit(){
	}
	
	public Exit(String releasereason) {
		// TODO Auto-generated constructor stub
		this.releasereason =releasereason;
	}
	
	public String execute(Event event, String qName, UserCallDetail userCallDetail)
    {
    	Call call = null;
    	
    	
    	try
    	{
    		if(userCallDetail.isMagicParrotTimer())
        	{
    			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] MagicParrot Timer id = "+userCallDetail.getSampleCheckTimerId());
        		int mpTimerInterval = Timer.stopTimer(Long.parseLong(userCallDetail.getSampleCheckTimerId()));
        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] MagicParrot Timer interval before Call-end = "+mpTimerInterval);
        		MagicParrot magicParrot = new MagicParrot(0,CoreEnums.magicParrotOper.StopMP.ordinal(),event.getvId(),qName,event.getHardware());
    			SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(magicParrot));
        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event magicParrotStop added to queue = "+event.getCoreToTelephony());
        		magicParrot = null;
    			
        	}
    		
    		if(userCallDetail.isRecordTimerFlag())
			{
    			if(userCallDetail.getRecordTimer() != null)
    			{
    				int timerInterval = Timer.stopTimer(Long.parseLong(userCallDetail.getRecordTimer()));
            		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Record Event timer interval  = "+timerInterval+" , timer id = "+userCallDetail.getNpTimerId());
            		userCallDetail.setRecordTimerFlag(false);
            		userCallDetail.setRecordTimer(null);
            		Record record = new Record(userCallDetail.getRecordFileName(), event.getvId(), event.getIp(), CoreEnums.recordOperations.end.ordinal(), event.getaPartyMsisdn(), "-1", false, event.getHardware(), qName);
        			SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(record));
        			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Telephony event record end added to queue : "+event.getCoreToTelephony());
        			record = null;
    			}
				
    		}	
    		if(userCallDetail.isGenRandomDtmfTimer())
    		{
    			if(userCallDetail.getGenRandomDtmfTimerId() != null)
    			{
    				int timerInterval = Timer.stopTimer(Long.parseLong(userCallDetail.getGenRandomDtmfTimerId()));
    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]genreate stop Event timer interval ="+timerInterval);
            		userCallDetail.setGenRandomDtmfTimer(false);
            		userCallDetail.setGenRandomDtmfTimerId(null);
    			}
    		}
    		
	        if(userCallDetail.getStatus() != null && (userCallDetail.getStatus().equalsIgnoreCase("active")))
	        {
		        Subscription subscription = Utility.checkSubscription(Utility.numbercorrected(userCallDetail.getaPartyMsisdn()), userCallDetail.getShortCode(), userCallDetail.getService());
		        
		        Logger.sysLog(LogValues.info, this.getClass().getName(),"["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] subscription ="+subscription);
	        	
		        if(subscription != null)
		        {
		        	Logger.sysLog(LogValues.info, this.getClass().getName(),"["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CallAttempts="+subscription.getCallAttempts()+" ,language="+userCallDetail.getLanguage()+" ,serviceName="+userCallDetail.getService());
		        	if(subscription.getCallAttempts() == null)
		        		subscription.setCallAttempts(1);
		        	else
		        		subscription.setCallAttempts(subscription.getCallAttempts()+1);
		        /*	
		        	if(subscription.isFirstCaller())
		        		subscription.setFirstCaller(false);
		        	*/
		        	subscription.setLanguage(userCallDetail.getLanguage());
		        	if(userCallDetail.isFreeMinTimerFlag() || userCallDetail.isUpdatefreeMins())
		        	{
		        		int timerInterval =0;
		        		if(userCallDetail.isFreeMinTimerFlag())	
		        		{
		        			timerInterval = Timer.stopTimer(Long.parseLong(userCallDetail.getFreeMinTimerId())); 
		        			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Free Minutes Timer interval = "+timerInterval);
		        		}		        		
	        			if(userCallDetail.isUpdatefreeMins())
	        				subscription.setSubTimeLeft(userCallDetail.getRemainingFreeMinutes());
	        			else
	        				subscription.setSubTimeLeft(subscription.getSubTimeLeft() - timerInterval);		    	        		        		
		        	}
					boolean updateFlag = Utility.updateSub(subscription, event.getaPartyMsisdn());
    	        	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] FreeMins, CallAttempts and language is updated in DB.");
		        }
        	}
	        
	        if(userCallDetail.isNpTimerFlag())
        	{
	        	int timerInterval = Timer.stopTimer(Long.parseLong(userCallDetail.getNpTimerId())); 
        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] NP Timer interval before Call-end = "+timerInterval);
        	}
        	if(userCallDetail.isMasterTimerFlag())
        	{
        		int timerInterval = Timer.stopTimer(Long.parseLong(userCallDetail.getMasterTimerId())); 
        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Master Timer interval before Call-end = "+timerInterval);
        	}  
        	if(userCallDetail.isUpdateVisitorsDialCount())
        	{
        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Going to update visitors entry.");
				Utility.updateVisitorsDialCount(userCallDetail.getaPartyMsisdn(),userCallDetail.getVisitorsServiceName(),userCallDetail.getShortCode());
        		userCallDetail.setUpdateVisitorsDialCount(false);
        	}
        	if(userCallDetail.isDialoutTimer())
        	{
				int dialoutTimerinterval = Timer.stopTimer(Long.parseLong(userCallDetail.getDialoutTimerId()));
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Dialout timer interval = "+dialoutTimerinterval);
        	}
        	if(userCallDetail.isUserDefTimerFlag())
        	{
        		int timerInterval = Timer.stopTimer(Long.parseLong(userCallDetail.getUserDefTimerId())); 
        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] User-def Timer interval before Call-end = "+timerInterval);
        	}
        	userCallDetail.getCallCdr().setEnddatetime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
        	if(userCallDetail.getCallCdr().getReason() ==null)
        		userCallDetail.getCallCdr().setReason(releasereason);
        	/*if(userCallDetail.getCallCdr().getStatus() == null || userCallDetail.getCallCdr().getStatus().equals(""))
        	{
        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Going to update status in callCdr.");
        		userCallDetail.getCallCdr().setStatus("Success");
        		userCallDetail.getCallCdr().setReason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
        	}*/      	
        	call = new Call(CoreEnums.callOperations.hangup.ordinal(),event.getvId(),false,"","",event.getHardware(), qName);
	        SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(call));
	        Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event Call added to queue = "+event.getCoreToTelephony());
	        userCallDetail.setCallEndTime(new Date());
	        userCallDetail.setExecuteCurrentState(false);
        	userCallDetail.setNextStateFlag(false);
        	userCallDetail.setCallEndReceived(true);
        	return "";        
    	}
    	catch(Exception e)
    	{
    		Logger.sysLog(LogValues.error, Exit.class.getName(), coreException.GetStack(e));
    		return "";
    	}
    	finally
    	{
    		call = null;
    	}        
    }
}
