package com.bng.core.jsonBean;

public class DigitCollection {
	
	private String className = "DigitCollection";
	private int vId;
	private int maxLen;
	private int timeOut;
	private String endChar;
	private boolean withEndChar;
	private String ip;
	private int hardware;
	private String telephonyToCore;
	
	public DigitCollection(int vId, int maxLen, int timeOut, String endChar,
			boolean withEndChar, String ip, int hardware, String telephonyToCore) {
		super();
		this.vId = vId;
		this.maxLen = maxLen;
		this.timeOut = timeOut;
		this.endChar = endChar;
		this.withEndChar = withEndChar;
		this.ip = ip;
		this.hardware = hardware;
		this.telephonyToCore = telephonyToCore;
		this.className = this.getClassName();
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public int getvId() {
		return vId;
	}
	public void setvId(int vId) {
		this.vId = vId;
	}
	public int getMaxLen() {
		return maxLen;
	}
	public void setMaxLen(int maxLen) {
		this.maxLen = maxLen;
	}
	public int getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	public String getEndChar() {
		return endChar;
	}
	public void setEndChar(String endChar) {
		this.endChar = endChar;
	}
	public boolean isWithEndChar() {
		return withEndChar;
	}
	public void setWithEndChar(boolean withEndChar) {
		this.withEndChar = withEndChar;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getHardware() {
		return hardware;
	}
	public void setHardware(int hardware) {
		this.hardware = hardware;
	}
	public String getTelephonyToCore() {
		return telephonyToCore;
	}
	public void setTelephonyToCore(String telephonyToCore) {
		this.telephonyToCore = telephonyToCore;
	}	
}
