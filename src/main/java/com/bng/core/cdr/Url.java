package com.bng.core.cdr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "urls")
@XmlAccessorType(XmlAccessType.FIELD)
public class Url {

	@XmlElement(name = "uri")
	private String uri;
	
	@XmlElement(name = "method")
	private String method;
	
	@XmlElement(name = "time")
	private String time;
	
	@XmlElement(name = "response")
	private String response;
	
	@XmlElement(name = "options")
	private String options;
	
	@XmlElement(name = "type")
	private String type;
	
	@XmlElement(name="urlRespTime")
	private String urlRespTime;
	
	@XmlElement(name="mode")
	private String mode;
	
	public Url(){
		
	}
	
	public Url(String uri, String method, String time, String response,
			String options,String type,String urlRespTime,String mode) {
		super();
		this.uri = uri;
		this.method = method;
		this.time = time;
		this.response = response;
		this.options = options;
		this.type = type;
		this.urlRespTime = urlRespTime;
		this.mode = mode;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrlRespTime() {
		return urlRespTime;
	}

	public void setUrlRespTime(String urlRespTime) {
		this.urlRespTime = urlRespTime;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return "Url [uri=" + uri + ", method=" + method + ", time=" + time
				+ ", response=" + response + ", options=" + options + ", urlRespTime="+urlRespTime+", mode="+mode+"]";
	}
	
	
	
}
