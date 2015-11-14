package com.bng.core.bo;

import com.bng.core.entity.Bookmark;

public interface BookmarkBo {
	public Bookmark getSeekBytes(String msisdn, int playId);
	public Bookmark getBookmark(String msisdn);
	public boolean addBookmark(String msisdn, int playId, String file, String bytesPlayed);
	public boolean updateBookmark(String numbercorrected, int parseInt,String lastFilePlayed, String seekBytes);
	public boolean updateSeekWithListID(String msisdn, int playId, String file, String bytesPlayed,int listid);
	public boolean insertSeekWithListID(String msisdn, int playId, String file, String bytesPlayed,int listid);
	
}
