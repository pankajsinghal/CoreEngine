package com.bng.core.parser;

import java.io.Serializable;

public class NameValuePair implements Serializable{
	private String file;
	private String contentId;
	private String downloadUrl;
	private String crbtUrl;
	
	public NameValuePair(String file, String contentId) {
		this.file = file;
		this.contentId = contentId;
	}
	
	public NameValuePair(String file, String contentId, String downloadUrl, String crbtUrl) {
		super();
		this.file = file;
		this.contentId = contentId;
		this.downloadUrl = downloadUrl;
		this.crbtUrl = crbtUrl;
	}
	
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getContentId() {
		return contentId;
	}
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getCrbtUrl() {
		return crbtUrl;
	}

	public void setCrbtUrl(String crbtUrl) {
		this.crbtUrl = crbtUrl;
	}
}
