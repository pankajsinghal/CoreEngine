package com.bng.core.bean;

import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.CallPatch;
import com.bng.core.jsonBean.Event;
import com.bng.core.memCache.MemCacheJSon;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

public class Patch implements Execute{
	private String patchWith;
	private static MemCacheJSon memCacheJSon = null;	
	
	
	public String getPatchWith() {
		return patchWith;
	}

	public void setPatchWith(String patchWith) {
		this.patchWith = patchWith;
	}

	public void setMemCacheJSon(MemCacheJSon memCacheJSon) {
		Patch.memCacheJSon = memCacheJSon;
	}
	
	public String execute(Event event,String qName,UserCallDetail userCallDetail)
	{
		//String bgFile = "";
		String searchstring = "";
		CallPatch callPatch = null;
		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Patch call with = "+patchWith);
		if(patchWith.equalsIgnoreCase("consentgateway"))
		{
			if(!userCallDetail.isInPatchWithCG())
			{
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] aPartyVid = "+userCallDetail.getaPartyVId()+" , bPartyVid = "+userCallDetail.getbPartyVId());
				callPatch = new CallPatch(userCallDetail.getaPartyVId(),userCallDetail.getCgVid(), event.getIp(),CoreEnums.patchOperations.Patch.ordinal(), event.getHardware(),Integer.parseInt(userCallDetail.getVoiceEffectFrequency()), userCallDetail.getBackgroundFile(), qName,"0_0");
				SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(callPatch));
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event callPatch with CG added to queue : "+event.getCoreToTelephony());
				userCallDetail.setNextStateFlag(true);
				userCallDetail.setExecuteCurrentState(true);
				userCallDetail.setInPatchWithCG(true);
				userCallDetail.setPatchTime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
				
				Event selfGenEvent = new Event();				
				if(userCallDetail.getCallTypePatchedWithCg() == CoreEnums.callType.Incoming.ordinal())
				{
					selfGenEvent.setaPartyMsisdn(userCallDetail.getaPartyMsisdn());
					selfGenEvent.setCallType(CoreEnums.callType.Incoming.ordinal());
				}
				else
				{
					selfGenEvent.setbPartyMsisdn(userCallDetail.getaPartyMsisdn());
					selfGenEvent.setCallType(CoreEnums.callType.Outgoing.ordinal());
				}			
				selfGenEvent.setvId(userCallDetail.getaPartyVId());				
				selfGenEvent.setEvent(CoreEnums.events.DEFAULT.ordinal());
				selfGenEvent.setSubEvent(CoreEnums.subEvents.CallPatchedWithCG.ordinal());
				selfGenEvent.setCurrentState(userCallDetail.getCurrentState());
				SendToTelephony.addMessageToQueue(qName,Utility.convertObjectToJsonStr(selfGenEvent));
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Self-generated event for callPatch added to queue : "+qName);
				selfGenEvent = null;
				callPatch = null;	
			}
			else
			{
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Already in patch state with CG.");
				userCallDetail.setNextStateFlag(true);
				userCallDetail.setExecuteCurrentState(true);
				userCallDetail.setDialStatusbparty(false);
			}
		}
		else if(event.getEvent() == CoreEnums.events.E_DTMF.ordinal())
		{
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] DTMF received at patch DISCARD.");
			userCallDetail.setNextStateFlag(false);
			userCallDetail.setExecuteCurrentState(false);
			return "" ;
		}
		else
		{
			try
			{
				/*UserCallDetail aPartyUCD = (UserCallDetail) memCacheJSon.get(userCallDetail.getaPartyVId()+"_"+userCallDetail.getaPartyMsisdn());
				if(aPartyUCD == null)
				{
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] AParty doesnt exist. Hangup BParty.");
					Exit exit = new Exit();
					exit.execute(event, qName, userCallDetail);
					userCallDetail.setNextStateFlag(false);
					return "";
				}*/
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] InPatch state = "+userCallDetail.isInPatchState()+", executePatch = "+userCallDetail.isExecuteCurrentState());
				if(!userCallDetail.isExecutePatch())
				{
					callPatch = new CallPatch(userCallDetail.getaPartyVId(),userCallDetail.getbPartyVId(), event.getIp(),CoreEnums.patchOperations.Patch.ordinal(), event.getHardware(),Integer.parseInt(userCallDetail.getVoiceEffectFrequency()), userCallDetail.getBackgroundFile(), qName,"0_0");
					SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(callPatch));
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event callPatch added to queue : "+event.getCoreToTelephony());
					userCallDetail.setNextStateFlag(true);
					userCallDetail.setExecuteCurrentState(true);
					userCallDetail.setInPatchState(true);
					userCallDetail.setPatchTime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
					
					/*if(userCallDetail.getBackgroundFile() != null)
					{
						MediaCdr media = new MediaCdr();
			        	media.setType("PlayContent");
			        	media.setLocation("Local");
			        	media.setStarttime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
			        	media.setFilename(userCallDetail.getBackgroundFile());
			        	media.setCode("0_0");
			        	userCallDetail.getCallCdr().addMediaMap("0_0",media);
					}*/
					if(event.getCallType() == CoreEnums.callType.Outgoing.ordinal())
					{
						Event selfGenEvent = new Event();				
						selfGenEvent.setvId(userCallDetail.getaPartyVId());
						selfGenEvent.setaPartyMsisdn(userCallDetail.getaPartyMsisdn());
						selfGenEvent.setCallType(CoreEnums.callType.Incoming.ordinal());
						selfGenEvent.setEvent(CoreEnums.events.DEFAULT.ordinal());
						selfGenEvent.setSubEvent(CoreEnums.subEvents.CallPatched.ordinal());
						selfGenEvent.setCurrentState(userCallDetail.getCurrentState());
						SendToTelephony.addMessageToQueue(qName,Utility.convertObjectToJsonStr(selfGenEvent));
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Self-generated event for callPatch (to notify aparty ucd) added to queue : "+event.getCoreToTelephony());
						selfGenEvent = null;
						callPatch = null;	
					}	
					else
					{
						Event selfGenEvent = new Event();				
						selfGenEvent.setvId(userCallDetail.getbPartyVId());
						selfGenEvent.setbPartyMsisdn(userCallDetail.getbPartyMsisdn());
						selfGenEvent.setCallType(CoreEnums.callType.Outgoing.ordinal());
						selfGenEvent.setEvent(CoreEnums.events.DEFAULT.ordinal());
						selfGenEvent.setSubEvent(CoreEnums.subEvents.CallPatched.ordinal());
						selfGenEvent.setCurrentState(userCallDetail.getCurrentState());
						SendToTelephony.addMessageToQueue(qName,Utility.convertObjectToJsonStr(selfGenEvent));
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Self-generated event for callPatch (to notify bparty ucd) added to queue : "+event.getCoreToTelephony());
						selfGenEvent = null;
						callPatch = null;	
					}
				}
				else
				{
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Already in patch state.");
					userCallDetail.setNextStateFlag(true);
					userCallDetail.setExecuteCurrentState(true);
				}
			}
			catch(Exception e)
			{
				Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
			}
		}
		return searchstring;
	}
}
