/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.bo;

import com.bng.core.entity.Subscription;

/**
 *
 * @author richa
 */
public interface SubscriptionBo 
{
    public Subscription checkExistingSubs(String msisdn, String shortCode, String serviceName);
    public boolean updateSubscription(Subscription subscription, String msisdn);
    public boolean insertSubscription(String msisdn, String serviceName, String status, String language);
}
