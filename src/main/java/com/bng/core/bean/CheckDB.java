/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.bean;

import com.bng.core.bo.BlackListedBo;
import com.bng.core.bo.BookmarkBo;
import com.bng.core.bo.CircleBo;
import com.bng.core.bo.RedCarpetListBo;
import com.bng.core.bo.SubscriptionBo;
import com.bng.core.bo.UnsubscriptionBo;
import com.bng.core.bo.VisitorsBo;
import com.bng.core.bo.WhiteListedBo;
import com.bng.core.entity.Bookmark;
import com.bng.core.entity.CircleMapper;
import com.bng.core.entity.Subscription;
import com.bng.core.entity.Unsubscription;
import com.bng.core.entity.Visitors;
import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Event;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

/**
 *
 * @author richa
 */
public class CheckDB implements Execute{
    private String value;
    private int cellId;
    private String serviceName;
    private static SubscriptionBo subsBoImpl;
    private static UnsubscriptionBo unsubBoImpl;
    private static CircleBo circleBoImpl;
    private static BlackListedBo blackListedBoImpl;
    private static WhiteListedBo whiteListedBoImpl;
    private static RedCarpetListBo redCarpetListBoImpl;
    private static VisitorsBo visitorsBoImpl;
    private static BookmarkBo bookmarkBoImpl;
    private int bpartyminlength ;
    

	public void setSubsBoImpl(SubscriptionBo subsBoImpl) {
		CheckDB.subsBoImpl = subsBoImpl;
	}

	public void setUnsubBoImpl(UnsubscriptionBo unsubBoImpl) {
		CheckDB.unsubBoImpl = unsubBoImpl;
	}

	public void setCircleBoImpl(CircleBo circleBoImpl) {
		CheckDB.circleBoImpl = circleBoImpl;
	}

	public void setBlackListedBoImpl(BlackListedBo blackListedBoImpl) {
		CheckDB.blackListedBoImpl = blackListedBoImpl;
	}	
	
	public CircleBo getCircleBoImpl() {
		return circleBoImpl;
	}

	public void setVisitorsBoImpl(VisitorsBo visitorsBoImpl) {
		CheckDB.visitorsBoImpl = visitorsBoImpl;
	}

	public void setBookmarkBoImpl(BookmarkBo bookmarkBoImpl) {
		CheckDB.bookmarkBoImpl = bookmarkBoImpl;
	}	

	public void setWhiteListedBoImpl(WhiteListedBo whiteListedBoImpl) {
		CheckDB.whiteListedBoImpl = whiteListedBoImpl;
	}

	public void setRedCarpetListBoImpl(RedCarpetListBo redCarpetListBoImpl) {
		CheckDB.redCarpetListBoImpl = redCarpetListBoImpl;
	}

	public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public int getCellId() {
		return cellId;
	}

