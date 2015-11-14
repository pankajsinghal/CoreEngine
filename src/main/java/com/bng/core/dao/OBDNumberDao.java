package com.bng.core.dao;

import java.sql.ResultSet;
import java.util.Date;

public interface OBDNumberDao {

	boolean insertnumber(String aparty, String bparty, Date endtime, Date calltime, String file,String tablename);
	ResultSet getfiles(String aparty, String bparty, Date date,String tablename);
	ResultSet getrecordlist(String bparty,String tablename);
	public ResultSet getAandBpartylist(String aparty, String bparty, String tablename );
	boolean reschedule(String aparty,String bparty,Date calltime,String tablename);
	boolean deleterecord(String filename,String tablename);
}
