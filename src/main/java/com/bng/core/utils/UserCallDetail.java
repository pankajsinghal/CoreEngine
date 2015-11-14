/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.utils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.bng.core.coreCdrBean.CallCdr;
import com.bng.core.parser.NameValuePair;

/**
 *
 * @author richa
 */
public class UserCallDetail implements Serializable{
	private String callStartTime;
	private String callEndTime;
    private int currentState;    
    private int numberOfFileInPlay = 0;
    private int numberOfFilesPlayed = 0;
    
    private int timeOutForNP;
    private String npTimerId;
    private boolean npTimerFlag = false;
    private int nprepeatcount = 0;
    private String freeMinTimerId;
    private boolean freeMinTimerFlag =false;
    private int freeMinTimerCellId = 0;
    
    private String coreTimer;
    private boolean coreTimerFlag = false;
    
    private String masterTimerId;
    private boolean masterTimerFlag = false;
    private int masterTimerInterval = 0;
    private int masterTimerCellId = 0;
    
    private String recordTimer;
    private boolean recordTimerFlag = false;
    
    private boolean nextStateFlag = true;
    private boolean executeCurrentState = false;
    
    private String key;
    private String service;
    
    private int aPartyVId;
    private String aPartyMsisdn;
    
    private int bPartyVId;
    private String bPartyMsisdn;
    
    private ArrayList<UserCallDetail> ucdList = new ArrayList<UserCallDetail>();
    private UserCallDetail ucd;
    
    private String defLang = "_e";
    private int repeateVal = 0;
    private boolean acceptBPartyDtmf = false;
    private int recording = 0;
    private boolean copyCallDetail = false;
    private boolean aPartyCallDetail = false;
    private boolean bPartyCallDetail = false;
    
    //variables related to digitCollection
    private String processingDateFormat;
    private String dateEntered;
    private String ccNumber;
    private int age = 0;
    private String password;
    private int surasNo = 0;
    private int noOfTasbih = 0;
    private boolean surasNoSetFlag = true;
    
    //subscription related variables
    private int serviceId = 0;
    private String language;
    private int callAttempt = 0;
    private int remainingFreeMinutes = 0;
    private boolean blackListed;
    private boolean whiteListed;
    private boolean inRedCarpetList;
    private String circle;
    private String status;
    
    //Processing related variables
    private String voiceEffectFrequency="0";
    private String backgroundFile;
    
    private String[] confirmList;
    
    private int repeatCurrentState = 0;  
    private String recordFileName;
    private boolean npPlayComplete = false;
    private boolean dtmfBufferReceived = false;
    private boolean updatefreeMins = false;
    private boolean dialSent = false;
    
    private CallCdr callCdr;
    private String confirmFile;
    private String confirmFileStartTime;
    private String confirmFileId;
        
    private String npFile;
    private String npStartTime;
    private String npContentId;
    
    private String playContentFile;
    private String playContentStarttime;
    private String playContentId;
    private boolean repeatTasbih = false;
    private boolean sentDCReq = false;
    
    private String downloadFile;
    private String crbtFile;
    private int crbtContentId;
    private boolean callEndReceived = false;
    
    private String obdTable;
    private String sampleCheckTimerId;
    private String shortCode;
    
    private boolean inPatchState = false;
    private String dialStatus;
    private boolean dialStatusbparty;
    private String dialFailureReason;
    private String dialTime;
    private String patchTime;
    private boolean dropAParty = false;
    private boolean executePatch = false;
    private String directCall = "false";
    private boolean magicParrotTimer = false;
    
    private String cgMsisdn;
    private int cgVid;
    private boolean createCgUCD = false;
    private boolean cgCallDetail = false;
    private int callTypePatchedWithCg;
    
    private boolean isInPatchWithCG = false;
    private boolean seekHandled = false;    
    private boolean seek = false;
    private String lastFilePlayed;
    private String subsServiceName;
    
    private String tasbihFileSelected;
    private int tasbihFileId;
    private boolean updateVisitorsDialCount = false;
    private String visitorsServiceName;
    
    private int bookmarkId;
    
    private boolean dialoutTimer = false;
    private String dialoutTimerId;
    
    private boolean genRandomDtmfTimer = false;
    private String genRandomDtmfTimerId;
    private boolean alreadyRandomGen = false;
    
