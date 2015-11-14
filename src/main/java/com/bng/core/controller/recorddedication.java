package com.bng.core.controller;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bng.core.bo.OBDNumberBo;
import com.bng.core.exception.coreException;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;


@Controller
public class recorddedication {
	
	
	private static OBDNumberBo obdnumberbo;
	private static String scheduletimegap;
	private static String trydurationminutes;
	private static String gapminutes;
	
	public  void setScheduletimegap(String scheduletimegap) {
		recorddedication.scheduletimegap = scheduletimegap;
	}

	public  void setTrydurationminutes(String trydurationminutes) {
		recorddedication.trydurationminutes = trydurationminutes;
	}
	
	public  void setGapminutes(String gapminutes) {
		recorddedication.gapminutes = gapminutes;
	}

	public  void setObdnumberbo(OBDNumberBo obdnumberbo) {
		recorddedication.obdnumberbo = obdnumberbo;
	}

	@RequestMapping(value="/getrecordfile", method = RequestMethod.GET)
//	@RequestMapping(value="/getrecordfile/{tablename}/{aparty}/{bparty}")
	public @ResponseBody String getrecordfile(HttpServletRequest request,HttpServletResponse response,@RequestParam("tablename") String tablename, @RequestParam("aparty") String aparty, @RequestParam("bparty") String bparty)
	{
		Date currenttime = new java.util.Date();
		List fileslist = new ArrayList();
		String file ="";
		Logger.sysLog(LogValues.info, this.getClass().getName(),"Inside Controller :getrecordfile  method, values received : " +aparty+"::"+ bparty);
		ResultSet resultset = obdnumberbo.getfilelist(aparty, bparty, currenttime, tablename);
//	Logger.sysLog(LogValues.info, this.getClass().getName(),"resultset ="+resultset+"   ,result set coloumn value ="+resultset.getString("message"));
		
		if(resultset == null )
		{
			System.out.println("this should not be printed");
		}
		else
		{
			 Logger.sysLog(LogValues.info, this.getClass().getName(),"resultset not null");
			try 
			{
				int size =0;
				if (resultset != null) {
					resultset.beforeFirst();
					resultset.last();
				    size = resultset.getRow();
				    Logger.sysLog(LogValues.info, this.getClass().getName(),"result size ="+size);
				}
				
				resultset.beforeFirst();
				
				//if(resultset.next())
				//{
				//	Logger.sysLog(LogValues.info, this.getClass().getName(),"result set coloumn value ="+resultset.getString("message"));
				//	Logger.sysLog(LogValues.info, this.getClass().getName(),"resultset.next() ="+resultset.next());
					
					while(resultset.next())
					{
						
						Logger.sysLog(LogValues.info, this.getClass().getName(),"result set coloumn value ="+resultset.getString("message"));
						fileslist.add(resultset.getString("message"));
						if(file.isEmpty() || file.equals(""))
						{
							file = resultset.getString("message");
							Logger.sysLog(LogValues.info, this.getClass().getName(),"file ="+file);
						}
							
						else
						{
							file = file+","+(resultset.getString("message"));
							Logger.sysLog(LogValues.info, this.getClass().getName(),"file ="+file);
						}
							
					}
			//	}
				/*
				else
				{
					if(resultset.first())
					{
						Logger.sysLog(LogValues.info, this.getClass().getName(),"result set coloumn value ="+resultset.getString("message"));
						fileslist.add(resultset.getString("message"));
						if(file.isEmpty() || file.equals(""))
							file = resultset.getString("message");
						else
							file = file+","+(resultset.getString("message"));}
					}
					else
					{
						while(resultset.next())
						{
							
							Logger.sysLog(LogValues.info, this.getClass().getName(),"result set coloumn value ="+resultset.getString("message"));
							fileslist.add(resultset.getString("message"));
							if(file.isEmpty() || file.equals(""))
								file = resultset.getString("message");
							else
								file = file+","+(resultset.getString("message"));
						}
					}
				
				*/
			} 
			catch (SQLException e) {
				Logger.sysLog(LogValues.error, this.getClass().getName(),coreException.GetStack(e));
			}
		}
		Logger.sysLog(LogValues.info, this.getClass().getName(),"filelist ="+fileslist);
		Logger.sysLog(LogValues.info, this.getClass().getName(),"file ="+file);
		
		return file;
	}
	
