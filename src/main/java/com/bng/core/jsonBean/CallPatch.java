/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.jsonBean;

/**
 *
 * @author richa
 */
public class CallPatch {
    private String className = "CallPatch";
    private int vId;
    private int vId1;
    private String ip;
    private int operation;
    private int hardware;
    private int frequency;
    private String telephonyToCore;
    private String filePath;
    private String transId;
    
    
    public CallPatch(int virtualId1, int virtualId2, String hostIp,
			int operation, int hardware, int frequency, String filePath, String telephonyToCore, String transId) {
		super();
		this.vId = virtualId1;
		this.vId1 = virtualId2;
		this.ip = hostIp;
		this.operation = operation;
		this.hardware = hardware;
		this.frequency = frequency;
		this.className = "CallPatch";
		this.filePath = filePath;
		this.telephonyToCore = telephonyToCore;
		this.transId = transId;
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

	public int getvId1() {
		return vId1;
	}

	public void setvId1(int vId1) {
		this.vId1 = vId1;
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

	public int getHardware() {
		return hardware;
	}

	public void setHardware(int hardware) {
		this.hardware = hardware;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public String getTelephonyToCore() {
		return telephonyToCore;
	}

	public void setTelephonyToCore(String telephonyToCore) {
		this.telephonyToCore = telephonyToCore;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}  
}
