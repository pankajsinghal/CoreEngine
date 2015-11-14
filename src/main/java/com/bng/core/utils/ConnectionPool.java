package com.bng.core.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;

import com.bng.core.exception.coreException;
import com.jolbox.bonecp.BoneCPDataSource;

public class ConnectionPool{

	private static LinkedBlockingQueue<Connection> queueConnection = new LinkedBlockingQueue<Connection>();

	public String getDriverClassName(){
		return this.boneCPDataSource.getDriverClass();
	}
	public int size(){
		return queueConnection.size();
	}
	private BoneCPDataSource boneCPDataSource;

	public void setBoneCPDataSource(BoneCPDataSource boneCPDataSource) {
		this.boneCPDataSource = boneCPDataSource;
	}

	private void loadc3p0() throws Exception{
		Util.sysLog(LogValues.debug, this.getClass().getName(), "INTIALIZING THE CONNECTIONS");
		for(int k=0;k<this.boneCPDataSource.getPartitionCount();k++){
			for(int i = 0 ;i<(this.boneCPDataSource.getMinConnectionsPerPartition()) ; i++){
				try{
					queueConnection.put(this.boneCPDataSource.getConnection());
				}catch(Exception e){Util.sysLog(LogValues.error, this.getClass().getName()," Unable to connect to data source ["+this.boneCPDataSource.getJdbcUrl()+"]");}
			}
		}
	}

	public void init()throws Exception{
		try{
			loadc3p0();
		}catch(Exception e){
			Util.sysLog(3, this.getClass().getName(),"Exception "+coreException.GetStack(e));
		}
		Util.sysLog(4, this.getClass().getName(), "INTIALIZATION CONNECTION DONE QUEUE SIZE"+size());
	}
	
	public Connection getNewConnection(){
		try {
			return this.boneCPDataSource.getConnection();
		} catch (SQLException e) {
			Util.sysLog(3, this.getClass().getName(),"Exception "+coreException.GetStack(e));
			return null;
		}
	}
	
	private static int i=0;
	public Connection getConnection(){
		Connection connection = null;
		try {
			connection = queueConnection.take();
		} catch (InterruptedException e) {
			Logger.sysLog(LogValues.debug, this.getClass().getName(), "exception "+coreException.GetStack(e));
		}
		if(connection == null){
			if(i < 5){
				i++;
				connection = getConnection();
			}
		}
		i=0;
		return connection;
	}
	public void disConnect(Connection connection){
		try {
			queueConnection.put(connection);
		} catch (InterruptedException e) {
			Logger.sysLog(LogValues.debug, this.getClass().getName(), "exception "+coreException.GetStack(e));
		} 
		Util.sysLog(LogValues.debug, this.getClass().getName(), "ADDING THE CONNECTION TO LOCAL QUEUE");
	}

	public void destroy(){
		Util.sysLog(LogValues.debug, this.getClass().getName(),"____________________________________________Shutting down thread_____________________________________");
		try {
			for(int i=0;i<queueConnection.size();i++){
				queueConnection.take().close();
			}
		} catch (Exception e) {
			Util.sysLog(LogValues.debug, this.getClass().getName(),"Exception in destroying datasource");
		}
	}
}