package com.bng.core.bean;

import com.bng.core.coreCdrBean.CallCdr;
import com.bng.core.coreCdrBean.DtmfCdr;
import com.bng.core.coreCdrBean.MediaCdr;
import com.bng.core.coreCdrBean.SpecialEffectCdr;
import com.bng.core.entity.Subscription;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Event;
import com.bng.core.jsonBean.SpecialEffects;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

public class Processing implements Execute{
	private String processingType;
	private String value;
	
	public String getProcessingType() {
		return processingType;
	}

	public void setProcessingType(String processingType) {
		this.processingType = processingType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String execute(Event event, String qName, UserCallDetail userCallDetail) 
	{
		String searchString = null;
		SpecialEffects specialEffects = null;
		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] ProcessingType = "+processingType+", value = "+value);
		if(processingType.equalsIgnoreCase("dateformatter"))
		{
			userCallDetail.setProcessingDateFormat(value);
			searchString = "";
		}
		else if(processingType.equalsIgnoreCase("SetVoiceEffect"))
		{
			CallCdr callcdr = userCallDetail.getCallCdr();
			userCallDetail.setVoiceEffectFrequency(value);
			SpecialEffectCdr specialeffect = new SpecialEffectCdr(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"),value);
			callcdr.addspecialeffectList(specialeffect);
			if(userCallDetail.isInPatchState())
			{
				specialEffects = new SpecialEffects(event.getvId(), Integer.parseInt(value), "", qName,"0_0");
				SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(specialEffects));
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event specialEffects for voiceEffect added to queue = "+event.getCoreToTelephony());
			}
			searchString = "";
		}
		else if(processingType.equalsIgnoreCase("setbackground"))
		{
			userCallDetail.setBackgroundFile(value);
			if(userCallDetail.isInPatchState())
			{
				specialEffects = new SpecialEffects(event.getvId(), 0, value, qName,"0_0");
				SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(specialEffects));
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event specialEffects for backGroundFile added to queue = "+event.getCoreToTelephony());
				MediaCdr media = new MediaCdr();
	        	media.setType("PlayContent");
	        	media.setLocation("Local");
	        	media.setStarttime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
	        	media.setFilename(value);
	        	media.setCode("0");
	        	userCallDetail.getCallCdr().addMediaMap("0_0",media);
        		media = null;
			}
    		searchString = "";
		}
		else if(processingType.equalsIgnoreCase("SetLanguage"))
		{
			userCallDetail.setLanguage(value);
			searchString = "";
		}
		else if(processingType.equalsIgnoreCase("namazalert"))
		{
			Subscription subscription = Utility.checkSubscription(event.getaPartyMsisdn(), event.getIvrCode(), userCallDetail.getService());
			if(value.equalsIgnoreCase("DeActivateNamazAlert"))
			{
				subscription.setAlert(0);
				Utility.updateSub(subscription, event.getaPartyMsisdn());
			}
			else if(value.equalsIgnoreCase("ActivateNamazAlert"))
			{
				subscription.setAlert(1);
				Utility.updateSub(subscription, event.getaPartyMsisdn());
			}
		}
		else if(processingType.equalsIgnoreCase("startfrombookmark"))
		{
			if(value.equalsIgnoreCase("true"))
				userCallDetail.setStartfrombookmark(true);
		}
		userCallDetail.setNextStateFlag(true);
		userCallDetail.setExecuteCurrentState(true);
		return searchString;
	}
	
	
}
