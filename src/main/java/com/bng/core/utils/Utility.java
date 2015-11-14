/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.bng.core.bo.BookmarkBo;
import com.bng.core.bo.SubscriptionBo;
import com.bng.core.bo.VisitorsBo;
import com.bng.core.coreCdrBean.CallCdr;
import com.bng.core.entity.Subscription;
import com.bng.core.entity.Visitors;
import com.bng.core.exception.coreException;
import com.bng.core.jsonBean.Event;
import com.bng.core.jsonBean.ServiceInfo;
import com.bng.core.memCache.MemCacheJSon;
import com.google.gson.Gson;


/**
 *
 * @author richa
 */
public class Utility 
{   
	static SubscriptionBo subsBoImpl;
	static BookmarkBo bookmarkBoImpl;
	static VisitorsBo visitorsBoImpl;
	private static String countryCodes;
	private static MemCacheJSon memCacheJSon = null;
	private static String mscCode;
	private static String prefix;
	private static String suffix;
	
	public void setMemCacheJSon(MemCacheJSon memCacheJSon) {
		Utility.memCacheJSon = memCacheJSon;
	}
	
	public void setSubsBoImpl(SubscriptionBo subsBoImpl) {
		Utility.subsBoImpl = subsBoImpl;
	}
	
	public void setBookmarkBoImpl(BookmarkBo bookmarkBoImpl) {
		Utility.bookmarkBoImpl = bookmarkBoImpl;
	}

	public void setVisitorsBoImpl(VisitorsBo visitorsBoImpl) {
		Utility.visitorsBoImpl = visitorsBoImpl;
	}

	public void setCountryCodes(String countryCodes) {
		Utility.countryCodes = countryCodes;
	}

	public String getMscCode() {
		return mscCode;
	}

	public void setMscCode(String mscCode) {
		this.mscCode = mscCode;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}


	private static Gson gson = new Gson();

	public static int getModValue(String msisdn, int modFactor)
    {
		Logger.sysLog(LogValues.info, Utility.class.getName(),"msisdn = "+msisdn+" , modFactor = "+modFactor);
        int mod = Integer.parseInt(msisdn.substring(msisdn.length()-4))%(modFactor);
        return mod;
    }
    
	public static String numbercorrected(String aPartyMsisdn)
	{	String msisdntruncated = aPartyMsisdn;
		if(countryCodes != null)
		{
			String[] countryCode =  countryCodes.split(",");
			for (int i = 0; i < countryCode.length; i++)
			{
				if(aPartyMsisdn.startsWith(countryCode[i]))
				{
					msisdntruncated = aPartyMsisdn.substring(countryCode[i].length(),aPartyMsisdn.length());
					break;
				}
				else
					msisdntruncated = aPartyMsisdn;
			}	
		}
		return msisdntruncated;
	}
	
	
    public static boolean getPercentage(int percentage)
    {
        boolean flag = false;
        try
        {
            Random randomGenerator = new Random();
            int randomNumber = randomGenerator.nextInt(100);
            if(randomNumber <= percentage)
                flag = true;
            Logger.sysLog(LogValues.info, Utility.class.getName(),"Recording = "+flag);
        }
        catch(Exception e)
        {
            Logger.sysLog(LogValues.error, Utility.class.getName(), coreException.GetStack(e));
        }
        return flag;
    }   
    