	public void setCellId(int cellId) {
		this.cellId = cellId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public int getBpartyminlength() {
		return bpartyminlength;
	}

	public void setBpartyminlength(int bpartyminlength) {
		this.bpartyminlength = bpartyminlength;
	}

	public String execute(Event event, String qName, UserCallDetail userCallDetail)
    {
        Subscription subscription = null;
        Unsubscription unsubscription = null;
        CircleMapper circleMapper = null;
        Bookmark bookmark = null;
        String searchString = null;
        //String msisdntruncated = "";
        
        try
        {   
        	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] DBCheck value = "+value);
        	
        	if(value.equalsIgnoreCase("GetUserDetail"))
	        {   
        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Servicename Entered by user = "+serviceName);
        		if(serviceName == null || serviceName.equals(""))
	        		{
        				serviceName = userCallDetail.getService();
        				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Servicename from start = "+serviceName);
	        		}
	        	
	        	userCallDetail.setSubsServiceName(serviceName);
	        	if(event.getCallType() == CoreEnums.callType.Incoming.ordinal())
	        		subscription = subsBoImpl.checkExistingSubs(Utility.numbercorrected(userCallDetail.getaPartyMsisdn()), userCallDetail.getShortCode(),serviceName);
	        	else
	        		subscription = subsBoImpl.checkExistingSubs(Utility.numbercorrected(userCallDetail.getbPartyMsisdn()), userCallDetail.getShortCode(),serviceName);
	        	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Subscription = "+subscription);
	        	
	        	if(subscription != null)
	        	{
		        	if(subscription.getStatus().equalsIgnoreCase("active"))
		        	{
		        		searchString = "active";	
		        		if(subscription.getSubType() != null && !(subscription.getSubType().equals("")))
		        		{
			        		userCallDetail.setSubType(subscription.getSubType());	
			        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] subtype = "+userCallDetail.getSubType());
		        			if(userCallDetail.getSubType().equalsIgnoreCase("combo"))
		        			{
		        				if(subscription.getSubTimeLeft() !=null && !(subscription.getSubTimeLeft().equals("")))
		        				{
		        					userCallDetail.setRemainingFreeMinutes(subscription.getSubTimeLeft());	
		        					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] SubTime LEft in dB = "+subscription.getSubTimeLeft());
		        				}
		        				else
		        				{
		        					userCallDetail.setRemainingFreeMinutes(0);
		        					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] SubTime LEft in dB = "+subscription.getSubTimeLeft());
		        				}	
		        			}
		        			else
		        				userCallDetail.setRemainingFreeMinutes(0);
		        		}
		        		else
		        			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] subtype  DB = "+subscription.getSubType());
	        			
		        		if(subscription.getCallAttempts() != null && !(subscription.getCallAttempts().equals("")))
		        			userCallDetail.setCallAttempt(subscription.getCallAttempts());
		        		else
		        			userCallDetail.setCallAttempt(0);
		        		
		        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"] subscription.getLanguage() ="+subscription.getLanguage());
	        			if(subscription.getLanguage() != null && !(subscription.getLanguage().equals("")))
	        			{
	        				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"] subscription.getLanguage() ="+subscription.getLanguage());
	        				if(subscription.getLanguage().startsWith("_"))
	        					userCallDetail.setLanguage(subscription.getLanguage());
	        				else
	        					userCallDetail.setLanguage("_"+subscription.getLanguage());
	        				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"] userCallDetail.getLanguage() ="+userCallDetail.getLanguage());
	        			}
	        			else
	        			{
	        				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"] subscription.getLanguage() ="+subscription.getLanguage());
	        				userCallDetail.setSubscriptionlangauge("nolanguage");
	        			}
	        				
	        			
	        			
	        			if(subscription.getAlert() != null && !(subscription.getAlert().equals("")))
	        				userCallDetail.setSubAlert(subscription.getAlert());
	        			else
	        				userCallDetail.setSubAlert(0);
	        			
	        			/*if(subscription.isFirstCaller())
	        				userCallDetail.setFirstCallerofmonth(true);
	        			else
	        				userCallDetail.setFirstCallerofmonth(false);*/
	        				
		        	}
		        	else if(subscription.getStatus().equalsIgnoreCase("pending"))
		        		searchString = "pending";	
		        	else if(subscription.getStatus().equalsIgnoreCase("inactive"))
		        		searchString = "unsubscribed";	
		        	
		        }
	        	else
	        	{
	        		unsubscription = unsubBoImpl.checkUnsubscription(event.getaPartyMsisdn(), userCallDetail.getShortCode(), userCallDetail.getService());
	        		if(unsubscription != null)
	        		{
	        			searchString = "unsubscribed";
	        		}
	        		else
	        		{
	        			searchString = "new";
	        		}
	        	}
	        	userCallDetail.setStatus(searchString);
	        	userCallDetail.getCallCdr().setUserstatus(searchString);
	        	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CheckDB for "+value+", status = "+searchString);
	        }
        	else if(value.equalsIgnoreCase("getcircle"))
	        {
        		if(event.getCallType() == CoreEnums.callType.Incoming.ordinal())
	        		subscription = subsBoImpl.checkExistingSubs(Utility.numbercorrected(userCallDetail.getaPartyMsisdn()), userCallDetail.getShortCode(),serviceName);
	        	else
	        		subscription = subsBoImpl.checkExistingSubs(Utility.numbercorrected(userCallDetail.getbPartyMsisdn()), userCallDetail.getShortCode(),serviceName);
	        	
	        	if(subscription == null || subscription.getLanguage().equals("") || subscription.getLanguage() == null)
	        	{
	        		circleMapper = circleBoImpl.getCircleInfo(event.getaPartyMsisdn());
	        		if(circleMapper != null)
	        		{
	        			searchString = circleMapper.getDefLang();
	        			if(searchString != null)
	        				userCallDetail.setCircle(searchString);
	        			else
	        				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Get default language set by Start.");
	        		}
	        	}
	        	else
	        	{
	        		searchString = subscription.getCircle();
	        		userCallDetail.setCircle(searchString);
	        	}
	        	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CheckDB for "+value+", status = "+searchString);
	        }
        	/*else if(value.equalsIgnoreCase("FirstCallerofMonth"))
        	{
        		if(userCallDetail.isFirstCallerofmonth())
        			searchString = "true";
        		else
        			searchString = "false";
        	}
        	*/
        	else if(value.equalsIgnoreCase("BPartyExists"))
	        {
	        	
        		if(userCallDetail.getbPartyMsisdn() == null || userCallDetail.getbPartyMsisdn().equals(""))
	        		searchString = "false";
	        	else if(userCallDetail.getbPartyMsisdn().length() < bpartyminlength)
	        		searchString = "false";
	        	else
	        		searchString = "true";
	        }
	        else if(value.equalsIgnoreCase("VoiceEffectExists"))
	        {
	        	if(userCallDetail.getVoiceEffectFrequency() == null || userCallDetail.getVoiceEffectFrequency().equals(""))
	        		searchString = "false";
	        	else 
	        		searchString = "true";
	        }
        	else if(value.equalsIgnoreCase("GetBlacklisted"))
	        {
	        	boolean isBlackListed;
	        	if(event.getCallType() == CoreEnums.callType.Outgoing.ordinal())	        		
	        		isBlackListed  = blackListedBoImpl.isBlackListed(event.getbPartyMsisdn());
	        	else
	        		isBlackListed  = blackListedBoImpl.isBlackListed(event.getaPartyMsisdn());
	        	
	        	userCallDetail.setBlackListed(isBlackListed);
	        	if(isBlackListed)
	        		searchString = "yes";
	        	else
	        		searchString = "no";
	        	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CheckDB for "+value+", status = "+searchString);
	        }
	        else if(value.equalsIgnoreCase("Getwhitelisted"))
	        {
	        	boolean isWhiteListed;
	        	if(event.getCallType() == CoreEnums.callType.Outgoing.ordinal())	        		
	        		isWhiteListed  = whiteListedBoImpl.isWhiteListed(event.getbPartyMsisdn());
	        	else
	        		isWhiteListed  = whiteListedBoImpl.isWhiteListed(event.getaPartyMsisdn());
	        	
	        	userCallDetail.setWhiteListed(isWhiteListed);
	        	if(isWhiteListed)
	        		searchString = "yes";
	        	else
	        		searchString = "no";
	        	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CheckDB for "+value+", status = "+searchString);
	        }
	        else if(value.equalsIgnoreCase("GetRedCarpetCheck"))
	        {
	        	boolean isInRedCarpetList;
	        	if(event.getCallType() == CoreEnums.callType.Outgoing.ordinal())	        		
	        		isInRedCarpetList  = redCarpetListBoImpl.isInRedCarpetList(event.getbPartyMsisdn());
	        	else
	        		isInRedCarpetList  = redCarpetListBoImpl.isInRedCarpetList(event.getaPartyMsisdn());
	        	
	        	userCallDetail.setInRedCarpetList(isInRedCarpetList);
	        	if(isInRedCarpetList)
	        		searchString = "yes";
	        	else
	        		searchString = "no";
	        	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CheckDB for "+value+", status = "+searchString);
	        }
	        else if(value.equalsIgnoreCase("GetVisitorDialCount"))
	        {
	        	userCallDetail.setUpdateVisitorsDialCount(true);
	        	userCallDetail.setVisitorsServiceName(serviceName);
	        	Visitors visitors = visitorsBoImpl.getVisitorsDialCount(Utility.numbercorrected(event.getaPartyMsisdn()), serviceName, userCallDetail.getShortCode());
	        	if(visitors == null)
	        		searchString = "1";
	        	else
	        	{
	        		if(visitors.getCallAttempts() > 10)
	        			searchString = "10+";
	        		else
	        			searchString = ""+(visitors.getCallAttempts()+1);
	        	}
	        }
	        else if(value.equalsIgnoreCase("GetTimeOfDay"))
	        {
	        	searchString = Utility.getTimeOfDay();
	        	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Current hour of the day = "+searchString);
	        }
	        else if(value.equalsIgnoreCase("GetDayOfWeek"))
	        {
	        	searchString = Utility.getDayOfWeek();
	        	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Current day of the week = "+searchString);
	       
	        }
	        else if(value.equalsIgnoreCase("GetBookmark"))
	        {
	        	bookmark = bookmarkBoImpl.getBookmark(Utility.numbercorrected(userCallDetail.getaPartyMsisdn()));
	        	if(bookmark != null)
	        	{
	        		searchString = "bookmarkfound";
	        		userCallDetail.setBookmarkId(bookmark.getPromptId());
	        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Bookmark found for playId = "+bookmark.getPromptId());
	        	}
	        	else
	        		searchString = "bookmarknotfound";
	        }
	        else if(value.equalsIgnoreCase("GetLanguage"))
	        {
	        	if(!(userCallDetail.getSubscriptionlangauge().equalsIgnoreCase("nolanguage")))
	        		searchString = userCallDetail.getLanguage();
	        	else
	        		searchString ="nolanguage" ;
        		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Language got from DB = "+searchString);
	        }
	        else if(value.equalsIgnoreCase("getfreeminutes"))
		    {	  
		        if(userCallDetail.getStatus().equalsIgnoreCase("active"))
		        {
        			if(userCallDetail.getSubType().equalsIgnoreCase("combo"))
        			{
	        			int freeTimeLeft = userCallDetail.getRemainingFreeMinutes();
	        			if(freeTimeLeft > 0)
		        		{
		        			userCallDetail.setRemainingFreeMinutes(freeTimeLeft);	     
		        			userCallDetail.setFreeMinTimerCellId(cellId);
		            		searchString = "FreeMinsLeft";
		        		}
		        		else
		        			searchString = "NoFreeMins";
	        		}
        			else
        				searchString = "NA";
        		}		        	
		        Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CheckDB for "+value+", status = "+searchString);
		     }
	        else if(value.equalsIgnoreCase("getsubsdialcount"))
	        {
	        		int callAttempts = userCallDetail.getCallAttempt();
	        		if(callAttempts == 0)
	        			searchString = "0";
	        		else if(callAttempts <= 3)
	        			searchString = "1-3";
	        		else if(callAttempts <= 7)
	        			searchString = "4-7";
	        		else
	        			searchString = "7+";		        		
	        		//userCallDetail.setCallAttempt(callAttempts);
	        	
	        	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CheckDB for "+value+", status = "+searchString);
	        }
		        else if(value.equalsIgnoreCase("getalertstatus"))
		        {
		        	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] alert = "+subscription.getAlert());
		        	if(userCallDetail.getSubAlert() == 0)
		        		searchString = "false";
		        	else if(userCallDetail.getSubAlert() == 1)
		        		searchString = "true";
		        	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] CheckDB for "+value+", status = "+searchString);
		        }		        
	        userCallDetail.setNextStateFlag(true);
	        userCallDetail.setExecuteCurrentState(true);
	        Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"] searchString = "+searchString);
	        return searchString;
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
        	return searchString;
        }
        finally
        {
        	
        }
    }
}