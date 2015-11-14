/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.bean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richa
 */
@XmlRootElement(namespace = "MxParams")  
@XmlAccessorType(XmlAccessType.FIELD)
public class MxParam implements Serializable{
	
	@XmlAttribute(name = "shortcode")
	private String shortcode = "";

	@XmlAttribute(name = "service")
	private String service = "";

	@XmlAttribute(name = "previousdtmf")
	private String previousdtmf = "";

	@XmlAttribute(name = "nextdtmf")
	private String nextdtmf = "";

	@XmlAttribute(name = "promptfile")
	private String promptfile = "";

	@XmlAttribute(name = "contentlist")
	private String playcontent = "";

	@XmlAttribute(name = "checkdb")
	private String checkdb = "";

	@XmlAttribute(name = "calltype")
	private String calltype = "";

	@XmlAttribute(name = "startoverdtmf")
	private String startoverdtmf = "";

	@XmlAttribute(name = "url")
	private String url = "";

	@XmlAttribute(name = "addtofavdtmf")
	private String addtofavdtmf = "";

	@XmlAttribute(name = "defaultlang")
	private String defaultlang = "";

	@XmlAttribute(name = "method")
	private String method = "";

	@XmlAttribute(name = "patch")
	private String patch = "";

	@XmlAttribute(name = "contentlist")
	private String contentlist = "";

	@XmlAttribute(name = "stoprecord")
	private String stoprecord = "";

	@XmlAttribute(name = "sleep")
	private String sleep = "";

	@XmlAttribute(name = "cli")
	private String cli = "";

	@XmlAttribute(name = "dialout")
	private String dialout = "";

	@XmlAttribute(name = "recording")
	private String recording = "";

	@XmlAttribute(name = "repeatcount")
	private String repeatcount = "";

	@XmlAttribute(name = "repeatcurrent")
	private String repeatcurrent = "";

	@XmlAttribute(name = "minlen")
	private String minlen = "";

	@XmlAttribute(name = "maxlen")
	private String maxlen = "";

	@XmlAttribute(name = "exittimer")
	private String exittimer = "";

	@XmlAttribute(name = "timeout")
	private String timeout = "";

	@XmlAttribute(name = "seekdtmf")
	private String seekdtmf = "";

	@XmlAttribute(name = "autoanswer")
	private boolean autoanswer;

	@XmlAttribute(name = "randomplay")
	private boolean randomplay;

	@XmlAttribute(name = "seek")
	private boolean seek;

	@XmlAttribute(name = "bargein")
	private boolean bargein;

	@XmlAttribute(name = "type")
	private String type = "";

	@XmlAttribute(name = "cutonringing")
	private boolean cutonringing;

	@XmlAttribute(name = "resourceurl")
	private String resourceurl = "";

	@XmlAttribute(name = "confirmlist")
	private String confirmlist = "";

	@XmlAttribute(name = "terminationchar")
	private String terminationchar = "";

	@XmlAttribute(name = "confirmation")
	private boolean confirmation;

	@XmlAttribute(name = "digittype")
	private String digittype = "";

	@XmlAttribute(name = "exit")
	private String exit ="";
	    
    @XmlAttribute(name="dialouttype")
    private int dialouttype;
    
    @XmlAttribute(name="dialouttime")
    private int dialouttime;
    
    @XmlAttribute(name="value")
    private String value = "";
    
    @XmlAttribute(name="contentid")
    private String contentId = "";

    @XmlAttribute(name="urltype")
    private String urlType ="";
    
    @XmlAttribute(name="startfrom")
    private String startFrom = "";
    
    @XmlAttribute(name="live")
    private String liveService = "";
    
    @XmlAttribute(name="timertype")
    private String timerType ="";
    
    @XmlAttribute(name="timervalue")
    private String timerValue ="";
    
    @XmlAttribute(name="downloaddtmf")
    private String downloaddtmf ="";
    
    @XmlAttribute(name="downloadurl")
    private String downloadurl ="";
    
    @XmlAttribute(name="crbtdtmf")
    private String crbtdtmf ="";
    
    @XmlAttribute(name="crbturl")
    private String crbturl ="";
    
    @XmlAttribute(name="voicefreq")
    private String voicefreq ="";
    
    @XmlAttribute(name="time")
    private String time ="";
    
    @XmlAttribute(name="callmode")
    private String callmode = "";
    
    @XmlAttribute(name="synchronous")
    private String synchronous = "";
    
    @XmlAttribute(name="consentgateway")
    private String consentgateway = "";
    
    @XmlAttribute(name = "patchwith")
    private String patchwith = "";
    
