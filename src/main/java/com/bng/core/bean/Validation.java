package com.bng.core.bean;

import com.bng.core.exception.coreException;
import com.bng.core.interfaces.Execute;
import com.bng.core.jsonBean.Event;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.UserCallDetail;

public class Validation implements Execute {
	private String ioccode;
	private String countrycode;
	private String prefixforIOC;
	private String suffixforIOC;
	private String IOCaction;
	private String prefixforCountrycode;
	private String suffixforCountrycode;
	private String Countrycodeaction;
	private int bpartylength;	
	private int apartylength;
	private String tovalidate;
	private String bpartyprefix;
	private boolean allowgreater;
	private String invalidchar;
	private String bpartyprefixaction;
	private String firstfound;
	
	public String getIoccode() {
		return ioccode;
	}

	public void setIoccode(String ioccode) {
		this.ioccode = ioccode;
	}

	public String getCountrycode() {
		return countrycode;
	}

	public void setCountrycode(String countrycode) {
		this.countrycode = countrycode;
	}

	public String getPrefixforIOC() {
		return prefixforIOC;
	}

	public void setPrefixforIOC(String prefixforIOC) {
		this.prefixforIOC = prefixforIOC;
	}

	public String getSuffixforIOC() {
		return suffixforIOC;
	}

	public void setSuffixforIOC(String suffixforIOC) {
		this.suffixforIOC = suffixforIOC;
	}
	
	public String getIOCaction() {
		return IOCaction;
	}

	public void setIOCaction(String iOCaction) {
		IOCaction = iOCaction;
	}

	public String getPrefixforCountrycode() {
		return prefixforCountrycode;
	}

	public void setPrefixforCountrycode(String prefixforCountrycode) {
		this.prefixforCountrycode = prefixforCountrycode;
	}

	public String getSuffixforCountrycode() {
		return suffixforCountrycode;
	}

	public void setSuffixforCountrycode(String suffixforCountrycode) {
		this.suffixforCountrycode = suffixforCountrycode;
	}
	
	public String getCountrycodeaction() {
		return Countrycodeaction;
	}

	public void setCountrycodeaction(String countrycodeaction) {
		Countrycodeaction = countrycodeaction;
	}

	public int getBpartylength() {
		return bpartylength;
	}
	
	public void setBpartylength(int bpartylength) {
		this.bpartylength = bpartylength;
	}

	public String getTovalidate() {
		return tovalidate;
	}

	public void setTovalidate(String tovalidate) {
		this.tovalidate = tovalidate;
	}
	
	public int getApartylength() {
		return apartylength;
	}

	public void setApartylength(int apartylength) {
		this.apartylength = apartylength;
	}

	public String getBpartyprefix() {
		return bpartyprefix;
	}

	public void setBpartyprefix(String bpartyprefix) {
		this.bpartyprefix = bpartyprefix;
	}
	
	public boolean isAllowgreater() {
		return allowgreater;
	}

	public void setAllowgreater(boolean allowgreater) {
		this.allowgreater = allowgreater;
	}
	
	public String getInvalidchar() {
		return invalidchar;
	}

	public void setInvalidchar(String invalidchar) {
		this.invalidchar = invalidchar;
	}
	
	public String getBpartyprefixaction() {
		return bpartyprefixaction;
	}

	public void setBpartyprefixaction(String bpartyprefixaction) {
		this.bpartyprefixaction = bpartyprefixaction;
	}

	public String getFirstfound() {
		return firstfound;
	}

	public void setFirstfound(String firstfound) {
		this.firstfound = firstfound;
	}

	public String execute(Event event, String qName,UserCallDetail userCallDetail)
	{
		String ioccodeArr[] = ioccode.split(",");
		String countrycodeArr[] = countrycode.split(",");
		String iocreceived ="";
		String countrycodereceived ="";
		boolean Countrycodefound = false ;
		boolean IOCcodefound = false;
		String bpartynumberreceived = "";
		String bpartynumbersent = "";
		String searchString = "";
		String invalchar[] =invalidchar.split(",");
		String bpartyprefixArr[] = bpartyprefix.split(",");
		String bpartyprefixreceived = "";
		
			try{
				if(firstfound.equalsIgnoreCase("OICKCode"))
				{	
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] first found ="+firstfound);
            		bpartynumberreceived = userCallDetail.getbPartyMsisdn();
					bpartynumbersent = bpartynumberreceived;
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] bpartynumberreceived = "+bpartynumberreceived);
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Countrycodes[] = "+countrycodeArr.toString());
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]ioccodeArr = "+ioccodeArr.toString());
					
