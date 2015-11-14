package com.bng.core.boImpl;

import com.bng.core.bo.BookmarkBo;
import com.bng.core.dao.BookmarkDao;
import com.bng.core.entity.Bookmark;

public class BookmarkBoImpl implements BookmarkBo{
	
	BookmarkDao bookmarkDaoImpl;
	
	public void setBookmarkDaoImpl(BookmarkDao bookmarkDaoImpl) {
		this.bookmarkDaoImpl = bookmarkDaoImpl;
	}

	public Bookmark getSeekBytes(String msisdn, int playId)
	{
		Bookmark bookmark = bookmarkDaoImpl.getBookmark(msisdn, playId);
		return bookmark;
	}
	
	public boolean addBookmark(String msisdn, int playId, String file, String bytesPlayed)
	{
		return bookmarkDaoImpl.insertBookmark(msisdn, playId, file, bytesPlayed);
	}
	
	public Bookmark getBookmark(String msisdn)
	{
		return bookmarkDaoImpl.getBookmark(msisdn);
	}

	
	public boolean updateBookmark(String msisdn, int playId,String file, String bytesPlayed) {
		
		return bookmarkDaoImpl.updateBookmark(msisdn, playId, file, bytesPlayed);
	}

	public boolean updateSeekWithListID(String msisdn, int playId, String file,String bytesPlayed, int listid) {
		
		return bookmarkDaoImpl.updateSeekWithListID(msisdn, playId, file, bytesPlayed, listid);
	}

	
	public boolean insertSeekWithListID(String msisdn, int playId, String file,
			String bytesPlayed, int listid) {
		
		return bookmarkDaoImpl.insertSeekWithListID(msisdn, playId, file, bytesPlayed, listid);
	}
	
	
	
}
