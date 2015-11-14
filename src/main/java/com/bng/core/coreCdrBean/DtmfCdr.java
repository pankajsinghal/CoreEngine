package com.bng.core.coreCdrBean;

import java.io.Serializable;

public class DtmfCdr implements Serializable{
	
	private String dtmftime;
	private String dtmfinput;

	public DtmfCdr(String dtmftime, String dtmfinput) {
		super();
		this.dtmftime = dtmftime;
		this.dtmfinput = dtmfinput;
	}

	public String getDtmftime() {
		return dtmftime;
	}

	public void setDtmftime(String dtmftime) {
		this.dtmftime = dtmftime;
	}

	public String getDtmfinput() {
		return dtmfinput;
	}

	public void setDtmfinput(String dtmfinput) {
		this.dtmfinput = dtmfinput;
	}
	
	
}
