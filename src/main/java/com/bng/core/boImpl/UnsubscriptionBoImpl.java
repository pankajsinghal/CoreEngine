package com.bng.core.boImpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bng.core.bo.UnsubscriptionBo;
import com.bng.core.dao.UnsubscriptionDao;
import com.bng.core.entity.Unsubscription;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

@Service("UnsubscriptionBo") 
@Transactional(propagation=Propagation.SUPPORTS, readOnly=true) 
public class UnsubscriptionBoImpl implements UnsubscriptionBo{

	private UnsubscriptionDao unsubscriptionDao; 

    public void setUnsubscriptionDao(UnsubscriptionDao unsubscriptionDao) {
		this.unsubscriptionDao = unsubscriptionDao;
	}

	public Unsubscription checkUnsubscription(String msisdn,String shortCode, String serviceName)
    {
        Unsubscription unsubscription = null;
        try
        {
        	unsubscription = unsubscriptionDao.selectUnsubscription(msisdn, shortCode, serviceName);            
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.info, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
        }               
        return unsubscription;
    }	
}
