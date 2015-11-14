package com.bng.core.daoImpl;

import java.util.Map;

import org.hibernate.SessionFactory;

import com.bng.core.exception.coreException;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

public class SessionFactoryList {
	public Map<String, SessionFactory> sessionfactorylist;
    public int noOfDB;

    public void setNoOfDB(int noOfDB) {
        this.noOfDB = noOfDB;
    }

    public void setSessionfactorylist(Map<String, SessionFactory> sessionfactorylist) {
    this.sessionfactorylist = sessionfactorylist;
    }
    
    public SessionFactory getSessionFactory(String msisdn)
    {
    	try
        {            
            int mod = 0;//Utility.getModValue(msisdn, noOfDB); 
            //Logger.sysLog(LogValues.info, this.getClass().getName(),"noOfDB = "+noOfDB+"\tmod = "+mod+"\tmsisdn = "+msisdn);
            //Logger.sysLog(LogValues.info, this.getClass().getName(),"sessionFactory = "+sessionfactorylist.get(""+mod));
            return sessionfactorylist.get(""+mod);
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.error, this.getClass().getName(),coreException.GetStack(e));
        	return null;
        }
    }
}
