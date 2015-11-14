package com.bng.core.daoImpl;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bng.core.dao.CircleDao;
import com.bng.core.entity.CircleMapper;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

@Repository("CircleDao") 
public class CicleDaoImpl implements CircleDao{
	
	SessionFactoryList sessionFactoryList;
	
	public void setSessionFactoryList(SessionFactoryList sessionFactoryList) {
		this.sessionFactoryList = sessionFactoryList;
	}
	
	@Transactional(readOnly = true)    
	public CircleMapper getCircleInfo(String msisdn)
	{
		SessionFactory sessionFactory = null;
		CircleMapper circleMapper = null;
		try{
			
			if(msisdn.startsWith("0"))
        		msisdn = msisdn.substring(1,msisdn.length());
        	else if(msisdn.startsWith("91"))
        		msisdn = msisdn.substring(2,msisdn.length());
			
			if(msisdn.length() > 5)
			{
				sessionFactory = sessionFactoryList.getSessionFactory(msisdn); 
				Criteria crit = sessionFactory.getCurrentSession().createCriteria(CircleMapper.class);
				List list = crit.add(Restrictions.eq("series", Integer.parseInt(msisdn.substring(0, 5))))
	                            .list(); 
	            
	            Iterator iter = list.iterator();
	            while(iter.hasNext())
	            {
	            	circleMapper = (CircleMapper)iter.next();
	            }
			}
            return circleMapper;
		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
			return circleMapper;
		}
	}
}
