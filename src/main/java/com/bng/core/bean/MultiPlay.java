package com.bng.core.bean;

import java.util.ArrayList;
import java.util.HashMap;

import com.bng.core.bo.BookmarkBo;
import com.bng.core.coreCdrBean.MediaCdr;
import com.bng.core.entity.Bookmark;
import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Event;
import com.bng.core.jsonBean.Play;
import com.bng.core.memCache.MemCacheJSon;
import com.bng.core.parser.NameValuePair;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

public class MultiPlay implements Execute {
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
    private String nextListDTMF;
    private String previousListDTMF;
    private static MemCacheJSon memCacheJSon;
    private static BookmarkBo bookmarkBoImpl;

    public void setMemCacheJSon(MemCacheJSon memCacheJSon) {
    	MultiPlay.memCacheJSon = memCacheJSon;
	}
    
	public  void setBookmarkBoImpl(BookmarkBo bookmarkBoImpl) {
		MultiPlay.bookmarkBoImpl = bookmarkBoImpl;
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
		Logger.sysLog(LogValues.info, this.getClass().getName(), " seek : "+seek);
	}

	public String getSeekdtmf() {
		return seekdtmf;
	}

	public void setSeekdtmf(String seekdtmf) {
		this.seekdtmf = seekdtmf;
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

	public String getPlayId() {
		return playId;
	}

	public void setPlayId(String playId) {
		this.playId = playId;
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
	
	public String getNextListDTMF() {
		return nextListDTMF;
	}
	public void setNextListDTMF(String nextListDTMF) {
		this.nextListDTMF = nextListDTMF;
	}
	
	public String getPreviousListDTMF() {
		return previousListDTMF;
	}
	
	public void setPreviousListDTMF(String previousListDTMF) {
		this.previousListDTMF = previousListDTMF;
	}

	public String execute(Event event,String qName,UserCallDetail userCallDetail)
	{
		Play play = null;
        String searchString = "";
        HashMap multiplay = new HashMap();
        NameValuePair[] contentFile = null;
        NameValuePair nameValuePair = null;
        String seekBytesPlayed = "";
		
		try
		{
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Number of files already played : "+userCallDetail.getNumberOfFilesPlayed()+", total files : "+userCallDetail.getNumberOfFileInPlay());
			if((event.getEvent()== CoreEnums.events.E_PlayEnd.ordinal()) && 
            		(event.getSubEvent()== CoreEnums.playEndSubEvent.S_PlayStopByApp.ordinal()))
            {	
        		
				if(event.getTransId().substring(0, event.getTransId().indexOf("_")).equals(playId))
        		{
	        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Discard the forcefully play-end event");
	            	userCallDetail.setNextStateFlag(false);
        		}
	            return "";
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
    			//Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Save file for "+saveFileDtmf+" save file for"+saveFileFor);
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
    					contentFile = userCallDetail.getPlaylist();
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
    			else if(dtmf.equals(nextListDTMF))
    			{
    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] current play list ="+userCallDetail.getCurrentPlayListid()+ " ,number of files in list ="+userCallDetail.getNumberOfFileInPlay());
    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Dtmf received is related to nextListDTMF.");
    				
    				multiplay = (HashMap) memCacheJSon.get("multiplay_hm_"+playId);
    				contentFile = (NameValuePair[]) multiplay.get(userCallDetail.getCurrentPlayListid()+1);
    				if(contentFile != null)
    				{
    					userCallDetail.setCurrentPlayListid(userCallDetail.getCurrentPlayListid()+1);
    					userCallDetail.setPlaylist(contentFile);
        				userCallDetail.setNumberOfFilesPlayed(0);
        				userCallDetail.setNumberOfFileInPlay(contentFile.length); 
        				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] current play list ="+userCallDetail.getCurrentPlayListid()+ " ,number of files in list ="+userCallDetail.getNumberOfFileInPlay());
        				
    				}
    				else
    				{
    					multiplay = (HashMap) memCacheJSon.get("multiplay_hm_"+playId);
        				contentFile = (NameValuePair[]) multiplay.get(1);
        				userCallDetail.setCurrentPlayListid(1);
    					userCallDetail.setPlaylist(contentFile);
        				userCallDetail.setNumberOfFilesPlayed(0);
        				userCallDetail.setNumberOfFileInPlay(contentFile.length);
    					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] NO NEXT BOOK MOVE TO FIRST");
    				}
    			}
    			else if(dtmf.equals(previousListDTMF))
    			{
    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] current play list ="+userCallDetail.getCurrentPlayListid()+ " ,number of files in list ="+userCallDetail.getNumberOfFileInPlay());
    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Dtmf received is related to previousListDTMF.");
    				if(userCallDetail.getCurrentPlayListid() > 1)
    					userCallDetail.setCurrentPlayListid(userCallDetail.getCurrentPlayListid()-1);
    				multiplay = (HashMap) memCacheJSon.get("multiplay_hm_"+playId);
    				contentFile = (NameValuePair[]) multiplay.get(userCallDetail.getCurrentPlayListid());
    				userCallDetail.setPlaylist(contentFile);
    				userCallDetail.setNumberOfFilesPlayed(0);
    				userCallDetail.setNumberOfFileInPlay(contentFile.length);
    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] current play list ="+userCallDetail.getCurrentPlayListid()+ " ,number of files in list ="+userCallDetail.getNumberOfFileInPlay());
    			}
    			else
    			{
    				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Doesnt have any function for "+event.getSubEvent()+" dtmf, Going to search for the next node.");
    				searchString = ""+event.getSubEvent();
    				userCallDetail.setNextStateFlag(true);
    				userCallDetail.setExecuteCurrentState(true);
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
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] first time in multiplay");
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] playId ="+playId+ "   get from memcache = multiplay_hm_"+playId);
				multiplay = (HashMap) memCacheJSon.get("multiplay_hm_"+playId);
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] multiplay from memcache ="+multiplay.toString());	
				contentFile = (NameValuePair[]) multiplay.get(1);
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] contentfile ="+contentFile[0].toString());
				userCallDetail.setCurrentPlayListid(1);
				userCallDetail.setPlaylist(contentFile);
				userCallDetail.setNumberOfFileInPlay(contentFile.length);
				nameValuePair = contentFile[userCallDetail.getNumberOfFilesPlayed()];
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] current play list ="+userCallDetail.getCurrentPlayListid()+ " ,number of files in list ="+userCallDetail.getNumberOfFileInPlay());
				userCallDetail.setNextStateFlag(false);
				userCallDetail.setExecuteCurrentState(false);
				
        	}
			
			
			if(!userCallDetail.isCallEndReceived())
			{
				if(userCallDetail.getNumberOfFilesPlayed() != userCallDetail.getNumberOfFileInPlay())
	        	{
					userCallDetail.setSeek(seek);
					if(userCallDetail.getBooknumber() > 0)
					{
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] START FROM BOOKNUMBER ="+userCallDetail.getBooknumber());
						userCallDetail.setCurrentPlayListid(userCallDetail.getBooknumber());
						
						multiplay = (HashMap) memCacheJSon.get("multiplay_hm_"+playId);
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] multiplay from memcache ="+multiplay.toString());
						contentFile = (NameValuePair[]) multiplay.get(userCallDetail.getCurrentPlayListid());
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] contentfile ="+contentFile.toString()+" size ="+contentFile.length);
						userCallDetail.setPlaylist(contentFile);
						userCallDetail.setNumberOfFileInPlay(contentFile.length);
						if((userCallDetail.getChapternumber() > 0) && (userCallDetail.getChapternumber() < userCallDetail.getPlaylist().length))
						{
							userCallDetail.setNumberOfFilesPlayed(userCallDetail.getChapternumber() - 1 );
							userCallDetail.setChapternumber(0);
							Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] START FROM Chaptnumber ="+userCallDetail.getChapternumber());
						}
						else
						{
							userCallDetail.setNumberOfFilesPlayed(0);
						}
						nameValuePair = contentFile[userCallDetail.getNumberOfFilesPlayed()]; 
						userCallDetail.setNextStateFlag(false);
						userCallDetail.setExecuteCurrentState(false);
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] current play list ="+userCallDetail.getCurrentPlayListid()+ " ,number of files in list ="+userCallDetail.getNumberOfFileInPlay());
						userCallDetail.setBooknumber(0);
					}
					
					if(seek && (userCallDetail.isStartfrombookmark()))
					{
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Inside seek. Going to fetch bookmark value for playId = "+playId+" and Bookmark ="+bookmarkBoImpl);
						userCallDetail.setStartfrombookmark(false);
	        			nameValuePair = contentFile[userCallDetail.getNumberOfFilesPlayed()];
	        			Bookmark bookmark = bookmarkBoImpl.getSeekBytes(event.getaPartyMsisdn(), Integer.parseInt(playId));
						if(bookmark != null)
	        			{
							Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Found bookmark ");
	        				if(bookmark.getPromptId() == Integer.parseInt(playId) )
	        				{
	        					if(bookmark.getPlaylistid() > 0 )
        						{
	        						int playlistid = bookmark.getPlaylistid();
			        				String playFile = bookmark.getFilePlayed();
				        			seekBytesPlayed = ""+bookmark.getBytesPlayed();
		        					multiplay = (HashMap) memCacheJSon.get("multiplay_hm_"+playId);
		        					if(multiplay.containsKey(playlistid))
		        					{
		        						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] HAS VALID PLAYLIST ID = "+playlistid);
		        						contentFile = (NameValuePair[]) multiplay.get(playlistid);
										userCallDetail.setCurrentPlayListid(playlistid);
										userCallDetail.setPlaylist(contentFile);
										userCallDetail.setNumberOfFileInPlay(contentFile.length);
										userCallDetail.setNextStateFlag(false);
										userCallDetail.setExecuteCurrentState(false);
										
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
		        					{
		        						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] DOES NOT HAVE VALID LIST ID  = "+playlistid);
		        					}
        						}
	        					
			        		}
	        				else
	        					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Bookmark found for some other play.");
	        			}
	        			else
    	        		{
        					
    	        			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Bookmark is null.");
    	        			contentFile = userCallDetail.getPlaylist();
    	        			nameValuePair = contentFile[userCallDetail.getNumberOfFilesPlayed()];
    	        			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] nameValuePair = "+nameValuePair.toString());
    	        		}
						
					}
					else
	        		{
	        			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Number of files played = "+userCallDetail.getNumberOfFilesPlayed());
	        			contentFile = userCallDetail.getPlaylist();
	        			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] contentFile  size= "+contentFile.length );
	        			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] contentFile  next file = "+contentFile[userCallDetail.getNumberOfFilesPlayed()].getFile() );
	        			
	        			nameValuePair = contentFile[userCallDetail.getNumberOfFilesPlayed()];
	        			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] nameValuePair = "+nameValuePair.toString());
	        		}
	        	}
				else
				{
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] First List over");
					multiplay = (HashMap) memCacheJSon.get("multiplay_hm_"+playId);
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] current play list id ="+userCallDetail.getCurrentPlayListid()+" , Total number of lista ="+multiplay.size());
					
					if(userCallDetail.getCurrentPlayListid() < multiplay.size())
					{
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Has more List");
						userCallDetail.setCurrentPlayListid(userCallDetail.getCurrentPlayListid()+1);
						contentFile = (NameValuePair[]) multiplay.get(userCallDetail.getCurrentPlayListid());
						userCallDetail.setPlaylist(contentFile);
						userCallDetail.setNumberOfFileInPlay(contentFile.length);
						userCallDetail.setNumberOfFilesPlayed(0);
						nameValuePair = contentFile[userCallDetail.getNumberOfFilesPlayed()];
						
					}
					else
					{
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] No more Lists to play");
						userCallDetail.setNumberOfFileInPlay(0);
						userCallDetail.setNumberOfFilesPlayed(0);
						userCallDetail.setSaveSeekFile(true);
	            		userCallDetail.setNextStateFlag(true);
		            	userCallDetail.setExecuteCurrentState(true);
		            	return searchString;
					}
				}
				
				play = new Play(nameValuePair.getFile(),event.getvId(),event.getIp(),repeatcount,seek,CoreEnums.playOperations.play.ordinal(),event.getbPartyMsisdn(),seekBytesPlayed,event.getHardware(), qName, playId+"_"+nameValuePair.getContentId());
				SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(play));
		        Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event Play added to queue = "+event.getCoreToTelephony()); 
		        Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]  current playlist id = "+userCallDetail.getCurrentPlayListid());
				
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
	        	userCallDetail.getCallCdr().addMediaMap(playId+"_"+nameValuePair.getContentId() ,media);
	        	userCallDetail.setSaveSeekFile(true);
	        	media = null;
			}
			else
        	{
        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CallEnd is already received. Do-Nothing.");
        		userCallDetail.setNextStateFlag(false);        		
        	}
		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
		}
		return "";
		
	}
}
