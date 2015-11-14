package com.bng.core.boImpl;

import com.bng.core.bo.VisitorsBo;
import com.bng.core.dao.VisitorsDao;
import com.bng.core.entity.Visitors;

public class VisitorsBoImpl implements VisitorsBo
{	
	VisitorsDao visitorsDaoImpl;	
	
	public void setVisitorsDaoImpl(VisitorsDao visitorsDaoImpl) {
		this.visitorsDaoImpl = visitorsDaoImpl;
	}

	public Visitors getVisitorsDialCount(String msisdn, String servicename, String shortCode)
	{
		return visitorsDaoImpl.getVisitorsDialCount(msisdn, servicename, shortCode);
	}
	
	public void insertVisitorsDialCount(String msisdn, String serviceName, String shortCode)
	{
		visitorsDaoImpl.insertVisitorsDialCount(msisdn, serviceName, shortCode);
	}

	public void updateVisitorDialCount(Visitors visitor) {
		visitorsDaoImpl.updateVisitorDialCount(visitor);		
	}
	
	
}
