package com.bng.core.daoImpl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bng.core.dao.WhiteListedDao;
import com.bng.core.entity.IvrBlacklist;
import com.bng.core.entity.IvrWhitelist;
import com.bng.core.exception.coreException;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

@Repository("WhiteListedDao")
public class WhiteListedDaoImpl implements WhiteListedDao{
	
	SessionFactoryList sessionFactoryList;
	
	public void setSessionFactoryList(SessionFactoryList sessionFactoryList) {
		this.sessionFactoryList = sessionFactoryList;
	}
	
	@Transactional(readOnly = true)    
	public boolean checkWhiteListed(String msisdn)
	{
		SessionFactory sessionFactory = null;
		boolean isWhiteListed;
		try
		{
			Logger.sysLog(LogValues.debug, this.getClass().getName(), "["+msisdn+"] Inside checkWhiteListed.");
			sessionFactory = sessionFactoryList.getSessionFactory(msisdn);            
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(IvrWhitelist.class);
            List list = crit.add(Restrictions.eq("msisdn", msisdn))
                            .list(); 
            Logger.sysLog(LogValues.info, this.getClass().getName(), "["+msisdn+"] WhiteListed check :list = "+list);
            if(list == null || list.size() <= 0)
            	isWhiteListed =  false;
            else 
            	isWhiteListed =  true;  
        }
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
			isWhiteListed = false;
		}
		return isWhiteListed;
	}
}
