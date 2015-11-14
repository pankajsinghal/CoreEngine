package com.bng.core.daoImpl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import com.bng.core.dao.VisitorsDao;
import com.bng.core.entity.Visitors;
import com.bng.core.exception.coreException;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

public class VisitorsDaoImpl implements VisitorsDao
{	
	SessionFactoryList sessionFactoryList;
	
    public void setSessionFactoryList(SessionFactoryList sessionFactoryList) {
		this.sessionFactoryList = sessionFactoryList;
	}   
    
	@Transactional(readOnly = false)
	public Visitors getVisitorsDialCount(String msisdn, String servicename, String shortCode)
	{
		Visitors visitors = null;
		SessionFactory sessionFactory = null;
		try
		{
			sessionFactory = sessionFactoryList.getSessionFactory(msisdn);   
        	Logger.sysLog(LogValues.info, this.getClass().getName(),"Inside getVisitorsDialCount. serviceName = "+servicename);
            
        	Criteria crit = sessionFactory.getCurrentSession().createCriteria(Visitors.class);
            List list = crit.add(Restrictions.eq("msisdn", msisdn))
                            .add(Restrictions.eq("serviceId", servicename))
                            .add(Restrictions.eq("shortCode", shortCode))
                            .list(); 
            crit.setCacheable(true);
            Logger.sysLog(LogValues.info, this.getClass().getName(),"Query Executed : list = "+list);            
            if(list != null)
            {
                Iterator listIterator = list.iterator();
                while(listIterator.hasNext())
                {
                	visitors = (Visitors)listIterator.next();
                }
            }           
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
        }        
        return visitors;
	}
	
	@Transactional
	public void insertVisitorsDialCount(String msisdn, String serviceName, String shortCode)
	{
		SessionFactory sessionFactory = null;
		Visitors visitors = null;
		try
		{
			sessionFactory = sessionFactoryList.getSessionFactory(msisdn);   
        	Logger.sysLog(LogValues.info, this.getClass().getName(),"Inside insertVisitorsDialCount. serviceName = "+serviceName+ "msisdn = "+msisdn);
            
			visitors = new Visitors();
			visitors.setMsisdn(msisdn);
			visitors.setServiceId(serviceName);
			visitors.setShortCode(shortCode);
			visitors.setStartDate(new Date());
			visitors.setLastCallDate(new Date());
			visitors.setCallAttempts(1);
			sessionFactory.getCurrentSession().save(visitors);
			Logger.sysLog(LogValues.info, this.getClass().getName(),"New visitor added in DB. msisdn = "+msisdn+" , serviceName = "+serviceName+" , shortCode = "+shortCode);
		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
		}
	}
	
	@Transactional
	public void updateVisitorDialCount(Visitors visitor)
	{
		SessionFactory sessionFactory = null;
		try
		{
			sessionFactory = sessionFactoryList.getSessionFactory(visitor.getMsisdn());   
        	
        	sessionFactory.getCurrentSession().update(visitor);
        	Logger.sysLog(LogValues.info, this.getClass().getName(),"Visitor dial count updated successfully. serviceName = "+visitor.getServiceId()+"msisdn = "+visitor.getMsisdn());
		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
		}
	}
}
