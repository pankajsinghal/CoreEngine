package com.bng.core.bean;

import java.util.Random;

import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.DtmfGenerator;
import com.bng.core.jsonBean.Event;
import com.bng.core.parser.Core;
import com.bng.core.queue.SendToTelephony;
import com.bng.core.queue.Timer;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;
import com.bng.core.utils.Utility;

public class Randomizer	implements Execute {

	private int Dtmfmintime;
	private int dtmfmaxtime;
	private String generateDTMF;
	private int percentage;

	public int getDtmfmintime() {
		return Dtmfmintime;
	}

	public void setDtmfmintime(int dtmfmintime) {
		Dtmfmintime = dtmfmintime;
	}

	public int getDtmfmaxtime() {
		return dtmfmaxtime;
	}

	public void setDtmfmaxtime(int dtmfmaxtime) {
		this.dtmfmaxtime = dtmfmaxtime;
	}

	public String getGenerateDTMF() {
		return generateDTMF;
	}

	public void setGenerateDTMF(String generateDTMF) {
		this.generateDTMF = generateDTMF;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public String execute(Event event, String qName,UserCallDetail userCallDetail) 
	{
		Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Inside Random Executer method");
		DtmfGenerator dtmfgenerator ;
		userCallDetail.setDtmfgenerate(generateDTMF);
		
		if(event.getEvent() == CoreEnums.events.E_GenerateDTMF.ordinal())
		{	
			if(generateDTMF != null)
			{
				if((userCallDetail.getCgVid() > 0) && (userCallDetail.getaPartyVId() >0))
					dtmfgenerator = new DtmfGenerator(userCallDetail.getCgVid(), userCallDetail.getaPartyVId(), event.getIp(), event.getHardware(), qName, generateDTMF);
				else
					dtmfgenerator = new DtmfGenerator(event.getvId(), event.getvId(), event.getIp(), event.getHardware(), qName, generateDTMF);
				SendToTelephony.addMessageToQueue(event.getCoreToTelephony(),Utility.convertObjectToJsonStr(dtmfgenerator));
				Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Telephony event generate dtmf added to queue = "+event.getCoreToTelephony());
			}
			userCallDetail.setNextStateFlag(true);
			userCallDetail.setExecuteCurrentState(true);
		}
		else
		{	Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] AlreadyRandomGen = "+userCallDetail.isAlreadyRandomGen());
			if(!userCallDetail.isAlreadyRandomGen())
			{	
				if(percentage > 0)
				{	
					boolean generateDTMFflag = Utility.getPercentage(percentage);
					Logger.sysLog(LogValues.info, this.getClass().getName(),  "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Calling random = "+generateDTMFflag);
					if(generateDTMFflag)
					{	
						int timespan = dtmfmaxtime - Dtmfmintime ;
						Random rand = new Random();
						int dtmftime = rand.nextInt(timespan) + Dtmfmintime;
						event.setEvent(CoreEnums.events.E_GenerateDTMF.ordinal());
						event.setSubEvent(CoreEnums.subEvents.DEFAULT.ordinal());
						String randomTimer = Timer.startTimer(event, dtmftime, qName);
						userCallDetail.setGenRandomDtmfTimer(true);
						userCallDetail.setGenRandomDtmfTimerId(randomTimer);
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] random DTMF generated = "+generateDTMF+" , random time = "+dtmftime);
						userCallDetail.setNextStateFlag(true);
						userCallDetail.setExecuteCurrentState(true);
					}
					else
					{
						userCallDetail.setNextStateFlag(true);
						userCallDetail.setExecuteCurrentState(true);				
					}				
				}
				else
				{
					userCallDetail.setNextStateFlag(true);
					userCallDetail.setExecuteCurrentState(true);
				}
			}
		}
		return null;
	}	
}
