package com.bng.core.bo;

import com.bng.core.entity.Visitors;

public interface VisitorsBo 
{
	public Visitors getVisitorsDialCount(String msisdn, String servicename, String shortCode);
	public void insertVisitorsDialCount(String msisdn, String serviceName, String shortCode); 
	public void updateVisitorDialCount(Visitors visitor);
}
