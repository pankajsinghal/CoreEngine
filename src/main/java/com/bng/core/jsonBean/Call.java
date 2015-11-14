/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.jsonBean;

/**
 * @author richa
 */
public class Call {
    private String className;
    private int operation;
    private int vId;
    private boolean record;
    private String msisdn;
    private String filePath;
    private int hardware;
    private String telephonyToCore;

    public Call(int operation, int virtualId, boolean record, String msisdn, String filePath, int hardware, String telephonyToCore) {
        this.operation = operation;
        this.vId = virtualId;
        this.record = record;
        this.msisdn = msisdn;
        this.filePath = filePath;
        this.hardware = hardware;
        this.className = this.getClass().getSimpleName();
        this.telephonyToCore = telephonyToCore;
    }
    
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public boolean isRecord() {
        return record;
    }

    public void setRecord(boolean record) {
        this.record = record;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

	public int getvId() {
		return vId;
	}

	public void setvId(int vId) {
		this.vId = vId;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
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
