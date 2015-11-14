package com.bng.core.entity;
// Generated 14 Nov, 2013 12:00:29 PM by Hibernate Tools 3.2.1.GA


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

/**
 * Subscription generated by hbm2java
 */
@Entity
@Table(name="subscription"
    ,catalog="IVR_DATA"
    , uniqueConstraints = @UniqueConstraint(columnNames={"MSISDN", "SERVICE_ID"}) 
)
public class Subscription  implements java.io.Serializable {


     private Integer id;
     private String msisdn;
     private String serviceId;
     private Date startDate;
     private Date endDate;
     private Date lastRenewDate;
     private Date lastCallDate;
     private String transactionStatus;
     private String mdnType;
     private String circle;
     private String country;
     private String language;
     private Integer price;
     private Integer callAttempts;
     private String status;
     private String primaryActMode;
     private String secondaryActMode;
     private String errorMsg;
     private String subType;
     private Integer subTimeLeft;
     private Integer alert;
     private String subServiceName;
     private boolean isFirstCaller ;
    public Subscription() {
    }

    public Subscription(String msisdn, String serviceId, Date startDate, Date endDate, Date lastRenewDate, Date lastCallDate, String transactionStatus, String mdnType, String circle, String country, String language, Integer price, Integer callAttempts, String status, String primaryActMode, String secondaryActMode, String errorMsg, String subType, Integer subTimeLeft, Integer alert, String subServiceName, boolean isfirstcaller) {
       this.msisdn = msisdn;
       this.serviceId = serviceId;
       this.startDate = startDate;
       this.endDate = endDate;
       this.lastRenewDate = lastRenewDate;
       this.lastCallDate = lastCallDate;
       this.transactionStatus = transactionStatus;
       this.mdnType = mdnType;
       this.circle = circle;
       this.country = country;
       this.language = language;
       this.price = price;
       this.callAttempts = callAttempts;
       this.status = status;
       this.primaryActMode = primaryActMode;
       this.secondaryActMode = secondaryActMode;
       this.errorMsg = errorMsg;
       this.subType = subType;
       this.subTimeLeft = subTimeLeft;
       this.alert = alert;
       this.subServiceName = subServiceName;
       this.isFirstCaller = isfirstcaller;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)
    
    @Column(name="ID", unique=true, nullable=false)
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    @Column(name="MSISDN", length=25)
    public String getMsisdn() {
        return this.msisdn;
    }
    
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
    
    @Column(name="SERVICE_ID", length=15)
    public String getServiceId() {
        return this.serviceId;
    }
    
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="START_DATE", length=19)
    public Date getStartDate() {
        return this.startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="END_DATE", length=19)
    public Date getEndDate() {
        return this.endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="LAST_RENEW_DATE", length=19)
    public Date getLastRenewDate() {
        return this.lastRenewDate;
    }
    
    public void setLastRenewDate(Date lastRenewDate) {
        this.lastRenewDate = lastRenewDate;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="LAST_CALL_DATE", length=19)
    public Date getLastCallDate() {
        return this.lastCallDate;
    }
    
    public void setLastCallDate(Date lastCallDate) {
        this.lastCallDate = lastCallDate;
    }
    
    @Column(name="TRANSACTION_STATUS", length=15)
    public String getTransactionStatus() {
        return this.transactionStatus;
    }
    
    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
    
    @Column(name="MDN_TYPE", length=2)
    public String getMdnType() {
        return this.mdnType;
    }
    
    public void setMdnType(String mdnType) {
        this.mdnType = mdnType;
    }
    
    @Column(name="CIRCLE", length=3)
    public String getCircle() {
        return this.circle;
    }
    
    public void setCircle(String circle) {
        this.circle = circle;
    }
    
    @Column(name="COUNTRY", length=3)
    public String getCountry() {
        return this.country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    @Column(name="LANGUAGE", length=3)
    public String getLanguage() {
        return this.language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    @Column(name="PRICE")
    public Integer getPrice() {
        return this.price;
    }
    
    public void setPrice(Integer price) {
        this.price = price;
    }
    
    @Column(name="CALL_ATTEMPTS")
    public Integer getCallAttempts() {
        return this.callAttempts;
    }
    
    public void setCallAttempts(Integer callAttempts) {
        this.callAttempts = callAttempts;
    }
    
    @Column(name="STATUS", length=20)
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Column(name="PRIMARY_ACT_MODE", length=25)
    public String getPrimaryActMode() {
        return this.primaryActMode;
    }
    
    public void setPrimaryActMode(String primaryActMode) {
        this.primaryActMode = primaryActMode;
    }
    
    @Column(name="SECONDARY_ACT_MODE", length=25)
    public String getSecondaryActMode() {
        return this.secondaryActMode;
    }
    
    public void setSecondaryActMode(String secondaryActMode) {
        this.secondaryActMode = secondaryActMode;
    }
    
    @Column(name="ERROR_MSG", length=25)
    public String getErrorMsg() {
        return this.errorMsg;
    }
    
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
    
    @Column(name="SUB_TYPE", length=10)
    public String getSubType() {
        return this.subType;
    }
    
    public void setSubType(String subType) {
        this.subType = subType;
    }
    
    @Column(name="SUB_TIME_LEFT")
    public Integer getSubTimeLeft() {
        return this.subTimeLeft;
    }
    
    public void setSubTimeLeft(Integer subTimeLeft) {
        this.subTimeLeft = subTimeLeft;
    }
    
    @Column(name="ALERT")
    public Integer getAlert() {
        return this.alert;
    }
    
    public void setAlert(Integer alert) {
        this.alert = alert;
    }
    
    @Column(name="SUB_SERVICE_NAME", length=25)
    public String getSubServiceName() {
        return this.subServiceName;
    }
    
    public void setSubServiceName(String subServiceName) {
        this.subServiceName = subServiceName;
    }

	/*
    @Column(name="FirstCallerOfMonth")
    public boolean isFirstCaller() {
		return isFirstCaller;
	}

	public void setFirstCaller(boolean isFirstCaller) {
		this.isFirstCaller = isFirstCaller;
		
	}*/


}


