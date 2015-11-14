package com.bng.core.bean;

import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Event;
import com.bng.core.jsonBean.MagicParrot;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.queue.Timer;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

public class MagicParrotCheck implements Execute{
	private int sampleCheckTimer;
	private int voiceEffect;
	
	public int getSampleCheckTimer() {
		return sampleCheckTimer;
	}

	public void setSampleCheckTimer(int sampleCheckTimer) {
		this.sampleCheckTimer = sampleCheckTimer;
	}

	public int getVoiceEffect() {
		return voiceEffect;
	}

	public void setVoiceEffect(int voiceEffect) {
		this.voiceEffect = voiceEffect;
	}

	public String execute(Event event, String qName, UserCallDetail userCallDetail) {
		String searchString = "";
		if(event.getEvent() == CoreEnums.events.DEFAULT.ordinal() 
				&& event.getSubEvent() == CoreEnums.subEvents.SampleCheckTimerExp.ordinal())
		{
			MagicParrot magicParrot = new MagicParrot(voiceEffect,CoreEnums.magicParrotOper.StopMP.ordinal(),event.getvId(),qName,event.getHardware());
			SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(magicParrot));
    		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event magicParrotStop added to queue = "+event.getCoreToTelephony());
    		magicParrot = null;
			userCallDetail.setNextStateFlag(true);
			userCallDetail.setExecuteCurrentState(true);
			userCallDetail.setMagicParrotTimer(false);
		}
		else if(event.getEvent() == CoreEnums.events.E_DTMF.ordinal())
		{
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Receive DTMF event, dtfm input = "+event.getSubEvent());
			if(event.getSubEvent()== 10)
				searchString = "*";
            else if(event.getSubEvent()== 11)
            	searchString = "#";
            else if(event.getSubEvent()== 12)
            	searchString = "NoInput";
            else
            	searchString = ""+event.getSubEvent(); 
			userCallDetail.setNextStateFlag(true);
			userCallDetail.setExecuteCurrentState(true);
		}
		else if((event.getEvent()== CoreEnums.events.E_PlayEnd.ordinal()) && 
        		(event.getSubEvent()== CoreEnums.playEndSubEvent.S_PlayStopByApp.ordinal()))
		{
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Discard the Play-end event received");
    		userCallDetail.setNextStateFlag(false);
		}
		else
		{
			MagicParrot magicParrot = new MagicParrot(voiceEffect,CoreEnums.magicParrotOper.StartMP.ordinal(),event.getvId(),qName,event.getHardware());
			SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(magicParrot));
    		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event magicParrotStart added to queue = "+event.getCoreToTelephony());
    		magicParrot = null;
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Going to start free mins timer.");
			event.setEvent(CoreEnums.events.DEFAULT.ordinal());						//created timer event
			event.setSubEvent(CoreEnums.subEvents.SampleCheckTimerExp.ordinal());
			String timerId = Timer.startTimer(event, sampleCheckTimer, qName);
			
			userCallDetail.setSampleCheckTimerId(timerId);
			userCallDetail.setMagicParrotTimer(true);
			userCallDetail.setNextStateFlag(false);
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Sample check timer started. TimerId = "+sampleCheckTimer+", timerId = "+userCallDetail.getSampleCheckTimerId());
			searchString = "Success";
		}		
		return searchString;
	}

}
