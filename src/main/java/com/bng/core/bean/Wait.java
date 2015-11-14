package com.bng.core.bean;

import com.bng.core.coreCdrBean.MediaCdr;
import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Event;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.queue.Timer;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

public class Wait implements Execute{

	public String execute(Event event, String qName, UserCallDetail userCallDetail)
	{
		String searchString = "";
		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Inside Wait execute.");
		
		if(event.getEvent()== CoreEnums.events.E_DTMF.ordinal())
		{
			if(userCallDetail.isInPatchWithCG())
			{
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CG will handle the dtmf. Discard the event.");
				userCallDetail.setNextStateFlag(false);
				return "";
			}
			
			if(userCallDetail.isaPartyCallDetail())
			{
				if(event.getSubEvent()== 10)
					searchString = "*";
	            else if(event.getSubEvent()== 11)
	            	searchString = "#";
	            else if(event.getSubEvent()== 12)
	            	searchString = "NoInput";
	            else
	            	searchString = ""+event.getSubEvent();         
				
				event.setEvent(CoreEnums.events.DEFAULT.ordinal());
				event.setSubEvent(CoreEnums.subEvents.DoNothing.ordinal());
				SendToTelephony.addMessageToQueue(qName,Utility.convertObjectToJsonStr(event));
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Self-generated event for Wait added to queue : "+event.getCoreToTelephony());
				userCallDetail.setNextStateFlag(true);
				userCallDetail.setExecuteCurrentState(true);
			}
			else
			{
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] DTMF received from bParty. Discard the event.");
			}
		}
		else if((event.getEvent()== CoreEnums.events.E_VXML.ordinal()) && 
        		(event.getSubEvent()== CoreEnums.subEvents.vxmlplay.ordinal()))
		{
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Event recevied for Play from vxml ");
			MediaCdr media = new MediaCdr("vxmlplaycontent", "vxml", event.getFilePath(), event.getPlayStartTime(), event.getPlayEndTime(), event.getTransId());
			userCallDetail.getCallCdr().addMediaList(media);
			media = null;
		}
		else if((event.getEvent()== CoreEnums.events.E_PlayEnd.ordinal()) && 
        		(event.getSubEvent()== CoreEnums.playEndSubEvent.S_PlayComplete.ordinal()))
		{
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] playend event received for id : "+event.getTransId());
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
        		}
        	}
        	catch(Exception e)
        	{
        		Logger.sysLog(LogValues.error, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Caught Exception while getting MediaCDR for transId = "+event.getTransId());
        		Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
        	}        	
		}
		else if((event.getEvent()== CoreEnums.events.E_PlayEnd.ordinal()) && 
        		(event.getSubEvent()== CoreEnums.playEndSubEvent.S_PlayStopByApp.ordinal()))
        {
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] ForceFully playEnd received. Discard event.");
			
        }
		else if((event.getEvent()== CoreEnums.events.DEFAULT.ordinal()) && 
				(event.getSubEvent()== CoreEnums.subEvents.BpartyHangupContinueWithAparty.ordinal()))
		{
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Self Gen Event Received at wait for bPartyHangup");
			userCallDetail.setNextStateFlag(true);
			userCallDetail.setExecuteCurrentState(true);
			userCallDetail.setDialStatusbparty(false);
			searchString = "";			
		}
		else if((event.getEvent()== CoreEnums.events.E_VXML.ordinal()) && 
				(event.getSubEvent()== CoreEnums.subEvents.vxmlcontrolover.ordinal()))
		{
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Event Received at wait for vxmlcontrol end");
			userCallDetail.setNextStateFlag(true);
			userCallDetail.setExecuteCurrentState(true);
			searchString = "";
		}
		else
		{
			userCallDetail.setNextStateFlag(false);
			userCallDetail.setExecuteCurrentState(false);
		}
		return searchString;
	}

}
