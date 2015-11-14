/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.bean;

import java.net.InetAddress;
import java.util.Date;
import java.util.Properties;

import com.bng.core.bo.CircleBo;
import com.bng.core.coreCdrBean.CallCdr;
import com.bng.core.entity.CircleMapper;
import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Call;
import com.bng.core.jsonBean.Event;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.queue.Timer;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.SimpleParser;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

/**
 *
 * @author richa
 */
public class Start implements Execute{
    private String calltype;
    private String shortcode;
    private String service;
    private int exittimer;
    private int recording;
    private String defaultLang;
    private String numberDealing;
    private boolean autoAnswer;
    private boolean singleBookmark;
    private static CircleBo circleBoImpl;
    private static String recordFilePath;
    private static int transactionidlength;
    
    public void setCircleBoImpl(CircleBo circleBoImpl) {
    	Start.circleBoImpl = circleBoImpl;
	}

	public String getShortcode() {
        return shortcode;
    }

    public void setShortcode(String shortcode) {
        this.shortcode = shortcode;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public int getRecording() {
        return recording;
    }

    public void setRecording(int recording) {
        this.recording = recording;
    }    

    public void setDefaultLang(String defaultLang) {
		this.defaultLang = defaultLang;
	}

	public String getDefaultLang() {
		return defaultLang;
	}

	
	public boolean isAutoAnswer() {
		return autoAnswer;
	}

	public void setAutoAnswer(boolean autoAnswer) {
		this.autoAnswer = autoAnswer;
	}

	public String getCalltype() {
		return calltype;
	}

	public void setCalltype(String calltype) {
		this.calltype = calltype;
	}

	public int getExittimer() {
		return exittimer;
	}

	public void setExittimer(int exittimer) {
		this.exittimer = exittimer;
	}
	
	public void setRecordFilePath(String recordFilePath) {
		Start.recordFilePath = recordFilePath;
	}

	public String getNumberDealing() {
		return numberDealing;
	}

	public void setNumberDealing(String numberDealing) {
		this.numberDealing = numberDealing;
	}
	
	public boolean isSingleBookmark() {
		return singleBookmark;
	}

	public void setSingleBookmark(boolean singleBookmark) {
		this.singleBookmark = singleBookmark;
	}

	public void setTransactionidlength(int transactionidlength) {
		Start.transactionidlength = transactionidlength;
	}

	public String execute(Event event, String qName, UserCallDetail userCallDetail)
    {
    	boolean recordingFlag = false;
    	Call call;
    	CircleMapper circleMapper = null;
    	String finalrecordfilepath = "";
    	Date currentdate = new Date();
    	SimpleParser sp = null;
    	String transid = "";
    	try
    	{
    		userCallDetail.setLanguage(defaultLang);
    		userCallDetail.setService(service);
    		userCallDetail.setRecording(recording);
    		userCallDetail.setSinglebookmark(singleBookmark);
    		if(event.getCallType() == CoreEnums.callType.Incoming.ordinal())
    			 transid = Utility.generatetransid(transactionidlength, event.getaPartyMsisdn(), "yyyyMMddHHmmssSSS");
    		else
    			 transid = Utility.generatetransid(transactionidlength, event.getbPartyMsisdn(), "yyyyMMddHHmmssSSS");
    		userCallDetail.setTransactionid(transid);
    		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] transactionid  ="+userCallDetail.getTransactionid());
    	
    		if(event.getCallType() == CoreEnums.callType.Incoming.ordinal())
    		{
    			if(isAutoAnswer())
		    	{
    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] recording % from startnode ="+recording);
		    		if(recording > 0)
	    				recordingFlag = Utility.getPercentage(recording);
		    		
		    		if(recordingFlag)
		    		{
		    			Properties prop = Utility.convertUCDToProp(userCallDetail);
						sp = new SimpleParser(recordFilePath);
						String filepath = sp.parse(prop);
		    			finalrecordfilepath = filepath+"record_"+event.getaPartyMsisdn()+"_"+Utility.getCurrentDate("yyyy-MM-dd_HH-mm-ss.SSS")+".wav";
		    		}
	    			
		    		call = new Call(CoreEnums.callOperations.pickup.ordinal(),event.getvId(),recordingFlag,event.getaPartyMsisdn(),finalrecordfilepath,event.getHardware(), qName);
		    		SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(call));
		    		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event call added to queue = "+event.getCoreToTelephony());
		    		userCallDetail.setNextStateFlag(true);
		    	}
		    	else
		    	{
		    		userCallDetail.setNextStateFlag(true);
		    		userCallDetail.setExecuteCurrentState(true);
		    		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Auto-answer is off. So execute next state.");
		    	}		    	     
	    	}
    		else if(event.getCallType() == CoreEnums.callType.Outgoing.ordinal())
    		{
    			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Event is start obd.");
    			userCallDetail.setNextStateFlag(true);
    			userCallDetail.setExecuteCurrentState(true);
    		}
    		
