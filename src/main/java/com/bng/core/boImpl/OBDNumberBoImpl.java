package com.bng.core.boImpl;

import java.sql.ResultSet;
import java.util.Date;

import com.bng.core.bo.OBDNumberBo;
import com.bng.core.dao.OBDNumberDao;

public class OBDNumberBoImpl implements OBDNumberBo{
	private OBDNumberDao obdnumberdao;
	
	public void setObdnumberdao(OBDNumberDao obdnumberdao) {
		this.obdnumberdao = obdnumberdao;
	}
	
	
	public boolean insertnumber(String aparty, String bparty,Date endtime,Date calltime,String File,String tablename)
	{
		return (obdnumberdao.insertnumber(aparty,bparty,endtime,calltime,File,tablename));
	}
	
	public ResultSet getfilelist(String aparty, String bparty, Date date , String tablename)
	{
		return (obdnumberdao.getfiles(aparty,bparty,date,tablename));
	}

	public ResultSet getrecordlist(String bparty, String tablename) 
	{
		return  (obdnumberdao.getrecordlist(bparty,tablename));
	}

	public boolean reschedule(String aparty, String bparty, Date calltime,
			String tablename) 
	{
		return (obdnumberdao.reschedule(aparty,bparty,calltime,tablename));
	}

	public boolean deleterecord(String filename,String tablename) {
		
		return  (obdnumberdao.deleterecord(filename,tablename));
	}

	public ResultSet getAandBpartylist(String aparty, String bparty,String tablename) 
	{
		return (obdnumberdao.getAandBpartylist(aparty, bparty, tablename));
	}
	
	
}
