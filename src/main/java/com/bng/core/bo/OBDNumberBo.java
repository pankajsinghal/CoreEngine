package com.bng.core.bo;


import java.sql.ResultSet;
import java.util.Date;

public interface OBDNumberBo {
	
	public boolean insertnumber(String aparty, String bparty,Date endtime,Date calltime,String File,String tablename);
	public ResultSet getfilelist(String aparty, String bparty, Date date ,String tablename);
	public ResultSet getrecordlist(String bparty,String tablename);
	public ResultSet getAandBpartylist(String aparty, String bparty, String tablename );
	public boolean reschedule(String aparty, String bparty,Date calltime,String tablename);
	public boolean deleterecord(String filename,String tablename);
}
