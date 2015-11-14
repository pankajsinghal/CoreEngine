/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.dao;

import com.bng.core.entity.Subscription;

/**
 *
 * @author richa
 */
public interface SubscriptionDao{
    public Subscription selectSubscription(String msisdn, String shortCode, String serviceName);
    public boolean updateSubscription(Subscription subscription, String msisdn);
    public boolean insertSubscription(String msisdn, String serviceName, String status,String language);
}
