package com.bng.core.bean;

import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Event;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;

public class GoTo implements Execute{

	private String jumpFor;
	
	public String getJumpFor() {
		return jumpFor;
	}

	public void setJumpFor(String jumpFor) {
		this.jumpFor = jumpFor;
	}

	public String execute(Event event, String qName, UserCallDetail userCallDetail)
	{
		if(jumpFor.equalsIgnoreCase("bookmark"))
		{
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Going to jump on playId "+userCallDetail.getBookmarkId()+" for "+jumpFor);
			userCallDetail.setCurrentState(userCallDetail.getBookmarkId());
			userCallDetail.setExecuteCurrentState(true);
			userCallDetail.setNextStateFlag(false);
		}
		return "";
	}

}
