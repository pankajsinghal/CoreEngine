/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.jsonBean;

/**
 *
 * @author richa
 */
public class Record {
    private String className;
    private String filePath;
    private int vId;
    private String ip;
    private int operation;
    private String msisdn;
    private String length;
    private boolean timer;
    private int hardware;
    private String telephonyToCore;
    
    public Record(String filePath, int vId, String ip, int operation,
			String msisdn, String length, boolean timer, int hardware, String telephonyToCore) {
		super();
		this.filePath = filePath;
		this.vId = vId;
		this.ip = ip;
		this.operation = operation;
		this.msisdn = msisdn;
		this.length = length;
		this.timer = timer;
		this.hardware = hardware;
		this.telephonyToCore = telephonyToCore;
		this.className = "Record";
	}

	public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public int getvId() {
		return vId;
	}

	public void setvId(int vId) {
		this.vId = vId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public boolean isTimer() {
        return timer;
    }

    public void setTimer(boolean timer) {
        this.timer = timer;
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
