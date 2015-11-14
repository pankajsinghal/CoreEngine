/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.jsonBean;

/**
 *
 * @author richa
 */
public class Play {
    private String className;
    private String filePath;
    private int vId;
    private String ip;
    private int repeat;
    private boolean seek;
    private int operation;
    private String msisdn;
    private String startByte;
    private int hardware;
    private String telephonyToCore;
    private String transId;

    public Play(String filePath, int vId, String ip, int repeat, boolean seek, int operation, String msisdn, String startByte, int hardware, String telephonyToCore, String transId) {
        this.filePath = filePath;
        this.vId = vId;
        this.ip = ip;
        this.repeat = repeat;
        this.seek = seek;
        this.operation = operation;
        this.msisdn = msisdn;
        this.startByte = startByte;
        this.hardware = hardware;
        this.className = this.getClass().getSimpleName();
        this.telephonyToCore = telephonyToCore;
        this.transId = transId;
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

	public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public boolean isSeek() {
        return seek;
    }

    public void setSeek(boolean seek) {
        this.seek = seek;
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

    public String getStartByte() {
        return startByte;
    }

    public void setStartByte(String startByte) {
        this.startByte = startByte;
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

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

}
