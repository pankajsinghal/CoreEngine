package com.bng.core.bo;

import com.bng.core.entity.Unsubscription;

public interface UnsubscriptionBo {
	public Unsubscription checkUnsubscription(String msisdn, String shortCode, String serviceName);
}