					if(!invalidchar.equals(""))
					{
						for(int i =0; i<invalchar.length;i++)
						{
							if(bpartynumberreceived.contains(invalchar[i]))
							{
								Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] contains character  = "+invalchar[i]+" failure case");
								searchString = "failure";
								userCallDetail.setNextStateFlag(true);
								userCallDetail.setExecuteCurrentState(true);
								return searchString;
							}
						}
					}
					
					if(bpartynumberreceived.length() < bpartylength)
					{
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] bpartylenght received = "+bpartynumberreceived.length()+", valid bpartylength ="+bpartylength);
						searchString = "failure";
						userCallDetail.setNextStateFlag(true);
						userCallDetail.setExecuteCurrentState(true);
						return searchString;
					}
					
					for(int i =0; i<ioccodeArr.length;i++)
					{
						if(bpartynumbersent.startsWith(ioccodeArr[i]))
						{
							bpartynumbersent = bpartynumbersent.substring(ioccodeArr[i].length(), bpartynumbersent.length());
							iocreceived = ioccodeArr[i];
							IOCcodefound =true;
							break;
						}
					}
					
					for(int i =0; i<countrycodeArr.length;i++)
					{
						if(bpartynumbersent.startsWith(countrycodeArr[i]))
						{
							bpartynumbersent = bpartynumbersent.substring(countrycodeArr[i].length(), bpartynumbersent.length());
							countrycodereceived = countrycodeArr[i];
							Countrycodefound = true;
							break;
						}
					}
					
					if((bpartyprefix != null) && !(bpartyprefix.equals("")))
					{
						for(int i =0; i<bpartyprefixArr.length;i++)
						{
							if(bpartynumbersent.startsWith(bpartyprefixArr[i]))
							{
								bpartynumbersent = bpartynumbersent.substring(bpartyprefixArr[i].length(), bpartynumbersent.length());
								break;
							}
						}
					}
					
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] bparty number without C.C and IOC (after all truncation) = "+bpartynumbersent+", bpartylenght valid ="+bpartylength);
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] allowgreater lenght option ="+allowgreater );
					if(allowgreater)
					{
						if(bpartynumbersent.length() < bpartylength)
							searchString = "failure";
						else
							searchString = "success";
					}
					else
					{
						if(bpartynumbersent.length() == bpartylength)
							searchString = "success";
						else
							searchString = "failure";
					}
			
					if((bpartyprefix != null) && !(bpartyprefix.equals("")))
					{
						if(bpartyprefixaction.equalsIgnoreCase("ADD"))
						{
							if(!bpartyprefixreceived.equals(""))
							{
								bpartynumbersent = bpartyprefixreceived+bpartynumbersent;
								Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] bpartyprefix added = "+bpartyprefixreceived );
							}
							else
							{
								bpartynumbersent = bpartyprefixArr[0]+bpartynumbersent;
								Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] bpartyprefix added = "+bpartyprefixArr[0] );
							}	
						}
					}
					
					if(Countrycodeaction.equalsIgnoreCase("ADD"))
					{
						if(prefixforCountrycode != null)
						{
							if(Countrycodefound)
								countrycodereceived = prefixforCountrycode + countrycodereceived;
							else
								countrycodeArr[0] = prefixforCountrycode + countrycodeArr[0];
						}
						if(suffixforCountrycode != null)
						{
							if(IOCcodefound)
								countrycodereceived = countrycodereceived + suffixforCountrycode;
							else
								countrycodeArr[0] =countrycodeArr[0] + suffixforCountrycode;
						}
						
						if(Countrycodefound)
							bpartynumbersent = countrycodereceived + bpartynumbersent;
						else
							bpartynumbersent = countrycodeArr[0] + bpartynumbersent;
						
						userCallDetail.setbPartyMsisdn(bpartynumbersent);
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]  B party with C.C ="+bpartynumbersent);
						
					}
					else if(Countrycodeaction.equalsIgnoreCase("REMOVE"))
						userCallDetail.setbPartyMsisdn(bpartynumbersent);
					else
					{
						if(Countrycodefound)
							bpartynumbersent = countrycodereceived + bpartynumbersent;
						userCallDetail.setbPartyMsisdn(bpartynumbersent);
					}
				
					
					if(IOCaction.equalsIgnoreCase("ADD"))
					{	
						if(prefixforIOC != null)
						{
							if(IOCcodefound)
								iocreceived = prefixforIOC + iocreceived;
							else
								ioccodeArr[0] = prefixforIOC + ioccodeArr[0];
						}
						if(suffixforIOC != null)
						{
							if(IOCcodefound)
								iocreceived = iocreceived + suffixforIOC;
							else
								ioccodeArr[0] = ioccodeArr[0] + suffixforIOC;
						}
						
						if(IOCcodefound)
							bpartynumbersent = iocreceived + bpartynumbersent;
						else
							bpartynumbersent = ioccodeArr[0] + bpartynumbersent;
						
						userCallDetail.setbPartyMsisdn(bpartynumbersent);
					
					}
					else if(IOCaction.equalsIgnoreCase("REMOVE"))
						userCallDetail.setbPartyMsisdn(bpartynumbersent);
					else
					{
						if(IOCcodefound)
							bpartynumbersent = iocreceived + bpartynumbersent;
						userCallDetail.setbPartyMsisdn(bpartynumbersent);
					}
					
					userCallDetail.setbPartyMsisdn(bpartynumbersent);
				}
				else
				{
					
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] first found ="+firstfound);
            		bpartynumberreceived = userCallDetail.getbPartyMsisdn();
					bpartynumbersent = bpartynumberreceived;
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] bpartynumberreceived = "+bpartynumberreceived);
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] Countrycodes[] = "+countrycodeArr.toString());
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]ioccodeArr = "+ioccodeArr.toString());
					
					if(!invalidchar.equals(""))
					{
						for(int i =0; i<invalchar.length;i++)
						{
							Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] invalid char[i] in number ="+invalchar[i]);
							if(bpartynumberreceived.contains(invalchar[i]))
							{
								Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] contains character  = "+invalchar[i]+" , failure case");
								searchString = "failure";
								userCallDetail.setNextStateFlag(true);
								userCallDetail.setExecuteCurrentState(true);
								return searchString;
							}
						}
					}
					if(bpartynumberreceived.length() < bpartylength)
					{
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] bpartylenght received = "+bpartynumberreceived.length()+", valid bpartylength ="+bpartylength);
						searchString = "failure";
						userCallDetail.setNextStateFlag(true);
						userCallDetail.setExecuteCurrentState(true);
						return searchString;
					}
					
					for(int i =0; i<countrycodeArr.length;i++)
					{
						if(bpartynumbersent.startsWith(countrycodeArr[i]))
						{
							bpartynumbersent = bpartynumbersent.substring(countrycodeArr[i].length(), bpartynumbersent.length());
							countrycodereceived = countrycodeArr[i];
							Countrycodefound = true;
							break;
						}
							
					}
					
					for(int i =0; i<ioccodeArr.length;i++)
					{
						if(bpartynumbersent.startsWith(ioccodeArr[i]))
						{
							bpartynumbersent = bpartynumbersent.substring(ioccodeArr[i].length(), bpartynumbersent.length());
							iocreceived = ioccodeArr[i];
							IOCcodefound =true;
							break;
						}
					}
					
					if((bpartyprefix != null) && !(bpartyprefix.equals("")))
					{
						for(int i =0; i<bpartyprefixArr.length;i++)
						{
							if(bpartynumbersent.startsWith(bpartyprefixArr[i]))
							{
								bpartynumbersent = bpartynumbersent.substring(bpartyprefixArr[i].length(), bpartynumbersent.length());
								break;
							}
						}
					}
					
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] bparty number without C.C and IOC (after all truncation) = "+bpartynumbersent+", bpartylenght valid ="+bpartylength);
					Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] allowgreater lenght option ="+allowgreater );
					if(allowgreater)
					{
						if(bpartynumbersent.length() < bpartylength)
							searchString = "failure";
						else
							searchString = "success";
					}
					else
					{
						if(bpartynumbersent.length() == bpartylength)
							searchString = "success";
						else
							searchString = "failure";
					}
					
					if((bpartyprefix != null) && !(bpartyprefix.equals("")))
					{
						if(bpartyprefixaction.equalsIgnoreCase("ADD"))
						{
							if(!bpartyprefixreceived.equals(""))
							{
								bpartynumbersent = bpartyprefixreceived+bpartynumbersent;
								Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] bpartyprefix added = "+bpartyprefixreceived );
							}
							else
							{
								bpartynumbersent = bpartyprefixArr[0]+bpartynumbersent;
								Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"] bpartyprefix added = "+bpartyprefixArr[0] );
							}	
						}
					}
					
					if(IOCaction.equalsIgnoreCase("ADD"))
					{	
						if(prefixforIOC != null)
						{
							if(IOCcodefound)
								iocreceived = prefixforIOC + iocreceived;
							else
								ioccodeArr[0] = prefixforIOC + ioccodeArr[0];
						}
						if(suffixforIOC != null)
						{
							if(IOCcodefound)
								iocreceived = iocreceived + suffixforIOC;
							else
								ioccodeArr[0] = ioccodeArr[0] + suffixforIOC;
						}
						
						if(IOCcodefound)
							bpartynumbersent = iocreceived + bpartynumbersent;
						else
							bpartynumbersent = ioccodeArr[0] + bpartynumbersent;
						
						userCallDetail.setbPartyMsisdn(bpartynumbersent);
					
					}
					else if(IOCaction.equalsIgnoreCase("REMOVE"))
						userCallDetail.setbPartyMsisdn(bpartynumbersent);
					else
					{
						if(IOCcodefound)
							bpartynumbersent = iocreceived + bpartynumbersent;
						userCallDetail.setbPartyMsisdn(bpartynumbersent);
					}
					
					if(Countrycodeaction.equalsIgnoreCase("ADD"))
					{
						if(prefixforCountrycode != null)
						{
							if(Countrycodefound)
								countrycodereceived = prefixforCountrycode + countrycodereceived;
							else
								countrycodeArr[0] = prefixforCountrycode + countrycodeArr[0];
						}
						if(suffixforCountrycode != null)
						{
							if(IOCcodefound)
								countrycodereceived = countrycodereceived + suffixforCountrycode;
							else
								countrycodeArr[0] =countrycodeArr[0] + suffixforCountrycode;
						}
						
						if(Countrycodefound)
							bpartynumbersent = countrycodereceived + bpartynumbersent;
						else
							bpartynumbersent = countrycodeArr[0] + bpartynumbersent;
						
						userCallDetail.setbPartyMsisdn(bpartynumbersent);
						Logger.sysLog(LogValues.info, this.getClass().getName(), "["+event.getvId()+"]["+event.getaPartyMsisdn()+"]["+event.getbPartyMsisdn()+"]  B party with C.C ="+bpartynumbersent);
						
					}
					else if(Countrycodeaction.equalsIgnoreCase("REMOVE"))
						userCallDetail.setbPartyMsisdn(bpartynumbersent);
					else
					{
						if(Countrycodefound)
							bpartynumbersent = countrycodereceived + bpartynumbersent;
						userCallDetail.setbPartyMsisdn(bpartynumbersent);
					}
				
					userCallDetail.setbPartyMsisdn(bpartynumbersent);
				
				}
				Logger.sysLog(LogValues.info, this.getClass().getName()," bparty number processed = "+userCallDetail.getbPartyMsisdn());
				userCallDetail.setNextStateFlag(true);
				userCallDetail.setExecuteCurrentState(true);
			}
			catch(Exception e)
			{
				Logger.sysLog(LogValues.error, this.getClass().getName(),coreException.GetStack(e));
			}
			Logger.sysLog(LogValues.info, this.getClass().getName(),"searchstring ="+searchString);
			return searchString;
			
		}
}
			
	


