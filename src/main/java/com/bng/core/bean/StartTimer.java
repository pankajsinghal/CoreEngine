package com.bng.core.bean;

import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Event;
import com.bng.core.queue.Timer;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;

public class StartTimer implements Execute
{
	private String timerType;
	private int timerCellId;	
	private int timerValue;

	public String getTimerType() {
		return timerType;
	}

	public void setTimerType(String timerType) {
		this.timerType = timerType;
	}

	public int getTimerCellId() {
		return timerCellId;
	}

	public void setTimerCellId(int timerCellId) {
		this.timerCellId = timerCellId;
	}

	public long getTimerValue() {
		return timerValue;
	}

	public void setTimerValue(int timerValue) {
		this.timerValue = timerValue;
	}

	public String execute(Event event, String qName, UserCallDetail userCallDetail) 
	{
		String searchString = "";
		
		if(event.getEvent() == CoreEnums.events.DEFAULT.ordinal() && event.getSubEvent() == CoreEnums.subEvents.FreeMinTimerExp.ordinal())
		{
			userCallDetail.setUpdatefreeMins(true);
			userCallDetail.setNextStateFlag(true);
    		userCallDetail.setExecuteCurrentState(true);
    		searchString = "Failure";
		}
		else if(event.getEvent() == CoreEnums.events.DEFAULT.ordinal() && event.getSubEvent() == CoreEnums.subEvents.UserDefinedTimerExp.ordinal())
		{
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] User defined timer exp. Return Failure.");
			userCallDetail.setNextStateFlag(true);
    		userCallDetail.setExecuteCurrentState(true);
    		searchString = "Failure";
    		return searchString;
		}
		
		if(timerType.equalsIgnoreCase("FreeMinutesTimer"))
		{			    		
    		if(userCallDetail.getRemainingFreeMinutes() > 0)
    		{
    			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Going to start free mins timer.");
    			event.setEvent(CoreEnums.events.DEFAULT.ordinal());
	    		event.setSubEvent(CoreEnums.subEvents.FreeMinTimerExp.ordinal());
	    		String timerId = Timer.startTimer(event, userCallDetail.getRemainingFreeMinutes()*60, qName);
	    		userCallDetail.setFreeMinTimerId(timerId);
	    		userCallDetail.setFreeMinTimerFlag(true);
	    		userCallDetail.setFreeMinTimerCellId(timerCellId);
	    		
	    		userCallDetail.setNextStateFlag(true);
	    		userCallDetail.setExecuteCurrentState(true);
	    		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Free mins timer started. TimerId = "+userCallDetail.getFreeMinTimerId());
	    		searchString = "Success";
    		}
    		else
    		{
    			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Doesn't have freeMins left.");
    			searchString = "Failure";
    			userCallDetail.setNextStateFlag(true);
    			userCallDetail.setExecuteCurrentState(true);
    		}
		}				
		else if(timerType.equalsIgnoreCase("user"))
		{
			try 
			{
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Timer value given by user = "+timerValue+". Going in start timer.");
				//Thread.sleep(timerValue);
				event.setEvent(CoreEnums.events.DEFAULT.ordinal());
	    		event.setSubEvent(CoreEnums.subEvents.UserDefinedTimerExp.ordinal());
	    		String timerId = Timer.startTimer(event, timerValue, qName);
	    		userCallDetail.setUserDefTimerId(timerId);
	    		userCallDetail.setUserDefTimerFlag(true);
	    		userCallDetail.setUserDefTimerCellId(timerCellId);
	    		
	    		userCallDetail.setNextStateFlag(true);
	    		userCallDetail.setExecuteCurrentState(true);
	    		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]User defined timer started. TimerId = "+userCallDetail.getUserDefTimerId());
	    		searchString = "Success";
			} 
			catch (Exception e) 
			{
				Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
			}
		}		
		return searchString;
	}	
}
