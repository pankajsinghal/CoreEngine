/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.bng.core.bean.BnGModel;
import com.bng.core.bean.MxCell;
import com.bng.core.bean.MxParam;
import com.bng.core.exception.coreException;
import com.bng.core.memCache.MemCacheJSon;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

/**
 *
 * @author richa
 */
public class Controller {
    private MemCacheJSon memCacheJSon = null;

    public void setMemCacheJSon(MemCacheJSon memCacheJSon) {
		this.memCacheJSon = memCacheJSon;
	}
       
    /**
     * UnMarshaller method converts the xml file into class objects using JAXB
     */
	public void Unmarshaller(String xmlFile)
    {            
    	try
        {
    		Logger.sysLog(LogValues.info,this.getClass().getName(),"Inside Unmarshaller. XML = "+xmlFile);
            JAXBContext jaxbContext = JAXBContext.newInstance(BnGModel.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            InputStream is = new ByteArrayInputStream(xmlFile.getBytes());
            //File XMLfile = new File(xmlFile);
            BnGModel bngModel = (BnGModel) jaxbUnmarshaller.unmarshal(is);
            parseMxCell(bngModel);       
    		
            /*JAXBContext jaxbContext = JAXBContext.newInstance(BnGModel.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            File XMLfile = new File(xmlFile);
            BnGModel bngModel = (BnGModel) jaxbUnmarshaller.unmarshal(XMLfile);
            parseMxCell(bngModel); */
        } 
        catch (JAXBException e) 
        {
        	Logger.sysLog(LogValues.error, this.getClass().getName(),coreException.GetStack(e));
        }
    }

    /*
     * parseMxCell method stores each mxCell in a HashMap 
     */
    public void parseMxCell(BnGModel bngModel)
    {
        boolean found = false;
        String shortCode = null;
        String serviceName = null;
        String calltype = null;
        int startNode = 0;
        String key = "";
        Map<Integer, MxCell> objMap = new HashMap<Integer, MxCell>();
        ArrayList<Pair<Integer,ArrayList<Pair<String,Integer>>>> mainlist = new ArrayList<Pair<Integer,ArrayList<Pair<String,Integer>>>>(); 
        try
        {
            List<MxCell> listOfMxcell = bngModel.getMxCellList();
            for (int i =0; i<listOfMxcell.size();i++)
            {               
                MxCell mxCell = bngModel.getMxCellList().get(i);
                if(mxCell.getType() != null)
                {
                    if(mxCell.getType().equals("Start"))
                    {
                        startNode = mxCell.getId();
                        
                        ArrayList<MxParam> mxParamList = mxCell.getListOfMxParam();
                        for(MxParam mxParam : mxParamList)
                        {
                        	shortCode = mxParam.getShortcode();
                        	serviceName = mxParam.getService();
                        	calltype = mxParam.getCalltype();
                        }
                        //Logger.sysLog(LogValues.info, this.getClass().getName(),"serviceName = "+serviceName+"\tstartNode = "+startNode+"\tshortCode = "+shortCode+"\tcalltype = "+calltype);
                        if(calltype.equalsIgnoreCase("obd"))
                        	key = shortCode+"_"+calltype+"_"+serviceName;
                        else
                        	key = shortCode+"_"+calltype;        
                    }
                    else if(mxCell.getType().equals("Multiplay"))
                    { 
	                    try
	                    {
	                    	 ArrayList<MxParam> mxParamList = mxCell.getListOfMxParam();
	                    	 HashMap multiplay = new HashMap<>();
	                    	 int playlistid = 0;
	                    	 int k =0;
	                    //	 ArrayList playlist = null;
	                    	ArrayList<NameValuePair>  list = null;
	                    	 NameValuePair nameValuePair = null;
	                    	 String fileName;
	                 		String file;
	                 		String contentId;
	                         for(MxParam mxParam : mxParamList)
	                         {
	                        	Logger.sysLog(LogValues.info, this.getClass().getName(), " playlistid ="+playlistid+" ,  mxParam.getPlaylistnumber()  ="+ mxParam.getPlaylistnumber() );
	                        	if( playlistid == mxParam.getPlaylistnumber())
	                        	{
	                        		file = mxParam.getPlaylist();
	                    			fileName = file.substring(file.indexOf("-") + 1,file.length()); 
	                				contentId = file.substring(0,file.indexOf("-"));
	                				nameValuePair = new NameValuePair(fileName,contentId);	
	                				list.add(nameValuePair);
	                				k++;
	                        		
	                        	}
	                        	else
	                        	{
	                        		if(list != null)
	                        		{
	                        			if(list.size() > 0)
	                        			{
	                        				NameValuePair[] contentlist = new NameValuePair[list.size()];
	                        				for(int m = 0 ;m < list.size() ; m++)
	                        				{
	                        					contentlist[m] = list.get(m);
	                        					Logger.sysLog(LogValues.info, this.getClass().getName(), "contentlist["+m+"]  ="+contentlist[m].getFile());
	                        				}
	                        				multiplay.put( playlistid, contentlist);
	                        			}
	                        			 Logger.sysLog(LogValues.info, this.getClass().getName(), "list not null , for playlistid ="+playlistid+" , put list of size = "+list.size());
	                        			 list = null;
	                        		}
	                        		
	                        		playlistid = mxParam.getPlaylistnumber();
	                        		file = mxParam.getPlaylist();
	                    			fileName = file.substring(file.indexOf("-") + 1,file.length()); 
	                				contentId = file.substring(0,file.indexOf("-"));
	                				nameValuePair = new NameValuePair(fileName,contentId);	
	                				list = new ArrayList<NameValuePair>();
	                				list.add(nameValuePair);
	                			}
	                        	
	                         }
	                         if(list != null)
	                 		{
	                 			if(list.size() > 0)
	                 			{
	                 				NameValuePair[] contentlist = new NameValuePair[list.size()];
	                 				for(int m = 0 ;m < list.size() ; m++)
	                 				{
	                 					contentlist[m] = list.get(m);
	                 					Logger.sysLog(LogValues.info, this.getClass().getName(), "contentlist["+m+"]  ="+contentlist[m].getFile());
	                 				}
	                 				multiplay.put( playlistid, contentlist);
	                 			}
	                 			Logger.sysLog(LogValues.info, this.getClass().getName(), "list not null , for playlistid ="+playlistid+" , put list of size = "+list.size());
	                 			list = null;
	                 		}
	                         memCacheJSon.set("multiplay_hm_"+mxCell.getId(), multiplay);
	                         Logger.sysLog(LogValues.info, this.getClass().getName(), "PLAYLIST HashMap key = multiplay_hm_"+mxCell.getId());
	                         Logger.sysLog(LogValues.info, this.getClass().getName(), "PLAYLIST HashMap = "+multiplay);
	                    
	                    }
	                    catch(Exception e)
	                    {
	                    	  Logger.sysLog(LogValues.error, this.getClass().getName(), "Error while USING MULTIPLAY");
	                    }
                    }
                }
                
                objMap.put(mxCell.getId(), mxCell);
                Integer id = mxCell.getId();
                //Logger.sysLog(LogValues.info, this.getClass().getName(),"1. id = "+id+"\t Type = "+mxCell.getType());
                if(mxCell.getType() != null)
                {
                    if ((!(mxCell.getType()).equalsIgnoreCase("Normal")) || (!(mxCell.getType()).equalsIgnoreCase("DB")) ||
                    		(!(mxCell.getType()).equalsIgnoreCase("DTMF")) || (!(mxCell.getType()).equalsIgnoreCase("Check")))
                    {
                        ArrayList<Pair<String, Integer>> adjcentlist =new ArrayList<Pair<String, Integer>>();
                        for ( int j = 0; j < listOfMxcell.size();j++)
                        { 
                            MxCell internalCell = bngModel.getMxCellList().get(j);
                            //Logger.sysLog(LogValues.info, this.getClass().getName(),"2. id = "+mxCell.getId()+"\t internal cell source = "+internalCell.getSource()+"\tinternal cell id = "+internalCell.getId());
                            if(mxCell.getId() == internalCell.getSource())
                            {
                                found=true;
                                Integer target= internalCell.getTarget();
                                String value= internalCell.getValue(); 
                                Pair<String,Integer> p = new Pair<String, Integer>(value,target);
                                //Logger.sysLog(LogValues.info, this.getClass().getName(), "@#@ value = "+value+"\tsource = "+mxCell.getId()+"\ttarget = "+target);
                                adjcentlist.add(p);
                                // put the cell and the internal cell in a map  
                            }
                            if(found)
                            { 
                                // now put this in to another array list
                                Pair<Integer,ArrayList<Pair<String,Integer>>> pPrime= new Pair<Integer,ArrayList<Pair<String,Integer>>>(id,adjcentlist);
                                mainlist.add(pPrime);
                                //System.out.printf("Mainlist id=%d\n",id);
                                found = false;
                            }
                        }// end for internal loop                        
                    } // find out which type of object it is 
                } //if type not null
            } // external loop
            
            //adding startNodeId,objMap and mainList to memCache.
            memCacheJSon.set(key+"_startNode", startNode);
            Logger.sysLog(LogValues.info, this.getClass().getName(), "StartNode(ID) key = "+key+"_startNode");
			
            memCacheJSon.set(key+"_hm", objMap);
            Logger.sysLog(LogValues.info, this.getClass().getName(), "Service HashMap key = "+key+"_hm");
            
            memCacheJSon.set(key+"_ml", mainlist);
            Logger.sysLog(LogValues.info,this.getClass().getName(),"Service MainList key = "+key+"_ml ,  mainlist size = "+mainlist.size());
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.error, this.getClass().getName(),coreException.GetStack(e));
        }
    }
}