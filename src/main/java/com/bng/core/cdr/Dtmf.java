package com.bng.core.cdr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "dtmfs")
@XmlAccessorType(XmlAccessType.FIELD)
public class Dtmf {
	
	@XmlElement(name = "dtmftime")
	private String dtmftime;

	@XmlElement(name = "dtmfinput")
	private String dtmfinput;

	public Dtmf() {
	}
	
	public Dtmf(String dtmftime, String dtmfinput) {
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

	@Override
	public String toString() {
		return "Dtmf [dtmftime=" + dtmftime + ", dtmfinput=" + dtmfinput + "]";
	}
	
	
}
