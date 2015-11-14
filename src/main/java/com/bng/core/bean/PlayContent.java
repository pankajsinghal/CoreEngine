/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.bean;

import java.util.Random;

import com.bng.core.bo.BookmarkBo;
import com.bng.core.coreCdrBean.MediaCdr;
import com.bng.core.entity.Bookmark;
import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Event;
import com.bng.core.jsonBean.Play;
import com.bng.core.parser.NameValuePair;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;
/**
 *
 * @author richa
 */
public class PlayContent implements Execute{
    private String value;
    private boolean randomplay;
    private NameValuePair[] contentFile;    
    private int repeatcount;
    private String repeatcurrent;
    private String nextdtmf;
    private String previousdtmf;
    private boolean seek;   
    private String seekdtmf;
    private boolean random;
    private String addtofavDTMF;
    private String startOverDTMF;
    private String playId;
    private String startFrom;
    private String downloadDTMF;
    private String saveFileFor;
    private String saveFileDtmf;
    private static BookmarkBo bookmarkBoImpl;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public NameValuePair[] getContentFile() {
		return contentFile;
	}

	public void setContentFile(NameValuePair[] contentFile) {
		this.contentFile = contentFile;
	}

	public String getNextdtmf() {
        return nextdtmf;
    }

    public void setNextdtmf(String nextdtmf) {
        this.nextdtmf = nextdtmf;
    }

    public String getPreviousdtmf() {
        return previousdtmf;
    }

    public void setPreviousdtmf(String previousdtmf) {
        this.previousdtmf = previousdtmf;
    }

    public boolean isSeek() {
        return seek;
    }

    public void setSeek(boolean seek) {
        this.seek = seek;
    }

    public boolean isRandom() {
        return random;
    }

    public void setRandom(boolean random) {
        this.random = random;
    }

    public String getAddtofavDTMF() {
        return addtofavDTMF;
    }

    public void setAddtofavDTMF(String addtofavDTMF) {
        this.addtofavDTMF = addtofavDTMF;
    }

    public String getStartOverDTMF() {
        return startOverDTMF;
    }

    public void setStartOverDTMF(String startOverDTMF) {
        this.startOverDTMF = startOverDTMF;
    }
    
    public boolean isRandomplay() {
		return randomplay;
	}

	public void setRandomplay(boolean randomplay) {
		this.randomplay = randomplay;
	}

	public int getRepeatcount() {
		return repeatcount;
	}

	public void setRepeatcount(int repeatcount) {
		this.repeatcount = repeatcount;
	}

	public String getRepeatcurrent() {
		return repeatcurrent;
	}

	public void setRepeatcurrent(String repeatcurrent) {
		this.repeatcurrent = repeatcurrent;
	}

	public String getPlayId() {
		return playId;
	}

	public void setPlayId(String playId) {
		this.playId = playId;
	}

	public void setBookmarkBoImpl(BookmarkBo bookmarkBoImpl) {
		PlayContent.bookmarkBoImpl = bookmarkBoImpl;
	}
	
	public String getSeekdtmf() {
		return seekdtmf;
	}

	public void setSeekdtmf(String seekdtmf) {
		this.seekdtmf = seekdtmf;
	}

	public String getStartFrom() {
		return startFrom;
	}

	public void setStartFrom(String startFrom) {
		this.startFrom = startFrom;
	}

	public String getDownloadDTMF() {
		return downloadDTMF;
	}

	public void setDownloadDTMF(String downloadDTMF) {
		this.downloadDTMF = downloadDTMF;
	}

	public String getSaveFileFor() {
		return saveFileFor;
	}

	public void setSaveFileFor(String saveFileFor) {
		this.saveFileFor = saveFileFor;
	}

	public String getSaveFileDtmf() {
		return saveFileDtmf;
	}

	public void setSaveFileDtmf(String saveFileDtmf) {
		this.saveFileDtmf = saveFileDtmf;
	}

