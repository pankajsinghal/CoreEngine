package com.bng.core.daoImpl;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import com.bng.core.dao.UnsubscriptionDao;
import com.bng.core.entity.Unsubscription;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

public class UnsubscriptionDaoImpl implements UnsubscriptionDao{

	SessionFactoryList sessionFactoryList;
	
	public void setSessionFactoryList(SessionFactoryList sessionFactoryList) {
		this.sessionFactoryList = sessionFactoryList;
	}
	
	@Transactional(readOnly = true)    
	public Unsubscription selectUnsubscription(String msisdn, String shortCode, String serviceName) {
    	Unsubscription unsubscription = null;
        SessionFactory sessionFactory = null;
        try
        {            
        	sessionFactory = sessionFactoryList.getSessionFactory(msisdn);            
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(Unsubscription.class);
            List list = crit.add(Restrictions.eq("msisdn", msisdn))
                    .add(Restrictions.eq("serviceId", serviceName))
                    .list(); 
            
            Logger.sysLog(LogValues.info, this.getClass().getName(),"Query Executed : list = "+list);            
            if(list != null)
            {
                Iterator listIterator = list.iterator();
                while(listIterator.hasNext())
                {
                    unsubscription = (Unsubscription)listIterator.next();
                }
            }
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
            //sessionFactory.getCurrentSession().getTransaction().rollback();
        }
        return unsubscription;
	}

}
