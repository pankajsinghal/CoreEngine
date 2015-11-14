package com.bng.core.coreCdrBean;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.bng.core.cdr.BngCallCdr;
import com.bng.core.cdr.Callconf;
import com.bng.core.cdr.Dtmf;
import com.bng.core.cdr.Media;
import com.bng.core.cdr.Url;
import com.bng.core.cdr.specialeffect;
import com.bng.core.exception.coreException;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

public class CallCdr implements Serializable{

	private static final long serialVersionUID = 1L;
	private String tpsystemip;
	private String cesystemip;
	private String hardware;
	private String protocol;
	private String calltype;
	private int cic;
	private int callid;
	private String aparty;
	private String bparty;
	private String shortcode;
	private String servicename;
	private String timeZone;
	private String country;
	private String operator;
	private String circle;
	private String startdatetime;
	private String enddatetime;
	private String status;
	private String releasereason;
	private String pickupdatetime;
	private String directcall;
	private ArrayList<DtmfCdr> dtmfList = new ArrayList<DtmfCdr>();
	private ArrayList<MediaCdr> mediaList = new ArrayList<MediaCdr>();
	private ArrayList<CallconfCdr> callconfList = new ArrayList<CallconfCdr>();
	private ArrayList<UrlCdr> urlList = new ArrayList<UrlCdr>();
	private HashMap<String,MediaCdr> mediaMap = new  HashMap<String,MediaCdr>();
	private ArrayList<SpecialEffectCdr> SpecialEffectList = new ArrayList<SpecialEffectCdr>();
	private HashMap<String,CallconfCdr> callconfMap = new HashMap<String, CallconfCdr>();
	private String userstatus;

	public CallCdr() {
	}

	public CallCdr(String tpsystemip, String cesystemip, String hardware,
			String protocol, String calltype, int cic, int callid,
			String aparty, String bparty, String shortcode, String servicename,
			String timeZone, String country, String operator, String circle,
			String startdatetime, String enddatetime, String status,
			String releasereason,String pickupDateTime, String directcall, String userstatus) {
		super();
		this.tpsystemip = tpsystemip;
		this.cesystemip = cesystemip;
		this.hardware = hardware;
		this.protocol = protocol;
		this.calltype = calltype;
		this.cic = cic;
		this.callid = callid;
		this.aparty = aparty;
		this.bparty = bparty;
		this.shortcode = shortcode;
		this.servicename = servicename;
		this.timeZone = timeZone;
		this.country = country;
		this.operator = operator;
		this.circle = circle;
		this.startdatetime = startdatetime;
		this.enddatetime = enddatetime;
		this.status = status;
		this.releasereason = releasereason;
		this.pickupdatetime = pickupDateTime;
		this.directcall = directcall;
		this.userstatus = userstatus;
	}

	public void finish() {
		Logger.sysLog(LogValues.debug, this.getClass().getName(), "Inside Finish method of callCdr.");
		if(mediaMap.size() >0)
		{
			Logger.sysLog(LogValues.debug, this.getClass().getName(), "Some MediaCdr doesn't have endTime. Remaining mediaCdr = "+mediaMap.size());
			Set<String> keySet = mediaMap.keySet();
			Iterator<String> setIter = keySet.iterator();
			while(setIter.hasNext())
			{
				MediaCdr mediaCdr = mediaMap.get(setIter.next());
				addMediaList(mediaCdr);
			}		
			Logger.sysLog(LogValues.debug, this.getClass().getName(), "MediaCdr added successfully in arrayList");
		}
		BngCallCdr bngCallCdr = new BngCallCdr();
		Field[] fields = this.getClass().getDeclaredFields();
		Logger.sysLog(LogValues.debug, this.getClass().getName(), "fields length : " + fields.length);

		for (Field field : fields) {
			if(field.getName().equalsIgnoreCase("serialVersionUID"))
				continue;
			String methodName = "set"
					+ Character.toUpperCase(field.getName().charAt(0))
					+ field.getName().substring(1);
			//Logger.sysLog(LogValues.info, this.getClass().getName(), "calling the following setter " + methodName);
			Method method = null;
			try {
				Object object = null;
				if(field.getType() == HashMap.class)
					continue;
				method = bngCallCdr.getClass().getDeclaredMethod(methodName,field.getType());
				
				if (field.getType() == ArrayList.class && ((ArrayList)field.get(this)).size()>0) {
					String s = ((ArrayList)field.get(this)).get(0).getClass().getSimpleName().toLowerCase();
					Logger.sysLog(LogValues.debug, this.getClass().getName(), "Calling class :"+s);
					switch (s) {
					case "urlcdr":
						object = getNewObject(field.get(this),
								UrlCdr.class, Url.class);
						break;
					case "callconfcdr":
						object = getNewObject(field.get(this),
								CallconfCdr.class, Callconf.class);
						break;
					case "dtmfcdr":
						object = getNewObject(field.get(this),
								DtmfCdr.class, Dtmf.class);
						break;
					case "mediacdr":
						object = getNewObject(field.get(this),
								MediaCdr.class, Media.class);
						break;
					case "specialeffectcdr":
						object = getNewObject(field.get(this),
								SpecialEffectCdr.class, specialeffect.class);
						break;
					default:
						break;
					}
					method.invoke(bngCallCdr, object);
				}
				else if(field.getType() != ArrayList.class )
					method.invoke(bngCallCdr, field.get(this));
			} catch (NoSuchMethodException | SecurityException
					| IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				Logger.sysLog(LogValues.error, this.getClass().getName(), "caught exception"+coreException.GetStack(e));
			}
		}
		Logger.sysLog(LogValues.debug, this.getClass().getName(), ""+bngCallCdr.toString());
		bngCallCdr.finish(bngCallCdr);
		Logger.sysLog(LogValues.debug, this.getClass().getName(), "getting out of Finish method of callCdr.");
	}