    public static String callUrl(String httpurl)
    {
        String httpUrlResponse = "";
        try
        {
            //Logger.sysLog(LogValues.info, this.getClass().getName(),"HTTP url = "+httpurl);
            URL url = new URL(httpurl);
            URLConnection uc = url.openConnection();
            HttpURLConnection con = (HttpURLConnection) uc;     
            InputStream rd = con.getInputStream();
            int c = 0;
            while ((c = rd.read()) != -1)
            {
            	httpUrlResponse+= (char) c;
            }
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.error, Utility.class.getName(), com.bng.core.exception.coreException.GetStack(e));
        }
        return httpUrlResponse.trim();
    }
    
    public static String callUrlWithTimeOut(String httpurl, int timeout)
    {
        String httpUrlResponse = "";
        try
        {
            //Logger.sysLog(LogValues.info, this.getClass().getName(),"HTTP url = "+httpurl);
            URL url = new URL(httpurl);
            URLConnection uc = url.openConnection();
            HttpURLConnection con = (HttpURLConnection) uc;     
            con.setConnectTimeout(timeout);
            InputStream rd = con.getInputStream();
            int c = 0;
            while ((c = rd.read()) != -1)
            {
            	httpUrlResponse+= (char) c;
            }
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.error, Utility.class.getName(), com.bng.core.exception.coreException.GetStack(e));
        }
        return httpUrlResponse.trim();
    }
    
    public static String callUrlWithHeaderResponse(String httpurl, int timeout)
    {
        String httpUrlResponse = "";
        Map<String,List<String>> responseMap = null;
        try
        {
            //Logger.sysLog(LogValues.info, this.getClass().getName(),"HTTP url = "+httpurl);
            URL url = new URL(httpurl);
            
            URLConnection uc = url.openConnection();
            HttpURLConnection con = (HttpURLConnection) uc;     
            con.setConnectTimeout(timeout);
            responseMap=con.getHeaderFields();
            
            for (Map.Entry entry : responseMap.entrySet()) {
            	 Logger.sysLog(LogValues.info, Utility.class.getName(), " "+entry.getKey() + "," + entry.getValue());
			}
            Logger.sysLog(LogValues.info, Utility.class.getName(), "response :"+responseMap.toString());
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.error, Utility.class.getName(), com.bng.core.exception.coreException.GetStack(e));
        }
		return responseMap.toString();
    }
    
    public synchronized static String convertObjectToJsonStr(Object object)
    {    	
    	return gson.toJson(object);
    }
    
    public synchronized static <T> T convertJsonStrToObject(String json, Class<T> classOfT)
    {    	 
    	return gson.fromJson(json,classOfT);
    }
    
    public static byte[] object2byte(Object obj) throws Exception{
            ByteArrayOutputStream byteObject = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteObject);
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            objectOutputStream.reset();
            objectOutputStream.close();
            byteObject.close();
            return byteObject.toByteArray();
    }

    public static Object byte2object(byte[] obj) throws Exception{
            ByteArrayInputStream bais = new ByteArrayInputStream(obj); 
            ObjectInputStream object = new ObjectInputStream(bais);
            bais.close();
            object.close();
            return object.readObject();
    }
    
    public static String getCurrentDate(String dateFormat)
    {    	
	    SimpleDateFormat sdfDate = new SimpleDateFormat(dateFormat);
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;    	
    }
    
    public static Date addDays(Date d, int days)
    {
        d.setTime(d.getTime() + days * 1000 * 60 * 60 * 24);
        return d;
    }
    
    public static Subscription checkSubscription(String msisdn, String shortCode, String serviceName)
    {
    	Subscription subscription = subsBoImpl.checkExistingSubs(msisdn,shortCode,serviceName);
    	return subscription;
    }
    
    public static boolean updateSub(Subscription subscription, String msisdn)
    {
    	return subsBoImpl.updateSubscription(subscription, msisdn);
    }
    
    public static String findLastModified(String directoryPath)
	{
		File LastModifiedFile = null;
		File dir = new File(directoryPath);
		String[] ext = null; 
		File [] file = FileUtils.convertFileCollectionToFileArray(FileUtils.listFiles(dir, ext, false));
		try
		{
			long max = Long.MIN_VALUE;
			for (File f : file) 
			{	
				if (f.lastModified() > max) 
				{
					LastModifiedFile = f;
					max = f.lastModified();
				}
			}
			return directoryPath+"/"+LastModifiedFile.getName();
		}
		catch(Exception ex){
			Logger.sysLog(LogValues.error, Utility.class.getName(), coreException.GetStack(ex));	
			return "";
		}
	} 
    
    public static Properties convertUCDToProp(UserCallDetail ucd)
    {
    	Properties prop = new Properties();
    	Method[] methods = ucd.getClass().getDeclaredMethods();
		for(Method method : methods)
		{
			if(method.getName().startsWith("get") || method.getName().startsWith("is"))
			{
				try 
				{
					Object obj = method.invoke(ucd);
					if(obj != null)
					{
						Logger.sysLog(LogValues.info, Utility.class.getName(), "@#@ key = "+method.getName()+" , value = "+obj);
						if(method.getName().startsWith("get"))						
							prop.put(method.getName().substring("get".length()).toLowerCase(), obj);
						else if(method.getName().startsWith("is"))
							prop.put(method.getName().substring("is".length()).toLowerCase(), obj);
					}
				} 
				catch (Exception e) 
				{
					Logger.sysLog(LogValues.error, Utility.class.getName(), "Caught Exception while loading UCD to properties."+e.getMessage());
				}
			}
		}
		return prop;
    }    
    
    public static String numberCorrection(String ivrCode, int ivrCodeLength)
    {
    	return ivrCode.substring(ivrCodeLength);
    }
    
    public static boolean addBookMark(Event event, UserCallDetail ucd)
    {
    	try
    	{
    		Logger.sysLog(LogValues.info, Utility.class.getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Going to add played bytes in DB for lastFilePlayed = "+ucd.getLastFilePlayed()+" bookmarkBoImpl = "+bookmarkBoImpl);
    	//	bookmarkBoImpl.addBookmark(Utility.numbercorrected(event.getaPartyMsisdn()), Integer.parseInt(event.getTransId().substring(0, event.getTransId().indexOf("_"))), ucd.getLastFilePlayed(), event.getSeekBytes());
    		bookmarkBoImpl.insertSeekWithListID(Utility.numbercorrected(event.getaPartyMsisdn()), Integer.parseInt(event.getTransId().substring(0, event.getTransId().indexOf("_"))), ucd.getLastFilePlayed(), event.getSeekBytes() , ucd.getCurrentPlayListid());
    		return true;
    	}
    	catch(Exception e)
    	{
    		Logger.sysLog(LogValues.error, Utility.class.getName(), coreException.GetStack(e));
    		return false;
    	}
    }
    
    public static boolean updateBookMark(Event event, UserCallDetail ucd)
    {
    	try
    	{
    		Logger.sysLog(LogValues.info, Utility.class.getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Going to update played bytes in DB for lastFilePlayed = "+ucd.getLastFilePlayed()+" bookmarkBoImpl = "+bookmarkBoImpl);
    	//	bookmarkBoImpl.updateBookmark(Utility.numbercorrected(event.getaPartyMsisdn()), Integer.parseInt(event.getTransId().substring(0, event.getTransId().indexOf("_"))), ucd.getLastFilePlayed(), event.getSeekBytes());
    		bookmarkBoImpl.updateSeekWithListID(Utility.numbercorrected(event.getaPartyMsisdn()), Integer.parseInt(event.getTransId().substring(0, event.getTransId().indexOf("_"))), ucd.getLastFilePlayed(), event.getSeekBytes() , ucd.getCurrentPlayListid());
    		return true;
    	}
    	catch(Exception e)
    	{
    		Logger.sysLog(LogValues.error, Utility.class.getName(), coreException.GetStack(e));
    		return false;
    	}
    }
    
    public static boolean updateVisitorsDialCount(String msisdn, String serviceName, String shortCode)
    {
    	Visitors visitors = visitorsBoImpl.getVisitorsDialCount(Utility.numbercorrected(msisdn), serviceName, shortCode);
    	if(visitors == null)
    	{
    		visitorsBoImpl.insertVisitorsDialCount(Utility.numbercorrected(msisdn), serviceName, shortCode);
    	}
    	else
    	{
    		visitors.setCallAttempts(visitors.getCallAttempts()+1);
    		visitors.setLastCallDate(new Date());
    		visitorsBoImpl.updateVisitorDialCount(visitors);
    	}
    	return false;
    }
    
    public static String getTimeOfDay()
    {
    	Date date = new Date();  
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);    
		calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
    	return ""+calendar.get(Calendar.HOUR_OF_DAY);
    }
    
    public static String getDayOfWeek()
    {	
    	Date date = new Date(); 
    	DateFormat format1 = new SimpleDateFormat("EEEE");
		String finalDay=format1.format(date); 	
		return finalDay;
    }
    
    public static void modifyServiceMap(ServiceInfo serviceinfo)
    {	 HashMap ServiceMap = null;
    	 String key;
    	try{
    		ServiceMap = (HashMap) memCacheJSon.get("ServiceMap");
    		if(ServiceMap == null)
    			ServiceMap = new HashMap();		
	    	if(serviceinfo.getOperation().equalsIgnoreCase("ADD"))
	    	{	
	    		
	    		ArrayList<String> keys = serviceinfo.getKeys();
	    		if(keys == null)
	    			ServiceMap.put(serviceinfo.getValue(),serviceinfo.getValue());
	    		
	    		for(String key1 : keys)
	    		{	
	    			ServiceMap.put(key1,serviceinfo.getValue());
	    		}
	    	}
	    	else if(serviceinfo.getOperation().equalsIgnoreCase("DELETE"))
	    	{	
	    		ArrayList<String> keys = serviceinfo.getKeys();
	    		
	    		for(String key1 : keys)
	    		{
	    			ServiceMap.remove(key1);
	    			
	    		}
	    	}
	    	
	    	memCacheJSon.set("ServiceMap",ServiceMap);
	    	
	    	if(serviceinfo.isService() && serviceinfo.getOperation().equalsIgnoreCase("DELETE"))
	    	{
	    		if(serviceinfo.getCalltype().equalsIgnoreCase("obd"))
                	key = serviceinfo.getValue()+"_"+serviceinfo.getCalltype()+"_"+serviceinfo.getServicename();
                else
                	key = serviceinfo.getValue()+"_"+serviceinfo.getCalltype(); 
	    		
	    		memCacheJSon.delete(key+"_startNode");
	    		memCacheJSon.delete(key+"_hm");
	    		memCacheJSon.delete(key+"_ml");
	    		Logger.sysLog(LogValues.info,Utility.class.getName(),"delete key  = "+key+"_startNode");
	    	}
	    	
	    	Logger.sysLog(LogValues.info, Utility.class.getName(), " ServiceMap = "+memCacheJSon.get("ServiceMap"));
	    }
    	catch(Exception e)
    	{
    		Logger.sysLog(LogValues.error,Utility.class.getName(),coreException.GetStack(e));
    	}
    }
    
    public static String getmaxmatch(HashMap<String,String> ServiceMap,String shortcode)
	{
		int i,prevmatched = 0;
		int maxmatchdigit = 0;
		String fullmatch = "";
		Set<String> keys = ServiceMap.keySet();

		for(String key : keys)
		{
			maxmatchdigit = 0;
			for(i=1;i<=key.length();i++)
			{	if(shortcode.length() >= key.length())
				{
					if(key.substring(0,i).equals(shortcode.substring(0,i)) )
					{
						maxmatchdigit = i;
					}
				}	
			}
			
			if(maxmatchdigit ==key.length())
			{	
				if(maxmatchdigit >= prevmatched)
				{ 
					prevmatched=maxmatchdigit;	
					fullmatch = key;
				}
			}

		}
		return fullmatch;
	}
    
    public static void createCdr(Event event, String status, String reason)
    {
    	try
    	{
	    	CallCdr callCdr = new CallCdr();
	    	callCdr.setTpsystemip(event.getIp());
			callCdr.setCesystemip(InetAddress.getLocalHost().getHostAddress());
			callCdr.setHardware(CoreEnums.hardware.values[event.getHardware()].toString());
			callCdr.setProtocol(event.getProtocol());
			if((event.getCallType() == CoreEnums.callType.Incoming.ordinal()) || (event.getCallType() == CoreEnums.callType.Outgoing.ordinal()))
				callCdr.setCalltype(CoreEnums.callType.values[event.getCallType()].toString());
			else
				callCdr.setCalltype(""+event.getCallType());
			callCdr.setCallid(event.getvId());    		
			callCdr.setShortcode(event.getIvrCode());
			callCdr.setAparty(event.getaPartyMsisdn());
			callCdr.setBparty(event.getbPartyMsisdn());
			callCdr.setStartdatetime(getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
			callCdr.setEnddatetime(getCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
			callCdr.setStatus(status);
			callCdr.setReleasereason(reason);
			callCdr.finish();
			Logger.sysLog(LogValues.info, Utility.class.getName(), "CDR create successfully.");
    	}
    	catch(Exception e)
    	{
    		Logger.sysLog(LogValues.error, Utility.class.getName(), coreException.GetStack(e));
    	}
    }
    
    public static String formatBPartyNumber(String bPartyMsisdn)
	{
    	boolean flag = false;
    	if(mscCode != null && !(mscCode.equals("")))
		{
			String mscCodeArr[] = mscCode.split(",");
			for(int i =0; i<mscCodeArr.length;i++)
			{
				if(bPartyMsisdn.startsWith(mscCodeArr[i]))
				{
					if(suffix != null && !(suffix.equals("")))
					{
						bPartyMsisdn = bPartyMsisdn.substring(mscCodeArr[i].length(), bPartyMsisdn.length());
						bPartyMsisdn = mscCodeArr[i]+suffix+bPartyMsisdn;
					}
					if(prefix != null && !(prefix.equals("")))
						bPartyMsisdn = prefix+bPartyMsisdn;
					flag = true;
					break;
				}				
			}
			
			if(!flag)
			{
				if(suffix != null && !(suffix.equals("")))
					bPartyMsisdn = bPartyMsisdn+suffix;
				if(prefix != null && !(prefix.equals("")))
					bPartyMsisdn = prefix+bPartyMsisdn;
			}
		}
		else
		{
			if(suffix != null && !(suffix.equals("")))
				bPartyMsisdn = bPartyMsisdn+suffix;
			if(prefix != null && !(prefix.equals("")))
				bPartyMsisdn = prefix+bPartyMsisdn;
		}
		return bPartyMsisdn;
	}
    
    
    public static String generatetransid(int idsize , String numbertoappend ,String transidformat)
    {
    	String transid = getCurrentDate(transidformat);
    	String addstring = "";
    	try
    	{
    		if(transid.length() < idsize)
    		{
    			int addlength = idsize - transid.length();
    			if(numbertoappend.length() > addlength)
    				addstring = numbertoappend.substring((numbertoappend.length() - addlength), numbertoappend.length());
    			transid = transid + addstring;
    		}
    		Logger.sysLog(LogValues.info, Utility.class.getName(), "transid ="+transid+" , length ="+transid.length());
    	}
    	catch(Exception e)
    	{
    		Logger.sysLog(LogValues.error,Utility.class.getName(),coreException.GetStack(e));
    	}
		return transid;
    }
}
    
