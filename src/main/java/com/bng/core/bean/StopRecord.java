package com.bng.core.bean;

import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Event;
import com.bng.core.jsonBean.Record;
import com.bng.core.parser.Engine;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.queue.Timer;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

public class StopRecord implements Execute{

	public String execute(Event event,String qName,UserCallDetail userCallDetail)
	{
		Record record = null;
		try
		{
			if(userCallDetail.isRecordTimerFlag())
			{
				int timerInterval = Timer.stopTimer(Long.parseLong(userCallDetail.getRecordTimer()));
        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Record Event timer interval  = "+timerInterval+" , timer id = "+userCallDetail.getNpTimerId());
        		userCallDetail.setRecordTimerFlag(false);
        		userCallDetail.setRecordTimer(null);
        		record = new Record(userCallDetail.getRecordFileName(), event.getvId(), event.getIp(), CoreEnums.recordOperations.end.ordinal(), event.getaPartyMsisdn(), "-1", false, event.getHardware(), qName);
    			SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(record));
    			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Telephony event record added to queue : "+event.getCoreToTelephony());
    			userCallDetail.setNextStateFlag(true);
    			userCallDetail.setExecuteCurrentState(true);
			}			
		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, this.getClass().getSimpleName(), coreException.GetStack(e));
		}
		return "";
	}
}
