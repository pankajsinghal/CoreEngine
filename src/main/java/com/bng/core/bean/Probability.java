package com.bng.core.bean;

import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Event;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

public class Probability implements Execute{

	private int probability;
	
	public int getProbability() {
		return probability;
	}
	public void setProbability(int probability) {
		this.probability = probability;
	}
	
	public String execute(Event event, String qName,
			UserCallDetail userCallDetail) 
	{
		boolean flag = false;
		String searchstring = "";
		try
		{
			if(probability > 0)
				flag = Utility.getPercentage(probability);
			if(flag)
				searchstring = "Success";
			else
				searchstring = "failure";
			
		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] "+coreException.GetStack(e));
		}
		return searchstring;
	}

}
