/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.parser;

/**
 *
 * @author richa
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.bng.core.bean.Answer;
import com.bng.core.bean.CheckDB;
import com.bng.core.bean.Dial;
import com.bng.core.bean.DigitCollect;
import com.bng.core.bean.DynamicPlay;
import com.bng.core.bean.Exit;
import com.bng.core.bean.JumpFor;
import com.bng.core.bean.Live;
import com.bng.core.bean.MagicParrotCheck;
import com.bng.core.bean.MultiPlay;
import com.bng.core.bean.MxCell;
import com.bng.core.bean.MxParam;
import com.bng.core.bean.NavigationPrompt;
import com.bng.core.bean.Patch;
import com.bng.core.bean.PlayContent;
import com.bng.core.bean.Playstreaming;
import com.bng.core.bean.Probability;
import com.bng.core.bean.Processing;
import com.bng.core.bean.Randomizer;
import com.bng.core.bean.Start;
import com.bng.core.bean.StartRecord;
import com.bng.core.bean.StartTimer;
import com.bng.core.bean.StopRecord;
import com.bng.core.bean.Url;
import com.bng.core.bean.Validation;
import com.bng.core.bean.Vxml;
import com.bng.core.bean.Wait;
import com.bng.core.exception.coreException;
import com.bng.core.memCache.MemCacheJSon;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

public class Core {
	private MemCacheJSon memCacheJSon = null;

	public void setMemCacheJSon(MemCacheJSon memCacheJSon) {
		this.memCacheJSon = memCacheJSon;
	}

	public ArrayList<Object> getCurrentNode(int id, String key) {
		Logger.sysLog(LogValues.info, this.getClass().getName(),
				"Inside getCurrentNode key send to read [" + key + "_hm]");
		try {
			Map objMap = (Map) memCacheJSon.get(key + "_hm");// new
																// Gson().fromJson(memCacheJSonImpl.get(service+"_hm").toString(),
			//Logger.sysLog(LogValues.info, Core.class.getName(),	"current ID ==: " + objMap.get(id)+" objmap in string =="+objMap.toString());												// Map.class);
			return parseMxParam((MxCell) objMap.get(id));
		} catch (Exception e) {
			Logger.sysLog(LogValues.error, Core.class.getName(),
					coreException.GetStack(e));
			return null;
		}
	}

	public int getNextNodeId(int currId, String searchCriteria, String key) 
	{
		Logger.sysLog(LogValues.info, this.getClass().getName(), "Inside getNextNodeId key send to read [" + key + "_ml]");
		ArrayList<Pair<Integer, ArrayList<Pair<String, Integer>>>> mainlist = (ArrayList<Pair<Integer, ArrayList<Pair<String, Integer>>>>) memCacheJSon.get(key + "_ml");// memCacheJSonImpl.get(service+"_ml");
		if (mainlist != null) 
		{
			// loop through the list
			HashMap possibleTargets = new HashMap();
			for (int i = 0; i < mainlist.size(); i++) 
			{
				int source = (int) mainlist.get(i).getL();
				if (currId == source)
				{
					for (int j = 0; j < mainlist.get(i).getR().size(); j++) 
					{
						possibleTargets.put(mainlist.get(i).getR().get(j).getL(), mainlist.get(i).getR().get(j).getR());
						Logger.sysLog(LogValues.info,this.getClass().getName(),"currId = "+ currId+ "\tsearchCriteria = "+ searchCriteria+ ", get = "+ (mainlist.get(i).getR().get(j).getL()));
						if ((searchCriteria == null || searchCriteria.equals("null"))
								&& ((mainlist.get(i).getR().get(j).getL() == null) 
								|| (mainlist.get(i).getR().get(j).getL().equals(""))))
							return mainlist.get(i).getR().get(j).getR();																		

						if (searchCriteria.equalsIgnoreCase(mainlist.get(i).getR().get(j).getL())) 
						{
							return mainlist.get(i).getR().get(j).getR();
						}
					}
					// must be a last node - return 0;
					return 0;
				}
			}
		} 
		else
			Logger.sysLog(LogValues.info, this.getClass().getName(),"MainList doesnt exist for key = " + key + "_ml");
		return 0;
	}

	/*
	 * public HashMap getNextPossibleNodes(Integer currId, String key) {
	 * Logger.sysLog(LogValues.info, this.getClass().getName(),
	 * "Inside getNextPossibleNodes. key for memcache ["+key+"_ml]");
	 * ArrayList<Pair<Integer,ArrayList<Pair<String,Integer>>>> mainlist =
	 * (ArrayList
	 * <Pair<Integer,ArrayList<Pair<String,Integer>>>>)memCacheJSon.get
	 * (key+"_ml");//memCacheJSonImpl.get(service+"_ml"); //loop through the
	 * list HashMap possibleTargets = new HashMap();
	 * Logger.sysLog(LogValues.info, this.getClass().getName(),
	 * "mainlist = "+mainlist.size()); for (int i= 0; i<mainlist.size(); i++) {
	 * Integer source=mainlist.get(i).getL(); Logger.sysLog(LogValues.info,
	 * this.getClass().getName(), "source = "+source+" , currId = "+currId); if(
	 * currId == source) { Logger.sysLog(LogValues.info,
	 * this.getClass().getName(),
	 * "mainlist.get(i).getR() = "+mainlist.get(i).getR()); for (int j=0; j<
	 * mainlist.get(i).getR().size(); j++) { Logger.sysLog(LogValues.info,
	 * this.getClass().getName(),
	 * "1 = "+mainlist.get(i).getR()+" , 2 = "+mainlist
	 * .get(i).getR().get(j).getL
	 * ()+" , 3 = "+mainlist.get(i).getR().get(j).getR());
	 * possibleTargets.put(mainlist
	 * .get(i).getR().get(j).getL(),mainlist.get(i).getR().get(j).getR()); } } }
	 * return possibleTargets; }
	 */

	public static ArrayList parseMxParam(MxCell sourceMxCell) {
		ArrayList<MxParam> sourceMxParamList = sourceMxCell.getListOfMxParam();
		ArrayList listReturnToCore = null;
		try 
		{
			if (sourceMxParamList != null) 
			{
				listReturnToCore = new ArrayList();
				Logger.sysLog(LogValues.info, Core.class.getName(),	"currentNode Type : " + sourceMxCell.getType());
				CoreEnums.SCPTypeEnum typeEnum = CoreEnums.SCPTypeEnum.valueOf(sourceMxCell.getType());
				Logger.sysLog(LogValues.info, Core.class.getName(),	"currentNode Type : " + sourceMxCell.getType());

				switch (typeEnum) 
				{
					case Start:
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),	"Inside core : Start");
						int j = 0;
						Start startCall = new Start();
						for (MxParam mxParam : sourceMxParamList) 
						{
							startCall.setService(mxParam.getService());
							startCall.setShortcode(mxParam.getShortcode());
							startCall.setExittimer(Integer.parseInt(mxParam.getExittimer()));
							startCall.setDefaultLang(mxParam.getDefaultlang());
							startCall.setAutoAnswer(mxParam.isAutoanswer());
							startCall.setNumberDealing(mxParam.getOptiondealing());
							startCall.setSingleBookmark(mxParam.isSinglebookmark());
							if(!mxParam.getRecording().equals(""))
								startCall.setRecording(Integer.parseInt(mxParam.getRecording()));
							listReturnToCore.add(startCall);
							startCall = null;
							j++;
							Logger.sysLog(LogValues.info, Core.class.getName(),	"Start node param is set.");
						}
					}
						break;
					case Answer:
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : Answer");
						Answer answer = new Answer();
						listReturnToCore.add(answer);
					}
						break;
					case Dialout: 
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : Dialout");
						int j = 0;
						Dial dial = new Dial();
						for (MxParam mxParam : sourceMxParamList) 
						{
							dial.setDialOut(mxParam.getDialout());
							dial.setResourceUrl(mxParam.getResourceurl());
							dial.setCutonringing(mxParam.isCutonringing());
							dial.setDialouttime(mxParam.getDialouttime());
							dial.setCellId(sourceMxCell.getId());
							dial.setCgDialOutMsisdn(mxParam.getConsentgateway());
	                        dial.setCgCli(mxParam.getCgcli());
	                        dial.setDialbackground(mxParam.getDialbg());
							j++;
						}
						listReturnToCore.add(dial);
						dial = null;
					}
						break;
					case Timer:
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : Timer");
						int j = 0;
						StartTimer timer = new StartTimer();
						for (MxParam mxParam : sourceMxParamList) 
						{
							Logger.sysLog(LogValues.info,Core.class.getName(),"mxParam.getTimerValue() = "+ mxParam.getTimerValue());
							//if (mxParam.getTimerValue() != null	&& !(mxParam.getTimerValue().equals("null")) && !(mxParam.getTimerValue().equals("")))
							//	timer.setTimerValue(Long.parseLong(mxParam.getTimerValue()));
							timer.setTimerValue(mxParam.getUser());
							timer.setTimerType(mxParam.getTimerType());
							timer.setTimerCellId(sourceMxCell.getId());
							j++;
						}
						listReturnToCore.add(timer);
						timer = null;
					}
						break;
					case Database: 
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),	"Inside core : Database");
						int j = 0;
						CheckDB check = null;
						//if (sourceMxParamList.size() > 0) 
						//{
							for (MxParam mxParam : sourceMxParamList) 
							{
								check = new CheckDB();
								check.setValue(mxParam.getCheckdb());
								check.setServiceName(mxParam.getServicename());
								if(mxParam.getCheckdb().equalsIgnoreCase("BPartyExists"))
									check.setBpartyminlength(Integer.parseInt(mxParam.getBpartyminlength()));
								
								j++;
							}
							listReturnToCore.add(check);
							check = null;
						/*} 
						else 
						{
							check = new CheckDB();
							check.setValue(sourceMxCell.getValue());
							listReturnToCore.add(check);
							check = null;
						}*/
					}
						break;
					case Navigation: 
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : NavigationPrompt");
						int j = 0;
						NavigationPrompt navigationPrompt = new NavigationPrompt();
						NameValuePair nameValuePair = null;
						NameValuePair[] promptFile = new NameValuePair[sourceMxParamList.size()];
						for (MxParam mxParam : sourceMxParamList) 
						{
							String promptFileWithId = mxParam.getPromptfile();
							nameValuePair = new NameValuePair(promptFileWithId.substring(promptFileWithId.indexOf("-") + 1,promptFileWithId.length()),promptFileWithId.substring(0,promptFileWithId.indexOf("-")));
							promptFile[j] = nameValuePair;
							navigationPrompt.setBragein(mxParam.isBargein());
							navigationPrompt.setTimeOut(Integer.parseInt(mxParam.getTimeout()));
							if((mxParam.getRepeatcount() != null) && !(mxParam.getRepeatcount().equals("")))
								navigationPrompt.setRepeatcount(Integer.parseInt(mxParam.getRepeatcount()));
							else
								navigationPrompt.setRepeatcount(0);
							navigationPrompt.setCellId("" + sourceMxCell.getId());
							j++;
						}
						navigationPrompt.setValue(sourceMxCell.getValue());
						navigationPrompt.setPromptFile(promptFile);
	
						listReturnToCore.add(navigationPrompt);
						navigationPrompt = null;
					}
						break;
					case Processing: 
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : Processing");
						int j = 0;
						Processing processing = new Processing();
						for (MxParam mxParam : sourceMxParamList)
						{
							processing.setProcessingType(sourceMxCell.getValue());
							processing.setValue(mxParam.getValue());
							j++;
						}
						listReturnToCore.add(processing);
						processing = null;
					}
						break;
					case Play: 
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : PlayContent.");
						int j = 0;
						PlayContent playContent = new PlayContent();
						NameValuePair nameValuePair = null;
						NameValuePair[] contentFileList = new NameValuePair[sourceMxParamList.size()];
						for (MxParam mxParam : sourceMxParamList) 
						{
							String promptFileWithId = mxParam.getPlaycontent(); 
							if ((promptFileWithId != null) && !promptFileWithId.equals(""))
							{
								String fileName = promptFileWithId.substring(promptFileWithId.indexOf("-") + 1,promptFileWithId.length()); 
								String contentId = promptFileWithId.substring(0,promptFileWithId.indexOf("-")); 
								// String downloadUrl = mxParam.getDownloadurl();
								// String crbtUrl = mxParam.getCrbturl();
	
								// nameValuePair = new
								// NameValuePair(fileName,contentId,downloadUrl,crbtUrl);
								nameValuePair = new NameValuePair(fileName,contentId);
								contentFileList[j] = nameValuePair;
							}
							playContent.setRepeatcount(Integer.parseInt(mxParam.getRepeatcount()));
							playContent.setPlayId("" + sourceMxCell.getId());
							playContent.setAddtofavDTMF(mxParam.getAddtofavdtmf());
							playContent.setStartOverDTMF(mxParam.getStartoverdtmf());
							playContent.setNextdtmf(mxParam.getNextdtmf());
							playContent.setPreviousdtmf(mxParam.getPreviousdtmf());
							playContent.setRepeatcurrent((mxParam.getRepeatcurrent()));
							playContent.setSeek(mxParam.isSeek());
							playContent.setSeekdtmf(mxParam.getSeekdtmf());
							playContent.setRandom(mxParam.isRandomplay());
							playContent.setStartFrom(mxParam.getStartFrom());
							playContent.setSaveFileDtmf(mxParam.getSavefiledtmf());
							playContent.setSaveFileFor(mxParam.getSavefilefor());
							j++;
						}
	
						playContent.setContentFile(contentFileList);
						listReturnToCore.add(playContent);
						playContent = null;
					}
						break;
					case PlayStreaming:
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : PlayStreaming");
						int j = 0;
						Playstreaming playStreaming = null;
						for (MxParam mxParam : sourceMxParamList) 
						{
							playStreaming.setUrl(mxParam.getUrl());
							playStreaming.setTimeout(Long.parseLong(mxParam.getTimeout()));
							playStreaming.setBargein(mxParam.isBargein());
							j++;
						}
						listReturnToCore.add(playStreaming);
						playStreaming = null;
					}
						break;
					case URL: 
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : URL");
						int j = 0;
						Url url = null;
						for (MxParam mxParam : sourceMxParamList) 
						{
							url = new Url();
							url.setMethod(mxParam.getMethod());
							url.setUrl(mxParam.getUrl());
							url.setType(mxParam.getUrlType());
							url.setMode(mxParam.getCallmode());
							if(mxParam.getSynchronous() != null)
								url.setConnectTimeOut(mxParam.getSynchronous());
							url.setServicename(mxParam.getSubservicename());
							listReturnToCore.add(url);
							url = null;
							j++;
						}
					}
						break;
					case DigitCollection: 
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : DigitCollection");
						int j = 0;
						DigitCollect digitCollect = null;
						NameValuePair nameValuePair = null;
						NameValuePair[] confirmPromptFile = new NameValuePair[sourceMxParamList.size()];
						NameValuePair[] contentFileList = new NameValuePair[sourceMxParamList.size()];
						for (MxParam mxParam : sourceMxParamList) 
						{
							digitCollect = new DigitCollect();
							digitCollect.setConfirmation(mxParam.isConfirmation());
							if (mxParam.isConfirmation()) 
							{
								String promptFileWithId = mxParam.getConfirmlist();
								nameValuePair = new NameValuePair(promptFileWithId.substring(promptFileWithId.indexOf("-") + 1,promptFileWithId.length()),promptFileWithId.substring(0,promptFileWithId.indexOf("-")));
								confirmPromptFile[j] = nameValuePair;
								promptFileWithId = null;
							}
							else if(!mxParam.getConfirmlist().equals(""))
							{
								String promptFileWithId = mxParam.getConfirmlist(); 
								if ((promptFileWithId != null) && !promptFileWithId.equals(""))
								{
									String fileName = promptFileWithId.substring(promptFileWithId.indexOf("-") + 1,promptFileWithId.length()); 
									String contentId = promptFileWithId.substring(0,promptFileWithId.indexOf("-")); 
									nameValuePair = new NameValuePair(fileName,contentId);
									contentFileList[j] = nameValuePair;
								}
							}
							digitCollect.setRepeatcount(Integer.parseInt(mxParam.getRepeatcount()));
							digitCollect.setMinlen(Integer.parseInt(mxParam.getMinlen()));
							digitCollect.setMaxlen(Integer.parseInt(mxParam.getMaxlen()));
	
							digitCollect.setDigittype(mxParam.getDigittype());
							digitCollect.setTerminationchar(mxParam.getTerminationchar());
							digitCollect.setTimeout(Integer.parseInt(mxParam.getTimeout()));
							digitCollect.setCellId(sourceMxCell.getId());
							digitCollect.setMaxcount(mxParam.getMaxcount());
							j++;
						}
						digitCollect.setConfirmPromptFile(confirmPromptFile);
						digitCollect.setContentFile(contentFileList);
						listReturnToCore.add(digitCollect);
						digitCollect = null;
					}
						break;
					case RecordDedication: 
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : RecordDedication");
						int j = 0;
						StartRecord startRecord = null;
						for (MxParam mxParam : sourceMxParamList) 
						{
							startRecord = new StartRecord();
							startRecord.setTimeout(Integer.parseInt(mxParam
									.getTimeout()));
							startRecord.setRecorddedication(mxParam.isRecorddedication());
							startRecord.setRecordenddtmf(mxParam.getRecordenddtmf());
							j++;
						}
						listReturnToCore.add(startRecord);
						startRecord = null;
					}
						break;
					case StopRecording: 
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : StopRecording");
						int j = 0;
						StopRecord stopRecord = stopRecord = new StopRecord();
						listReturnToCore.add(stopRecord);
						stopRecord = null;
					}
						break;
					case Exit:
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : Exit");
						int j = 0;
						Exit exit = new Exit();
						listReturnToCore.add(exit);
						exit = null;
					}
						break;
					case Live:
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : Live");
						int j = 0;
						Live live = null;
						for (MxParam mxParam : sourceMxParamList)
						{
							live = new Live();
							live.setLiveService(mxParam.getLiveService());
							live.setPlayId(sourceMxCell.getId());
							j++;
						}
						listReturnToCore.add(live);
						live = null;
					}
						break;
					case Patch: 
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : Patch");
						int j = 0;
						Patch patch = new Patch();
						for(MxParam mxParam : sourceMxParamList)
						{
							patch.setPatchWith(mxParam.getPatchwith());
						}
						listReturnToCore.add(patch);
						patch = null;
					}
						break;
					case MagicParrot: 
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : MagicParrot");
						int j = 0;
						MagicParrotCheck magicParrotCheck = null;
						for (MxParam mxParam : sourceMxParamList) 
						{
							magicParrotCheck = new MagicParrotCheck();
							magicParrotCheck.setSampleCheckTimer(Integer.parseInt(mxParam.getTime()));
							magicParrotCheck.setVoiceEffect(Integer.parseInt(mxParam.getVoicefreq()));
						}
						listReturnToCore.add(magicParrotCheck);
						magicParrotCheck = null;
					}
						break;
					case Goto: 
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : GoTo");
						int j = 0;
						JumpFor jump = null;
						for (MxParam mxParam : sourceMxParamList) 
						{
							jump = new JumpFor();
							jump.setJumpFor(mxParam.getJumpfor());
							j++;
						}
						listReturnToCore.add(jump);
						jump =null;
					}
						break;
					case Randomizer:
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : Randomizer");
						int j = 0;
						Randomizer randomize = null;
						for (MxParam mxParam : sourceMxParamList)
						{
							randomize = new Randomizer();
							randomize.setDtmfmaxtime(mxParam.getMaxtime());
							randomize.setDtmfmintime(mxParam.getMintime());
							randomize.setGenerateDTMF(mxParam.getDtmfvalue());
							randomize.setPercentage(mxParam.getPercentage());
							j++;
						}	
						listReturnToCore.add(randomize);
						randomize =null;
					}
						break;
					case Validation:
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : Validation");
						int j = 0;
						Validation validate =null;
						for(MxParam mxParam : sourceMxParamList)
						{
							validate = new Validation();
							validate.setBpartylength(Integer.parseInt(mxParam.getBpartylength()));
							validate.setIoccode(mxParam.getIoccode());
							validate.setIOCaction(mxParam.getIocaction());
							validate.setPrefixforIOC(mxParam.getPrefixioc());
							validate.setSuffixforIOC(mxParam.getSuffixioc());
							validate.setCountrycode(mxParam.getCountrycode());
							validate.setCountrycodeaction(mxParam.getConaction());
							validate.setPrefixforCountrycode(mxParam.getPrefixconcode());
							validate.setSuffixforCountrycode(mxParam.getSuffixconcode());
							validate.setBpartyprefix(mxParam.getBpartyprefix());
							validate.setAllowgreater(mxParam.isAllowgreater());
							validate.setInvalidchar(mxParam.getInvalidcharacters());
							validate.setBpartyprefixaction(mxParam.getBpartyprefixaction());
							validate.setFirstfound(mxParam.getCheckfirst());
							j++;
						}
						listReturnToCore.add(validate);
						validate =null;
					}
						break;
					case Vxml:
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : VXML");
						int j = 0;
						Vxml vxml =null;
						for(MxParam mxParam : sourceMxParamList)
						{
							vxml = new Vxml();
							vxml.setUrl(mxParam.getVxmlurl());
							j++;
						}
						listReturnToCore.add(vxml);
						vxml = null; 
					}
					break;
					case DynamicPlay: 
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : dynamic play");
						int j = 0;
						DynamicPlay dynamicplay =null;
						for(MxParam mxParam : sourceMxParamList)
						{
							 dynamicplay = new DynamicPlay();
							dynamicplay.setUrl(mxParam.getDynamicplayurl());
							j++;
						}
						
						listReturnToCore.add(dynamicplay);
						dynamicplay = null;
					}
					break;
					case Multiplay:
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : multi play");
						//int j = 0;
						MultiPlay multiplay =null;
						
						for(int i = 0 ;i < 1 ; i++ )
						{
							MxParam mxParam = sourceMxParamList.get(i);
							multiplay = new MultiPlay();
							multiplay.setRepeatcount(Integer.parseInt(mxParam.getRepeatcount()));
							multiplay.setPlayId("" + sourceMxCell.getId());
							multiplay.setAddtofavDTMF(mxParam.getAddtofavdtmf());
							multiplay.setStartOverDTMF(mxParam.getStartoverdtmf());
							multiplay.setNextdtmf(mxParam.getNextdtmf());
							multiplay.setPreviousdtmf(mxParam.getPreviousdtmf());
							multiplay.setRepeatcurrent((mxParam.getRepeatcurrent()));
							multiplay.setSeek(mxParam.isSeek());
							multiplay.setSeekdtmf(mxParam.getSeekdtmf());
							multiplay.setRandom(mxParam.isRandomplay());
							multiplay.setStartFrom(mxParam.getStartFrom());
							multiplay.setSaveFileDtmf(mxParam.getSavefiledtmf());
							multiplay.setSaveFileFor(mxParam.getSavefilefor());
							multiplay.setNextListDTMF(mxParam.getNextbookdtmf());
							multiplay.setPreviousListDTMF(mxParam.getPreviousbookdtmf());
							
						}
						
						listReturnToCore.add(multiplay);
						multiplay = null;
					}
					break;
					case Probability: 
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : probability");
						int j = 0;
						Probability prob =null;
						for(MxParam mxParam : sourceMxParamList)
						{
							prob = new Probability();
							if((mxParam.getProbability() != null) && (!mxParam.getProbability().equals("")))
								prob.setProbability(Integer.parseInt(mxParam.getProbability()));
							else
								Logger.sysLog(LogValues.error, Core.class.getName(),"Probability node : value NULL");
							j++;
						}
						
						listReturnToCore.add(prob);
						prob = null;
					}
					break;
					default: 
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),	"Inside core : default");
						listReturnToCore.add("Default");
					}
				}

			} 
			else 
			{

				listReturnToCore = new ArrayList();
				CoreEnums.SCPTypeEnum typeEnum = CoreEnums.SCPTypeEnum.valueOf(sourceMxCell.getType());
				Logger.sysLog(LogValues.info, Core.class.getName(),"currentNode Type = " + sourceMxCell.getType());
	
				switch (typeEnum) 
				{
					case Exit: 
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : Exit");
						Exit exit = new Exit("rlcsent");
						listReturnToCore.add(exit);
						exit = null;
					}
						break;
					case Answer: 
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : Answer");
						Answer answer = new Answer();
						listReturnToCore.add(answer);
						answer = null;
					}
						break;
					case StopRecording: 
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : StopRecording");
						StopRecord stopRecord = stopRecord = new StopRecord();
						listReturnToCore.add(stopRecord);
						stopRecord = null;
					}
						break;
					case Wait: 
					{
						Logger.sysLog(LogValues.info, Core.class.getName(),"Inside core : Wait");
						Wait wait = new Wait();
						listReturnToCore.add(wait);
						wait = null;
					}
						break;
				}
			}
		} 
		catch (Exception e) 
		{
			Logger.sysLog(LogValues.error, Core.class.getName(),coreException.GetStack(e));
		}
		return listReturnToCore;
	}
}
