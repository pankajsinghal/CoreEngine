package com.bng.core.bean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bng.core.coreCdrBean.DtmfCdr;
import com.bng.core.coreCdrBean.MediaCdr;
import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.DigitCollection;
import com.bng.core.jsonBean.Event;
import com.bng.core.jsonBean.Play;
import com.bng.core.parser.NameValuePair;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

public class DigitCollect implements Execute{

	private int maxlen;
	private int repeatcount;
	private int minlen;
	private String terminationchar;
	private int timeout;
	private boolean confirmation; 
	private String digittype;
	private NameValuePair[] confirmPromptFile;
	private String[] confirmlist;
	private int cellId;
	private int maxcount;
	private NameValuePair[] contentFile; 
	
	public int getMaxlen() {
		return maxlen;
	}
	
	public void setMaxlen(int maxlen) {
		this.maxlen = maxlen;
	}

	public String getTerminationchar() {
		return terminationchar;
	}

	public void setTerminationchar(String terminationchar) {
		this.terminationchar = terminationchar;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isConfirmation() {
		return confirmation;
	}
	
	public void setConfirmation(boolean confirmation) {
		this.confirmation = confirmation;
	}

	public String getDigittype() {
		return digittype;
	}

	public void setDigittype(String digittype) {
		this.digittype = digittype;
	}

	public NameValuePair[] getConfirmPromptFile() {
		return confirmPromptFile;
	}

	public void setConfirmPromptFile(NameValuePair[] confirmPromptFile) {
		this.confirmPromptFile = confirmPromptFile;
	}

	public String[] getConfirmlist() {
		return confirmlist;
	}

	public void setConfirmlist(String[] confirmlist) {
		this.confirmlist = confirmlist;
	}

	public int getRepeatcount() {
		return repeatcount;
	}

	public void setRepeatcount(int repeatcount) {
		this.repeatcount = repeatcount;
	}

	public int getMinlen() {
		return minlen;
	}

	public void setMinlen(int minlen) {
		this.minlen = minlen;
	}

	public int getCellId() {
		return cellId;
	}

	public void setCellId(int cellId) {
		this.cellId = cellId;
	}
	
	public int getMaxcount() {
		return maxcount;
	}

	public void setMaxcount(int maxcount) {
		this.maxcount = maxcount;
	}

	public NameValuePair[] getContentFile() {
		return contentFile;
	}

	public void setContentFile(NameValuePair[] contentFile) {
		this.contentFile = contentFile;
	}

	public String execute(Event event,String qName,UserCallDetail userCallDetail)
    {
		DigitCollection digitCollection = null;
		String searchString = "";
		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Inside digitCollection execute. digitType = "+digittype+", DTMF_Buffer = "+event.getDtmfBuffer());
		try
		{
			if(event.getEvent() == CoreEnums.events.E_WaitDTMF.ordinal() && (!event.getDtmfBuffer().equals("")))
			{
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Dtmf buffer received = "+event.getDtmfBuffer());
				if(digittype.equalsIgnoreCase("Date"))
				{
					if(userCallDetail.getProcessingDateFormat() != null)
					{
						try
						{
							DateFormat dateFormatter = new SimpleDateFormat("ddMMyyyy");
							Date enteredDate = (Date)dateFormatter.parse(event.getDtmfBuffer());
							Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Date entered by user in  ddMMyyyy format : "+ enteredDate);
							
							dateFormatter = new SimpleDateFormat(userCallDetail.getProcessingDateFormat());
						    String strDateTime = dateFormatter.format(enteredDate);
					
						    Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Formatted date in "+userCallDetail.getProcessingDateFormat()+" format is : " + strDateTime);
							userCallDetail.setDateEntered(strDateTime);
							
							searchString = "success";
							dateFormatter = null;
							enteredDate = null;
							strDateTime = null;
						}
						catch(Exception e)
						{
							Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
							searchString = "";
						}
					}
					else
					{
						searchString = "";
						userCallDetail.setDateEntered(event.getDtmfBuffer());
					}
				}
				else if(digittype.equalsIgnoreCase("creditcard"))
				{
					searchString = "";
					userCallDetail.setCcNumber(event.getDtmfBuffer());
				}
				else if(digittype.equalsIgnoreCase("BPartyNumber"))
				{
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] previously bParty : "+userCallDetail.getbPartyMsisdn()+" .Set dtmf buffer as BPartyMsisdn : "+event.getDtmfBuffer());
					searchString = "";
					userCallDetail.setbPartyMsisdn(event.getDtmfBuffer());					
				}
				else if(digittype.equalsIgnoreCase("Age"))
				{
					searchString = "";
					userCallDetail.setAge(Integer.parseInt(event.getDtmfBuffer()));
				}
				else if(digittype.equalsIgnoreCase("Password"))
				{
					searchString = "";
					userCallDetail.setPassword(event.getDtmfBuffer());
				}
				else if(digittype.equalsIgnoreCase("Surah"))
				{	
					if(Integer.parseInt(event.getDtmfBuffer()) <= maxcount)
					{
						userCallDetail.setSurasNo(Integer.parseInt(event.getDtmfBuffer()));
						searchString = "success";
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Setting suras no.");
						userCallDetail.setSurasNoSetFlag(false);
					}
					else
					{
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]  Value = "+event.getDtmfBuffer()+" is greater than total no of Surah count = "+maxcount);
						searchString = "failure";
					}
					
				}
				else if(digittype.equalsIgnoreCase("Tasbih"))
				{
					if(event.getDtmfBuffer().isEmpty())
						searchString = "failure";
					else
						searchString = "success";
					userCallDetail.setNoOfTasbih(Integer.parseInt(event.getDtmfBuffer()));
					userCallDetail.setRepeatTasbih(false);
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Setting no. of tisbah");
				}
				else if(digittype.equalsIgnoreCase("booknumberformultiplay"))
				{	
					if(Integer.parseInt(event.getDtmfBuffer()) <= maxcount)
					{
						searchString = "success";
						userCallDetail.setBooknumber(Integer.parseInt(event.getDtmfBuffer()));
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Move to book number = "+event.getDtmfBuffer());
					}
					else
					{
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]  Value = "+event.getDtmfBuffer()+" is greater than total no of Book count = "+maxcount);
						searchString = "failure";
					}
				}
				else if(digittype.equalsIgnoreCase("booknumbernotformultiplay"))
				{	
					if(Integer.parseInt(event.getDtmfBuffer()) <= maxcount)
					{
						searchString = event.getDtmfBuffer();
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Move to book number = "+event.getDtmfBuffer());
					}
					else
					{
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]  Value = "+event.getDtmfBuffer()+" is greater than total no of Book count = "+maxcount);
						searchString = "failure";
					}
				}
				else if(digittype.equalsIgnoreCase("chapternumber"))
				{	
					if(maxcount > 0)
					{
						if(Integer.parseInt(event.getDtmfBuffer()) <= maxcount)
						{
							searchString = "success";
							userCallDetail.setChapternumber(Integer.parseInt(event.getDtmfBuffer()));
							Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Move to chapter number = "+event.getDtmfBuffer());
						}
						else
						{
							Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]  Value = "+event.getDtmfBuffer()+" is greater than total no of chapter count = "+maxcount);
							searchString = "failure";
						}
					}
					else
					{
						if(!event.getDtmfBuffer().isEmpty())
						{
							searchString = "success";
							userCallDetail.setChapternumber(Integer.parseInt(event.getDtmfBuffer()));
							Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Move to chapter number = "+event.getDtmfBuffer());
						}
						else
						{
							Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]  value received in buffer = "+event.getDtmfBuffer());
							searchString = "failure";
						}
					}
					
				}
				else
				{
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Unknown digitType = "+digittype);
				}
				
				if(confirmation)
				{
					Logger.sysLog(LogValues.info, this.getClass().getName(),"DigitCollection confirmation = "+confirmation);
					String dtmfbuffer = event.getDtmfBuffer();
					confirmlist = new String[dtmfbuffer.length()+1];
					NameValuePair nameValuePair = null;
					for(int i =0; i<confirmPromptFile.length; i++)
			        {
						nameValuePair = confirmPromptFile[i];
			        	Logger.sysLog(LogValues.info, this.getClass().getName(),"confirmPromptFile = "+nameValuePair.getFile()+", lang in userCallDetail = "+userCallDetail.getLanguage());
			        	if(nameValuePair.getFile().endsWith(userCallDetail.getLanguage()+".wav"))
			        	{
			        		confirmlist[0] = nameValuePair.getFile();
			        		userCallDetail.setConfirmFile(nameValuePair.getFile());
			        		userCallDetail.setConfirmFileId(nameValuePair.getContentId());
			        		break;
			        	}
			        }	
					if(confirmlist.length>0)
					{
						for(int i=0; i<dtmfbuffer.length(); i++)
						{
							Logger.sysLog(LogValues.info, this.getClass().getName(),""+dtmfbuffer.charAt(i));
							confirmlist[i+1] = "/root/PlayFiles/"+dtmfbuffer.charAt(i);
						}
						userCallDetail.setConfirmList(confirmlist);
						userCallDetail.setNumberOfFileInPlay(confirmlist.length);
						
						Play play = new Play(confirmlist[userCallDetail.getNumberOfFilesPlayed()],event.getvId(),event.getIp(),0,false,CoreEnums.playOperations.stop.ordinal(),event.getaPartyMsisdn(),"",event.getHardware(), qName, ""+cellId);
						SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(play));
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]confirmation is true. send confirm list files to telephony in queue : "+event.getCoreToTelephony());
						
						userCallDetail.setNumberOfFilesPlayed(userCallDetail.getNumberOfFilesPlayed()+1);
						userCallDetail.setNextStateFlag(false);
						userCallDetail.setExecuteCurrentState(false);
						userCallDetail.setDtmfBufferReceived(true);
						
						MediaCdr media = new MediaCdr();
			        	media.setType("Navigation");
			        	media.setLocation("Local");
			        	media.setStarttime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
			        	media.setFilename(confirmlist[userCallDetail.getNumberOfFilesPlayed()]+userCallDetail.getLanguage()+".wav");
			        	media.setCode("0");
			        	userCallDetail.getCallCdr().addMediaMap(cellId+"_"+userCallDetail.getNumberOfFilesPlayed(),media);
		        		media = null;
					}					
				}
				else
				{
					userCallDetail.setNextStateFlag(true);
					userCallDetail.setExecuteCurrentState(true);
				}
				DtmfCdr dtmf = new DtmfCdr(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"),event.getDtmfBuffer());
				userCallDetail.getCallCdr().addDtmfList(dtmf);
			}
			else if(event.getEvent() == CoreEnums.events.E_PlayEnd.ordinal() && event.getSubEvent() == CoreEnums.playEndSubEvent.S_PlayComplete.ordinal())
			{
				if(event.getTransId().equals(cellId+"_"+0))
				{
					if(userCallDetail.isDtmfBufferReceived())
					{
						
		        		if(confirmation)
		        		{
		        			MediaCdr mediaCdr = userCallDetail.getCallCdr().getMediaCdr(cellId+"_"+(userCallDetail.getNumberOfFilesPlayed()-1));//new MediaCdr("Navigation","Local",userCallDetail.getNpFile(),userCallDetail.getNpStartTime(), Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"),userCallDetail.getNpContentId());
							mediaCdr.setEndtime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
			        		userCallDetail.getCallCdr().addMediaMap(cellId+"_0", mediaCdr);
			        		mediaCdr = null;
		        			if(userCallDetail.getNumberOfFilesPlayed() != userCallDetail.getNumberOfFileInPlay())
				        	{
								//MediaCdr media = new MediaCdr("Navigation","Local",userCallDetail.getConfirmFile(),userCallDetail.getConfirmFileStartTime(),Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"),userCallDetail.getConfirmFileId());
								//userCallDetail.getCallCdr().addMediaList(media);
								confirmlist = userCallDetail.getConfirmList();
								Logger.sysLog(LogValues.info, this.getClass().getName(),"confirmlist length = "+confirmlist);
				        		Play play = new Play(confirmlist[userCallDetail.getNumberOfFilesPlayed()]+userCallDetail.getLanguage()+".wav",event.getvId(),event.getIp(),0,false,CoreEnums.playOperations.play.ordinal(),event.getaPartyMsisdn(),"0",event.getHardware(), qName, ""+cellId);
					        	SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(play));
						        Logger.sysLog(LogValues.info, PlayContent.class.getName(), "Telephony event Digit Confirmation added to queue = "+event.getCoreToTelephony()); 
						        //userCallDetail.setConfirmFile(confirmlist[userCallDetail.getNumberOfFilesPlayed()]+userCallDetail.getLanguage()+".wav");
						        //userCallDetail.setConfirmFileStartTime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
						        //userCallDetail.setConfirmFileId("");
						        userCallDetail.setNextStateFlag(false);
						        Logger.sysLog(LogValues.info, PlayContent.class.getName(), "Confirmation Play Processed.");
						        MediaCdr media = new MediaCdr();
					        	media.setType("Navigation");
					        	media.setLocation("Local");
					        	media.setStarttime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
					        	media.setFilename(confirmlist[userCallDetail.getNumberOfFilesPlayed()]+userCallDetail.getLanguage()+".wav");
					        	media.setCode("0");
					        	userCallDetail.getCallCdr().addMediaMap(cellId+"_"+userCallDetail.getNumberOfFilesPlayed(),media);
				        		media = null;
				        		userCallDetail.setNumberOfFilesPlayed(userCallDetail.getNumberOfFilesPlayed()+1);
				        	}
				        	else
				            {
				            	Logger.sysLog(LogValues.info, PlayContent.class.getName(), "No more files to play for confirmation. Reset all the values.");
				            	//MediaCdr media = new MediaCdr("Navigation","Local",userCallDetail.getConfirmFile(),userCallDetail.getConfirmFileStartTime(),Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"),userCallDetail.getConfirmFileId());
								//userCallDetail.getCallCdr().addMediaList(media);
				            	userCallDetail.setNumberOfFilesPlayed(0);
				            	userCallDetail.setNumberOfFileInPlay(0);
				            	userCallDetail.setNextStateFlag(true);
					            userCallDetail.setExecuteCurrentState(true);
					            searchString = "";
				            }
		        		}
		        		else
		        		{
		        			MediaCdr mediaCdr = userCallDetail.getCallCdr().getMediaCdr(cellId+"_"+0);//new MediaCdr("Navigation","Local",userCallDetail.getNpFile(),userCallDetail.getNpStartTime(), Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"),userCallDetail.getNpContentId());
							mediaCdr.setEndtime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
			        		userCallDetail.getCallCdr().addMediaMap(cellId+"_0", mediaCdr);
			        		mediaCdr = null;
		        		}
		        		
						
					}
					else
					{
						MediaCdr mediaCdr = userCallDetail.getCallCdr().getMediaCdr(cellId+"_"+0);//new MediaCdr("Navigation","Local",userCallDetail.getNpFile(),userCallDetail.getNpStartTime(), Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"),userCallDetail.getNpContentId());
						mediaCdr.setEndtime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
		        		userCallDetail.getCallCdr().addMediaMap(cellId+"_0", mediaCdr);
		        		mediaCdr = null;
					}
				}
				else
				{
					Logger.sysLog(LogValues.info, PlayContent.class.getName(), "Play end received for some other play");
					userCallDetail.setNextStateFlag(false);					
				}
			}
			else if(event.getEvent() == CoreEnums.events.E_PlayEnd.ordinal() && event.getSubEvent() == CoreEnums.playEndSubEvent.S_PlayStopByApp.ordinal())
			{
				Logger.sysLog(LogValues.info, this.getClass().getSimpleName(), "Discard the playEnd_stopByApp event received.");				
			}
			else if(event.getEvent() == CoreEnums.events.E_DTMF.ordinal() && userCallDetail.isSentDCReq())
			{
				Logger.sysLog(LogValues.info, this.getClass().getSimpleName(), "Discard the dtmf received.");
				userCallDetail.setNextStateFlag(false);
			}
			else if(event.getEvent() == CoreEnums.events.E_WaitDTMF.ordinal() && (event.getDtmfBuffer() == null || event.getDtmfBuffer().equals("")))
			{
				Logger.sysLog(LogValues.info, this.getClass().getSimpleName(), "No digit is received via DC.Discard the event.");
				userCallDetail.setNextStateFlag(true);
				userCallDetail.setExecuteCurrentState(true);
				searchString = "failure";
				return searchString;
			}
			else
			{
				Logger.sysLog(LogValues.info, this.getClass().getSimpleName(), "DigitCollection event added to telephony q.");
				boolean withEndChar = false;
				if((terminationchar != null) && (!terminationchar.equals("")))
					withEndChar = true;
				try{
					if(!confirmation)
					{
						if(contentFile.length > 0)
						{	
							NameValuePair nameValuePair;
							nameValuePair = contentFile[0]; 
							Logger.sysLog(LogValues.info, this.getClass().getSimpleName(), "nameValuePair.getFile() ="+nameValuePair.getFile());
							Play play = new Play(nameValuePair.getFile(),event.getvId(),event.getIp(),0,false,CoreEnums.playOperations.play.ordinal(),event.getaPartyMsisdn(),"",event.getHardware(), qName, cellId+"_"+0);
							SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(play));
							Logger.sysLog(LogValues.info, this.getClass().getSimpleName(), "Telephony event get digit play added to queue : "+event.getCoreToTelephony()); 
							MediaCdr media = new MediaCdr();
					        	media.setType("DigitCollection");
					        	media.setLocation("Local");
					        	media.setStarttime(Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
					        	media.setFilename(nameValuePair.getFile());
					        	media.setCode("0");
					        	userCallDetail.getCallCdr().addMediaMap(cellId+"_"+0,media);
				        		media = null;
						}
					}
				}
				catch(Exception e)
				{
					Logger.sysLog(LogValues.error, this.getClass().getSimpleName(), "NO FILE FOUND IN DIGIT COLLECTION");
				}
				
				digitCollection = new DigitCollection(event.getvId(),maxlen,timeout,terminationchar,withEndChar, event.getIp(), event.getHardware(), qName);
				SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(digitCollection));
				Logger.sysLog(LogValues.info, this.getClass().getSimpleName(), "Telephony event collectDigit added to queue : "+event.getCoreToTelephony());
				userCallDetail.setSentDCReq(true);
				userCallDetail.setNextStateFlag(false);
				digitCollection = null;
			}
		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
		}		
    	return searchString;
    }
}
