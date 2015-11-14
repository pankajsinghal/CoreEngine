package com.bng.core.daoImpl;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Repository;

import com.bng.core.dao.OBDNumberDao;
import com.bng.core.exception.coreException;
import com.bng.core.utils.ConnectionPool;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;


@Repository("OBDNumberDao") 
public class OBDNumberDaoImpl implements OBDNumberDao {

	 SessionFactoryList sessionFactoryList;
	private ConnectionPool connectionPool;
	
	
	public void setConnectionPool(ConnectionPool connectionPool) {
		this.connectionPool = connectionPool;
	}

	public boolean insertnumber(String aparty, String bparty, Date endtime, Date calltime, String file,String tablename)
	 {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 Connection connection = null;
		 String status= "scheduled";
		 PreparedStatement ps = null;
		 boolean flag = false;
		 Logger.sysLog(LogValues.info, this.getClass().getName(),"Inside insertnumber : with aparty :"+aparty+" , bparty:"+bparty);
		 try{
			 connection = this.connectionPool.getConnection();
			 file=file.replace("\\", "\\\\");
			 String SQL = "INSERT INTO "+tablename+" (cli,msisdn,endtime,calltime,status,message) values (\""+aparty+"\",\""+bparty+"\",\""+sdf.format(endtime)+"\",\""+sdf.format(calltime)+"\",\""+status+"\",\""+file+"\")";
			 ps = connection.prepareStatement(SQL);
			 Logger.sysLog(LogValues.info, this.getClass().getName(),"tablename: "+tablename+ " query: "+SQL);
			 int rows = ps.executeUpdate();
			 Logger.sysLog(LogValues.info, this.getClass().getName(),"rows affected ="+rows);
			 flag = true;
			 
		 }
		 catch(Exception e)
		 {
			 Logger.sysLog(LogValues.error, this.getClass().getName(),coreException.GetStack(e));
		 }
		 finally
		 {
			 if(connection != null){
					this.connectionPool.disConnect(connection);
					connection = null;
				}
		 }
		return flag;
	 }
	
	 public ResultSet getfiles(String aparty, String bparty, Date date , String tablename)
	 {
		 Connection connection = null;
			PreparedStatement ps = null;
			ResultSet resultSet = null;
			String status= "to_core_engine";
			//String status1 = "success";
			Logger.sysLog(LogValues.info, this.getClass().getName(),"Inside getfiles : with bparty:"+bparty + " for table :"+tablename);
			try {
				connection = this.connectionPool.getConnection();
				String SQL = "Select * from "+tablename+" where msisdn =\""+bparty+"\" and cli =\""+aparty+"\"";
				
				ps = connection.prepareStatement(SQL);
				Logger.sysLog(LogValues.info, this.getClass().getName(),"tablename: "+tablename+ " query: "+SQL);
				resultSet = ps.executeQuery();
				
				//int rows = ps.executeUpdate();
			//	 Logger.sysLog(LogValues.info, this.getClass().getName(),"rows affected ="+rows);
				//String SQL1 = "Update "+tablename+"  set status =\""+status1+"\"  where msisdn =\""+bparty+"\" and cli =\""+aparty+"\" and  status = \""+status+"\"";
				//ps = connection.prepareStatement(SQL1);
				//rows = ps.executeUpdate();
				// Logger.sysLog(LogValues.info, this.getClass().getName(),"rows affected ="+rows);
				
				Logger.sysLog(LogValues.info, this.getClass().getName(),"resultset ="+resultSet);
				
				
			}
			catch(Exception e)
			{
				Logger.sysLog(LogValues.error, this.getClass().getName(),coreException.GetStack(e));
			 
			}
			finally
			{
				if(connection != null){
					this.connectionPool.disConnect(connection);
					connection = null;
				}
			}
			return resultSet;
	 }

