package com.bng.core.daoImpl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bng.core.dao.RedCarpetListDao;
import com.bng.core.entity.IvrBlacklist;
import com.bng.core.entity.IvrRedcarpetlist;
import com.bng.core.exception.coreException;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

@Repository("RedCarpetListDao")
public class RedCarpetListDaoImpl implements RedCarpetListDao
{
	SessionFactoryList sessionFactoryList;
	
	public void setSessionFactoryList(SessionFactoryList sessionFactoryList) {
		this.sessionFactoryList = sessionFactoryList;
	}
	
	@Transactional(readOnly = true)    
	public boolean checkInRedCarpetList(String msisdn)
	{
		SessionFactory sessionFactory = null;
		boolean isInRedCarpetList;
		try
		{
			Logger.sysLog(LogValues.debug, this.getClass().getName(), "["+msisdn+"] Inside checkInRedCarpetList.");
			sessionFactory = sessionFactoryList.getSessionFactory(msisdn);            
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(IvrRedcarpetlist.class);
            List list = crit.add(Restrictions.eq("msisdn", msisdn))
                            .list(); 
            Logger.sysLog(LogValues.info, this.getClass().getName(), "["+msisdn+"] InRedCarpetList check : list = "+list);
            if(list == null || list.size() <= 0)
            	isInRedCarpetList =  false;
            else 
            	isInRedCarpetList =  true;  
        }
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
			isInRedCarpetList = false;
		}
		return isInRedCarpetList;
	}
}
