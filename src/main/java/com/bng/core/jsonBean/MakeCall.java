/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.jsonBean;

/**
 *
 * @author richa
 */
public class MakeCall {
    private String className;
    private String bPartyMsisdn;
    private String aPartyMsisdn;
    private String ivrCode;
    private int hardware;
    private String ip;
    private int vId;
    private String telephonyToCore;
    
    
    public MakeCall(String bPartyMsisdn, String aPartyMsisdn, String ivrCode, int hardware, String ip, int vId, String telephonyToCore) 
    {
		this.bPartyMsisdn = bPartyMsisdn;
		this.aPartyMsisdn = aPartyMsisdn;
		this.ivrCode = ivrCode;
		this.hardware = hardware;
		this.ip = ip;
		this.vId = vId;
		this.className = this.getClass().getSimpleName();
		this.telephonyToCore = telephonyToCore;
	}

	public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }    

    public String getbPartyMsisdn() {
		return bPartyMsisdn;
	}

	public void setbPartyMsisdn(String bPartyMsisdn) {
		this.bPartyMsisdn = bPartyMsisdn;
	}

	public String getaPartyMsisdn() {
		return aPartyMsisdn;
	}

	public void setaPartyMsisdn(String aPartyMsisdn) {
		this.aPartyMsisdn = aPartyMsisdn;
	}

	public String getIvrCode() {
        return ivrCode;
    }

    public void setIvrCode(String ivrCode) {
        this.ivrCode = ivrCode;
    }

    public int getHardware() {
        return hardware;
    }

    public void setHardware(int hardware) {
        this.hardware = hardware;
    }

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getvId() {
		return vId;
	}

	public void setvId(int vId) {
		this.vId = vId;
	}

	public String getTelephonyToCore() {
		return telephonyToCore;
	}

	public void setTelephonyToCore(String telephonyToCore) {
		this.telephonyToCore = telephonyToCore;
	}	
}
