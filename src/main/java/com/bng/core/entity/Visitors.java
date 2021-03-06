package com.bng.core.entity;
// Generated 9 Dec, 2013 3:48:47 PM by Hibernate Tools 3.2.1.GA


import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * Visitors generated by hbm2java
 */
@Entity
@Table(name="visitors"
    , uniqueConstraints = @UniqueConstraint(columnNames={"MSISDN", "SERVICE_ID"}) 
)
public class Visitors  implements java.io.Serializable 
{
	private Integer id;
    private String msisdn;
    private String serviceId;
    private String shortCode;
    private Date startDate;
    private Date lastCallDate;
    private Integer callAttempts;

    public Visitors() {
    }

    public Visitors(String msisdn, String serviceId, String shortCode, Date startDate, Date lastCallDate, Integer callAttempts) {
       this.msisdn = msisdn;
       this.serviceId = serviceId;
       this.shortCode = shortCode;
       this.startDate = startDate;
       this.lastCallDate = lastCallDate;
       this.callAttempts = callAttempts;
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
    
    @Column(name="SHORT_CODE", length=10)
    public String getShortCode() {
        return this.shortCode;
    }
    
    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
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
    @Column(name="LAST_CALL_DATE", length=19)
    public Date getLastCallDate() {
        return this.lastCallDate;
    }
    
    public void setLastCallDate(Date lastCallDate) {
        this.lastCallDate = lastCallDate;
    }
        
    @Column(name="CALL_ATTEMPTS")
    public Integer getCallAttempts() {
        return this.callAttempts;
    }
    
    public void setCallAttempts(Integer callAttempts) {
        this.callAttempts = callAttempts;
    }




}


