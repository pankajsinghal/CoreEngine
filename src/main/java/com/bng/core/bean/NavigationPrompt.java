/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.bean;

import com.bng.core.coreCdrBean.MediaCdr;
import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Event;
import com.bng.core.jsonBean.Play;
import com.bng.core.parser.NameValuePair;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.queue.Timer;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

/**
 *
 * @author richa
 */
public class NavigationPrompt implements Execute{
    private String value;
    private NameValuePair[] Promptfile;
    private boolean bragein;
    private int timeOut;
    private int repeatcount ;
    
    private String cellId;
    
    public String getCellId() {
		return cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}

	public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public NameValuePair[] getPromptFile() {
        return Promptfile;
    }

    public void setPromptFile(NameValuePair[] Promptfile) {
        this.Promptfile = Promptfile;
    }

    public boolean isBragein() {
        return bragein;
    }

    public void setBragein(boolean bragein) {
        this.bragein = bragein;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }
    
    public int getRepeatcount() {
		return repeatcount;
	}

	public void setRepeatcount(int repeatcount) {
		this.repeatcount = repeatcount;
	}

	public String execute(Event event, String qName, UserCallDetail userCallDetail)
    {
        boolean seek = false;
        String seekBytesPlayed = "";
        Play play = null;
        String searchstring = "";
        String promptFile = "";
        String promptFileId = "";    
        
        try
        {
        	if((event.getEvent()== CoreEnums.events.E_PlayEnd.ordinal()) && 
            		(event.getSubEvent()== CoreEnums.playEndSubEvent.S_PlayComplete.ordinal()))
            {
        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]TransId = "+event.getTransId()+", cellId = "+event.getTransId().substring(0, event.getTransId().indexOf("_")));
        		if(event.getTransId().substring(0, event.getTransId().indexOf("_")).equals(cellId))
        		{
	            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]NP play end. TimeOut in NP = "+userCallDetail.getTimeOutForNP());
	        		if(!userCallDetail.isNpPlayComplete())
	        		{
	        			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Starting timer for No-Input.");
		            	event.setEvent(CoreEnums.events.E_DTMF.ordinal());
		        		event.setSubEvent(12); // For NoInput 
		        		event.setSource("timer");
		        		String timerId = Timer.startTimer(event, userCallDetail.getTimeOutForNP(), qName);	        		
		        		userCallDetail.setNpTimerId(timerId);
		        		userCallDetail.setNpTimerFlag(true);
		        		userCallDetail.setNpPlayComplete(true);
	        		}
	        		MediaCdr media = userCallDetail.getCallCdr().getMediaCdr(event.getTransId());//new MediaCdr("Navigation","Local",userCallDetail.getNpFile(),userCallDetail.getNpStartTime(), Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"),userCallDetail.getNpContentId());
	        		media.setEndtime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
	        		userCallDetail.getCallCdr().addMediaMap(event.getTransId(), media);
	        		userCallDetail.getCallCdr().removeMedia(event.getTransId());
	        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Timer started. id = "+userCallDetail.getNpTimerId());
        		}
        		else
        		{
        			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Play end received for some other object. Discard the event");
        		}
            }
            else if(event.getEvent()== CoreEnums.events.E_DTMF.ordinal())
            {
            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Event is DTMF input, value = "+event.getSubEvent()+", source="+event.getSource());
            	/*if(event.getvId() == userCallDetail.getbPartyVId())
            	{
            		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] DTMF is received from bParty. Discard the DTMF.");
            		userCallDetail.setNextStateFlag(false);
            	}
            	else
            	{*/
	            	Logger.sysLog(LogValues.info, this.getClass().getName(),"Bargein = "+bragein+" , isNpPlayComplete = "+userCallDetail.isNpPlayComplete());
	            	if((bragein) || (userCallDetail.isNpPlayComplete()))
	            	{
	            		Logger.sysLog(LogValues.info, this.getClass().getName(),"Accept dtmf input.");
		            	if(userCallDetail.getNpTimerId() != null)
		            	{
		            		int timerInterval = Timer.stopTimer(Long.parseLong(userCallDetail.getNpTimerId()));
		            		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]DTMF Event timer interval  = "+timerInterval+" , timer id = "+userCallDetail.getNpTimerId());
		            		userCallDetail.setNpTimerId(null);
		            		userCallDetail.setNpTimerFlag(false);
		            	}
		                if(event.getSubEvent()== 10)
		                    searchstring = "*";
		                else if(event.getSubEvent()== 11)
		                    searchstring = "#";
		                else if(event.getSubEvent()== 12)
		                {
		                	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]repeat count in ucd = "+userCallDetail.getNprepeatcount());
		                	if(userCallDetail.getNprepeatcount() > 0)
		                	{
		                		 for(int i =0; i<Promptfile.length; i++)
		     			        {
		     			        	NameValuePair nameValuePair = Promptfile[i];
		     			        	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]promptFile = "+nameValuePair.getFile()+", lang in userCallDetail = "+userCallDetail.getLanguage());
		     			        	if(nameValuePair.getFile().endsWith(userCallDetail.getLanguage()+".wav"))
		     			        	{
		     			        		promptFile = nameValuePair.getFile();
		     			        		promptFileId = nameValuePair.getContentId();
		     			        		break;
		     			        	}
		     			        }
		     			        if(!promptFile.equals(""))
		     			        {
		     			        	play = new Play(promptFile,event.getvId(),event.getIp(),0,seek,CoreEnums.playOperations.play.ordinal(),event.getaPartyMsisdn(),seekBytesPlayed,event.getHardware(), qName ,cellId+"_"+promptFileId);
		     			        	SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(play));
		     			        	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Telephony event NP added to queue = "+event.getCoreToTelephony());
		     			        	//userCallDetail.setNpFile(promptFile);
		     			        	//userCallDetail.setNpContentId(promptFileId);
		     			        	MediaCdr media = new MediaCdr();
		     			        	media.setType("Navigation");
		     			        	media.setLocation("Local");
		     			        	media.setStarttime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
		     			        	media.setFilename(promptFile);
		     			        	media.setCode(promptFileId);
		     			        	//MediaCdr media = new MediaCdr("Navigation","Local",userCallDetail.getNpFile(),userCallDetail.getNpStartTime(), Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"),userCallDetail.getNpContentId());
		     		        		userCallDetail.getCallCdr().addMediaMap(cellId+"_"+promptFileId ,media);
		     		        		media = null;
		     			        }
		     			        else
		     			        	Logger.sysLog(LogValues.error, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]NP doesnt have any file for selected language. = "+userCallDetail.getLanguage());
		     		       
