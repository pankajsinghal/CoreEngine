package com.bng.core.coreCdrBean;

import java.io.Serializable;

public class MediaCdr implements Serializable{
	private String type;
	private String location;
	private String filename;
	private String starttime;
	private String endtime;
	private String code;
	
	public MediaCdr(){
		
	}
	public MediaCdr(String type, String location, String filename,
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
	
	
}
