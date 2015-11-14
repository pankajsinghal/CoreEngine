package com.bng.core.bean;

import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Call;
import com.bng.core.jsonBean.Event;
import com.bng.core.parser.Core;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

public class Answer implements Execute{

	public String execute(Event event,String qName,UserCallDetail userCallDetail)
	{
		boolean recordingFlag = false;
    	Call call;
    	try
    	{
    		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Inside Answer:  event = "+event.getEvent()+" : subEvent = "+event.getSubEvent());
    		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] call already picked ="+userCallDetail.isAlreadypicked());
    		
    		if((event.getEvent()== CoreEnums.events.E_ChState.ordinal()) && 
            		(event.getSubEvent()== CoreEnums.subEvents.S_CallTalking.ordinal()))
    		{
    			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Talking event received. Move to next state..");
    			userCallDetail.setNextStateFlag(true);
    			userCallDetail.setExecuteCurrentState(true);
    			userCallDetail.setAlreadypicked(true);
    		}
    		else
    		{
    			if(userCallDetail.getRecording() > 0)
        			recordingFlag = Utility.getPercentage(userCallDetail.getRecording());
        	
        		if(event.getCallType() == CoreEnums.callType.Incoming.ordinal())
    			{
    				if(!userCallDetail.isAlreadypicked())
    				{
    					call = new Call(CoreEnums.callOperations.pickup.ordinal(),event.getvId(),recordingFlag,event.getaPartyMsisdn(),"http://filePath",event.getHardware(), qName);
    					SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(call));
    					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event call is added to queue.");
    	    			userCallDetail.setNextStateFlag(false);
    	    			userCallDetail.setExecuteCurrentState(false);
    				}
    				else
    				{
    					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Move to next state already picked.");
    	    			userCallDetail.setNextStateFlag(true);
    	    			userCallDetail.setExecuteCurrentState(true);
    				}
    			}
        		else if(event.getCallType() == CoreEnums.callType.Outgoing.ordinal())
    			{
        			userCallDetail.setNextStateFlag(false);
	    			userCallDetail.setExecuteCurrentState(false);
        			
    				Event selfGenEvent = new Event();				
    				selfGenEvent.setvId(userCallDetail.getaPartyVId());
    				selfGenEvent.setaPartyMsisdn(userCallDetail.getaPartyMsisdn());
    				selfGenEvent.setCallType(CoreEnums.callType.Incoming.ordinal());
    				selfGenEvent.setEvent(CoreEnums.events.DEFAULT.ordinal());
    				selfGenEvent.setSubEvent(CoreEnums.subEvents.AlreadyPicked.ordinal());
    				selfGenEvent.setCoreToTelephony(event.getCoreToTelephony());
    				SendToTelephony.addMessageToQueue(qName,Utility.convertObjectToJsonStr(selfGenEvent));
    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Self-generated event for callalreadypicked added to queue : "+event.getCoreToTelephony());
    				selfGenEvent = null;
    			}
    			
    		}
    		
    	}
    	catch(Exception e)
    	{
    		Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
    	}
    	return "";
	}
}
