package com.bng.core.dao;

import com.bng.core.entity.Unsubscription;

public interface UnsubscriptionDao {
	public Unsubscription selectUnsubscription(String msisdn, String shortCode, String serviceName);
}
