package com.bng.core.bean;

import javax.jms.Queue;

import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Event;
import com.bng.core.jsonBean.Record;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.queue.Timer;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

public class StartRecord implements Execute{

	private int timeout;
	private static String recordFilePath;
	private String recordenddtmf;
	private boolean recorddedication; 
	
	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}	

	public void setRecordFilePath(String recordFilePath) {
		StartRecord.recordFilePath = recordFilePath;
	}
	
	public void setRecordenddtmf(String recordenddtmf) {
		this.recordenddtmf = recordenddtmf;
	}
	
	public boolean isRecorddedication() {
		return recorddedication;
	}

	public void setRecorddedication(boolean recorddedication) {
		this.recorddedication = recorddedication;
	}

	public String execute(Event event,String qName,UserCallDetail userCallDetail)
	{
		Record record = null;
		String recordFileName = recordFilePath+"record_"+event.getaPartyMsisdn()+"_"+Utility.getCurrentDate("yyyy-MM-dd_HH-mm-ss.SSS")+".wav";
		try
		{
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Inside StartRecord execute method.");
			
			
			if(event.getEvent()== CoreEnums.events.E_DTMF.ordinal())
        	{
        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Got dtmf input from user : event = "+event.getEvent()+", subEvent = "+event.getSubEvent());
        		String dtmf = "";
    			if(event.getSubEvent()== 10)
    				dtmf = "*";
                else if(event.getSubEvent()== 11)
                	dtmf = "#";
                else if(event.getSubEvent()== 12)
                	dtmf = "NoInput";
                else
                	dtmf = ""+event.getSubEvent();  
    			
    			if(dtmf.equalsIgnoreCase(recordenddtmf))
    			{
    				if(userCallDetail.isRecordTimerFlag())
                	{
     
                		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Record End DTMF received");
                		StopRecord stopRecord = new StopRecord();
                		stopRecord.execute(event, qName, userCallDetail);
                		return "";
                	}
    			}
    			else
    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Some Other DTMF received,Discard");
        		
			
        	}
			else if(event.getEvent() == CoreEnums.events.DEFAULT.ordinal() && event.getSubEvent() == CoreEnums.subEvents.RecordTimerExp.ordinal())
			{
				if(userCallDetail.isRecordTimerFlag())
            	{
					if(userCallDetail.getRecordTimer() != null)
					{
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Record End DTMF received");
	            		StopRecord stopRecord = new StopRecord();
	            		stopRecord.execute(event, qName, userCallDetail);
					}
            		
            	}
				return "";
			}
			if(!userCallDetail.isRecordTimerFlag())
			{
				if(timeout > 0)
				{
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Recording timeout = "+timeout);
					event.setEvent(CoreEnums.events.DEFAULT.ordinal());
					event.setSubEvent(CoreEnums.subEvents.RecordTimerExp.ordinal());
					
					String timerId = Timer.startTimer(event, timeout, qName);
	        		userCallDetail.setRecordTimer(timerId);
	        		userCallDetail.setRecordTimerFlag(true);
				}
				
				if(recorddedication)
				{
					record = new Record(recordFileName, event.getvId(), event.getIp(), CoreEnums.recordOperations.record.ordinal(), event.getaPartyMsisdn(), "-1", false, event.getHardware(), qName);
					userCallDetail.setRecordFileName(recordFileName);
					SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(record));
					Logger.sysLog(LogValues.info, this.getClass().getSimpleName(), "Telephony event record added to queue : "+event.getCoreToTelephony());
					userCallDetail.setNextStateFlag(false);
					userCallDetail.setExecuteCurrentState(false);
					userCallDetail.setRecorddedication(true);
				}
				else
				{
					record = new Record(recordFileName, event.getvId(), event.getIp(), CoreEnums.recordOperations.record.ordinal(), event.getaPartyMsisdn(), "-1", false, event.getHardware(), qName);
					userCallDetail.setRecordFileName(recordFileName);
					SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(record));
					Logger.sysLog(LogValues.info, this.getClass().getSimpleName(), "Telephony event record added to queue : "+event.getCoreToTelephony());
					userCallDetail.setNextStateFlag(true);
					userCallDetail.setExecuteCurrentState(true);
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
