package com.bng.core.cdr;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.bng.core.queue.QueueConnection;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.Utility;

@XmlRootElement(name = "bngcallcdr")
@XmlAccessorType(XmlAccessType.FIELD)
public class BngCallCdr {

	@XmlElement(name = "tpsystemip")
	private String tpsystemip;

	@XmlElement(name = "cesystemip")
	private String cesystemip;

	@XmlElement(name = "hardware")
	private String hardware;

	@XmlElement(name = "protocol")
	private String protocol;

	@XmlElement(name = "calltype")
	private String calltype;

	@XmlElement(name = "cic")
	private int cic;

	@XmlElement(name = "callid")
	private int callid;

	@XmlElement(name = "aparty")
	private String aparty;

	@XmlElement(name = "bparty")
	private String bparty;

	@XmlElement(name = "shortcode")
	private String shortcode;

	@XmlElement(name = "servicename")
	private String servicename;

	@XmlElement(name = "timeZone")
	private String timeZone;

	@XmlElement(name = "country")
	private String country;

	@XmlElement(name = "operator")
	private String operator;

	@XmlElement(name = "circle")
	private String circle;

	@XmlElement(name = "startdatetime")
	private String startdatetime;

	@XmlElement(name = "enddatetime")
	private String enddatetime;

	@XmlElement(name = "status")
	private String status;

	@XmlElement(name = "releasereason")
	private String releasereason;
	
	@XmlElement(name="pickupdatetime")
	private String pickupdatetime;
	
	@XmlElement(name="directcall")
	private String directcall;

	@XmlElementWrapper(name = "dtmfs")
	@XmlElement(name = "dtmf", type = Dtmf.class)
	private ArrayList<Dtmf> dtmfList ;//= new ArrayList<Dtmf>();

	@XmlElementWrapper(name = "play")
	@XmlElement(name = "media", type = Media.class)
	private ArrayList<Media> mediaList ;//= new ArrayList<Media>();

	@XmlElementWrapper(name = "callconference")
	@XmlElement(name = "callconf", type = Callconf.class)
	private ArrayList<Callconf> callconfList ;//= new ArrayList<Callconf>();

	@XmlElementWrapper(name = "urls")
	@XmlElement(name = "url", type = Url.class)
	private ArrayList<Url> urlList ;//= new ArrayList<Url>();
	
	@XmlElement(name = "userstatus")
	private String userstatus;
	
	@XmlElementWrapper(name = "specialeffects")
	@XmlElement(name = "specialeffect", type = specialeffect.class)
	private ArrayList<specialeffect> specialeffectList; 
	
//	@XmlElementWrapper(name = "answers")
	//@XmlElement(name = "answer", type = AnswerListcdr.class)
	//private ArrayList<AnswerListcdr> answerList; 
	// @XmlElementWrapper(name = "bills")
	// @XmlElement(name = "bill", type = Bill.class)
	// private ArrayList<Bill> billList;
	//
	// @XmlElementWrapper(name = "smss")
	// @XmlElement(name = "sms", type = Sms.class)
	// private ArrayList<Sms> smsList;

	
	public BngCallCdr() {
		
	}
	
	public BngCallCdr(String tpsystemip, String cesystemip, String hardware,
			String protocol, String calltype, int cic, int callid,
			String aparty, String bparty, String shortcode, String servicename,
			String timeZone, String country, String operator, String circle,
			String startdatetime, String enddatetime, String status,
			String releasereason,String pickupdatetime,String directcall,String userstatus) {
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
		this.pickupdatetime = pickupdatetime;
		this.directcall = directcall;
		this.userstatus = userstatus;
	}

	public void finish(BngCallCdr bngCallCdr) {
		try {
			Logger.sysLog(LogValues.info, this.getClass().getName(), "reached bngfinish");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			CdrSendObject cdrSendObject = new CdrSendObject();
			JAXBContext jaxbContext = JAXBContext.newInstance(BngCallCdr.class);

			Marshaller m = jaxbContext.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(bngCallCdr, baos);

			cdrSendObject.setXml(baos.toString());
			cdrSendObject.setDate(enddatetime);
			String filename = "";
			if(calltype.equalsIgnoreCase("outgoing"))
				filename = System.currentTimeMillis() + "_" + callid + "_" + bparty + ".xml";
			else
				filename = System.currentTimeMillis() + "_" + callid + "_" + aparty + ".xml";
			cdrSendObject.setFilename(filename);
			Logger.sysLog(LogValues.info, this.getClass().getName(), "sending cdr message for ["+aparty +"] on path ["+cdrSendObject.getFilename()+"]" );
			SendToTelephony
					.addMessageToQueue(
							QueueConnection.getCdrQueueList().get(
									(int) Math.random()
											* QueueConnection.getCdrQueueList()
													.size()),
							Utility.convertObjectToJsonStr(cdrSendObject));
		} catch (JAXBException e) {
			Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
		}
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

	public ArrayList<Dtmf> getDtmfList() {
		return dtmfList;
	}

	public void setDtmfList(ArrayList<Dtmf> dtmfList) {
		this.dtmfList = dtmfList;
	}

	public ArrayList<Media> getMediaList() {
		return mediaList;
	}

	public void setMediaList(ArrayList<Media> mediaList) {
		this.mediaList = mediaList;
	}

	public ArrayList<Callconf> getCallconfList() {
		return callconfList;
	}

	public void setCallconfList(ArrayList<Callconf> callconfList) {
		this.callconfList = callconfList;
	}

	public ArrayList<Url> getUrlList() {
		return urlList;
	}

	public void setUrlList(ArrayList<Url> urlList) {
		this.urlList = urlList;
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
	
	public ArrayList<specialeffect> getSpecialeffectList() {
		return specialeffectList;
	}

	public void setSpecialEffectList(ArrayList<specialeffect> specialeffectList) {
		this.specialeffectList = specialeffectList;
	}
	
	/*public ArrayList<AnswerListcdr> getAnswerList() {
		return answerList;
	}

	public void setAnswerList(ArrayList<AnswerListcdr> answerList) {
		this.answerList = answerList;
	}
*/
	@Override
	public String toString() {
		 String s = "BngCallCdr [tpsystemip=" + tpsystemip + ", cesystemip="
				+ cesystemip + ", hardware=" + hardware + ", protocol="
				+ protocol + ", calltype=" + calltype + ", cic=" + cic
				+ ", callid=" + callid + ", aparty=" + aparty + ", bparty="
				+ bparty + ", shortcode=" + shortcode + ", servicename="
				+ servicename + ", timeZone=" + timeZone + ", country="
				+ country + ", operator=" + operator + ", circle=" + circle
				+ ", startdatetime=" + startdatetime + ", enddatetime="
				+ enddatetime + ", status=" + status + ", releasereason="
				+ releasereason; 
				
				if(dtmfList != null)
				s+= ", dtmfList=" + dtmfList.toString();
				if(mediaList != null)
				s+= ", mediaList=" + mediaList.toString();
				if(callconfList != null)
				s+= ", callconfList=" + callconfList.toString();
				if(urlList != null)
				s+= ", urlList="+ urlList.toString();
				if(specialeffectList != null)
				s+= ", specialeffectList="+ specialeffectList.toString()+ "]";
				
				return s;
	}

	// public ArrayList<Bill> getBillList() {
	// return billList;
	// }
	//
	// public void setBillList(ArrayList<Bill> billList) {
	// this.billList = billList;
	// }
	//
	// public ArrayList<Sms> getSmsList() {
	// return smsList;
	// }
	//
	// public void setSmsList(ArrayList<Sms> smsList) {
	// this.smsList = smsList;
	// }

}
