package com.bng.core.boImpl;

import com.bng.core.bo.RedCarpetListBo;
import com.bng.core.dao.RedCarpetListDao;
import com.bng.core.dao.WhiteListedDao;
import com.bng.core.exception.coreException;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

public class RedCarpetListBoImpl implements RedCarpetListBo{

	RedCarpetListDao redCarpetListDaoImpl;

	public void setRedCarpetListDaoImpl(RedCarpetListDao redCarpetListDaoImpl) {
		this.redCarpetListDaoImpl = redCarpetListDaoImpl;
	}

	public boolean isInRedCarpetList(String msisdn) 
	{
		try
		{
			return (redCarpetListDaoImpl.checkInRedCarpetList(msisdn));
		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.info, this.getClass().getName(), coreException.GetStack(e));
			return false;
		}
	}

}
