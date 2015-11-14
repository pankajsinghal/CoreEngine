package com.bng.core.bean;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;

import com.bng.core.bo.CircleBo;
import com.bng.core.bo.OBDNumberBo;
import com.bng.core.coreCdrBean.CallconfCdr;
import com.bng.core.coreCdrBean.MediaCdr;
import com.bng.core.entity.CircleMapper;
import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Event;
import com.bng.core.jsonBean.MakeCall;
import com.bng.core.jsonBean.Play;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.queue.Timer;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.DBConnection;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.SimpleParser;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

import java.util.Date;


public class Dial implements Execute{
	private String cli;
	private String dialOut;
	private int dialouttime;
	private String resourceUrl;
	private boolean cutonringing;
	private static CircleBo circleBoImpl;
	private int cellId;
	//private static String playFile = "D:\\BNG\\bg.wav";
	private String dialbackground;
	private String cgDialOutMsisdn;
	private String cgCli;	
	private static String APartyPrefix;
	private static String APartySuffix ;
	private static OBDNumberBo obdnumberbo;
	private static String gapminutes;
	
	public int getDialouttime() {
		return dialouttime;
	}

	public void setDialouttime(int dialouttime) {
		this.dialouttime = dialouttime;
	}
	
	public void setCircleBoImpl(CircleBo circleBoImpl) {
		Dial.circleBoImpl = circleBoImpl;
	}
	
	public void setObdnumberbo(OBDNumberBo obdnumberbo) {
		Dial.obdnumberbo = obdnumberbo;
	}

	public void setGapminutes(String gapminutes) {
		Dial.gapminutes = gapminutes;
	}

	public String getCli() {
		return cli;
	}

	public void setCli(String cli) {
		this.cli = cli;
	}

	public String getDialOut() {
		return dialOut;
	}

	public void setDialOut(String dialOut) {
		this.dialOut = dialOut;
	}
	
	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	public boolean isCutonringing() {
		return cutonringing;
	}

	public void setCutonringing(boolean cutonringing) {
		this.cutonringing = cutonringing;
	}

	public int getCellId() {
		return cellId;
	}

	public void setCellId(int cellId) {
		this.cellId = cellId;
	}

	public String getCgDialOutMsisdn() {
		return cgDialOutMsisdn;
	}

	public void setCgDialOutMsisdn(String cgDialOutMsisdn) {
		this.cgDialOutMsisdn = cgDialOutMsisdn;
	}

	public String getCgCli() {
		return cgCli;
	}

	public void setCgCli(String cgCli) {
		this.cgCli = cgCli;
	}
	
	public String getDialbackground() {
		return dialbackground;
	}

	public void setDialbackground(String dialbackground) {
		this.dialbackground = dialbackground;
	}
	
	public void setAPartyPrefix(String aPartyPrefix) {
		APartyPrefix = aPartyPrefix;
	}

	public void setAPartySuffix(String aPartySuffix) {
		APartySuffix = aPartySuffix;
	}

	
	
