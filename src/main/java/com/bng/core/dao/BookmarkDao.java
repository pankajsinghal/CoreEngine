package com.bng.core.dao;

import com.bng.core.entity.Bookmark;

public interface BookmarkDao {
	public Bookmark getBookmark(String msisd,int playId);
	public Bookmark getBookmark(String msisdn);
	public boolean insertBookmark(String msisdn, int playId, String file, String bytesPlayed);
	public boolean updateBookmark(String msisdn, int playId, String file, String bytesPlayed);
	public boolean updateSeekWithListID(String msisdn, int playId, String file, String bytesPlayed,int listid);
	public boolean insertSeekWithListID(String msisdn, int playId, String file, String bytesPlayed,int listid);
}
