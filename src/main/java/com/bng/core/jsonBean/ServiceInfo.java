package com.bng.core.jsonBean;

import java.util.ArrayList;

public class ServiceInfo {


	private String classname;
	private String operation ="serviceInfo";
	private ArrayList<String> keys = null;
	private boolean service ;
	private String value;
	private String calltype;
	private String servicename;
	
	public String getServicename() {
		return servicename;
	}

	public void setServicename(String servicename) {
		this.servicename = servicename;
	}

	public String getCalltype() {
		return calltype;
	}

	public void setCalltype(String calltype) {
		this.calltype = calltype;
	}
	
	public ServiceInfo (String operation, ArrayList<String> keys, boolean service, String value)
	{
		this.keys = keys;
		this.operation = operation;
		this.service = service;
		this.value = value;
	}
	
	public String getClassname() {
		return classname;
	}
	
	public void setClassname(String classname) {
		this.classname = classname;
	}
	
	public String getOperation() {
		return operation;
	}
	
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public ArrayList<String> getKeys() {
		return keys;
	}
	public void setKeys(ArrayList<String> keys) {
		this.keys = keys;
	}
	public boolean isService() {
		return service;
	}
	public void setService(boolean service) {
		this.service = service;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	
}
