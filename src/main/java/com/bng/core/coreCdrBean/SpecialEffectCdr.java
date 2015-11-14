package com.bng.core.coreCdrBean;

import java.io.Serializable;

public class SpecialEffectCdr implements Serializable{
	
	private String specialeffecttime;
	private String specialeffectvalue;

	public SpecialEffectCdr(String specialeffecttime, String specialeffectvalue) {
		super();
		this.specialeffecttime = specialeffecttime;
		this.specialeffectvalue = specialeffectvalue;
	}

	public String getSpecialeffecttime() {
		return specialeffecttime;
	}

	public void setSpecialeffecttime(String specialeffecttime) {
		this.specialeffecttime = specialeffecttime;
	}

	public String getSpecialeffectvalue() {
		return specialeffectvalue;
	}

	public void setSpecialeffectvalue(String specialeffectvalue) {
		this.specialeffectvalue = specialeffectvalue;
	}

}
