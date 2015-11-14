/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.daoImpl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bng.core.dao.SubscriptionDao;
import com.bng.core.entity.Subscription;
import com.bng.core.exception.coreException;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

/**
 *
 * @author richa
 */
@Repository("SubscriptionDao") 
public class SubscriptionDaoImpl implements SubscriptionDao{

    SessionFactoryList sessionFactoryList;
	
    public void setSessionFactoryList(SessionFactoryList sessionFactoryList) {
		this.sessionFactoryList = sessionFactoryList;
	}    

    @Transactional(readOnly = false)
    public Subscription selectSubscription(String msisdn, String shortCode, String serviceName) {
        Subscription subscription = null;
        SessionFactory sessionFactory = null;
        try
        {       
        	sessionFactory = sessionFactoryList.getSessionFactory(msisdn);   
        	Logger.sysLog(LogValues.info, this.getClass().getName(),"Inside selectSubscription. shortCode = "+shortCode+", serviceName = "+serviceName);
            
        	Criteria crit = sessionFactory.getCurrentSession().createCriteria(Subscription.class);
            List list = crit.add(Restrictions.eq("msisdn", msisdn))
                            .add(Restrictions.eq("serviceId", serviceName))
                            .list(); 
            crit.setCacheable(true);
            Logger.sysLog(LogValues.info, this.getClass().getName(),"Query Executed : list = "+list);            
            if(list != null)
            {
                Iterator listIterator = list.iterator();
                while(listIterator.hasNext())
                {
                    subscription = (Subscription)listIterator.next();
                }
            }           
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
        }        
        return subscription;
    }    

    @Transactional(readOnly = false)
    public boolean updateSubscription(Subscription subscription, String msisdn)
    {
    	SessionFactory sessionFactory = null;
    	Session session = null;
    	try
        {       
    		Logger.sysLog(LogValues.debug, this.getClass().getName(), "Inside updateSubscription.");
            sessionFactory = sessionFactoryList.getSessionFactory(msisdn);  
            session = sessionFactory.getCurrentSession();
            
            if(subscription != null)
            	session.update(subscription);  
            Logger.sysLog(LogValues.info, this.getClass().getName(), "Subscription updated for msisdn = "+msisdn);
            return true; 
        }
	    catch(Exception e)
	    {
	    	Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
	        sessionFactory.getCurrentSession().getTransaction().rollback();
	        return false;
	    }	
    	finally
    	{
    		sessionFactory = null;
    		session = null;
    	}    	
    } 
    
  
	@Transactional
	public boolean insertSubscription(String msisdn, String serviceName, String status,String language)
	{
		SessionFactory sessionFactory = null;
		Subscription subscription = null;
		boolean flag =false;
		try
		{
			Logger.sysLog(LogValues.info, this.getClass().getName(),"["+msisdn+"]["+serviceName+"] Going to insert pending status in Subscription table.");
			sessionFactory = sessionFactoryList.getSessionFactory(msisdn);  
			subscription = new Subscription();
			subscription.setMsisdn(msisdn);
			subscription.setServiceId(serviceName);
			subscription.setStatus(status);
			subscription.setLanguage(language);
			subscription.setStartDate(new Date());
			sessionFactory.getCurrentSession().saveOrUpdate(subscription);
			flag = true;
		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, this.getClass().getName(),"INSERT ERROR");
	        sessionFactory.getCurrentSession().getTransaction().rollback();
	        
		}  
		return flag;
	}
	}