    private String userDefTimerId;
    private boolean userDefTimerFlag =false;
    private int userDefTimerCellId = 0;
    private String subType;
    private int subAlert = 0;
    private boolean isFirstCallerofmonth = true;
    private boolean saveSeekFile = false;
    private boolean recorddedication=false;
    private boolean dynamicplaystarted =false;
    private String dynamicplaylist[] ; 
    private boolean dynamicplayover =false;
    private String obdjobtype ="";
    private String subscriptionlangauge ="";	////used only to handle no langauge selected case.
    private boolean startfromsurah;		///to handle conflict between start from surah and seek
    private boolean startfrombookmark = false;
    private boolean singlebookmark = false;

    private boolean alreadypicked = false;
    
    private NameValuePair[] playlist;
    private int currentPlayListid =0;
    private int booknumber = 0;
    private int chapternumber = 0;
    private String transactionid;
    private String dtmfgenerate;

	public String getCallStartTime() {
		return this.callStartTime;
	}
	public void setCallStartTime(Date date) {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		this.callStartTime = dateFormatter.format(date);
	}
	public String getCallEndTime() {
		return this.callEndTime;
	}
	public void setCallEndTime(Date date) {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		this.callEndTime = dateFormatter.format(date);
	}
	public int getCurrentState() {	//?????
		return this.currentState;
	}
	public void setCurrentState(int currentState) {
		this.currentState = currentState;
	}
	public int getNumberOfFileInPlay() {		
		return this.numberOfFileInPlay;
	}
	public void setNumberOfFileInPlay(int numberOfFileInPlay) {
		this.numberOfFileInPlay = numberOfFileInPlay;
	}
	public int getNumberOfFilesPlayed() {
		return this.numberOfFilesPlayed;
	}
	public void setNumberOfFilesPlayed(int numberOfFilesPlayed) {
		this.numberOfFilesPlayed = numberOfFilesPlayed;
	}
	public int getTimeOutForNP() {
		return this.timeOutForNP;
	}
	public void setTimeOutForNP(int timeOutForNP) {
		this.timeOutForNP = timeOutForNP;
	}
	public String getNpTimerId() {
		return this.npTimerId;
	}
	public void setNpTimerId(String npTimerId) {
		this.npTimerId = npTimerId;
	}
	public boolean isNpTimerFlag() {		//	true when NP timer over or set???????
		return this.npTimerFlag;
	}
	public void setNpTimerFlag(boolean npTimerFlag) {
		this.npTimerFlag = npTimerFlag;
	}
	public String getFreeMinTimerId() {
		return this.freeMinTimerId;
	}
	public void setFreeMinTimerId(String freeMinTimerId) {
		this.freeMinTimerId = freeMinTimerId;
	}
	public boolean isFreeMinTimerFlag() {			//???
		return this.freeMinTimerFlag;
	}
	public void setFreeMinTimerFlag(boolean freeMinTimerFlag) {
		this.freeMinTimerFlag = freeMinTimerFlag;
	}
	public String getCoreTimer() {
		return this.coreTimer;
	}
	public void setCoreTimer(String coreTimer) {
		this.coreTimer = coreTimer;
	}
	public boolean isCoreTimerFlag() {
		return this.coreTimerFlag;
	}
	public void setCoreTimerFlag(boolean coreTimerFlag) {
		this.coreTimerFlag = coreTimerFlag;
	}		
	public String getMasterTimerId() {
		return this.masterTimerId;
	}
	public void setMasterTimerId(String masterTimerId) {
		this.masterTimerId = masterTimerId;
	}
	public boolean isMasterTimerFlag() {
		return this.masterTimerFlag;
	}
	public void setMasterTimerFlag(boolean masterTimerFlag) {
		this.masterTimerFlag = masterTimerFlag;
	}
	public String getRecordTimer() {
		return this.recordTimer;
	}
	public void setRecordTimer(String recordTimer) {
		this.recordTimer = recordTimer;
	}
	public boolean isRecordTimerFlag() {
		return this.recordTimerFlag;
	}
	public void setRecordTimerFlag(boolean recordTimerFlag) {
		this.recordTimerFlag = recordTimerFlag;
	}
	public boolean isNextStateFlag() {
		return this.nextStateFlag;
	}
	public void setNextStateFlag(boolean nextStateFlag) {			// if true, go to next state
		this.nextStateFlag = nextStateFlag;
	}
	public boolean isExecuteCurrentState() {			//
		return this.executeCurrentState;
	}
	public void setExecuteCurrentState(boolean executeCurrentState) {
		this.executeCurrentState = executeCurrentState;
	}
	public String getKey() {		//get key ?????
		return this.key;
	}
	public void setKey(String key) {			//set kaha se ho ri h one example????
		this.key = key;
	}
	public String getService() {
		return this.service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public int getaPartyVId() {
		return this.aPartyVId;
	}
	public void setaPartyVId(int aPartyVId) {
		this.aPartyVId = aPartyVId;
	}
	public String getaPartyMsisdn() {
		return this.aPartyMsisdn;
	}
	public void setaPartyMsisdn(String aPartyMsisdn) {
		this.aPartyMsisdn = aPartyMsisdn;
	}
	public int getbPartyVId() {
		return this.bPartyVId;
	}
	public void setbPartyVId(int bPartyVId) {
		this.bPartyVId = bPartyVId;
	}
	public String getbPartyMsisdn() {
		return this.bPartyMsisdn;
	}
	public void setbPartyMsisdn(String bPartyMsisdn) {
		this.bPartyMsisdn = bPartyMsisdn;
	}
	/*public String getDefLang() {
		return this.defLang;
	}
	public void setDefLang(String defLang) {
		this.defLang = defLang;
	}*/
	public int getRepeateVal() {
		return this.repeateVal;
	}
	public void setRepeateVal(int repeateVal) {
		this.repeateVal = repeateVal;
	}
	public boolean isAcceptBPartyDtmf() {
		return this.acceptBPartyDtmf;
	}
	public void setAcceptBPartyDtmf(boolean acceptBPartyDtmf) {
		this.acceptBPartyDtmf = acceptBPartyDtmf;
	}
	public int getRecording() {
		return this.recording;
	}
	public void setRecording(int recording) {
		this.recording = recording;
	}
	public boolean isCopyCallDetail() {
		return this.copyCallDetail;
	}
	public void setCopyCallDetail(boolean copyCallDetail) {
		this.copyCallDetail = copyCallDetail;
	}
	public String getLanguage() {
		return this.language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public int getCallAttempt() {
		return this.callAttempt;
	}
	public void setCallAttempt(int callAttempt) {
		this.callAttempt = callAttempt;
	}
	public int getRemainingFreeMinutes() {
		return this.remainingFreeMinutes;
	}
	public void setRemainingFreeMinutes(int remainingFreeMinutes) {
		this.remainingFreeMinutes = remainingFreeMinutes;
	}
	public boolean isBlackListed() {
		return this.blackListed;
	}
	public void setBlackListed(boolean blackListed) {
		this.blackListed = blackListed;
	}
	public boolean isWhiteListed() {
		return whiteListed;
	}
	public void setWhiteListed(boolean whiteListed) {
		this.whiteListed = whiteListed;
	}
	public boolean isInRedCarpetList() {
		return inRedCarpetList;
	}
	public void setInRedCarpetList(boolean inRedCarpetList) {
		this.inRedCarpetList = inRedCarpetList;
	}
	public String getCircle() {
		return this.circle;
	}
	public void setCircle(String circle) {
		this.circle = circle;
	}
	public String getStatus() {
		return this.status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isaPartyCallDetail() {
		return this.aPartyCallDetail;
	}
	public void setaPartyCallDetail(boolean aPartyCallDetail) {
		this.aPartyCallDetail = aPartyCallDetail;
	}
	public boolean isbPartyCallDetail() {
		return this.bPartyCallDetail;
	}
	public void setbPartyCallDetail(boolean bPartyCallDetail) {
		this.bPartyCallDetail = bPartyCallDetail;
	}
	public String getProcessingDateFormat() {
		return this.processingDateFormat;
	}
	public void setProcessingDateFormat(String processingDateFormat) {
		this.processingDateFormat = processingDateFormat;
	}
	public String getDateEntered() {
		return this.dateEntered;
	}
	public void setDateEntered(String dateEntered) {
		this.dateEntered = dateEntered;
	}
	public String getCcNumber() {
		return this.ccNumber;
	}
	public void setCcNumber(String ccNumber) {
		this.ccNumber = ccNumber;
	}
	public int getAge() {
		return this.age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getSurasNo() {
		return this.surasNo;
	}
	public void setSurasNo(int surasNo) {
		this.surasNo = surasNo;
	}	
	public boolean isSurasNoSetFlag() {
		return this.surasNoSetFlag;
	}
	public void setSurasNoSetFlag(boolean surasNoSetFlag) {
		this.surasNoSetFlag = surasNoSetFlag;
	}
	public int getNoOfTasbih() {
		return this.noOfTasbih;
	}
	public void setNoOfTasbih(int noOfTasbih) {
		this.noOfTasbih = noOfTasbih;
	}
	public String getVoiceEffectFrequency() {
		return this.voiceEffectFrequency;
	}
	public void setVoiceEffectFrequency(String voiceEffectFrequency) {
		this.voiceEffectFrequency = voiceEffectFrequency;
	}
	public String getBackgroundFile() {
		return this.backgroundFile;
	}
	public void setBackgroundFile(String backgroundFile) {
		this.backgroundFile = backgroundFile;
	}
	public int getRepeatCurrentState() {
		return this.repeatCurrentState;
	}
	public void setRepeatCurrentState(int repeatCurrentState) {
		this.repeatCurrentState = repeatCurrentState;
	}
	public String[] getConfirmList() {
		return this.confirmList;
	}
	public void setConfirmList(String[] confirmList) {
		this.confirmList = confirmList;
	}
	public String getRecordFileName() {
		return this.recordFileName;
	}
	public void setRecordFileName(String recordFileName) {
		this.recordFileName = recordFileName;
	}
	public boolean isNpPlayComplete() {
		return this.npPlayComplete;
	}
	public void setNpPlayComplete(boolean npPlayComplete) {
		this.npPlayComplete = npPlayComplete;
	}
	public boolean isDtmfBufferReceived() {
		return this.dtmfBufferReceived;
	}
	public void setDtmfBufferReceived(boolean dtmfBufferReceived) {
		this.dtmfBufferReceived = dtmfBufferReceived;
	}
	public int getFreeMinTimerCellId() {
		return this.freeMinTimerCellId;
	}
	public void setFreeMinTimerCellId(int freeMinTimerCellId) {
		this.freeMinTimerCellId = freeMinTimerCellId;
	}
	public int getMasterTimerInterval() {
		return this.masterTimerInterval;
	}
	public void setMasterTimerInterval(int masterTimerInterval) {
		this.masterTimerInterval = masterTimerInterval;
	}
	public int getMasterTimerCellId() {
		return this.masterTimerCellId;
	}
	public void setMasterTimerCellId(int masterTimerCellId) {
		this.masterTimerCellId = masterTimerCellId;
	}
	public int getServiceId() {
		return this.serviceId;
	}
	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}	
	public boolean isDialSent() {
		return this.dialSent;
	}
	public void setDialSent(boolean dialSent) {
		this.dialSent = dialSent;
	}	
	public boolean isUpdatefreeMins() {
		return this.updatefreeMins;
	}
	public void setUpdatefreeMins(boolean updatefreeMins) {
		this.updatefreeMins = updatefreeMins;
	}
	public CallCdr getCallCdr() {
		return this.callCdr;
	}
	public void setCallCdr(CallCdr callCdr) {
		this.callCdr = callCdr;
	}
	public String getConfirmFile() {
		return this.confirmFile;
	}
	public void setConfirmFile(String confirmFile) {
		this.confirmFile = confirmFile;
	}
	public String getConfirmFileStartTime() {
		return this.confirmFileStartTime;
	}
	public void setConfirmFileStartTime(String confirmFileStartTime) {
		this.confirmFileStartTime = confirmFileStartTime;
	}
	public void setCallStartTime(String callStartTime) {
		this.callStartTime = callStartTime;
	}
	public void setCallEndTime(String callEndTime) {
		this.callEndTime = callEndTime;
	}
	public String getConfirmFileId() {
		return this.confirmFileId;
	}
	public void setConfirmFileId(String confirmFileId) {
		this.confirmFileId = confirmFileId;
	}
	public String getNpFile() {
		return this.npFile;
	}
	public void setNpFile(String npFile) {
		this.npFile = npFile;
	}
	public String getNpStartTime() {
		return this.npStartTime;
	}
	public void setNpStartTime(String npStartTime) {
		this.npStartTime = npStartTime;
	}
	public String getNpContentId() {
		return npContentId;
	}
	public void setNpContentId(String npContentId) {
		this.npContentId = npContentId;
	}
	public String getPlayContentFile() {
		return this.playContentFile;
	}
	public void setPlayContentFile(String playContentFile) {
		this.playContentFile = playContentFile;
	}
	public String getPlayContentStarttime() {
		return this.playContentStarttime;
	}
	public void setPlayContentStarttime(String playContentStarttime) {
		this.playContentStarttime = playContentStarttime;
	}
	public String getPlayContentId() {
		return this.playContentId;
	}
	public void setPlayContentId(String playContentId) {
		this.playContentId = playContentId;
	}
	public boolean isRepeatTasbih() {
		return this.repeatTasbih;
	}
	public void setRepeatTasbih(boolean repeatTasbih) {
		this.repeatTasbih = repeatTasbih;
	}
	public boolean isSentDCReq() {
		return this.sentDCReq;
	}
	public void setSentDCReq(boolean sentDCReq) {
		this.sentDCReq = sentDCReq;
	}
	public String getObdTable() {
		return this.obdTable;
	}
	public void setObdTable(String obdTable) {
		this.obdTable = obdTable;
	}
	public UserCallDetail getUcd(int vId, String msisdn) {
		for(UserCallDetail ucd : ucdList)
		{
			if(ucd.getbPartyVId() == vId && ucd.getbPartyMsisdn().equals(msisdn))
			{
				this.ucd = ucd;
				break;
			}
		}
		return this.ucd;
	}
	public void setUcd(UserCallDetail ucd) {
		ucdList.add(ucd);
	}
	public boolean deleteUcd(UserCallDetail ucd)
	{
		return ucdList.remove(ucd);
	}
	public String getDownloadFile() {
		return this.downloadFile;
	}
	public void setDownloadFile(String downloadFile) {
		this.downloadFile = downloadFile;
	}
	public String getCrbtFile() {
		return crbtFile;
	}
	public void setCrbtFile(String crbtFile) {
		this.crbtFile = crbtFile;
	}
	public int getCrbtContentId() {
		return crbtContentId;
	}
	public void setCrbtContentId(int crbtContentId) {
		this.crbtContentId = crbtContentId;
	}
	public boolean isCallEndReceived() {
		return this.callEndReceived;
	}
	public void setCallEndReceived(boolean callEndReceived) {
		this.callEndReceived = callEndReceived;
	}
	public String getSampleCheckTimerId() {
		return this.sampleCheckTimerId;
	}
	public void setSampleCheckTimerId(String sampleCheckTimerId) {
		this.sampleCheckTimerId = sampleCheckTimerId;
	}
	public String getShortCode() {
		return this.shortCode;
	}
	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}
	public boolean isInPatchState() {
		return this.inPatchState;
	}
	public void setInPatchState(boolean inPatchState) {
		this.inPatchState = inPatchState;
	}
	public String getDialStatus() {
		return this.dialStatus;
	}
	public void setDialStatus(String dialStatus) {
		this.dialStatus = dialStatus;
	}
	public String getDialFailureReason() {
		return this.dialFailureReason;
	}
	public void setDialFailureReason(String dialFailureReason) {
		this.dialFailureReason = dialFailureReason;
	}
	public String getDialTime() {
		return this.dialTime;
	}
	public void setDialTime(String dialTime) {
		this.dialTime = dialTime;
	}
	public String getPatchTime() {
		return this.patchTime;
	}
	public void setPatchTime(String patchTime) {
		this.patchTime = patchTime;
	}
	public boolean isDropAParty() {
		return this.dropAParty;
	}
	public void setDropAParty(boolean dropAParty) {
		this.dropAParty = dropAParty;
	}
	public boolean isExecutePatch() {
		return this.executePatch;
	}
	public void setExecutePatch(boolean executePatch) {
		this.executePatch = executePatch;
	}
	public String getDirectCall() {
		return directCall;
	}
	public void setDirectCall(String directCall) {
		this.directCall = directCall;
	}
	public String getCgMsisdn() {
		return cgMsisdn;
	}
	public void setCgMsisdn(String cgMsisdn) {
		this.cgMsisdn = cgMsisdn;
	}
	public int getCgVid() {
		return cgVid;
	}
	public void setCgVid(int cgVid) {
		this.cgVid = cgVid;
	}
	public boolean isInPatchWithCG() {
		return isInPatchWithCG;
	}
	public void setInPatchWithCG(boolean isInPatchWithCG) {
		this.isInPatchWithCG = isInPatchWithCG;
	}
	public boolean isCreateCgUCD() {
		return createCgUCD;
	}
	public void setCreateCgUCD(boolean createCgUCD) {
		this.createCgUCD = createCgUCD;
	}
	public boolean isCgCallDetail() {
		return cgCallDetail;
	}
	public void setCgCallDetail(boolean cgCallDetail) {
		this.cgCallDetail = cgCallDetail;
	}
	public int getCallTypePatchedWithCg() {
		return callTypePatchedWithCg;
	}
	public void setCallTypePatchedWithCg(int callTypePatchedWithCg) {
		this.callTypePatchedWithCg = callTypePatchedWithCg;
	}
	public boolean isMagicParrotTimer() {
		return magicParrotTimer;
	}
	public void setMagicParrotTimer(boolean magicParrotTimer) {
		this.magicParrotTimer = magicParrotTimer;
	}
	public boolean isSeekHandled() {
		return seekHandled;
	}
	public void setSeekHandled(boolean seekHandled) {
		this.seekHandled = seekHandled;
	}
	public boolean isSeek() {
		return seek;
	}
	public void setSeek(boolean seek) {
		this.seek = seek;
	}
	public String getLastFilePlayed() {
		return lastFilePlayed;
	}
	public void setLastFilePlayed(String lastFilePlayed) {
		this.lastFilePlayed = lastFilePlayed;
	}
	public String getSubsServiceName() {
		return subsServiceName;
	}
	public void setSubsServiceName(String subsServiceName) {
		this.subsServiceName = subsServiceName;
	}
	public String getTasbihFileSelected() {
		return tasbihFileSelected;
	}
	public void setTasbihFileSelected(String tasbihFileSelected) {
		this.tasbihFileSelected = tasbihFileSelected;
	}
	public int getTasbihFileId() {
		return tasbihFileId;
	}
	public void setTasbihFileId(int tasbihFileId) {
		this.tasbihFileId = tasbihFileId;
	}
	public boolean isUpdateVisitorsDialCount() {
		return updateVisitorsDialCount;
	}
	public void setUpdateVisitorsDialCount(boolean updateVisitorsDialCount) {
		this.updateVisitorsDialCount = updateVisitorsDialCount;
	}
	public String getVisitorsServiceName() {
		return visitorsServiceName;
	}
	public void setVisitorsServiceName(String visitorsServiceName) {
		this.visitorsServiceName = visitorsServiceName;
	}
	public int getBookmarkId() {
		return bookmarkId;
	}
	public void setBookmarkId(int bookmarkId) {
		this.bookmarkId = bookmarkId;
	}
	public boolean isDialoutTimer() {
		return dialoutTimer;
	}
	public void setDialoutTimer(boolean dialoutTimer) {
		this.dialoutTimer = dialoutTimer;
	}
	public String getDialoutTimerId() {
		return dialoutTimerId;
	}
	public void setDialoutTimerId(String dialoutTimerId) {
		this.dialoutTimerId = dialoutTimerId;
	}
	public boolean isDialStatusbparty() {
		return dialStatusbparty;
	}
	public void setDialStatusbparty(boolean dialStatusbparty) {
		this.dialStatusbparty = dialStatusbparty;
	}
	public boolean isGenRandomDtmfTimer() {
		return genRandomDtmfTimer;
	}
	public void setGenRandomDtmfTimer(boolean genRandomDtmfTimer) {
		this.genRandomDtmfTimer = genRandomDtmfTimer;
	}
	public String getGenRandomDtmfTimerId() {
		return genRandomDtmfTimerId;
	}
	public void setGenRandomDtmfTimerId(String genRandomDtmfTimerId) {
		this.genRandomDtmfTimerId = genRandomDtmfTimerId;
	}
	public boolean isAlreadyRandomGen() {
		return alreadyRandomGen;
	}
	public void setAlreadyRandomGen(boolean alreadyRandomGen) {
		this.alreadyRandomGen = alreadyRandomGen;
	}
	public String getUserDefTimerId() {
		return userDefTimerId;
	}
	public void setUserDefTimerId(String userDefTimerId) {
		this.userDefTimerId = userDefTimerId;
	}
	public boolean isUserDefTimerFlag() {
		return userDefTimerFlag;
	}
	public void setUserDefTimerFlag(boolean userDefTimerFlag) {
		this.userDefTimerFlag = userDefTimerFlag;
	}
	public int getUserDefTimerCellId() {
		return userDefTimerCellId;
	}
	public void setUserDefTimerCellId(int userDefTimerCellId) {
		this.userDefTimerCellId = userDefTimerCellId;
	}
	public String getSubType() {
		return subType;
	}
	public void setSubType(String subType) {
		this.subType = subType;
	}
	public int getSubAlert() {
		return subAlert;
	}
	public void setSubAlert(int subAlert) {
		this.subAlert = subAlert;
	}
	public boolean isFirstCallerofmonth() {
		return isFirstCallerofmonth;
	}
	public void setFirstCallerofmonth(boolean isFirstCallerofmonth) {
		this.isFirstCallerofmonth = isFirstCallerofmonth;
	}
	public boolean isSaveSeekFile() {
		return saveSeekFile;
	}
	public void setSaveSeekFile(boolean saveSeekFile) {
		this.saveSeekFile = saveSeekFile;
	}
	public boolean isRecorddedication() {
		return recorddedication;
	}
	public void setRecorddedication(boolean recorddedication) {
		this.recorddedication = recorddedication;
	}
	public boolean isDynamicplaystarted() {
		return dynamicplaystarted;
	}
	public void setDynamicplaystarted(boolean dynamicplaystarted) {
		this.dynamicplaystarted = dynamicplaystarted;
	}
	public String[] getDynamicplaylist() {
		return dynamicplaylist;
	}
	public void setDynamicplaylist(String[] dynamicplaylist) {
		this.dynamicplaylist = dynamicplaylist;
	}
	public String getObdjobtype() {
		return obdjobtype;
	}
	public void setObdjobtype(String obdjobtype) {
		this.obdjobtype = obdjobtype;
	}
	public String getSubscriptionlangauge() {
		return subscriptionlangauge;
	}
	public void setSubscriptionlangauge(String subscriptionlangauge) {
		this.subscriptionlangauge = subscriptionlangauge;
	}
	public boolean isStartfromsurah() {
		return startfromsurah;
	}
	public void setStartfromsurah(boolean startfromsurah) {
		this.startfromsurah = startfromsurah;
	}
	public boolean isDynamicplayover() {
		return dynamicplayover;
	}
	public void setDynamicplayover(boolean dynamicplayover) {
		this.dynamicplayover = dynamicplayover;
	}
	public boolean isStartfrombookmark() {
		return startfrombookmark;
	}
	public void setStartfrombookmark(boolean startfrombookmark) {
		this.startfrombookmark = startfrombookmark;
	}
	public boolean isSinglebookmark() {
		return singlebookmark;
	}
	public void setSinglebookmark(boolean singlebookmark) {
		this.singlebookmark = singlebookmark;
	}
	
	public boolean isAlreadypicked() {
		return alreadypicked;
	}
	public void setAlreadypicked(boolean alreadypicked) {
		this.alreadypicked = alreadypicked;
	}

	public NameValuePair[] getPlaylist() {
		return playlist;
	}
	public void setPlaylist(NameValuePair[] playlist) {
		this.playlist = playlist;
	}
	public int getCurrentPlayListid() {
		return currentPlayListid;
	}
	public void setCurrentPlayListid(int currentPlayListid) {
		this.currentPlayListid = currentPlayListid;
	}
	public int getBooknumber() {
		return booknumber;
	}
	public void setBooknumber(int booknumber) {
		this.booknumber = booknumber;
	}
	public int getChapternumber() {
		return chapternumber;
	}
	public void setChapternumber(int chapternumber) {
		this.chapternumber = chapternumber;
	}
	public int getNprepeatcount() {
		return nprepeatcount;
	}
	public void setNprepeatcount(int nprepeatcount) {
		this.nprepeatcount = nprepeatcount;
	}
	public String getTransactionid() {
		return transactionid;
	}
	public void setTransactionid(String transactionid) {
		this.transactionid = transactionid;
	}
	public String getDtmfgenerate() {
		return dtmfgenerate;
	}
	public void setDtmfgenerate(String dtmfgenerate) {
		this.dtmfgenerate = dtmfgenerate;
	}
	
	
}
