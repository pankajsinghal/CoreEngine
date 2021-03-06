package com.bng.core.daoImpl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bng.core.dao.BlackListedDao;
import com.bng.core.entity.IvrBlacklist;
import com.bng.core.exception.coreException;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

@Repository("BlackListedDao") 
public class BlackListedDaoImpl implements BlackListedDao{

	SessionFactoryList sessionFactoryList;
	
	public void setSessionFactoryList(SessionFactoryList sessionFactoryList) {
		this.sessionFactoryList = sessionFactoryList;
	}
	
	@Transactional(readOnly = true)    
	public boolean checkBlackListed(String msisdn)
	{
		SessionFactory sessionFactory = null;
		boolean isBlackListed;
		try
		{
			Logger.sysLog(LogValues.debug, this.getClass().getName(), "["+msisdn+"] Inside checkBlackListed.");
			sessionFactory = sessionFactoryList.getSessionFactory(msisdn);            
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(IvrBlacklist.class);
            List list = crit.add(Restrictions.eq("msisdn", msisdn))
                            .list(); 
            Logger.sysLog(LogValues.info, this.getClass().getName(), "["+msisdn+"] BlackListed check list = "+list+" for msisdn = "+msisdn);
            if(list == null || list.size() <= 0)
            	isBlackListed =  false;
            else 
            	isBlackListed =  true;  
        }
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
			isBlackListed = false;
}
		return isBlackListed;
	}
}
