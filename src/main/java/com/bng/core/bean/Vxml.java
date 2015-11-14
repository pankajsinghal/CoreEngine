package com.bng.core.bean;

import java.util.Properties;

import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Event;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.SimpleParser;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

public class Vxml implements Execute{

	private String url;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}


	@Override
	public String execute(Event event, String qName, UserCallDetail userCallDetail) 
	{
		String msisdn;
		SimpleParser sp = null;
		Properties prop = Utility.convertUCDToProp(userCallDetail);
		try
		{
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] GET url = "+url);
			sp = new SimpleParser(url);
			url = sp.parse(prop);
			url = url.replaceAll("#", "&");
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Formatted GET url = "+url);
			
			
			if(event.getCallType() == CoreEnums.callType.Incoming.ordinal())
				msisdn = event.getaPartyMsisdn();
			else
				msisdn = event.getbPartyMsisdn();
			
			 Event vxmlevent = new Event(event.getvId(), msisdn, "", CoreEnums.events.E_VXML.ordinal(), CoreEnums.subEvents.Vxmlcontrolstart.ordinal(), "", 0, event.getCallType(), "", "", "", 0, 0, "", "", "", "", 0, "", event.getCoreToTelephony(), "", "", 0, qName, url,"","");
			 SendToTelephony.addMessageToQueue("vxml",Utility.convertObjectToJsonStr(vxmlevent));
			 Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Event for transfering control to vxml added to queue = vxml");
			 userCallDetail.setNextStateFlag(true);
			 userCallDetail.setExecuteCurrentState(true);
			 return "";
		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
			return "";
		}
		
	}
	
	
}