    @XmlAttribute(name="cgcli")
    private String cgcli = "";
    
    @XmlAttribute(name="savefilefor")
    private String savefilefor = "";
    
    @XmlAttribute(name="savefiledtmf")
    private String savefiledtmf = "";
    
    @XmlAttribute(name="servicename")
    private String servicename = "";

    @XmlAttribute(name="optiondealing")
    private String optiondealing = "";
    
    @XmlAttribute(name="mintime")
    private int mintime ;
    
    @XmlAttribute(name="maxtime")
    private int maxtime ;
    
    @XmlAttribute(name="dtmfvalue")
    private String dtmfvalue ;
    
    @XmlAttribute(name="percentage")
    private int percentage ;
    
    @XmlAttribute(name="dialbg")
    private String dialbg ;
    
    @XmlAttribute(name="maxcount")
    private int maxcount ;
    
    @XmlAttribute(name="user")
    private int user;
    
    @XmlAttribute(name="subservicename")
    private String subservicename;
    
    @XmlAttribute(name="bpartyminlength")
    private String bpartyminlength;
    
    @XmlAttribute(name="validate")
    private String validate;
    
    @XmlAttribute(name="ioccode")
    private String ioccode;
    
    @XmlAttribute(name="prefixioc")
    private String prefixioc;
    
    @XmlAttribute(name="suffixioc")
    private String suffixioc;
    
    @XmlAttribute(name="iocaction")
    private String iocaction;
    
    @XmlAttribute(name="countrycode")
    private String countrycode;
    
    @XmlAttribute(name="prefixconcode")
    private String prefixconcode;
    
    @XmlAttribute(name="suffixconcode")
    private String suffixconcode;
    
    @XmlAttribute(name="conaction")
    private String conaction;
    
    @XmlAttribute(name="apartylength")
    private String apartylength;
    
    @XmlAttribute(name="bpartylength")
    private String bpartylength;
    
    @XmlAttribute(name = "recorddedication")
	private boolean recorddedication;
    
    @XmlAttribute(name = "recordenddtmf")
   	private String recordenddtmf;
    
    @XmlAttribute(name = "vxmlurl")
  	private String vxmlurl;
    
    @XmlAttribute(name = "bpartyprefix")
  	private String bpartyprefix;
    
    @XmlAttribute(name = "dynamicplayurl")
  	private String dynamicplayurl;
    
    @XmlAttribute(name = "jumpfor")
  	private String jumpfor;
    
    @XmlAttribute(name = "singlebookmark")
  	private boolean singlebookmark;
    
    @XmlAttribute(name = "allowgreater")
  	private boolean allowgreater;
    
    @XmlAttribute(name = "playlistnumber")
  	private int playlistnumber;
    
    @XmlAttribute(name = "playlist")
  	private String playlist;
    
    @XmlAttribute(name = "nextbookdtmf")
  	private String nextbookdtmf;
    
    @XmlAttribute(name = "previousbookdtmf")
  	private String previousbookdtmf;
    
    @XmlAttribute(name = "invalidcharacters")
  	private String invalidcharacters;
    
    @XmlAttribute(name = "checkfirst")
  	private String checkfirst;
    
    @XmlAttribute(name = "bpartyprefixaction")
  	private String bpartyprefixaction;
    
    @XmlAttribute(name = "probability")
  	private String probability;
    
    
	public String getSubservicename() {
		return subservicename;
	}

    public String getDialbg() {
		return dialbg;
	}

	public String getShortcode() {
		return shortcode;
	}

	public String getService() {
		return service;
	}

	public String getPreviousdtmf() {
		return previousdtmf;
	}

	public String getNextdtmf() {
		return nextdtmf;
	}

	public String getPromptfile() {
		return promptfile;
	}

	public String getPlaycontent() {
		return playcontent;
	}

	public String getCheckdb() {
		return checkdb;
	}

	public String getCalltype() {
		return calltype;
	}

	public String getStartoverdtmf() {
		return startoverdtmf;
	}

	public String getUrl() {
		return url;
	}

	public String getAddtofavdtmf() {
		return addtofavdtmf;
	}

	public String getDefaultlang() {
		return defaultlang;
	}

	public String getMethod() {
		return method;
	}

	public String getPatch() {
		return patch;
	}

	public String getContentlist() {
		return contentlist;
	}

	public String getStoprecord() {
		return stoprecord;
	}

	public String getSleep() {
		return sleep;
	}

	public String getCli() {
		return cli;
	}

	public String getRecording() {
		return recording;
	}

	public String getRepeatcount() {
		return repeatcount;
	}

	public String getRepeatcurrent() {
		return repeatcurrent;
	}

