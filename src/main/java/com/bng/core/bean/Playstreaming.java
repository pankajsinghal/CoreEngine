package com.bng.core.bean;

import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Event;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;

public class Playstreaming implements Execute{
	private String url;
	private boolean bargein;
	private long timeout;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isBargein() {
		return bargein;
	}

	public void setBargein(boolean bargein) {
		this.bargein = bargein;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public String execute(Event event,String qName,UserCallDetail userCallDetail)
	{
		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Inside PlayStreaming execute.");
		return "";
	}

}
