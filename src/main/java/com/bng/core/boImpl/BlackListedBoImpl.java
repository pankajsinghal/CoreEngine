package com.bng.core.boImpl;

import com.bng.core.bo.BlackListedBo;
import com.bng.core.dao.BlackListedDao;
import com.bng.core.exception.coreException;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

public class BlackListedBoImpl implements BlackListedBo{
	
	BlackListedDao blackListedDaoImpl;

	public void setBlackListedDaoImpl(BlackListedDao blackListedDaoImpl) {
		this.blackListedDaoImpl = blackListedDaoImpl;
	}

	public boolean isBlackListed(String msisdn)
	{
		try
		{
			return (blackListedDaoImpl.checkBlackListed(msisdn));
		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.info, this.getClass().getName(), coreException.GetStack(e));
			return false;
		}
	}
}
