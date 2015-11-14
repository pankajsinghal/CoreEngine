package com.bng.core.daoImpl;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import com.bng.core.dao.BookmarkDao;
import com.bng.core.entity.Bookmark;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

public class BookmarkDaoImpl implements BookmarkDao{

	SessionFactoryList sessionFactoryList;
	
	public void setSessionFactoryList(SessionFactoryList sessionFactoryList) {
		this.sessionFactoryList = sessionFactoryList;
	}

	@Transactional(readOnly = true)  
	public Bookmark getBookmark(String msisdn, int playId)
	{
		SessionFactory sessionFactory = null;
		Bookmark bookmark = null;
        try
        {            
        	if(msisdn.startsWith("0"))
        		msisdn = msisdn.substring(1,msisdn.length());
        	else if(msisdn.startsWith("91"))
        		msisdn = msisdn.substring(2,msisdn.length());
        	
            sessionFactory = sessionFactoryList.getSessionFactory(msisdn);            
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(Bookmark.class);
            
            List list = crit.add(Restrictions.eq("msisdn", msisdn))
            				.add(Restrictions.eq("promptId", playId))
                            .list();
            Logger.sysLog(LogValues.info, this.getClass().getName(),"bookmark found for msisdn = "+msisdn+" = "+list);
            if(list == null)
            	return null;
            
            Iterator iter = list.iterator();
            while(iter.hasNext())
            {
            	bookmark = (Bookmark)iter.next();
            } 
            list = null;
            crit = null;
            iter = null;
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
        }
        finally
        {
        	sessionFactory = null;
        }
		return bookmark;
	}
	
	@Transactional
	public boolean insertBookmark(String msisdn, int playId, String file, String bytesPlayed)
	{
		SessionFactory sessionFactory = null;
		Bookmark bookmark = null;
		try
        {			
			if(msisdn.startsWith("0"))
        		msisdn = msisdn.substring(1,msisdn.length());
        	else if(msisdn.startsWith("91"))
        		msisdn = msisdn.substring(2,msisdn.length());
			
			sessionFactory = sessionFactoryList.getSessionFactory(msisdn); 
			bookmark = getBookmark(msisdn, playId);
	        if(bookmark == null)
	        {
	        	Logger.sysLog(LogValues.info, this.getClass().getName(),"There is no bookmrk found for number "+msisdn+" and playId "+playId);
	        	bookmark = new Bookmark();
	        	bookmark.setMsisdn(msisdn);
		        bookmark.setPromptId(playId);
		        bookmark.setFilePlayed(file);
		        bookmark.setBytesPlayed(bytesPlayed);
		        sessionFactory.getCurrentSession().save(bookmark);
	        }	
	        else
	        {
	        	Logger.sysLog(LogValues.info, this.getClass().getName(),"Bookmark found for the number "+msisdn+" and playId "+playId+". Going to update bookmark.");
	        	 
	        	bookmark.setFilePlayed(file);
	 	        bookmark.setBytesPlayed(bytesPlayed);
	 	        sessionFactory.getCurrentSession().saveOrUpdate(bookmark);
	        }
	        	
	        
	       
	        
	       
	        Logger.sysLog(LogValues.info, this.getClass().getName(),"bookmark added successfully for number "+msisdn+" and playId "+playId);	        
	        return true;
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
        	return false;
        }
		finally
		{
			sessionFactory = null;
			bookmark = null;
		}
	}
	
	@Transactional(readOnly = true)  
	public Bookmark getBookmark(String msisdn)
	{
		SessionFactory sessionFactory = null;
		Bookmark bookmark = null;
        try
        {            
        	if(msisdn.startsWith("0"))
        		msisdn = msisdn.substring(1,msisdn.length());
        	else if(msisdn.startsWith("91"))
        		msisdn = msisdn.substring(2,msisdn.length());
        	
            sessionFactory = sessionFactoryList.getSessionFactory(msisdn);            
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(Bookmark.class);
            
            List list = crit.add(Restrictions.eq("msisdn", msisdn))
                            .list();
            Logger.sysLog(LogValues.info, this.getClass().getName(),"bookmark found"+list);
            if(list == null)
            	return null;
            
            Iterator iter = list.iterator();
            while(iter.hasNext())
            {
            	bookmark = (Bookmark)iter.next();
            } 
            list = null;
            crit = null;
            iter = null;
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
        }
        finally
        {
        	sessionFactory = null;
        }
		return bookmark;
	}

