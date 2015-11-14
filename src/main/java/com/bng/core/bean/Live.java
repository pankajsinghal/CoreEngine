package com.bng.core.bean;

import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Event;
import com.bng.core.jsonBean.Play;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

public class Live implements Execute{
	private String liveService;
	private int playId;

	public String getLiveService() {
		return liveService;
	}

	public void setLiveService(String liveService) {
		this.liveService = liveService;
	}

	public int getPlayId() {
		return playId;
	}

	public void setPlayId(int playId) {
		this.playId = playId;
	}

	public String execute(Event event, String qName, UserCallDetail userCallDetail) 
	{
		Logger.sysLog(LogValues.debug, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Start playing live streaming for "+liveService+" service");
		String fileDirPath = "/root/"+liveService;
		String liveStreamingFile = Utility.findLastModified(fileDirPath);
		Play play = new Play(liveStreamingFile,event.getvId(),event.getIp(),0,false,0,event.getaPartyMsisdn(),"0",event.getHardware(),qName,""+playId);
		SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(play));
        Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]Telephony event Play added to queue = "+event.getCoreToTelephony()+", file = "+liveStreamingFile); 
        userCallDetail.setNextStateFlag(false);
		return null;
	}
	
}