	public String execute(Event event,String qName,UserCallDetail userCallDetail)
    {
		MakeCall makeCall = null;
		String urlResp = null;
		String searchstring ="";
		int dialoutTimerinterval;
		SimpleParser sp = null;
		String cgCliSent = null;
		
		try{
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Dial : event.getEvent() = "+event.getEvent()+" , event.getSubEvent()= "+event.getSubEvent());
			if(event.getEvent() == CoreEnums.events.DEFAULT.ordinal() 
					&& event.getSubEvent() == CoreEnums.subEvents.BPartyCallFailed.ordinal())
			{
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Self-gen event received for BParty Call failed. Continue with AParty");
				searchstring = "Failed";
				
				userCallDetail.setNextStateFlag(true);
				userCallDetail.setExecuteCurrentState(true);
				userCallDetail.setDialStatusbparty(false);
				return searchstring;
			}
			else if(event.getEvent() == CoreEnums.events.E_DTMF.ordinal() && userCallDetail.isDialSent())
			{
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] DTMF received from aParty during Dial. Discard the event.");
				userCallDetail.setNextStateFlag(false);
				return searchstring;
			}
			else if((event.getEvent()== CoreEnums.events.E_PlayEnd.ordinal()) && 
	        		(event.getSubEvent()== CoreEnums.playEndSubEvent.S_PlayStopByApp.ordinal()))
			{
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Forcefully Play-end event received. Discard the event");
				userCallDetail.setNextStateFlag(false);
				return "";
			}
			else if(event.getEvent() == CoreEnums.events.DEFAULT.ordinal() && event.getSubEvent() == CoreEnums.subEvents.AlreadyPicked.ordinal())
            {
            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Self Gen event received for aparty pick tha call");
            	//userCallDetail.setAlreadypicked(true);
            	userCallDetail.setNextStateFlag(true);
            	userCallDetail.setExecuteCurrentState(true);
            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] already picked ="+userCallDetail.isAlreadypicked());
            	
            	return "Success";
            }
			else if(event.getEvent()== CoreEnums.events.E_MakeCall.ordinal() && 
					event.getSubEvent()== CoreEnums.makeCallSubEvent.S_OutGoingRinging.ordinal())
	    	{
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Ringing event received for OBD service. aPartyVid = "+userCallDetail.getaPartyVId()+" , bPartyVid = "+userCallDetail.getbPartyVId());
	    		if(cutonringing)
	    		{
	    			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Hangup on ringing is true. Cut the call");
	    			Exit exit = new Exit();
	    			exit.execute(event, qName, userCallDetail);
	    			exit = null;
	    		}
	    		else
	    		{
	    			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Discard Event, No action required.");
	    			userCallDetail.setNextStateFlag(false);
	    			
	    			if(dialouttime > 0)
	    			{
		    			event.setEvent(CoreEnums.events.DEFAULT.ordinal());
		        		event.setSubEvent(CoreEnums.subEvents.DialoutTimerExp.ordinal());   
		        		String timerId = Timer.startTimer(event, dialouttime, qName);
		        		userCallDetail.setDialoutTimer(true);
		        		userCallDetail.setDialoutTimerId(timerId);	        		
		        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] dialouttime started. dialouttimer interval: "+dialouttime);
		        		timerId = null;
	    			}
	    			return null;
	    		}
	    		
	    	}
			else if(event.getEvent() == CoreEnums.events.E_ChState.ordinal() && 
					event.getSubEvent() == CoreEnums.subEvents.S_CallPending.ordinal())
			{	
				if(!userCallDetail.isCallEndReceived())	
				{
					if(userCallDetail.isDialoutTimer())		//made false at tym of exit		//@gp
						dialoutTimerinterval = Timer.stopTimer(Long.parseLong(userCallDetail.getDialoutTimerId()));
					//hangUp
					userCallDetail.getCallCdr().setStatus("Failure");
					userCallDetail.getCallCdr().setReason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Setting CallEnd status.");
					                   
					
					searchstring = "Failed";
					
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]userCallDetail.getObdjobtype()="+userCallDetail.getObdjobtype());
					if(userCallDetail.getObdjobtype() != null  && !(userCallDetail.getObdjobtype().equalsIgnoreCase("")))
					{
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Inside recorddedication type service .");
						if(userCallDetail.getObdjobtype().equalsIgnoreCase("recorddedication"))
						{
							String status ="scheduled";
							boolean rescheduled = false;
							Date currenttime = new java.util.Date();
							//ArrayList<String> arrayList = new ArrayList<String>();
							//int i=0;
							ResultSet resultset = obdnumberbo.getAandBpartylist(userCallDetail.getaPartyMsisdn(),userCallDetail.getbPartyMsisdn(),userCallDetail.getObdTable());
							
							try{
								if(resultset != null)
								{
									while(resultset.next())
									{
										Logger.sysLog(LogValues.error, this.getClass().getName(),"for resultset element : cli ="+resultset.getString("cli")+" ,msisdn ="+resultset.getString("msisdn")+", endtime ="+resultset.getString("endtime"));
										String sDate = resultset.getString("endtime");
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
										Date dDate = sdf.parse(sDate);
										if(currenttime.after(dDate))
										{
											Logger.sysLog(LogValues.error, this.getClass().getName(),"currenttime ="+currenttime+" , dDate from resultset ="+dDate);
											obdnumberbo.deleterecord(resultset.getString("message"), userCallDetail.getObdTable());
										}
									}
									
									resultset.beforeFirst();
								}
							}
							catch(Exception e)
							{
								Logger.sysLog(LogValues.error, this.getClass().getName(), "Caught exception while getting and deleting records whose time is up");
								Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
							}
					
							try{
								if(resultset.last())
								{
									 //calltimenew =new java.util.Date();
									Logger.sysLog(LogValues.info, this.getClass().getName(),"for resultset element : cli ="+resultset.getString("cli")+" ,msisdn ="+resultset.getString("msisdn")+", endtime ="+resultset.getString("endtime"));
									Logger.sysLog(LogValues.info, this.getClass().getName()," found last row ="+resultset.last()+" with calltime ="+resultset.getString("calltime"));
									String sDate = resultset.getString("calltime");
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									Date dDate = sdf.parse(sDate);
									long time3 = dDate.getTime() + 1000*60*(Integer.parseInt(gapminutes));
									Date calltimenew = new java.util.Date(time3);
									//long time5 = calltimenew.getTime();
								//	calltimenew = new java.util.Date(time5);
								//	date = sdf.format(format.parse("yourdate");
									//calltimenew = sdf.parse(calltimenew)
									Logger.sysLog(LogValues.info, this.getClass().getName(),"NEW CALL TIME BEING INSERTED calltimenew= "+calltimenew);
									rescheduled = obdnumberbo.reschedule(userCallDetail.getaPartyMsisdn(), userCallDetail.getbPartyMsisdn(), dDate, userCallDetail.getObdTable());
									
								}
								else
								{
								//	long time3 = currenttime.getTime() ;
									//currenttime = new java.util.Date(time3);
									Logger.sysLog(LogValues.info, this.getClass().getName(),"resultset.last() ="+resultset.last() );
									Logger.sysLog(LogValues.info, this.getClass().getName(),"NEW CALL TIME BEING INSERTED currenttime= "+currenttime);
									rescheduled = obdnumberbo.reschedule(userCallDetail.getaPartyMsisdn(), userCallDetail.getbPartyMsisdn(), currenttime, userCallDetail.getObdTable());
								}
							}
							catch(Exception e)
							{
								Logger.sysLog(LogValues.error, this.getClass().getName(), "Caught exception while resheduling records whose call failed");
								Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
							}
							
							
							//Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Going to update obd call status in table : "+userCallDetail.getObdTable()+" , status : "+searchstring);
							//DBConnection.insertObdStatus("update "+userCallDetail.getObdTable()+" set status = \""+searchstring+"\", reason = \""+CoreEnums.subEventCause.values[event.getSubEventCause()].toString()+"\" where msisdn = \""+event.getbPartyMsisdn()+"\"");
							userCallDetail.setNextStateFlag(true);
							userCallDetail.setExecuteCurrentState(true);
						
						}
					}
					else if(userCallDetail.getObdTable() != null)
					{				
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Going to update obd call status in table : "+userCallDetail.getObdTable()+" , status : "+searchstring);
						DBConnection.insertObdStatus("update "+userCallDetail.getObdTable()+" set status = \""+searchstring+"\", reason = \""+CoreEnums.subEventCause.values[event.getSubEventCause()].toString()+"\" where msisdn = \""+event.getbPartyMsisdn()+"\"");
						userCallDetail.setNextStateFlag(true);
						userCallDetail.setExecuteCurrentState(true);
					}
					else
					{	
						userCallDetail.setCallEndReceived(true);
						Exit exit = new Exit();
			            exit.execute(event,qName,userCallDetail);
			            exit = null;
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] 1.Storing dial status in UCD.");
						userCallDetail.setDialStatus("Failure");		//searchstring was there which was failed
						userCallDetail.setDialFailureReason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
						if(userCallDetail.getaPartyVId() >0 && userCallDetail.getaPartyMsisdn() != null)
							userCallDetail.setDropAParty(true);
						
						Event aPartyEvent = new Event();
						aPartyEvent.setaPartyMsisdn(userCallDetail.getaPartyMsisdn());
						aPartyEvent.setvId(userCallDetail.getaPartyVId());
						aPartyEvent.setCallType(CoreEnums.callType.Incoming.ordinal());
						aPartyEvent.setEvent(CoreEnums.events.DEFAULT.ordinal());
						aPartyEvent.setSubEvent(CoreEnums.subEvents.BPartyCallFailed.ordinal());
						aPartyEvent.setSubEventCause(event.getSubEventCause());
						aPartyEvent.setCoreToTelephony(event.getCoreToTelephony());
						aPartyEvent.setIp(event.getIp());
						aPartyEvent.setHardware(event.getHardware());
						SendToTelephony.addMessageToQueue(qName,Utility.convertObjectToJsonStr(aPartyEvent));
				        Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"] Self-gen event for bParty Call failed added to queue = "+qName); 
				        userCallDetail.setNextStateFlag(false);
					}
				}
				else
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"] Call-end is already received. Do-nothing.");
			}
			else if(event.getEvent()== CoreEnums.events.E_MakeCall.ordinal() && 
					event.getSubEvent()== CoreEnums.makeCallSubEvent.S_OutGoingFailed.ordinal())
			{
				if(!userCallDetail.isCallEndReceived())
				{
					if(userCallDetail.isDialoutTimer())
						dialoutTimerinterval = Timer.stopTimer(Long.parseLong(userCallDetail.getDialoutTimerId()));
					
					searchstring = "Failed";
					userCallDetail.getCallCdr().setStatus("Failure");
					userCallDetail.getCallCdr().setReason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] MakeCall failed event received for dialOut. Move to next state. searchstring  = "+searchstring);
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]userCallDetail.getObdjobtype()="+userCallDetail.getObdjobtype());
					if(userCallDetail.getObdjobtype() != null  && !(userCallDetail.getObdjobtype().equalsIgnoreCase("")))
					{
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Inside recorddedication type service .");
						if(userCallDetail.getObdjobtype().equalsIgnoreCase("recorddedication"))
						{
							String status ="scheduled";
							boolean rescheduled = false;
							Date currenttime = new java.util.Date();
							//ArrayList<String> arrayList = new ArrayList<String>();
							//int i=0;
							ResultSet resultset = obdnumberbo.getAandBpartylist(userCallDetail.getaPartyMsisdn(),userCallDetail.getbPartyMsisdn(),userCallDetail.getObdTable());
							
							try{
								if(resultset != null)
								{
									while(resultset.next())
									{
										Logger.sysLog(LogValues.error, this.getClass().getName(),"for resultset element : cli ="+resultset.getString("cli")+" ,msisdn ="+resultset.getString("msisdn")+", endtime ="+resultset.getString("endtime"));
										String sDate = resultset.getString("endtime");
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
										Date dDate = sdf.parse(sDate);
										if(currenttime.after(dDate))
										{
											Logger.sysLog(LogValues.error, this.getClass().getName(),"currenttime ="+currenttime+" , dDate from resultset ="+dDate);
											obdnumberbo.deleterecord(resultset.getString("message"), userCallDetail.getObdTable());
										}
									}
									
									resultset.beforeFirst();
								}
							}
							catch(Exception e)
							{
								Logger.sysLog(LogValues.error, this.getClass().getName(), "Caught exception while getting and deleting records whose time is up");
								Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
							}
					
							try{
								if(resultset.last())
								{
									 //calltimenew =new java.util.Date();
									Logger.sysLog(LogValues.info, this.getClass().getName(),"for resultset element : cli ="+resultset.getString("cli")+" ,msisdn ="+resultset.getString("msisdn")+", endtime ="+resultset.getString("endtime"));
									Logger.sysLog(LogValues.info, this.getClass().getName()," found last row ="+resultset.last()+" with calltime ="+resultset.getString("calltime"));
									String sDate = resultset.getString("calltime");
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									Date dDate = sdf.parse(sDate);
									long time3 = dDate.getTime() + 1000*60*(Integer.parseInt(gapminutes));
									Date calltimenew = new java.util.Date(time3);
									//long time5 = calltimenew.getTime();
								//	calltimenew = new java.util.Date(time5);
								//	date = sdf.format(format.parse("yourdate");
									//calltimenew = sdf.parse(calltimenew)
									Logger.sysLog(LogValues.info, this.getClass().getName(),"NEW CALL TIME BEING INSERTED calltimenew= "+calltimenew);
									rescheduled = obdnumberbo.reschedule(userCallDetail.getaPartyMsisdn(), userCallDetail.getbPartyMsisdn(), dDate, userCallDetail.getObdTable());
									
								}
								else
								{
								//	long time3 = currenttime.getTime() ;
									//currenttime = new java.util.Date(time3);
									Logger.sysLog(LogValues.info, this.getClass().getName(),"resultset.last() ="+resultset.last() );
									Logger.sysLog(LogValues.info, this.getClass().getName(),"NEW CALL TIME BEING INSERTED currenttime= "+currenttime);
									rescheduled = obdnumberbo.reschedule(userCallDetail.getaPartyMsisdn(), userCallDetail.getbPartyMsisdn(), currenttime, userCallDetail.getObdTable());
								}
							}
							catch(Exception e)
							{
								Logger.sysLog(LogValues.error, this.getClass().getName(), "Caught exception while resheduling records whose call failed");
								Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
							}
							
							
							//Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Going to update obd call status in table : "+userCallDetail.getObdTable()+" , status : "+searchstring);
							//DBConnection.insertObdStatus("update "+userCallDetail.getObdTable()+" set status = \""+searchstring+"\", reason = \""+CoreEnums.subEventCause.values[event.getSubEventCause()].toString()+"\" where msisdn = \""+event.getbPartyMsisdn()+"\"");
							userCallDetail.setNextStateFlag(true);
							userCallDetail.setExecuteCurrentState(true);
						
						}
					}
					else if(userCallDetail.getObdTable() != null)
					{
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Going to update obd call status in table : "+userCallDetail.getObdTable()+" , status : OutGoingFailed");
						DBConnection.insertObdStatus("update "+userCallDetail.getObdTable()+" set status = \"Failed\", reason = \"OutGoingFailedByTelephony\" where msisdn = \""+event.getbPartyMsisdn()+"\"");
						//DBConnection.insertObdStatus("update "+userCallDetail.getObdTable()+" set status = \"OutGoingFailed\", reason = \""+CoreEnums.subEventCause.values[event.getSubEventCause()].toString()+"\" where msisdn = \""+event.getbPartyMsisdn()+"\"");
						userCallDetail.setNextStateFlag(true);
						userCallDetail.setExecuteCurrentState(true);
					}
					else
					{
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] 2.Storing dial status in UCD.");
						userCallDetail.setDialStatus("Failure");
						userCallDetail.setDialFailureReason(CoreEnums.subEventCause.values[event.getSubEventCause()].toString());
						if(userCallDetail.getaPartyVId() >0 && userCallDetail.getaPartyMsisdn() != null)
							userCallDetail.setDropAParty(true);
						Event aPartyEvent = new Event();
						aPartyEvent.setCallType(CoreEnums.callType.Incoming.ordinal());
						aPartyEvent.setaPartyMsisdn(userCallDetail.getaPartyMsisdn());
						aPartyEvent.setvId(userCallDetail.getaPartyVId());
						aPartyEvent.setEvent(CoreEnums.events.DEFAULT.ordinal());
						aPartyEvent.setSubEvent(CoreEnums.subEvents.BPartyCallFailed.ordinal());
						aPartyEvent.setCoreToTelephony(event.getCoreToTelephony());
						aPartyEvent.setIp(event.getIp());
						aPartyEvent.setHardware(event.getHardware());
						SendToTelephony.addMessageToQueue(qName,Utility.convertObjectToJsonStr(aPartyEvent));
				        Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"] Self-gen event for bParty Call failed added to queue = "+qName); 
				        userCallDetail.setNextStateFlag(false);		        
					}
					//userCallDetail.setDeleteCopyCallDetail(true);
				}
				else
				{	
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CallEnd is already received. Do-Nothing.");
	        		userCallDetail.setNextStateFlag(false);	
	        		userCallDetail.setExecuteCurrentState(false);
	        		searchstring = "" ;
	        		return searchstring;
				}
			}
			else if(event.getEvent() == CoreEnums.events.E_ChState.ordinal() && 
					event.getSubEvent() == CoreEnums.subEvents.S_CallTalking.ordinal())
			{
				if(event.getCallType() ==CoreEnums.callType.Outgoing.ordinal())
				{
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CallCdr = "+userCallDetail.getCallCdr());
					if(userCallDetail.isDialoutTimer())
						dialoutTimerinterval = Timer.stopTimer(Long.parseLong(userCallDetail.getDialoutTimerId()));
					
					searchstring = "Success";
					userCallDetail.setNextStateFlag(true);
					userCallDetail.setExecuteCurrentState(true);
					userCallDetail.getCallCdr().setStatus("Success");
					userCallDetail.getCallCdr().setPickupdatetime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Talking event received for dialOut. Move to next state. searchstring  = "+searchstring);
					
					if((userCallDetail.getObdjobtype() != null) && !(userCallDetail.getObdjobtype().equals("")))
					{
						if(!(userCallDetail.getObdjobtype().equalsIgnoreCase("recorddedication")))
						{
							if(userCallDetail.getObdTable() != null)
								DBConnection.insertObdStatus("update "+userCallDetail.getObdTable()+" set status = \"Success\" where msisdn = \""+event.getbPartyMsisdn()+"\"");
						}
					}
					else
					{
						if(userCallDetail.getObdTable() != null)
							DBConnection.insertObdStatus("update "+userCallDetail.getObdTable()+" set status = \"Success\" where msisdn = \""+event.getbPartyMsisdn()+"\"");
					
					}
					
							
					userCallDetail.setDialStatus("Success");
					userCallDetail.setDialStatusbparty(false);
					
					if(userCallDetail.isbPartyCallDetail())
					{
						Event apartyevent = new Event();
						apartyevent.setEvent(CoreEnums.events.DEFAULT.ordinal());
						apartyevent.setSubEvent(CoreEnums.subEvents.BpartyTalkingReceived.ordinal());
						apartyevent.setCallType(CoreEnums.callType.Incoming.ordinal());
						apartyevent.setvId(userCallDetail.getaPartyVId());
						apartyevent.setaPartyMsisdn(userCallDetail.getaPartyMsisdn());
						SendToTelephony.addMessageToQueue(qName,Utility.convertObjectToJsonStr(apartyevent));
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Self gen Event for B party picked call added to queue ");
						
					}
					
				}
				else
				{
					searchstring = "Success";
					userCallDetail.setNextStateFlag(true);
					userCallDetail.setExecuteCurrentState(true);
					return searchstring;
				}				
			}
			else if(event.getEvent()== CoreEnums.events.E_PlayEnd.ordinal() && 
	        		event.getSubEvent()== CoreEnums.playEndSubEvent.S_PlayComplete.ordinal() && 
	        		userCallDetail.isDialSent())
			{
				searchstring = "";
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"] Discard PlayEvent.searchstring  = "+searchstring);
				userCallDetail.setNextStateFlag(false);
			}
			else
			{
				if(!(resourceUrl.equals("")) && resourceUrl != null)
				{
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] resourceUrl : "+resourceUrl);
					resourceUrl = resourceUrl.replaceAll("#", "&").replace("$ip$", event.getIp().trim()).replace("$argument$", ""+event.getHardware());
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] After formating resourceUrl : "+resourceUrl);
					urlResp = Utility.callUrl(resourceUrl);
					Resources resources = Utility.convertJsonStrToObject(urlResp,Resources.class);
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Got VID for bParty/CG dial = "+resources.getvId());
					if(dialOut.equalsIgnoreCase("B party"))
					{
						userCallDetail.setaPartyVId(event.getvId());
						userCallDetail.setaPartyMsisdn(event.getaPartyMsisdn());
						userCallDetail.setbPartyVId(resources.getvId());
						if(userCallDetail.getbPartyMsisdn() == null)
							userCallDetail.setbPartyMsisdn(event.getbPartyMsisdn());	
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] B party no. before formatting = "+userCallDetail.getbPartyMsisdn());
						userCallDetail.setbPartyMsisdn(Utility.formatBPartyNumber(userCallDetail.getbPartyMsisdn()));
						userCallDetail.getCallCdr().setBparty(userCallDetail.getbPartyMsisdn());
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] B party no. after formatting = "+userCallDetail.getbPartyMsisdn());
						userCallDetail.setaPartyCallDetail(true);
						userCallDetail.setCopyCallDetail(true);
						
						CircleMapper circleMapper = circleBoImpl.getCircleInfo(userCallDetail.getbPartyMsisdn());
						CallconfCdr callconfCdr = new CallconfCdr();
						callconfCdr.setSystemip(event.getIp());
						callconfCdr.setHardware(CoreEnums.hardware.values[event.getHardware()].toString());
						callconfCdr.setProtocol(event.getProtocol());
						callconfCdr.setCalltype(CoreEnums.callType.Outgoing.toString());
						callconfCdr.setAparty(userCallDetail.getaPartyMsisdn());
						callconfCdr.setBparty(userCallDetail.getbPartyMsisdn());
						callconfCdr.setVid(""+userCallDetail.getbPartyVId());
						callconfCdr.setDialtime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
						if(circleMapper != null)
						{
							callconfCdr.setOperator(circleMapper.getOperator());
							callconfCdr.setCircle(circleMapper.getCircle());
						}
						userCallDetail.getCallCdr().addCallConfList(callconfCdr);
						//@gp// userCallDetail.getCallCdr().addCallConfMap(userCallDetail.getbPartyMsisdn()+"_"+userCallDetail.getbPartyVId(), callconfCdr);
						//@gp// Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"] CallconfCdrMap = "+userCallDetail.getCallCdr().getCallConfCdrMap(userCallDetail.getbPartyMsisdn()+"_"+userCallDetail.getbPartyVId()));
			    		//userCallDetail.setInPatchState(true);
						
						Play play = new Play(dialbackground,event.getvId(),event.getIp(),0,false,CoreEnums.playOperations.play.ordinal(),event.getaPartyMsisdn(),"0",event.getHardware(), qName, cellId+"_0");
		        		SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(play));
				        Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"] Telephony event Play added for AParty from Dial to queue = "+event.getCoreToTelephony()+", file = "+dialbackground); 
				        
						MediaCdr media = new MediaCdr();
			        	media.setType("PlayContent");
			        	media.setLocation("Local");
			        	media.setStarttime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
			        	media.setFilename(dialbackground);
			        	media.setCode(""+cellId);
			        	userCallDetail.getCallCdr().addMediaMap(cellId+"_0",media);
			        	media = null;
					}
					else if(dialOut.equalsIgnoreCase("consentgateway"))
					{
						if(cgDialOutMsisdn.contains("$"))
						{
							try{
								Properties prop = Utility.convertUCDToProp(userCallDetail);
								sp = new SimpleParser(cgDialOutMsisdn);
								String cgDialOut = sp.parse(prop);
								userCallDetail.setCgMsisdn(cgDialOut);
							}
							catch(Exception e)
							{
								Logger.sysLog(LogValues.error, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]"+coreException.GetStack(e));
							}
						}
						else
							userCallDetail.setCgMsisdn(cgDialOutMsisdn);
						
						if(cgCli.contains("$"))
						{
							try
							{
								Properties prop = Utility.convertUCDToProp(userCallDetail);
								sp = new SimpleParser(cgCli);
								cgCliSent = sp.parse(prop);
							}
							catch(Exception e)
							{
								Logger.sysLog(LogValues.error, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]"+coreException.GetStack(e));
							}
						}
						else
							cgCliSent = userCallDetail.getaPartyMsisdn();
						
								
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] DialOut is "+dialOut+", Going to make makeCall obj for Telephony for msisdn = "+userCallDetail.getaPartyMsisdn());
						
						if(event.getCallType() == CoreEnums.callType.Incoming.ordinal())
							userCallDetail.setaPartyMsisdn(event.getaPartyMsisdn());
						else	
							userCallDetail.setaPartyMsisdn(event.getbPartyMsisdn());
						
						userCallDetail.setaPartyVId(event.getvId());
						userCallDetail.setCgVid(resources.getvId());
						makeCall = new MakeCall(userCallDetail.getCgMsisdn(),cgCliSent,userCallDetail.getShortCode(), event.getHardware(),event.getIp(),userCallDetail.getCgVid(),qName);
						//SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(makeCall));
						//Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event makeCall for CG added to queue : "+event.getCoreToTelephony());
						//makeCall = null;
						userCallDetail.setDialSent(true);
						userCallDetail.setCreateCgUCD(true);
						userCallDetail.setNextStateFlag(false);		
						userCallDetail.setCallTypePatchedWithCg(event.getCallType());
						CallconfCdr callconfCdr = new CallconfCdr();
						callconfCdr.setSystemip(event.getIp());
						callconfCdr.setHardware(CoreEnums.hardware.values[event.getHardware()].toString());
						callconfCdr.setProtocol(event.getProtocol());
						callconfCdr.setCalltype(CoreEnums.callType.Outgoing.toString());
						callconfCdr.setAparty(userCallDetail.getaPartyMsisdn());
						
						callconfCdr.setBparty(userCallDetail.getCgMsisdn());
						callconfCdr.setVid(""+userCallDetail.getCgVid());
						callconfCdr.setDialtime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
						userCallDetail.getCallCdr().addCallConfList(callconfCdr);
						userCallDetail.setInPatchWithCG(true);	
						
						Play play = new Play(dialbackground,event.getvId(),event.getIp(),0,false,CoreEnums.playOperations.play.ordinal(),event.getaPartyMsisdn(),"0",event.getHardware(), qName, cellId+"_0");
		        		SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(play));
				        Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"] Telephony event Play added for AParty from Dial to queue = "+event.getCoreToTelephony()+", file = "+dialbackground); 
				        
						MediaCdr media = new MediaCdr();
			        	media.setType("PlayContent");
			        	media.setLocation("Local");
			        	media.setStarttime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
			        	media.setFilename(dialbackground);
			        	media.setCode(""+cellId);
			        	userCallDetail.getCallCdr().addMediaMap(cellId+"_0",media);
			        	media = null;
			    		resources = null;
					}
				}
				else
				{
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Doesnt have any resource url.");
					userCallDetail.setbPartyVId(event.getvId());
				}
				
				if(dialOut.equalsIgnoreCase("B party"))
				{					        
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] DialOut is "+dialOut+", Going to make makeCall obj for Telephony for msisdn = "+userCallDetail.getbPartyMsisdn());
				//	userCallDetail.setbPartyMsisdn(Utility.numbercorrected(userCallDetail.getbPartyMsisdn()));
					
					if(APartyPrefix == null)
						APartyPrefix ="";
					if(APartySuffix == null)
						APartySuffix = "";
					if(userCallDetail.getbPartyVId() != -1)
					{
						makeCall = new MakeCall(userCallDetail.getbPartyMsisdn(),APartyPrefix+event.getaPartyMsisdn()+APartySuffix,userCallDetail.getShortCode(), event.getHardware(),event.getIp(),userCallDetail.getbPartyVId(),qName);	    	
						userCallDetail.setDialSent(true);	
						userCallDetail.setDialStatusbparty(true);
					}
					else
					{
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]VID Received for Bparty = "+userCallDetail.getbPartyVId()+ "  ,NO MAKECALL SENT ,exit aparty");
						Exit exit = new Exit();
			            exit.execute(event,qName,userCallDetail);
			            exit = null;
			            userCallDetail.setCopyCallDetail(false);
			            userCallDetail.getCallCdr().getCallconf().setStatus("failure");
			            userCallDetail.getCallCdr().getCallconf().setReason("ChannelNotFree");
			            return "" ;
					}
					
				}			
				
				SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(makeCall));
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event makeCall added to queue : "+event.getCoreToTelephony());
				
				userCallDetail.setaPartyMsisdn(event.getaPartyMsisdn());			
				//userCallDetail.setbPartyMsisdn(event.getbPartyMsisdn());
				userCallDetail.setNextStateFlag(false);		
				makeCall = null;
			}
			return searchstring;

		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, Exit.class.getName(), coreException.GetStack(e));
			return "" ;
		}
	}
}
