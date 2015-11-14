package com.bng.core.jsonBean;

public class MagicParrot {
	private String className;
	private int frequency;
	private int operation;
	private int vId;
	private String telephonyToCore;
	private int hardware;
	
	
	public MagicParrot(int frequency, int operation, int vId,
			String telephonyToCore, int hardware) {
		super();
		this.className = this.getClass().getSimpleName();
		this.frequency = frequency;
		this.operation = operation;
		this.vId = vId;
		this.telephonyToCore = telephonyToCore;
		this.hardware = hardware;
	}
	
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public int getOperation() {
		return operation;
	}
	public void setOperation(int operation) {
		this.operation = operation;
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
	public int getHardware() {
		return hardware;
	}
	public void setHardware(int hardware) {
		this.hardware = hardware;
	}
	
	
}