	public String getMinlen() {
		return minlen;
	}

	public String getMaxlen() {
		return maxlen;
	}

	public String getExittimer() {
		return exittimer;
	}

	public String getTimeout() {
		return timeout;
	}

	public String getSeekdtmf() {
		return seekdtmf;
	}

	public boolean isAutoanswer() {
		return autoanswer;
	}

	public boolean isRandomplay() {
		return randomplay;
	}

	public boolean isSeek() {
		return seek;
	}

	public boolean isBargein() {
		return bargein;
	}

	public String getType() {
		return type;
	}

	public boolean isCutonringing() {
		return cutonringing;
	}

	public String getResourceurl() {
		return resourceurl;
	}

	public String getConfirmlist() {
		return confirmlist;
	}

	public String getTerminationchar() {
		return terminationchar;
	}

	public boolean isConfirmation() {
		return confirmation;
	}

	public String getDigittype() {
		return digittype;
	}

	public String getExit() {
		return exit;
	}

	public String getDialout() {
		return dialout;
	}

	public int getDialouttype() {
		return dialouttype;
	}
	
	public int getDialouttime() {
		return dialouttime;
	}
	

	public String getValue() {
		return value;
	}

	public String getContentId() {
		return contentId;
	}

	public String getUrlType() {
		return urlType;
	}

	public String getStartFrom() {
		return startFrom;
	}

	public String getLiveService() {
		return liveService;
	}

	public String getTimerType() {
		return timerType;
	}

	public String getTimerValue() {
		return timerValue;
	}

	public String getDownloaddtmf() {
		return downloaddtmf;
	}

	public String getDownloadurl() {
		return downloadurl;
	}

	public String getCrbtdtmf() {
		return crbtdtmf;
	}

	public String getCrbturl() {
		return crbturl;
	}

	public String getVoicefreq() {
		return voicefreq;
	}

	public String getTime() {
		return time;
	}

	public String getCallmode() {
		return callmode;
	}

	public String getSynchronous() {
		return synchronous;
	}

	public String getConsentgateway() {
		return consentgateway;
	}

	public String getPatchwith() {
		return patchwith;
	}

	public String getCgcli() {
		return cgcli;
	}

	public String getSavefilefor() {
		return savefilefor;
	}

	public String getSavefiledtmf() {
		return savefiledtmf;
	}

	public String getServicename() {
		return servicename;
	}

	public String getOptiondealing() {
		return optiondealing;
	}

	public int getMintime() {
		return mintime;
	}

	public int getMaxtime() {
		return maxtime;
	}

	public String getDtmfvalue() {
		return dtmfvalue;
	}

	public int getPercentage() {
		return percentage;
	}

	public int getMaxcount() {
		return maxcount;
	}

	public int getUser() {
		return user;
	}

	public String getBpartyminlength() {
		return bpartyminlength;
	}

	public String getIoccode() {
		return ioccode;
	}

	public String getPrefixioc() {
		return prefixioc;
	}

	public String getSuffixioc() {
		return suffixioc;
	}

	public String getIocaction() {
		return iocaction;
	}

	public String getCountrycode() {
		return countrycode;
	}

	public String getPrefixconcode() {
		return prefixconcode;
	}

	public String getSuffixconcode() {
		return suffixconcode;
	}

	public String getConaction() {
		return conaction;
	}

	public String getApartylength() {
		return apartylength;
	}

	public String getBpartylength() {
		return bpartylength;
	}

	public String getValidate() {
		return validate;
	}

	public boolean isRecorddedication() {
		return recorddedication;
	}

	public String getRecordenddtmf() {
		return recordenddtmf;
	}
	
	public String getVxmlurl() {
		return vxmlurl;
	}

	public String getBpartyprefix() {
		return bpartyprefix;
	}

	public String getDynamicplayurl() {
		return dynamicplayurl;
	}

	public String getJumpfor() {
		return jumpfor;
	}

	public boolean isSinglebookmark() {
		return singlebookmark;
	}	
	
	public boolean isAllowgreater() {
		return allowgreater;
	}
	
	public int getPlaylistnumber() {
		return playlistnumber;
	}

	public String getPlaylist() {
		return playlist;
	}

	public String getNextbookdtmf() {
		return nextbookdtmf;
	}

	public String getPreviousbookdtmf() {
		return previousbookdtmf;
	}

	public String getInvalidcharacters() {
		return invalidcharacters;
	}

	public String getCheckfirst() {
		return checkfirst;
	}

	public String getBpartyprefixaction() {
		return bpartyprefixaction;
	}

	public String getProbability() {
		return probability;
	}
	
}