	private <T, D> Object getNewObject(Object object, Class<D> declaringClass,
			Class<T> targetClass) {
		ArrayList<T> newlist = new ArrayList<T>();
		if (object instanceof ArrayList) {
			ArrayList<D> originalList = (ArrayList) object;
			for (D original : originalList) {
				T newobj = null;
				try {
					newobj = targetClass.newInstance();
				} catch (InstantiationException | IllegalAccessException e1) {
					Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e1));
				}
				Field[] fields = declaringClass.getDeclaredFields();
				for (Field field : fields) {
					String methodName = "set"
							+ Character.toUpperCase(field.getName().charAt(0))
							+ field.getName().substring(1);
					Logger.sysLog(LogValues.debug, this.getClass().getName(), "nested calling the following setter "
							+ methodName);
					Method method;
					try {

						method = newobj.getClass().getDeclaredMethod(
								methodName, field.getType());
						String getterMethodName = "get"
								+ Character.toUpperCase(field.getName().charAt(0))
								+ field.getName().substring(1);
						Method getterMethod = original.getClass().getDeclaredMethod(getterMethodName);
						method.invoke(newobj, getterMethod.invoke(original));
					} catch (NoSuchMethodException | SecurityException
							| IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
					}
				}
				newlist.add(newobj);
				Logger.sysLog(LogValues.debug, this.getClass().getName(), "new element added"); 
			}
			return newlist;
		}
		else
			return newlist;
	}

	public String getTpsystemip() {
		return tpsystemip;
	}

	public void setTpsystemip(String tpsystemip) {
		this.tpsystemip = tpsystemip;
	}

	public String getCesystemip() {
		return cesystemip;
	}

	public void setCesystemip(String cesystemip) {
		this.cesystemip = cesystemip;
	}

	public String getHardware() {
		return hardware;
	}

	public void setHardware(String hardware) {
		this.hardware = hardware;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getCalltype() {
		return calltype;
	}

	public void setCalltype(String calltype) {
		this.calltype = calltype;
	}

	public int getCic() {
		return cic;
	}

	public void setCic(int cic) {
		this.cic = cic;
	}

	public int getCallid() {
		return callid;
	}

	public void setCallid(int callid) {
		this.callid = callid;
	}

	public String getAparty() {
		return aparty;
	}

	public void setAparty(String aparty) {
		this.aparty = aparty;
	}

	public String getBparty() {
		return bparty;
	}

	public void setBparty(String bparty) {
		this.bparty = bparty;
	}

	public String getShortcode() {
		return shortcode;
	}

	public void setShortcode(String shortcode) {
		this.shortcode = shortcode;
	}

	public String getServicename() {
		return servicename;
	}

	public void setServicename(String servicename) {
		this.servicename = servicename;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getCircle() {
		return circle;
	}

	public void setCircle(String circle) {
		this.circle = circle;
	}

	public String getStartdatetime() {
		return startdatetime;
	}

	public void setStartdatetime(String startdatetime) {
		this.startdatetime = startdatetime;
	}

	public String getEnddatetime() {
		return enddatetime;
	}

	public void setEnddatetime(String enddatetime) {
		this.enddatetime = enddatetime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
		 Logger.sysLog(LogValues.info, this.getClass().getName(), "_______________________________________________________________ main cdr : status ="+status);
	}

	public String getReason() {
		return releasereason;
	}

	public void setReason(String releasereason) {
		Logger.sysLog(LogValues.info, this.getClass().getName(),"_______________________________________________________________ main cdr :releasereason = "+releasereason);
		this.releasereason = releasereason;
	}	

	public String getReleasereason() {
		return releasereason;
	}

	public void setReleasereason(String releasereason) {
		this.releasereason = releasereason;
	}

	public String getPickupdatetime() {
		return pickupdatetime;
	}

	public void setPickupdatetime(String pickupdatetime) {
		this.pickupdatetime = pickupdatetime;
	}

	public ArrayList<DtmfCdr> getDtmfList() {
		return dtmfList;
	}

	public void setDtmfList(ArrayList<DtmfCdr> dtmfList) {
		this.dtmfList = dtmfList;
	}

	public ArrayList<MediaCdr> getMediaList() {
		return mediaList;
	}

	public void setMediaList(ArrayList<MediaCdr> mediaList) {
		this.mediaList = mediaList;
	}

	public ArrayList<CallconfCdr> getCallconfList() {
		return callconfList;
	}

	public void setCallconfList(ArrayList<CallconfCdr> callconfList) {
		this.callconfList = callconfList;
	}

	public CallconfCdr getCallconf() {
		if(callconfList.size()>0)
			return callconfList.get(0);
		else
			return null;
	}
	public ArrayList<UrlCdr> getUrlList() {
		return urlList;
	}

	public void setUrlList(ArrayList<UrlCdr> urlList) {
		this.urlList = urlList;
	}

	public void addUrlList(UrlCdr urlCdr) {		
		urlList.add(urlCdr);
	}

	public void addDtmfList(DtmfCdr dtmf) {		
		dtmfList.add(dtmf);
	}

	public void addMediaList(MediaCdr media) {		
		mediaList.add(media);		
	}

	public void addCallConfList(CallconfCdr callconf) {		
		callconfList.add(callconf);
	}
	
	public void addCallConfMap(String key, CallconfCdr callconfcdr)
	{
		Logger.sysLog(LogValues.info, this.getClass().getName(),"addcallconfmap key = "+key+" callconfcdr = "+callconfcdr);
		callconfMap.put(key, callconfcdr);
	}
	public CallconfCdr getCallConfCdr(String key)
	{
		Logger.sysLog(LogValues.info, this.getClass().getName(),"getcallconf from map key = "+key+" callconfcdr = "+ callconfMap.get(key));
		return callconfMap.get(key);
	}
	
	public void  removecallconfCdr(String key) {
		callconfList.add(callconfMap.get(key));
		callconfMap.remove(key);
		Logger.sysLog(LogValues.info, this.getClass().getName(), "callconfList array size = "+callconfList.size()+", key = "+key);
	}

	
	public void addMediaMap(String key, MediaCdr mediaCdr)
	{
		Logger.sysLog(LogValues.info, this.getClass().getName(),"key = "+key+" , mediaCdr ="+mediaCdr);
		mediaMap.put(key, mediaCdr);
	}
	
	public MediaCdr getMediaCdr(String key)
	{
		return mediaMap.get(key);
	}
	
	public void removeMedia(String key)
	{
		mediaList.add(mediaMap.get(key));
		mediaMap.remove(key);
		Logger.sysLog(LogValues.debug, this.getClass().getName(), "mediaList array size = "+mediaList.size()+", key = "+key);
	}

	public String getDirectcall() {
		return directcall;
	}

	public void setDirectcall(String directcall) {
		this.directcall = directcall;
	}

	public String getUserstatus() {
		return userstatus;
	}

	public void setUserstatus(String userstatus) {
		this.userstatus = userstatus;
	}

	public ArrayList<SpecialEffectCdr> getSpecialEffectList() {
		return SpecialEffectList;
	}

	public void setSpecialEffectList(ArrayList<SpecialEffectCdr> specialEffectList) {
		SpecialEffectList = specialEffectList;
	}	
	
	public void addspecialeffectList(SpecialEffectCdr specialeffect) {		
		SpecialEffectList.add(specialeffect);
	}

	
}
