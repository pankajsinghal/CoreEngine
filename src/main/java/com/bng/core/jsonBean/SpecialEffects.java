package com.bng.core.jsonBean;

public class SpecialEffects {
	private String className;
	private int vId;
	private int frequency;
	private String backgroundMusicFilePath;
	private String telephonyToCore;
	private String transId;
	
	public SpecialEffects(int vId, int frequency, String backgroundMusicFilePath, String telephonyToCore, String transId) {
		super();
		this.vId = vId;
		this.frequency = frequency;
		this.backgroundMusicFilePath = backgroundMusicFilePath;
		this.telephonyToCore = telephonyToCore;
		this.className = this.getClass().getSimpleName();
		this.transId = transId;
	}
	public int getvId() {
		return vId;
	}
	public void setvId(int vId) {
		this.vId = vId;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}	
	public String getBackgroundMusicFilePath() {
		return backgroundMusicFilePath;
	}
	public void setBackgroundMusicFilePath(String backgroundMusicFilePath) {
		this.backgroundMusicFilePath = backgroundMusicFilePath;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
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
