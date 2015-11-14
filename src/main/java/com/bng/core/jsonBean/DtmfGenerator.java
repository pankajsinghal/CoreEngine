package com.bng.core.jsonBean;

public class DtmfGenerator {
	private String className;
    private int vId;
    private int genEventvId;
    private String ip;
    private int hardware;
    private String telephonyToCore;
    private String inJsonDtmf;
    
    public DtmfGenerator( int virtualId, int genEventvId, String ip, int hardware, String telephonyToCore, String inJsonDtmf) {
        this.inJsonDtmf = inJsonDtmf;		//Generate DTMF on virtualId 
        this.genEventvId = genEventvId;
        this.vId = virtualId;
        this.ip = ip;
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
    
    public int getvId() {
		return vId;
	}

	public void setvId(int vId) {
		this.vId = vId;
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getDTMF() {
		return inJsonDtmf;
	}

	public void setDTMF(String inJsonDtmf) {
		this.inJsonDtmf = inJsonDtmf;
	}

	public int getGenEventvId() {
		return genEventvId;
	}

	public void setGenEventvId(int genEventvId) {
		this.genEventvId = genEventvId;
	}

	public String getInJsonDtmf() {
		return inJsonDtmf;
	}

	public void setInJsonDtmf(String inJsonDtmf) {
		this.inJsonDtmf = inJsonDtmf;
	}
	
	
	
}