	@RequestMapping(value="/addrecording", method = RequestMethod.GET)
	public @ResponseBody String addRecord(HttpServletRequest request,HttpServletResponse response,@RequestParam("tablename") String tablename, @RequestParam("aparty") String aparty, @RequestParam("bparty") String bparty , @RequestParam("filename") String filename)
	//@RequestMapping(value="/addrecording/{tablename}/{aparty}/{bparty}/{filename}")
	//public @ResponseBody String addRecord(@PathVariable("tablename") String tablename, @PathVariable("aparty") String aparty, @PathVariable("bparty") String bparty , @PathVariable("filename") String filename)
	{
		
		Logger.sysLog(LogValues.info, this.getClass().getName(),"Inside Controller : addRecord method, values received ::"+tablename+":"+aparty+":"+ bparty+":"+filename);

		Date currenttime = new java.util.Date();
		
		long time1 = currenttime.getTime() + 1000*60*(Integer.parseInt(scheduletimegap));
		long time2 = currenttime.getTime() + 1000*60*(Integer.parseInt(trydurationminutes));
		Date calltime = new java.util.Date(time1);
		Date endtime = new java.util.Date(time2);
		
		try{
			ResultSet resultset = obdnumberbo.getrecordlist(bparty,tablename);
			 Logger.sysLog(LogValues.info, this.getClass().getName(),"resultset ="+resultset);
			if((resultset == null) || resultset.equals(""))
				obdnumberbo.insertnumber(aparty,bparty ,endtime, calltime,filename ,tablename);
			else 
			{
				 Logger.sysLog(LogValues.info, this.getClass().getName(),"resultset not null");
				boolean apartyexistpreviously = false;
				boolean calltimeexist = false;
				while(resultset.next())
				{
					 Logger.sysLog(LogValues.info, this.getClass().getName(),"Inside while loop with resultset.next()");
					//Logger.sysLog(LogValues.info, this.getClass().getName(),"inside loop for resultset");
					String sDate = resultset.getString("calltime");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date dDate = sdf.parse(sDate);
					//Logger.sysLog(LogValues.info, this.getClass().getName()," String retDate ="+sDate+", Date reDate ="+dDate);
				//	Logger.sysLog(LogValues.info, this.getClass().getName()," resultset.getString(cli) = "+resultset.getString("cli"));
					if(resultset.getString("cli").equals(aparty) )
					{
						Logger.sysLog(LogValues.info, this.getClass().getName()," resultset.getString(cli) = "+resultset.getString("cli")+" , aparty are same");
						apartyexistpreviously = true;
						Logger.sysLog(LogValues.info, this.getClass().getName(),"current time before last users call time ="+currenttime.before(dDate));
						if(currenttime.before(dDate))
						{
							Logger.sysLog(LogValues.info, this.getClass().getName()," currenttime ="+currenttime+ " , reDate ="+dDate);
							calltimeexist = true;
							obdnumberbo.insertnumber(aparty,bparty ,endtime, dDate,filename ,tablename);
							break;
						}
					}
				}
			//	resultset
				if(!apartyexistpreviously)
				{
					resultset.beforeFirst();
					//resultset = obdnumberbo.getrecordlist(bparty,tablename);
					Logger.sysLog(LogValues.info, this.getClass().getName()," apartyexistpreviously ="+apartyexistpreviously);
					
					if(resultset.last())
					{
						Logger.sysLog(LogValues.info, this.getClass().getName()," found last row ="+resultset.last()+" with calltime ="+resultset.getString("calltime"));
						String sDate = resultset.getString("calltime");
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date dDate = sdf.parse(sDate);
						long time3 = dDate.getTime() + 1000*60*(Integer.parseInt(gapminutes));
						Date calltimenew = new java.util.Date(time3);

						Logger.sysLog(LogValues.info, this.getClass().getName()," calltime according to last scheduled = "+calltimenew+ "calltime for now =" +calltime);
						if(calltimenew.compareTo(calltime) < 0)
						{
							Logger.sysLog(LogValues.info, this.getClass().getName()," calltime inserted in DB, calltime ="+calltime);
							obdnumberbo.insertnumber(aparty,bparty ,endtime, calltime,filename ,tablename);
						}
						else
						{
							Logger.sysLog(LogValues.info, this.getClass().getName()," calltime inserted in DB, Newcalltime ="+calltimenew);
							obdnumberbo.insertnumber(aparty,bparty ,endtime, calltimenew,filename ,tablename);
						}
						
					}
					else
					{
						Logger.sysLog(LogValues.info, this.getClass().getName(),"resultset.last() ="+resultset.last() +"calltime ="+calltime );
						obdnumberbo.insertnumber(aparty,bparty ,endtime, calltime,filename ,tablename);
					}
					
				}
				else if(!calltimeexist)
				{
					//resultset = obdnumberbo.getrecordlist(bparty,tablename);
					Logger.sysLog(LogValues.info, this.getClass().getName()," calltimeexist ="+calltimeexist);
					resultset.beforeFirst();
					if(resultset.last())
					{
						Logger.sysLog(LogValues.info, this.getClass().getName()," found last row ="+resultset.last()+" with calltime ="+resultset.getString("calltime"));
						String sDate = resultset.getString("calltime");
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date dDate = sdf.parse(sDate);
						long time3 = dDate.getTime() + 1000*60*(Integer.parseInt(gapminutes));
						Date calltimenew = new java.util.Date(time3);

						Logger.sysLog(LogValues.info, this.getClass().getName()," calltime according to last scheduled = "+calltimenew+ "calltime for now =" +calltime);
						if(calltimenew.compareTo(calltime) < 0)
						{
							Logger.sysLog(LogValues.info, this.getClass().getName()," calltime inserted in DB, calltime ="+calltime);
							obdnumberbo.insertnumber(aparty,bparty ,endtime, calltime,filename ,tablename);
						}
						else
						{
							Logger.sysLog(LogValues.info, this.getClass().getName()," calltime inserted in DB, Newcalltime ="+calltimenew);
							obdnumberbo.insertnumber(aparty,bparty ,endtime, calltimenew,filename ,tablename);
						}
						
					}
					else
					{
						Logger.sysLog(LogValues.info, this.getClass().getName(),"resultset.last() ="+resultset.last() );
						
						obdnumberbo.insertnumber(aparty,bparty ,endtime, calltime,filename ,tablename);
					}
					
					/*
					 * while(resultset.next())
					{
						String sDate = resultset.getString("calltime");
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date dDate = sdf.parse(sDate);
						if(!resultset.getString("cli").equalsIgnoreCase(aparty))
						{
							Logger.sysLog(LogValues.info, this.getClass().getName()," checking for b party time from other a parties");
							int diff = calltime.compareTo(dDate);
							if(diff == 0)
							{
								long time = dDate.getTime() + 1000*60*15 ;
								calltime = new java.util.Date(time);
							}
							else if (diff > 0)		// nows numbers calltime is after the previous number calltime.
							{
								if(calltime.getTime() - dDate.getTime() < 1000*60*15)
								{
									long time = dDate.getTime() + 1000*60*15 ;
									calltime = new java.util.Date(time);
								}
							}
							else
							{
								if(dDate.getTime() - calltime.getTime() < 1000*60*15)
								{
									long time = dDate.getTime() + 1000*60*15 ;
									calltime = new java.util.Date(time);
								}
							}
							
						}
						
					}
					obdnumberbo.insertnumber(aparty,bparty ,endtime, calltime,filename ,tablename);
					 * */
					
					
				}
			}
			return "done";	
		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, this.getClass().getSimpleName(), coreException.GetStack(e));
			return "error";
		}
		
		
	
	}
	
}