    		if(exittimer > 0)
    		{
    			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Exittimer interval: "+exittimer);
    			event.setEvent(CoreEnums.events.DEFAULT.ordinal());
	    		event.setSubEvent(CoreEnums.subEvents.MasterTimerExp.ordinal()); // For NoInput   
	    		String timerId = Timer.startTimer(event, exittimer, qName);
	    		userCallDetail.setMasterTimerId(timerId);
	    		userCallDetail.setMasterTimerFlag(true);
	    		userCallDetail.setMasterTimerInterval(exittimer);
	    		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Master timer started. Exittimer interval: "+exittimer);
	    		timerId = null;
    		}
    		
    		if(event.getCallType() == CoreEnums.callType.Incoming.ordinal())
    		{
    			if(circleBoImpl.getCircleInfo(event.getaPartyMsisdn()) != null)
    				circleMapper = circleBoImpl.getCircleInfo(event.getaPartyMsisdn());
    		}
    			
        	else
        	{
        		if(circleBoImpl.getCircleInfo(event.getbPartyMsisdn()) != null)
        			circleMapper = circleBoImpl.getCircleInfo(event.getbPartyMsisdn());
        	}
        		

    		CallCdr callCdr = new CallCdr();
    		try
    		{
    			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Going to create callCdr.");
	    		callCdr.setTpsystemip(event.getIp());
	    		callCdr.setCesystemip(InetAddress.getLocalHost().getHostAddress());
	    		callCdr.setHardware(CoreEnums.hardware.values[event.getHardware()].toString());
	    		callCdr.setProtocol(event.getProtocol());
	    		callCdr.setCalltype(CoreEnums.callType.values[event.getCallType()].toString());
	    		callCdr.setCallid(event.getvId());    		
	    		callCdr.setAparty(event.getaPartyMsisdn());
	    		//callCdr.setBparty(event.getbPartyMsisdn());
	    		callCdr.setShortcode(userCallDetail.getShortCode());
	    		callCdr.setBparty(userCallDetail.getbPartyMsisdn());
	    		callCdr.setServicename(userCallDetail.getService());	    		
	    		callCdr.setStartdatetime(userCallDetail.getCallStartTime()); 
	    		//callCdr.setProtocol(event.getProtocol());
	    		callCdr.setDirectcall(userCallDetail.getDirectCall());
	    		if(circleMapper != null)
	    		{
		    		callCdr.setTimeZone(circleMapper.getTimeZone());
		    		callCdr.setCountry(circleMapper.getCountryKey());
		    		callCdr.setOperator(circleMapper.getOperator());
		    		callCdr.setCircle(circleMapper.getCircleKey());
	    		}
	    		userCallDetail.setCallCdr(callCdr);   
	    		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CallCDR is created n saved in userCallDetail.");
	    		callCdr = null;
    		}
    		catch(Exception ex)
    		{
    			userCallDetail.setCallCdr(callCdr); 
    			Logger.sysLog(LogValues.error, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Error creating CallCdr."+"\n"+coreException.GetStack(ex));
        		callCdr = null;
    		}
    	}
    	catch(Exception e)
    	{
    		Logger.sysLog(LogValues.error, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Error adding call event to telephony queue"+"\n"+coreException.GetStack(e));
    	}
    	finally
    	{
    		call = null;
    	}
        return null;
    }
}
