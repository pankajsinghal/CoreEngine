package com.bng.core.bean;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.bng.core.bo.OBDNumberBo;
import com.bng.core.coreCdrBean.MediaCdr;
import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Event;
import com.bng.core.jsonBean.Play;
import com.bng.core.parser.NameValuePair;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.SimpleParser;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

public class DynamicPlay implements Execute{
	private String url="";
	private static OBDNumberBo obdnumberbo;
	private static String gapminutes;
	

	public  void setGapminutes(String gapminutes) {
		DynamicPlay.gapminutes = gapminutes;
	}
	public void setObdnumberbo(OBDNumberBo obdnumberbo) {
		DynamicPlay.obdnumberbo = obdnumberbo;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}


	public String execute(Event event,String qName,UserCallDetail userCallDetail)
    {
		Play play = null;
		String seekBytesPlayed = "";
		String searchString = "";
		SimpleParser sp = null;
		String urlresp ="";
		String Filelist[] =null;
		
		
		try{
			
			if(!userCallDetail.isDynamicplaystarted())
			 {
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] GET url = "+url);
				 sp = new SimpleParser(url);
				 Properties prop = Utility.convertUCDToProp(userCallDetail);
					url = sp.parse(prop);
					url = url.replaceAll("#", "&");
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Formatted GET url = "+url);
					urlresp = Utility.callUrl(url);
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] urlresp ="+urlresp);
					Filelist = urlresp.split(",");
					userCallDetail.setDynamicplaylist(Filelist);
					for(int i=0;i<Filelist.length;i++)
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"] Filelist ="+Filelist[i]);
										