	 public ResultSet getrecordlist(String bparty, String tablename)
	 {
		 Connection connection = null;
			PreparedStatement ps = null;
			ResultSet resultSet = null;
			String status= "scheduled";
			Logger.sysLog(LogValues.info, this.getClass().getName(),"Inside getrecordlist : with bparty:"+bparty);
			try {
				connection = this.connectionPool.getConnection();
				String SQL = "Select * from "+tablename+" where msisdn =\""+bparty+"\" and status = \""+status+"\" order by calltime";
				
				ps = connection.prepareStatement(SQL);
				Logger.sysLog(LogValues.info, this.getClass().getName(),"tablename: "+tablename+ " query: "+SQL);
				resultSet = ps.executeQuery();
				Logger.sysLog(LogValues.info, this.getClass().getName(),"resultset ="+resultSet);
				
				
			}
			catch(Exception e)
			{
				Logger.sysLog(LogValues.error, this.getClass().getName(),coreException.GetStack(e));
			 
			}
			finally
			{
				if(connection != null){
					this.connectionPool.disConnect(connection);
					connection = null;
				}
			}
			return resultSet;
	 }

	@Override
	public boolean reschedule(String aparty, String bparty, Date calltime,String tablename)
	{
		Connection connection = null;
		PreparedStatement ps = null;
		//ResultSet resultSet = null;
		String status= "scheduled";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		boolean flag = false;
		//String status1="to_core_engine";
		Logger.sysLog(LogValues.info, this.getClass().getName(),"Inside reschedule : with aparty :"+aparty+" , bparty:"+bparty);
		try {
			connection = this.connectionPool.getConnection();
			String SQL = "update "+tablename+" set status =\""+status+"\" ,calltime = \""+sdf.format(calltime)+"\"  where msisdn =\""+bparty+"\" and cli =\""+aparty+"\"";
			
			ps = connection.prepareStatement(SQL);
			Logger.sysLog(LogValues.info, this.getClass().getName(),"tablename: "+tablename+ " query: "+SQL);
			int rows = ps.executeUpdate();
			Logger.sysLog(LogValues.info, this.getClass().getName(),"rows affected ="+rows);
			flag = true;
		
			
		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, this.getClass().getName(),coreException.GetStack(e));
		 
		}
		finally
		{
			if(connection != null){
				this.connectionPool.disConnect(connection);
				connection = null;
			}
		}
		return flag;
	}

	@Override
	public boolean deleterecord(String filename,String tablename) {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		String status= "scheduled";
		boolean flag = false;
		Logger.sysLog(LogValues.info, this.getClass().getName(),"Inside deleterecord : with filename:"+filename);
		try {
			connection = this.connectionPool.getConnection();
			filename=filename.replace("\\", "\\\\");
			String SQL ="delete  from "+tablename+" where message =\""+filename+"\"";
			 
			ps = connection.prepareStatement(SQL);
			Logger.sysLog(LogValues.info, this.getClass().getName(),"tablename: "+tablename+ " query: "+SQL);
			int rows = ps.executeUpdate();
			Logger.sysLog(LogValues.info, this.getClass().getName(),"rows affected ="+rows);
			flag = true;	
		
		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, this.getClass().getName(),coreException.GetStack(e));
		 
		}
		finally
		{
			if(connection != null){
				this.connectionPool.disConnect(connection);
				connection = null;
			}
		}
		return flag;
	}

	@Override
	public ResultSet getAandBpartylist(String aparty, String bparty,
			String tablename) {

		 Connection connection = null;
			PreparedStatement ps = null;
			ResultSet resultSet = null;
			String status= "scheduled";
			Logger.sysLog(LogValues.info, this.getClass().getName(),"Inside getAandBpartylist : with bparty:"+bparty);
			try {
				connection = this.connectionPool.getConnection();
				String SQL = "Select * from "+tablename+" where msisdn =\""+bparty+"\" and cli = \""+aparty+"\" order by calltime";
				
				ps = connection.prepareStatement(SQL);
				Logger.sysLog(LogValues.info, this.getClass().getName(),"tablename: "+tablename+ " query: "+SQL);
				resultSet = ps.executeQuery();
				Logger.sysLog(LogValues.info, this.getClass().getName(),"resultset ="+resultSet);
			
				
			}
			catch(Exception e)
			{
				Logger.sysLog(LogValues.error, this.getClass().getName(),coreException.GetStack(e));
			 
			}
			finally
			{
				if(connection != null){
					this.connectionPool.disConnect(connection);
					connection = null;
				}
			}
			return resultSet;
	 
	}
	
	
	 
}