	public String execute(Event event, String qName, UserCallDetail userCallDetail)
    {    	
        Play play = null;
        String searchString = "";
        String seekBytesPlayed = "";
        NameValuePair nameValuePair = null;
        try
        {
        	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Number of files already played : "+userCallDetail.getNumberOfFilesPlayed()+", total files : "+userCallDetail.getNumberOfFileInPlay());
        	if((event.getEvent()== CoreEnums.events.E_PlayEnd.ordinal()) && 
            		(event.getSubEvent()== CoreEnums.playEndSubEvent.S_PlayStopByApp.ordinal()))
            {	
        		if(event.getTransId().substring(0, event.getTransId().indexOf("_")).equals(playId))
        		{
	        		if(seek)
	            	{
	        			if(userCallDetail.isSaveSeekFile() && !(userCallDetail.isSinglebookmark()))
	        			{
	        				nameValuePair = contentFile[userCallDetail.getNumberOfFilesPlayed()];
		            		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Play stop by core. Seek true, add the played bytes in DB");
		            		bookmarkBoImpl.addBookmark(event.getaPartyMsisdn(), Integer.parseInt(playId), nameValuePair.getFile(), event.getSeekBytes());
		            		userCallDetail.setSaveSeekFile(false);
	        			}
	            	}
	            }
	            	
	            	/*else
	            	{*/
	            		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Discard the forcefully play-end event");
	            		userCallDetail.setNextStateFlag(false);
	            		
	            	//}
        		/*}
        		else
        			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Forcefully play-end received for some other Cell. Discard the event.");
        		*/
        		return "";
            }
        	else if(event.getEvent() == CoreEnums.events.E_ChState.ordinal() && event.getSubEvent()== CoreEnums.subEvents.S_CallPending.ordinal())
        	{
        		
        	}
        	else if(event.getEvent()== CoreEnums.events.E_DTMF.ordinal())
        	{
        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Got dtfm input from user : event = "+event.getEvent()+", subEvent = "+event.getSubEvent());
        		String dtmf = "";
    			if(event.getSubEvent()== 10)
    				dtmf = "*";
                else if(event.getSubEvent()== 11)
                	dtmf = "#";
                else if(event.getSubEvent()== 12)
                	dtmf = "NoInput";
                else
                	dtmf = ""+event.getSubEvent();   
    			
    			/*DtmfCdr dtmfcdr = new DtmfCdr(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"), dtmf);
    			userCallDetail.getCallCdr().addDtmfList(dtmfcdr);
    			Logger.sysLog(LogValues.info, this.getClass().getName(), "Dtmf "+dtmf+" added to cdr");*/
    			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Save file for "+saveFileDtmf+" save file for"+saveFileFor);
    			if(dtmf.equals(nextdtmf))
    			{
    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Dtmf is nextDtmf. Increament the list by one");
    				userCallDetail.setNumberOfFilesPlayed(userCallDetail.getNumberOfFilesPlayed());
    			}
    			else if(dtmf.equals(previousdtmf))
    			{
    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Dtmf is previousdtmf. Decreament the list by one");
    				if(userCallDetail.getNumberOfFilesPlayed()-2 < 0)
    					userCallDetail.setNumberOfFilesPlayed(userCallDetail.getNumberOfFilesPlayed()-1);
    				else
    					userCallDetail.setNumberOfFilesPlayed(userCallDetail.getNumberOfFilesPlayed()-2);
    			}
    			else if(dtmf.equals(seekdtmf))
    			{
    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Dtmf is seekdtmf. Check for seek");
    				if(seek)
                	{
    					nameValuePair = contentFile[userCallDetail.getNumberOfFilesPlayed()];
    					userCallDetail.setNumberOfFilesPlayed(userCallDetail.getNumberOfFilesPlayed() - 1);
    					if(event.getCallType() == CoreEnums.callType.Incoming.ordinal())
    						play = new Play(nameValuePair.getFile(),event.getvId(),event.getIp(),repeatcount,seek,CoreEnums.playOperations.stop.ordinal(),event.getaPartyMsisdn(),seekBytesPlayed,event.getHardware(), qName,""+playId);
    					else
    						play = new Play(nameValuePair.getFile(),event.getvId(),event.getIp(),repeatcount,seek,CoreEnums.playOperations.stop.ordinal(),event.getbPartyMsisdn(),seekBytesPlayed,event.getHardware(), qName,""+playId);
    					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Seek is true. Bookmark is added in DB and play stop is sent to telephony");
                	}
                	else
                	{
                		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Seek is false, Discard the event.");
                		userCallDetail.setNextStateFlag(false);
                		return "";
                	}
    			}
    			else if(dtmf.equals(startOverDTMF))
    			{
    				userCallDetail.setNumberOfFilesPlayed(0);
    			}
    			else if(dtmf.equals(repeatcurrent))
    			{
    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Dtmf received is related to repeateCurrent.");
    				userCallDetail.setNumberOfFilesPlayed(userCallDetail.getNumberOfFilesPlayed()-1);
    			}
    			else if(dtmf.equals(downloadDTMF))
    			{
    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Dtmf received is used for download content.");
    				nameValuePair = contentFile[userCallDetail.getNumberOfFilesPlayed()-1];
    				userCallDetail.setDownloadFile(nameValuePair.getFile());
    				userCallDetail.setNextStateFlag(true);
    				userCallDetail.setExecuteCurrentState(true);
    			}
    			else if(dtmf.equals(saveFileDtmf))
    			{
    				if(saveFileFor.equalsIgnoreCase("crbt"))
    				{
	    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Dtmf received is used for crbt file selection.");
	    				nameValuePair = contentFile[userCallDetail.getNumberOfFilesPlayed()-1];
	    				userCallDetail.setCrbtFile(nameValuePair.getFile());
	    				userCallDetail.setCrbtContentId(Integer.parseInt(nameValuePair.getContentId()));
	    				userCallDetail.setNextStateFlag(true);
	    				userCallDetail.setExecuteCurrentState(true);
	    				searchString = saveFileDtmf;
    				}
    				else if(saveFileFor.equalsIgnoreCase("tasbih"))
    				{
    					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Dtmf received is used for tasbih file selection.");
    					nameValuePair = contentFile[userCallDetail.getNumberOfFilesPlayed()-1];
	    				userCallDetail.setTasbihFileSelected(nameValuePair.getFile());
	    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] File selected for tasbih = "+nameValuePair.getFile());
	    				userCallDetail.setTasbihFileId(Integer.parseInt(nameValuePair.getContentId()));
	    				userCallDetail.setNextStateFlag(true);
	    				userCallDetail.setExecuteCurrentState(true);
	    				searchString = saveFileDtmf;
	    				return searchString;
	    				
    				}
    			}
    			else
    			{
    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Doesnt have any function for "+event.getSubEvent()+" dtmf, Going to search for the next node.");
    				searchString = ""+event.getSubEvent();
    				userCallDetail.setNextStateFlag(true);
    				userCallDetail.setExecuteCurrentState(true);
    			//	userCallDetail.setNumberOfFileInPlay(0);			// @ gp
    			//	userCallDetail.setNumberOfFilesPlayed(0);		//for undefined dtmf during play files : : dont set it as 0  :no nextnode will be there
    				userCallDetail.setRepeatTasbih(false);
    				userCallDetail.setSurasNoSetFlag(false);
    				searchString = dtmf;
    				userCallDetail.setSaveSeekFile(true);
    				return searchString;
    			}
        	}
        	else if((event.getEvent()== CoreEnums.events.E_PlayEnd.ordinal()) && 
            		(event.getSubEvent()== CoreEnums.playEndSubEvent.S_PlayComplete.ordinal())) 
        	{
        		try
        		{
        			if(event.getTransId() != null || event.getTransId() != "0")
        			{
		        		Logger.sysLog(LogValues.info, this.getClass().getName(),"["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] TransId = "+event.getTransId()+", playId = "+event.getTransId().substring(0, event.getTransId().indexOf("_")));
		        		if(event.getTransId().substring(0, event.getTransId().indexOf("_")).equals(playId))
		        		{	
		        			if(isRandom())
			        		{
			        			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Random Play true,dont play next file");
				            	userCallDetail.setNumberOfFileInPlay(0);
								userCallDetail.setNumberOfFilesPlayed(0);
								userCallDetail.setNextStateFlag(true);
				            	userCallDetail.setExecuteCurrentState(true);
			        		}		        			
		        			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"] Play-end received. Process next file.");
		        			MediaCdr mediaCdr = userCallDetail.getCallCdr().getMediaCdr(event.getTransId());
		            		mediaCdr.setEndtime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
		            		userCallDetail.getCallCdr().addMediaMap(event.getTransId(), mediaCdr);
		            		userCallDetail.getCallCdr().removeMedia(event.getTransId());
		            		mediaCdr = null;
		            		if(startFrom.equalsIgnoreCase("tasbih"))
		            		{
		            			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]  Play-end received for tasbih. No more file to play.");
		            			userCallDetail.setNextStateFlag(true);
		            			userCallDetail.setExecuteCurrentState(true);
		            			return "";
		            		}
		        		}
		        		else
		        		{
		        			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Play end received for some other object. Discard the event");
			        		searchString = "";
			        		userCallDetail.setNextStateFlag(false);
			        		return searchString;
		        		}
        			}
        		}
        		catch(Exception e)
        		{
        			Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
        		}
        	}
        	else
        	{
        		userCallDetail.setNumberOfFileInPlay(contentFile.length);
        	}
        	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] startFrom = "+startFrom);
        	
        	if(!userCallDetail.isCallEndReceived())
        	{	
        		if(startFrom.equalsIgnoreCase("surah") && !(userCallDetail.isSurasNoSetFlag()))
            	{
            		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Start from is "+startFrom+", going to play file on index "+userCallDetail.getSurasNo());
            		
            		if(userCallDetail.getSurasNo() > 0)
            		{
            			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"] userCallDetail.getSurasNo() ="+userCallDetail.getSurasNo());
            			userCallDetail.setStartfromsurah(true);
            			userCallDetail.setNumberOfFilesPlayed(userCallDetail.getSurasNo() -1);
            		}
            		else
            			userCallDetail.setNumberOfFilesPlayed(userCallDetail.getSurasNo());
            		userCallDetail.setSurasNoSetFlag(true);
            		userCallDetail.setSurasNo(0);
            	}
            	else if(startFrom.equalsIgnoreCase("tasbih") && !userCallDetail.isRepeatTasbih())
            	{
            		repeatcount = userCallDetail.getNoOfTasbih();
            		userCallDetail.setRepeatTasbih(true); 
            		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Start from is "+startFrom+", Tisbah count : "+userCallDetail.getNoOfTasbih());
            		userCallDetail.setNoOfTasbih(0);
            		
            		play = new Play(userCallDetail.getTasbihFileSelected(),event.getvId(),event.getIp(),repeatcount,seek,CoreEnums.playOperations.play.ordinal(),event.getbPartyMsisdn(),seekBytesPlayed,event.getHardware(), qName, playId+"_"+userCallDetail.getTasbihFileId());
        		    SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(play));
		            Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"] Telephony event Play for Tasbih added to queue = "+event.getCoreToTelephony()+", file = "+contentFile[userCallDetail.getNumberOfFilesPlayed()]); 
		        
			        userCallDetail.setNumberOfFilesPlayed(0);	        		
			        userCallDetail.setNextStateFlag(false);
			        userCallDetail.setExecuteCurrentState(false);
			        //userCallDetail.setLastFilePlayed(nameValuePair.getFile());
			        Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Play Processed.");
			        MediaCdr media = new MediaCdr();
		        	media.setType("PlayContent");
		        	media.setLocation("Local");
		        	media.setStarttime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
		        	media.setFilename(userCallDetail.getTasbihFileSelected());
		        	media.setCode(""+userCallDetail.getTasbihFileId());
		        	
		        	userCallDetail.getCallCdr().addMediaMap(playId+"_"+userCallDetail.getTasbihFileId(),media);	
		        	return "";
            	}	      
        		
	        	if(userCallDetail.getNumberOfFilesPlayed() != userCallDetail.getNumberOfFileInPlay())
	        	{	
	        		userCallDetail.setSeek(seek);
	        		if(seek && (!userCallDetail.isSeekHandled()) && (userCallDetail.isStartfrombookmark()))
	        		{	
	        			userCallDetail.setStartfrombookmark(false);
	        			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Inside seek for first time. Going to fetch bookmark value for playId = "+playId+" and Bookmark ="+bookmarkBoImpl);
	        
	        			nameValuePair = contentFile[userCallDetail.getNumberOfFilesPlayed()];
	        			
	        			if(userCallDetail.isStartfromsurah())
	        			{
	        				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] start from surah therefor dont get bookmark");
	        				userCallDetail.setStartfromsurah(false);
	        			}
	        			else
	        			{
	        				Bookmark bookmark = bookmarkBoImpl.getSeekBytes(event.getaPartyMsisdn(), Integer.parseInt(playId));
							if(bookmark != null)
		        			{	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Found bookmark ");
		        				if(bookmark.getPromptId() == Integer.parseInt(playId))
		        				{
			        				String playFile = bookmark.getFilePlayed();
				        			seekBytesPlayed = ""+bookmark.getBytesPlayed();
				        			for(int i=0; i<contentFile.length; i++)
				        			{
				        				nameValuePair = contentFile[i];
				        				if(nameValuePair.getFile().equals(playFile))
				        				{
				        					userCallDetail.setNumberOfFilesPlayed(i);
				        					break;
				        				}
				        			}
				        		}
		        				else
		        					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Bookmark found for some other play.");
		        			}
		        			else
	    	        		{
	        					
	    	        			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Bookmark is null.");
	    	        			//nameValuePair = contentFile[userCallDetail.getNumberOfFilesPlayed()]; 
	    	        		}
		        				//userCallDetail.setSeekHandled(true);
				        		
		        			
	        			}
	        		}	
	        		else
	        		{
	        			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] userCallDetail.getNumberOfFilesPlayed() = "+userCallDetail.getNumberOfFilesPlayed());
	        			nameValuePair = contentFile[userCallDetail.getNumberOfFilesPlayed()]; 
	        			userCallDetail.setStartfromsurah(false);
	        		}
	        		
	        		if(isRandom())
		    		{   
	        			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] in random function");
	        			Random rand = new Random();
		    			int x = rand.nextInt(contentFile.length);
		    			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] in random function value of x "+x);
		    			nameValuePair = null;
		    			nameValuePair = contentFile[x];
				    } 		
	        		
        	    	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Play file = "+nameValuePair.getFile()+" ,No. of files already played = "+userCallDetail.getNumberOfFilesPlayed()+" ,Total no. of files in play = "+userCallDetail.getNumberOfFileInPlay()+", random = "+random);
        	    	if(event.getCallType() == CoreEnums.callType.Incoming.ordinal())
        	    	{
        	    		if(startFrom.equalsIgnoreCase("tasbih"))
        	    			play = new Play(userCallDetail.getTasbihFileSelected(),event.getvId(),event.getIp(),repeatcount,seek,CoreEnums.playOperations.play.ordinal(),event.getaPartyMsisdn(),seekBytesPlayed,event.getHardware(), qName, playId+"_"+userCallDetail.getTasbihFileId());
        	    		else
        	    			play = new Play(nameValuePair.getFile(),event.getvId(),event.getIp(),repeatcount,seek,CoreEnums.playOperations.play.ordinal(),event.getaPartyMsisdn(),seekBytesPlayed,event.getHardware(), qName, playId+"_"+nameValuePair.getContentId());
        	    	}
        	    	else
        	    		play = new Play(nameValuePair.getFile(),event.getvId(),event.getIp(),repeatcount,seek,CoreEnums.playOperations.play.ordinal(),event.getbPartyMsisdn(),seekBytesPlayed,event.getHardware(), qName, playId+"_"+nameValuePair.getContentId());
        		    SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(play));
		            Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event Play added to queue = "+event.getCoreToTelephony()+", file = "+contentFile[userCallDetail.getNumberOfFilesPlayed()]); 
		        
			        userCallDetail.setNumberOfFilesPlayed(userCallDetail.getNumberOfFilesPlayed()+1);	        		
			        userCallDetail.setNextStateFlag(false);
			        userCallDetail.setExecuteCurrentState(false);
			        userCallDetail.setLastFilePlayed(nameValuePair.getFile());
			        Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Play Processed.");
			        MediaCdr media = new MediaCdr();
		        	media.setType("PlayContent");
		        	media.setLocation("Local");
		        	media.setStarttime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
		        	media.setFilename(nameValuePair.getFile());
		        	media.setCode(nameValuePair.getContentId());
		        	if(startFrom.equalsIgnoreCase("tasbih"))
		        		userCallDetail.getCallCdr().addMediaMap(playId+"_"+userCallDetail.getTasbihFileId(),media);
		        	else
		        		userCallDetail.getCallCdr().addMediaMap(playId+"_"+nameValuePair.getContentId() ,media);
		        	media = null;
	        	}
	        	else
	            {
	            	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] No more files to play.");
	            	userCallDetail.setNumberOfFileInPlay(0);
					userCallDetail.setNumberOfFilesPlayed(0);
					userCallDetail.setRepeatTasbih(false);
					userCallDetail.setSurasNoSetFlag(false);
	            	if(userCallDetail.getRepeateVal() < repeatcount && !userCallDetail.isRepeatTasbih())
	            	{	
	            		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Reset all the values. Already repeat play count = "+userCallDetail.getRepeateVal()+" ,repeatcount = "+repeatcount);
	            		userCallDetail.setNextStateFlag(false);
	            		userCallDetail.setRepeateVal(userCallDetail.getRepeateVal()+1);
	            		userCallDetail.setExecuteCurrentState(true);
	            	}
	            	else
	            	{
	            		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Repeat is over.");
		            	userCallDetail.setSaveSeekFile(true);
	            		userCallDetail.setNextStateFlag(true);
		            	userCallDetail.setExecuteCurrentState(true);
	            	}
	            }
            }
        	else
        	{
        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CallEnd is already received. Do-Nothing.");
        		userCallDetail.setNextStateFlag(false);        		
        	}
        	return searchString;
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
        	return searchString;
        }        
    }
}