					if(event.getCallType() == CoreEnums.callType.Incoming.ordinal())
						play = new Play(Filelist[0],event.getvId(),event.getIp(),0,false,CoreEnums.playOperations.play.ordinal(),event.getaPartyMsisdn(),seekBytesPlayed,event.getHardware(), qName, 0+"_"+0);
					else 
						play = new Play(Filelist[0],event.getvId(),event.getIp(),0,false,CoreEnums.playOperations.play.ordinal(),event.getbPartyMsisdn(),seekBytesPlayed,event.getHardware(), qName, 0+"_"+0);
					SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(play));
		            Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"] Telephony event Play  added to queue = "+event.getCoreToTelephony()); 
		        
		            MediaCdr media = new MediaCdr();
		        	media.setType("recorddedication");
		        	media.setLocation("Local");
		        	media.setStarttime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
		        	media.setFilename(Filelist[0]);
		        	media.setCode(""+0);
		        	userCallDetail.getCallCdr().addMediaMap(0+"_"+0,media);
					userCallDetail.setNextStateFlag(false);
    				userCallDetail.setExecuteCurrentState(false);
    				userCallDetail.setDynamicplaystarted(true);
    				return searchString = "";
					
			 }
			
			if((event.getEvent()== CoreEnums.events.E_PlayEnd.ordinal()) && 
            		(event.getSubEvent()== CoreEnums.playEndSubEvent.S_PlayStopByApp.ordinal())) 
        	{
        	
				Filelist = userCallDetail.getDynamicplaylist();
    			if(event.getTransId() != null || event.getTransId() != "0")
    			{
    				boolean rescheduled = false;
					Date currenttime = new java.util.Date();
    				Logger.sysLog(LogValues.info, this.getClass().getName(),"["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] TransId = "+event.getTransId()+", playId = "+event.getTransId().substring(0, event.getTransId().indexOf("_")));
    			
    				MediaCdr mediaCdr = userCallDetail.getCallCdr().getMediaCdr(event.getTransId());
    				if(mediaCdr != null)
    					Logger.sysLog(LogValues.info, this.getClass().getName(),"["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] mediaCdr ="+mediaCdr+ ", mediaCdr.getFilename() ="+mediaCdr.getFilename());
    				
    				if(!userCallDetail.isDynamicplayover())
    				{
    					ResultSet resultset = obdnumberbo.getAandBpartylist(userCallDetail.getaPartyMsisdn(),userCallDetail.getbPartyMsisdn(),userCallDetail.getObdTable());
    					try{
    						if(resultset.last())
    						{
    							Logger.sysLog(LogValues.info, this.getClass().getName(),"for resultset element : cli ="+resultset.getString("cli")+" ,msisdn ="+resultset.getString("msisdn")+", endtime ="+resultset.getString("endtime"));
    							Logger.sysLog(LogValues.info, this.getClass().getName()," found last row ="+resultset.last()+" with calltime ="+resultset.getString("calltime"));
    							String sDate = resultset.getString("calltime");
    							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    							Date dDate = sdf.parse(sDate);
    							long time3 = dDate.getTime() + 1000*60*(Integer.parseInt(gapminutes));
    							Date calltimenew = new java.util.Date(time3);
    							
    							Logger.sysLog(LogValues.info, this.getClass().getName(),"NEW CALL TIME BEING INSERTED calltimenew= "+calltimenew);
    							rescheduled = obdnumberbo.reschedule(userCallDetail.getaPartyMsisdn(), userCallDetail.getbPartyMsisdn(), dDate, userCallDetail.getObdTable());
    							
    							
    						}
    						else
    						{
    							Logger.sysLog(LogValues.info, this.getClass().getName(),"resultset.last() ="+resultset.last() );
    							Logger.sysLog(LogValues.info, this.getClass().getName(),"NEW CALL TIME BEING INSERTED currenttime= "+currenttime);
    							rescheduled = obdnumberbo.reschedule(userCallDetail.getaPartyMsisdn(), userCallDetail.getbPartyMsisdn(), currenttime, userCallDetail.getObdTable());
    						}
    					}
    					catch(Exception e)
    					{
    						Logger.sysLog(LogValues.error, this.getClass().getName(), "Caught exception while resheduling records whose call failed");
    						Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
    					}
    				}
            		mediaCdr.setEndtime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
            		userCallDetail.getCallCdr().addMediaMap(event.getTransId(), mediaCdr);
            		userCallDetail.getCallCdr().removeMedia(event.getTransId());
            		mediaCdr = null;
            		
	        	}
	        			
        	}
			
			if((event.getEvent()== CoreEnums.events.E_PlayEnd.ordinal()) && 
            		(event.getSubEvent()== CoreEnums.playEndSubEvent.S_PlayComplete.ordinal())) 
        	{
				boolean deletefile = false;
				Filelist = userCallDetail.getDynamicplaylist();
        			if(event.getTransId() != null || event.getTransId() != "0")
        			{
		        		Logger.sysLog(LogValues.info, this.getClass().getName(),"["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] TransId = "+event.getTransId()+", playId = "+event.getTransId().substring(0, event.getTransId().indexOf("_")));
		        		Logger.sysLog(LogValues.info, this.getClass().getName(),"["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Filelist.length ="+ Filelist.length);
		        		for(int i=0;i< Filelist.length;i++)
		        		{
		        			Logger.sysLog(LogValues.info, this.getClass().getName(),"["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Inside for loop where i ="+i);
		        			
		        			if(event.getTransId().substring(event.getTransId().indexOf("_")+1,event.getTransId().length()).equals(""+i))
		        			{
		        				
		        				
		        				MediaCdr mediaCdr = userCallDetail.getCallCdr().getMediaCdr(event.getTransId());
		        				if(mediaCdr != null)
		        					Logger.sysLog(LogValues.info, this.getClass().getName(),"["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] mediaCdr ="+mediaCdr+ ", mediaCdr.getFilename() ="+mediaCdr.getFilename());
		        				//Logger.sysLog(LogValues.info, this.getClass().getName(),"["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]   Filelist ="+Filelist[i]);
		        				if(userCallDetail.getObdjobtype().equalsIgnoreCase("recorddedication"))
		        				{
		        					if(userCallDetail.getObdTable() != null)
		        					{
		        						Logger.sysLog(LogValues.info, this.getClass().getName(),"["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] userCallDetail.getObdTable() ="+userCallDetail.getObdTable());
		        						if(mediaCdr != null)
		        							deletefile = obdnumberbo.deleterecord(mediaCdr.getFilename(), userCallDetail.getObdTable());	
		        						else if(Filelist[i] != null)
		        						{
		        							deletefile = obdnumberbo.deleterecord(Filelist[i], userCallDetail.getObdTable());	
		        							
		        						}
		        						Logger.sysLog(LogValues.info, this.getClass().getName(),"["+event.getvId()+"]["+event.getaPartyMsisdn()+"]  deletefile ="+deletefile);
		        					}
		        						        						
		        				}
		        				
			            		mediaCdr.setEndtime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
			            		userCallDetail.getCallCdr().addMediaMap(event.getTransId(), mediaCdr);
			            		userCallDetail.getCallCdr().removeMedia(event.getTransId());
			            		mediaCdr = null;
			            		
			            		i++;
			            		if(i < Filelist.length)
			            		{
			            			Logger.sysLog(LogValues.info, this.getClass().getName(),"["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] send next file");
			            			if(event.getCallType() == CoreEnums.callType.Incoming.ordinal())
			        					play = new Play(Filelist[i],event.getvId(),event.getIp(),0,false,CoreEnums.playOperations.play.ordinal(),event.getaPartyMsisdn(),seekBytesPlayed,event.getHardware(), qName, 0+"_"+i);
			        				else 
			        					play = new Play(Filelist[i],event.getvId(),event.getIp(),0,false,CoreEnums.playOperations.play.ordinal(),event.getbPartyMsisdn(),seekBytesPlayed,event.getHardware(), qName, 0+"_"+i);
			        				SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(play));
			    		            Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"] Telephony event Play for Tasbih added to queue = "+event.getCoreToTelephony()); 
			    		        
			    		            MediaCdr media = new MediaCdr();
			    		        	media.setType("recorddedication");
			    		        	media.setLocation("Local");
			    		        	media.setStarttime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
			    		        	media.setFilename(Filelist[i]);
			    		        	media.setCode(""+i);
			    		        	userCallDetail.getCallCdr().addMediaMap(0+"_"+i,media);
			        				userCallDetail.setNextStateFlag(false);
			        				userCallDetail.setExecuteCurrentState(false);
			        				return searchString = "";
			        				
			            		}
			            		else
			            		{
			            			Logger.sysLog(LogValues.info, this.getClass().getName(),"["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] files over");
			        				userCallDetail.setDynamicplayover(true);
			            			userCallDetail.setNextStateFlag(true);
			        				userCallDetail.setExecuteCurrentState(true);
			        				return searchString = "";
			            		}
		        				
		        			}
		        			else
		        				Logger.sysLog(LogValues.info, this.getClass().getName(),"["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] play end for some other file");
		        			
		        		}
        			}
		
        	}
			
			
			
    
		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, this.getClass().getSimpleName(), coreException.GetStack(e));
		}
		return "";
	}
}