		     			        userCallDetail.setNprepeatcount(userCallDetail.getNprepeatcount() - 1);
		     			        userCallDetail.setNextStateFlag(false);
		     			        userCallDetail.setExecuteCurrentState(false);
		     			        userCallDetail.setNpPlayComplete(false);
		     			        
		     			        return "" ;
		                	}
		                	else
		                		searchstring = "NoInput";
		                }
		                else
		                    searchstring = ""+event.getSubEvent();                
		                
		                userCallDetail.setNextStateFlag(true);
				        userCallDetail.setExecuteCurrentState(true);
				        userCallDetail.setNpPlayComplete(false);
		                Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]stored current state = "+userCallDetail.getCurrentState()+" , searchString = "+searchstring);
		            }
	            	else
	            	{
	            		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Bargein = "+bragein+", so discard the event.");
	            		userCallDetail.setNextStateFlag(false);
	            		userCallDetail.setExecuteCurrentState(false);
	            	} 
            	//}
            }
            else if((event.getEvent()== CoreEnums.events.E_PlayEnd.ordinal()) && 
            		(event.getSubEvent()== CoreEnums.playEndSubEvent.S_PlayStopByApp.ordinal()))
            {
            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Forcefully play-end event received in NP, Discard the event");
            	userCallDetail.setNextStateFlag(false);
            	userCallDetail.setExecuteCurrentState(false); 
            	//MediaCdr media = new MediaCdr("Navigation","Local",userCallDetail.getNpFile(),userCallDetail.getNpStartTime(), Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"),userCallDetail.getNpContentId());
        		//userCallDetail.getCallCdr().addMediaList(media);
            }
            else
            {        	
            	Logger.sysLog(LogValues.info, this.getClass().getName(),"Promptfile.length = "+Promptfile.length);
            	Logger.sysLog(LogValues.info, this.getClass().getName()," repeatcount= "+repeatcount);
            	userCallDetail.setNprepeatcount(repeatcount - 1);
            	if(Promptfile.length > 0)
		        {
			        for(int i =0; i<Promptfile.length; i++)
			        {
			        	NameValuePair nameValuePair = Promptfile[i];
			        	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]promptFile = "+nameValuePair.getFile()+", lang in userCallDetail = "+userCallDetail.getLanguage());
			        	if(nameValuePair.getFile().endsWith(userCallDetail.getLanguage()+".wav"))
			        	{
			        		promptFile = nameValuePair.getFile();
			        		promptFileId = nameValuePair.getContentId();
			        		break;
			        	}
			        }
			        if(!promptFile.equals(""))
			        {
			        	play = new Play(promptFile,event.getvId(),event.getIp(),0,seek,CoreEnums.playOperations.play.ordinal(),event.getaPartyMsisdn(),seekBytesPlayed,event.getHardware(), qName ,cellId+"_"+promptFileId);
			        	SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(play));
			        	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Telephony event NP added to queue = "+event.getCoreToTelephony());
			        	//userCallDetail.setNpFile(promptFile);
			        	//userCallDetail.setNpContentId(promptFileId);
			        	MediaCdr media = new MediaCdr();
			        	media.setType("Navigation");
			        	media.setLocation("Local");
			        	media.setStarttime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
			        	media.setFilename(promptFile);
			        	media.setCode(promptFileId);
			        	//MediaCdr media = new MediaCdr("Navigation","Local",userCallDetail.getNpFile(),userCallDetail.getNpStartTime(), Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"),userCallDetail.getNpContentId());
		        		userCallDetail.getCallCdr().addMediaMap(cellId+"_"+promptFileId ,media);
		        		media = null;
			        }
			        else
			        	Logger.sysLog(LogValues.error, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]NP doesnt have any file for selected language. = "+userCallDetail.getLanguage());
		        }
		        userCallDetail.setNextStateFlag(false);
		        userCallDetail.setTimeOutForNP(timeOut);
		        //userCallDetail.setNpStartTime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));	
		    }
        	if(userCallDetail.isNextStateFlag())
        		userCallDetail.setNprepeatcount(0);
        	return searchstring;
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.error, NavigationPrompt.class.getName(), coreException.GetStack(e));
        	return "";
        }
        finally
        {
        	play = null;
        	seekBytesPlayed = null;
        }        
    }
}
