package com.bng.core.cdr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "play")
@XmlAccessorType(XmlAccessType.FIELD)
public class Media {
	
	@XmlElement(name = "type")
	private String type;

	@XmlElement(name = "location")
	private String location;
	
	@XmlElement(name = "filename")
	private String filename;

	@XmlElement(name = "starttime")
	private String starttime;

	@XmlElement(name = "endtime")
	private String endtime;

	@XmlElement(name = "code")
	private String code;
	
	public Media(){
		
	}
	
	public Media(String type, String location, String filename,
			String starttime, String endtime, String code) {
		super();
		this.type = type;
		this.location = location;
		this.filename = filename;
		this.starttime = starttime;
		this.endtime = endtime;
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "Media [type=" + type + ", location=" + location + ", filename="
				+ filename + ", starttime=" + starttime + ", endtime="
				+ endtime + ", code=" + code + "]";
	}
	
	
}
