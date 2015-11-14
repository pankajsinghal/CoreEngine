/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.boImpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bng.core.bo.SubscriptionBo;
import com.bng.core.dao.SubscriptionDao;
import com.bng.core.entity.Subscription;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

/**
 *
 * @author richa
 */
@Service("SubscriptionBo") 
@Transactional(propagation=Propagation.SUPPORTS, readOnly=true) 
public class SubscriptionBoImpl implements SubscriptionBo{
        
    //@Autowired  
    private SubscriptionDao subscriptionDao; 

    public void setSubscriptionDao(SubscriptionDao subscriptionDao) {
        this.subscriptionDao = subscriptionDao;
       
    }
    
    public Subscription checkExistingSubs(String msisdn,String shortCode,String serviceName)
    {
        Subscription subscription = null;
        try
        {
            subscription = subscriptionDao.selectSubscription(msisdn, shortCode, serviceName);            
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.info, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
        }               
        return subscription;
    }   

    public boolean updateSubscription(Subscription subscription, String msisdn)
	{		
		return subscriptionDao.updateSubscription(subscription, msisdn);
	} 
    
    public boolean insertSubscription(String msisdn, String serviceName, String status, String language)
    {
    	return subscriptionDao.insertSubscription(msisdn, serviceName, status, language);
    }
}
