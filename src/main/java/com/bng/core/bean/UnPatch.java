package com.bng.core.bean;

import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.CallPatch;
import com.bng.core.jsonBean.Event;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

public class UnPatch implements Execute{

	public String execute(Event event,String qName,UserCallDetail userCallDetail)
	{
		CallPatch callPatch = null;
		String bgFile = "";
		String searchstring = "";
		try
		{
			if(userCallDetail.isInPatchState())
			{
				callPatch = new CallPatch(userCallDetail.getaPartyVId(),userCallDetail.getbPartyVId(), event.getIp(),CoreEnums.patchOperations.Unpatch.ordinal(), event.getHardware(),Integer.parseInt(userCallDetail.getVoiceEffectFrequency()), bgFile, qName,"0_0");
				userCallDetail.setInPatchState(false);
			}
			else if(userCallDetail.isInPatchWithCG())
			{
				//if(event.getCallType() == CoreEnums.callType.Incoming.ordinal())
					callPatch = new CallPatch(userCallDetail.getaPartyVId(),userCallDetail.getCgVid(), event.getIp(),CoreEnums.patchOperations.Unpatch.ordinal(), event.getHardware(),Integer.parseInt(userCallDetail.getVoiceEffectFrequency()), bgFile, qName,"0_0");
					userCallDetail.setInPatchWithCG(false);
				//else
				//	callPatch = new CallPatch(userCallDetail.getbPartyVId(),userCallDetail.getCgVid(), event.getIp(),CoreEnums.patchOperations.Unpatch.ordinal(), event.getHardware(),Integer.parseInt(userCallDetail.getVoiceEffectFrequency()), bgFile, qName,"0_0");
			}
			SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(callPatch));
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Telephony event callPatch added to queue : "+event.getCoreToTelephony());
			userCallDetail.setNextStateFlag(false);
			
			callPatch = null;	
			
			(userCallDetail.getCallCdr().getCallconf()).setEnddatetime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));;
		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
		}
		return searchstring;
	}

}
