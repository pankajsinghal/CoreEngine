/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.bean;

import java.util.Properties;

import com.bng.core.bo.SubscriptionBo;
import com.bng.core.coreCdrBean.UrlCdr;
import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Event;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.DBConnection;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.SimpleParser;
import com.bng.core.utils.UrlHitter;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;
/**
 *
 * @author richa
 */
public class Url implements Execute{
    private String url; 
    private String method;
    private String type;
    private String mode;
    private String connectTimeOut;
    private static UrlHitter urlHitter;
    private static SubscriptionBo subsBoImpl;
    private String servicename;
    private String responsetype ;
    
	public String getServicename() {
		return servicename;
	}

	public void setServicename(String servicename) {
		this.servicename = servicename;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getConnectTimeOut() {
		return connectTimeOut;
	}

	public void setConnectTimeOut(String connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
	}
	
	public String getResponsetype() {
		return responsetype;
	}

	public void setResponsetype(String responsetype) {
		this.responsetype = responsetype;
	}

	public void setUrlHitter(UrlHitter urlHitter) {
		Url.urlHitter = urlHitter;
	}
	
	public void setSubsBoImpl(SubscriptionBo subsBoImpl) {
		Url.subsBoImpl = subsBoImpl;
	}

	public String execute(Event event, String qName, UserCallDetail userCallDetail)
    {
		String searchString = "";
		String urlresp = "";
		SimpleParser sp = null;
		Properties prop = Utility.convertUCDToProp(userCallDetail);
		String urlHitTime = "";
		String urlRespTime = "";
		
		if(method.equalsIgnoreCase("get"))
		{
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] GET url = "+url+", type = "+type);
			try
			{
				sp = new SimpleParser(url);
				url = sp.parse(prop);
				url = url.replaceAll("#", "&");
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Formatted GET url = "+url+" .Mode of URL hit = "+mode);
				
				if(mode.equalsIgnoreCase("synchronous"))
				{
					urlHitTime = Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS");
					if(responsetype.equalsIgnoreCase("header"))
						urlresp = Utility.callUrlWithHeaderResponse(url,Integer.parseInt(connectTimeOut));
					else
						urlresp = Utility.callUrlWithTimeOut(url,Integer.parseInt(connectTimeOut));
					urlRespTime = Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS");
					
					if(urlresp != null)
					{
						if( (urlresp.contains("ResponseCode : 200") || urlresp.contains("ResponseMessage : OK") || urlresp.contains("200|SUCCESS")))
							searchString = "Success";
						else
							searchString = "failure";
					}
					else
						searchString = "failure";
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"] searchstring ="+searchString);
				}
				else
				{
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Going to add url in urlHitterQ.");
					urlHitTime = Utility.getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS");
					urlHitter.hitUrl(url);
				}				
			}
			catch(Exception e)
			{
				Logger.sysLog(LogValues.error, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]"+coreException.GetStack(e));
			}		
		}
		else
		{
			Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Calling POST url = "+url);
		}
		
		UrlCdr urlCdr = new UrlCdr(url,method,urlHitTime,urlresp,"", type,urlRespTime,mode);		
		userCallDetail.getCallCdr().addUrlList(urlCdr);
		
		if(type.equalsIgnoreCase("subscription"))
	    {
		     if(event.getCallType() == CoreEnums.callType.Incoming.ordinal())
		     {	 try
			     {
			    	 Logger.sysLog(LogValues.info, this.getClass().getName(), "INSERTING sub status for aPartyMsisdn ="+userCallDetail.getaPartyMsisdn()+" and service ="+userCallDetail.getService()+" for langauge = "+userCallDetail.getLanguage());
			    	 if(servicename != null && (!servicename.equals(""))) 
		    		 {
			    		 subsBoImpl.insertSubscription(Utility.numbercorrected(userCallDetail.getaPartyMsisdn()), servicename, "pending", userCallDetail.getLanguage());
			    		 Logger.sysLog(LogValues.info, this.getClass().getName(), "servicename = "+servicename);
		    		 }
			    	 else
		    		 {
			    		 Logger.sysLog(LogValues.info, this.getClass().getName(), "userCallDetail.getSubsServiceName() = "+userCallDetail.getSubsServiceName());
			    		 subsBoImpl.insertSubscription(Utility.numbercorrected(userCallDetail.getaPartyMsisdn()), userCallDetail.getSubsServiceName(), "pending", userCallDetail.getLanguage());
		    		 }
			     }
			     catch(Exception e)
			     {
			    	 Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
			     }
		    }
		     else
		     {
		    	 Logger.sysLog(LogValues.info, this.getClass().getName(), "INSERTING sub status for bPartyMsisdn ="+userCallDetail.getaPartyMsisdn()+" and service ="+userCallDetail.getService());
		    	try
		    	{
		    		 if(servicename != null && (!servicename.equals("")))
			    	 {
			    		 subsBoImpl.insertSubscription(Utility.numbercorrected(userCallDetail.getbPartyMsisdn()), servicename, "pending", userCallDetail.getLanguage());
			    		 Logger.sysLog(LogValues.info, this.getClass().getName(), "servicename = "+servicename);
			    	 } 
			    	else
		    		 {
			    		subsBoImpl.insertSubscription(Utility.numbercorrected(userCallDetail.getbPartyMsisdn()), userCallDetail.getSubsServiceName(), "pending", userCallDetail.getLanguage());
			    		Logger.sysLog(LogValues.info, this.getClass().getName(), "userCallDetail.getSubsServiceName() = "+userCallDetail.getSubsServiceName());
		    		 }
		    	}
		    	catch(Exception e)
		    	{
		    		 Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
		    	}
		    	
		     }
	    }
		userCallDetail.setNextStateFlag(true);
		userCallDetail.setExecuteCurrentState(true);
		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Url hit successfully. NextStateFlag = "+userCallDetail.isNextStateFlag()+", ExecuteCurrentStateFlag = "+userCallDetail.isExecuteCurrentState());		
        return searchString;
    }
}
