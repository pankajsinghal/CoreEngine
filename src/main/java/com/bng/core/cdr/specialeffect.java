package com.bng.core.cdr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "specialeffects")
@XmlAccessorType(XmlAccessType.FIELD)
public class specialeffect {
	
	@XmlElement(name = "specialeffecttime")
	private String specialeffecttime;

	@XmlElement(name = "specialeffectvalue")
	private String specialeffectvalue;
	
	public specialeffect() {
	}
	
	public specialeffect(String specialeffecttime, String specialeffectvalue) {
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

	@Override
	public String toString() {
		return "specialeffect [specialeffecttime=" + specialeffecttime + ", specialeffectvalue=" + specialeffectvalue + "]";
	}

}
