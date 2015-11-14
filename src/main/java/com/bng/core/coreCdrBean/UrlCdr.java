package com.bng.core.coreCdrBean;

import java.io.Serializable;

public class UrlCdr implements Serializable{
	private String uri;
	private String method;
	private String time;
	private String response;
	private String options;
	private String type;
	private String urlRespTime;
	private String mode;

	public UrlCdr(String uri, String method, String time, String response,
			String options, String type, String urlRespTime,String mode) {
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
}
