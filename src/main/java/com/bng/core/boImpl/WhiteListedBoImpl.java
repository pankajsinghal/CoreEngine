package com.bng.core.boImpl;

import com.bng.core.bo.WhiteListedBo;
import com.bng.core.dao.WhiteListedDao;
import com.bng.core.exception.coreException;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

public class WhiteListedBoImpl implements WhiteListedBo{

	WhiteListedDao whiteListedDaoImpl;

	public void setWhiteListedDaoImpl(WhiteListedDao whiteListedDaoImpl) {
		this.whiteListedDaoImpl = whiteListedDaoImpl;
	}

	public boolean isWhiteListed(String msisdn)
	{
		try
		{
			return (whiteListedDaoImpl.checkWhiteListed(msisdn));
		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.info, this.getClass().getName(), coreException.GetStack(e));
			return false;
		}
	}
}
