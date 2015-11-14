package com.bng.core.boImpl;

import com.bng.core.bo.CircleBo;
import com.bng.core.dao.CircleDao;
import com.bng.core.entity.CircleMapper;

public class CircleBoImpl implements CircleBo{
	
	CircleDao circleDaoImpl;

	public void setCircleDaoImpl(CircleDao circleDaoImpl) {
		this.circleDaoImpl = circleDaoImpl;
	}

	public CircleMapper getCircleInfo(String msisdn)
	{
		CircleMapper circleMapper =  circleDaoImpl.getCircleInfo(msisdn);
		return circleMapper;
	}
}
