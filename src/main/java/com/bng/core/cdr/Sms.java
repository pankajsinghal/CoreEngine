package com.bng.core.cdr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "smss")
@XmlAccessorType(XmlAccessType.FIELD)
public class Sms {

	@XmlElement(name = "mode")
	private String mode;

	@XmlElement(name = "connection")
	private String connection;
	
	@XmlElement(name = "destinationip")
	private String destinationip;

	@XmlElement(name = "destinationport")
	private String destinationport;

	@XmlElement(name = "uri")
	private String uri;
	
	@XmlElement(name = "amount")
	private String amount;

	@XmlElement(name = "calltime")
	private String calltime;
	
	@XmlElement(name = "responsetime")
	private String responsetime;

	@XmlElement(name = "response")
	private String response;

	public Sms(String mode, String connection, String destinationip,
			String destinationport, String uri, String amount, String calltime,
			String responsetime, String response) {
		super();
		this.mode = mode;
		this.connection = connection;
		this.destinationip = destinationip;
		this.destinationport = destinationport;
		this.uri = uri;
		this.amount = amount;
		this.calltime = calltime;
		this.responsetime = responsetime;
		this.response = response;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public String getDestinationip() {
		return destinationip;
	}

	public void setDestinationip(String destinationip) {
		this.destinationip = destinationip;
	}

	public String getDestinationport() {
		return destinationport;
	}

	public void setDestinationport(String destinationport) {
		this.destinationport = destinationport;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCalltime() {
		return calltime;
	}

	public void setCalltime(String calltime) {
		this.calltime = calltime;
	}

	public String getResponsetime() {
		return responsetime;
	}

	public void setResponsetime(String responsetime) {
		this.responsetime = responsetime;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
}
