package com.bng.core.dao;

import com.bng.core.entity.Visitors;

public interface VisitorsDao 
{	
	public Visitors getVisitorsDialCount(String msisdn, String servicename, String shortCode);
	public void insertVisitorsDialCount(String msisdn, String serviceName, String shortCode);
	public void updateVisitorDialCount(Visitors visitor);
}
