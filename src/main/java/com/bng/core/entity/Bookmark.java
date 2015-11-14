package com.bng.core.entity;
// Generated 14 Nov, 2013 12:00:29 PM by Hibernate Tools 3.2.1.GA


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Bookmark generated by hbm2java
 */
@Entity
@Table(name="BOOKMARK"
    ,catalog="IVR_DATA"
)
public class Bookmark  implements java.io.Serializable {


     private int id;
     private String bytesPlayed;
     private String filePlayed;
     private String msisdn;
     private int promptId;
     private int playlistid;

    public Bookmark() {
    }

	
    public Bookmark(int id, int promptId) {
        this.id = id;
        this.promptId = promptId;
    }
    public Bookmark(int id, String bytesPlayed, String filePlayed, String msisdn, int promptId , int playlistid) {
       this.id = id;
       this.bytesPlayed = bytesPlayed;
       this.filePlayed = filePlayed;
       this.msisdn = msisdn;
       this.promptId = promptId;
       this.playlistid = playlistid;
    }
   
     @Id 
    
    @Column(name="ID", unique=true, nullable=false)
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    @Column(name="BYTES_PLAYED", length=100)
    public String getBytesPlayed() {
        return this.bytesPlayed;
    }
    
    public void setBytesPlayed(String bytesPlayed) {
        this.bytesPlayed = bytesPlayed;
    }
    
    @Column(name="FILE_PLAYED", length=100)
    public String getFilePlayed() {
        return this.filePlayed;
    }
    
    public void setFilePlayed(String filePlayed) {
        this.filePlayed = filePlayed;
    }
    
    @Column(name="MSISDN", length=25)
    public String getMsisdn() {
        return this.msisdn;
    }
    
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
    
    @Column(name="PROMPT_ID", nullable=false)
    public int getPromptId() {
        return this.promptId;
    }
    
    public void setPromptId(int promptId) {
        this.promptId = promptId;
    }
	
    @Column(name="PLAYLIST_ID")
    public int getPlaylistid() {
		return playlistid;
	}


	public void setPlaylistid(int playlistid) {
		this.playlistid = playlistid;
	}




}

