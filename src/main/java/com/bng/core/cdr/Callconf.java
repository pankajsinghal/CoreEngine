package com.bng.core.cdr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "callconference")
@XmlAccessorType(XmlAccessType.FIELD)
public class Callconf {

	@XmlElement(name = "confid")
	private String confid;

	@XmlElement(name = "systemip")
	private String systemip;
	
	@XmlElement(name = "hardware")
	private String hardware;

	@XmlElement(name = "protocol")
	private String protocol;

	@XmlElement(name = "calltype")
	private String calltype;
	
	@XmlElement(name = "aparty")
	private String aparty;

	@XmlElement(name = "bparty")
	private String bparty;
	
	@XmlElement(name = "vid")
	private String vid;

	@XmlElement(name = "cic")
	private String cic;

	@XmlElement(name = "dialtime")
	private String dialtime;
	
	@XmlElement(name = "startdatetime")
	private String startdatetime;

	@XmlElement(name = "enddatetime")
	private String enddatetime;
	
	@XmlElement(name = "status")
	private String status;

	@XmlElement(name = "reason")
	private String reason;

	@XmlElement(name = "operator")
	private String operator;

	@XmlElement(name = "circle")
	private String circle;

	public Callconf(){
		
	}
	
	public Callconf(String confid, String systemip, String hardware,
			String protocol, String calltype, String aparty, String bparty,
			String vid, String cic, String dialtime, String startdatetime,
			String enddatetime, String status, String reason, String operator,
			String circle) {
		super();
		this.confid = confid;
		this.systemip = systemip;
		this.hardware = hardware;
		this.protocol = protocol;
		this.calltype = calltype;
		this.aparty = aparty;
		this.bparty = bparty;
		this.vid = vid;
		this.cic = cic;
		this.dialtime = dialtime;
		this.startdatetime = startdatetime;
		this.enddatetime = enddatetime;
		this.status = status;
		this.reason = reason;
		this.operator = operator;
		this.circle = circle;
	}

	public String getConfid() {
		return confid;
	}

	public void setConfid(String confid) {
		this.confid = confid;
	}

	public String getSystemip() {
		return systemip;
	}

	public void setSystemip(String systemip) {
		this.systemip = systemip;
	}

	public String getHardware() {
		return hardware;
	}

	public void setHardware(String hardware) {
		this.hardware = hardware;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getCalltype() {
		return calltype;
	}

	public void setCalltype(String calltype) {
		this.calltype = calltype;
	}

	public String getAparty() {
		return aparty;
	}

	public void setAparty(String aparty) {
		this.aparty = aparty;
	}

	public String getBparty() {
		return bparty;
	}

	public void setBparty(String bparty) {
		this.bparty = bparty;
	}

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public String getCic() {
		return cic;
	}

	public void setCic(String cic) {
		this.cic = cic;
	}

	public String getDialtime() {
		return dialtime;
	}

	public void setDialtime(String dialtime) {
		this.dialtime = dialtime;
	}

	public String getStartdatetime() {
		return startdatetime;
	}

	public void setStartdatetime(String startdatetime) {
		this.startdatetime = startdatetime;
	}

	public String getEnddatetime() {
		return enddatetime;
	}

	public void setEnddatetime(String enddatetime) {
		this.enddatetime = enddatetime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getCircle() {
		return circle;
	}

	public void setCircle(String circle) {
		this.circle = circle;
	}

	@Override
	public String toString() {
		return "Callconf [confid=" + confid + ", systemip=" + systemip
				+ ", hardware=" + hardware + ", protocol=" + protocol
				+ ", calltype=" + calltype + ", aparty=" + aparty + ", bparty="
				+ bparty + ", vid=" + vid + ", cic=" + cic + ", dialtime="
				+ dialtime + ", startdatetime=" + startdatetime
				+ ", enddatetime=" + enddatetime + ", status=" + status
				+ ", reason=" + reason + ", operator=" + operator + ", circle="
				+ circle + "]";
	}
	
	
}