	@Transactional
	public boolean updateBookmark(String msisdn, int playId, String file,String bytesPlayed) {
		SessionFactory sessionFactory = null;
		Bookmark bookmark = null;
		try
        {			
			if(msisdn.startsWith("0"))
        		msisdn = msisdn.substring(1,msisdn.length());
        	else if(msisdn.startsWith("91"))
        		msisdn = msisdn.substring(2,msisdn.length());
			
			sessionFactory = sessionFactoryList.getSessionFactory(msisdn); 
			bookmark = getBookmark(msisdn);
	        if(bookmark == null)
	        {
	        	Logger.sysLog(LogValues.info, this.getClass().getName(),"There is no bookmrk found for number "+msisdn+" and playId "+playId);
	        	bookmark = new Bookmark();
	        	bookmark.setMsisdn(msisdn);
		       
	        }	
	        else
	        {
	        	Logger.sysLog(LogValues.info, this.getClass().getName(),"Bookmark found for the number "+msisdn+" and playId "+playId+". Going to update bookmark."); 
	        }
	        	
	        
	        bookmark.setPromptId(playId);
        	bookmark.setFilePlayed(file);
 	        bookmark.setBytesPlayed(bytesPlayed);
 	        sessionFactory.getCurrentSession().saveOrUpdate(bookmark);
	       
	        Logger.sysLog(LogValues.info, this.getClass().getName(),"bookmark update successfully for number "+msisdn+" and playId "+playId);	        
	        return true;
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
        	return false;
        }
		finally
		{
			sessionFactory = null;
			bookmark = null;
		}
	}
	
	@Transactional
	public boolean updateSeekWithListID(String msisdn, int playId, String file,String bytesPlayed , int listid) {
		SessionFactory sessionFactory = null;
		Bookmark bookmark = null;
		try
        {			
			if(msisdn.startsWith("0"))
        		msisdn = msisdn.substring(1,msisdn.length());
        	else if(msisdn.startsWith("91"))
        		msisdn = msisdn.substring(2,msisdn.length());
			
			sessionFactory = sessionFactoryList.getSessionFactory(msisdn); 
			bookmark = getBookmark(msisdn);
	        if(bookmark == null)
	        {
	        	Logger.sysLog(LogValues.info, this.getClass().getName(),"There is no bookmrk found for number "+msisdn+" and playId "+playId);
	        	bookmark = new Bookmark();
	        	bookmark.setMsisdn(msisdn);
		       
	        }	
	        else
	        {
	        	Logger.sysLog(LogValues.info, this.getClass().getName(),"Bookmark found for the number "+msisdn+" and playId "+playId+". Going to update bookmark."); 
	        }
	        	
	        
	        bookmark.setPromptId(playId);
        	bookmark.setFilePlayed(file);
        	bookmark.setPlaylistid(listid);
 	        bookmark.setBytesPlayed(bytesPlayed);
 	        sessionFactory.getCurrentSession().saveOrUpdate(bookmark);
	       
	        Logger.sysLog(LogValues.info, this.getClass().getName(),"bookmark update successfully for number "+msisdn+" and playId "+playId);	        
	        return true;
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
        	return false;
        }
		finally
		{
			sessionFactory = null;
			bookmark = null;
		}
	}

	@Transactional
	public boolean insertSeekWithListID(String msisdn, int playId, String file,String bytesPlayed, int listid) {
		SessionFactory sessionFactory = null;
		Bookmark bookmark = null;
		try
        {			
			if(msisdn.startsWith("0"))
        		msisdn = msisdn.substring(1,msisdn.length());
        	else if(msisdn.startsWith("91"))
        		msisdn = msisdn.substring(2,msisdn.length());
			
			sessionFactory = sessionFactoryList.getSessionFactory(msisdn); 
			bookmark = getBookmark(msisdn, playId);
	        if(bookmark == null)
	        {
	        	Logger.sysLog(LogValues.info, this.getClass().getName(),"There is no bookmrk found for number "+msisdn+" and playId "+playId);
	        	bookmark = new Bookmark();
	        	bookmark.setMsisdn(msisdn);
		        bookmark.setPromptId(playId);
		        bookmark.setFilePlayed(file);
		        bookmark.setBytesPlayed(bytesPlayed);
		        bookmark.setPlaylistid(listid);
		        sessionFactory.getCurrentSession().save(bookmark);
	        }	
	        else
	        {
	        	Logger.sysLog(LogValues.info, this.getClass().getName(),"Bookmark found for the number "+msisdn+" and playId "+playId+". Going to update bookmark.");
	        	 
	        	bookmark.setFilePlayed(file);
	 	        bookmark.setBytesPlayed(bytesPlayed);
	 	        bookmark.setPlaylistid(listid);
	 	        sessionFactory.getCurrentSession().saveOrUpdate(bookmark);
	        }
	    
	        Logger.sysLog(LogValues.info, this.getClass().getName(),"bookmark added successfully for number "+msisdn+" and playId "+playId+"  and listid "+listid);	        
	        return true;
        }
        catch(Exception e)
        {
        	Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
        	return false;
        }
		finally
		{
			sessionFactory = null;
			bookmark = null;
		}
	}

	
}
