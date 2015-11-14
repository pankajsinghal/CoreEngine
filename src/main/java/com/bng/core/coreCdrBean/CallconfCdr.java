package com.bng.core.coreCdrBean;

import java.io.Serializable;

import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

public class CallconfCdr implements Serializable{

	private String confid;
	private String systemip;
	private String hardware;
	private String protocol;
	private String calltype;
	private String aparty;
	private String bparty;
	private String vid;
	private String cic;
	private String dialtime;
	private String startdatetime;
	private String enddatetime;
	private String status;
	private String reason;
	private String operator;
	private String circle;
	
	public CallconfCdr() {
		super();
	}

	public CallconfCdr(String confid, String systemip, String hardware,
			String protocol, String calltype, String aparty, String bparty,
			String vid, String cic, String dialtime, String startdatetime,
			String enddatetime, String status, String reason, String operator,
			String circle) {
		super();
		this.confid = confid;
		this.systemip = systemip;
		this.hardware = hardware;
		this.protocol = protocol;
		this.calltype = calltype;//
		this.aparty = aparty;
		this.bparty = bparty;
		this.vid = vid;//bParty
		this.cic = cic;
		this.dialtime = dialtime;//
		this.startdatetime = startdatetime;//patchStartTime
		this.enddatetime = enddatetime;//patchEndTime
		this.status = status;//
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
		Logger.sysLog(LogValues.info, this.getClass().getName(),"_______________________________________________________________callconf :status = "+status);
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		Logger.sysLog(LogValues.info, this.getClass().getName(),"_______________________________________________________________callconf :reason = "+reason);
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
	
}
