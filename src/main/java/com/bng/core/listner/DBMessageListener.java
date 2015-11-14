package com.bng.core.listner;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.bng.core.exception.coreException;
import com.bng.core.utils.ConnectionPool;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

public class DBMessageListener implements MessageListener {

	private ConnectionPool connectionPool;

	public void setConnectionPool(ConnectionPool connectionPool) {
		this.connectionPool = connectionPool;
	}

	@SuppressWarnings("resource")
	@Override
	public void onMessage(Message message) {
		Connection conn = null;
		Statement stmt = null;
		try
		{
			String query = ((TextMessage) message).getText();
			Logger.sysLog(LogValues.debug, this.getClass().getName(), "Query = "+query);
			conn = this.connectionPool.getConnection();
			int status = -1;
			try{
				stmt = conn.createStatement();
				status = stmt.executeUpdate(query);
			}catch(CommunicationsException e){
				try{conn.close();}catch(Exception e1){}
				conn = this.connectionPool.getNewConnection();
				stmt = conn.createStatement();
				status = stmt.executeUpdate(query);
			}
			this.connectionPool.disConnect(conn);
			conn = null;
			if(status > 0 )
				Logger.sysLog(LogValues.info, this.getClass().getName(), "Updated query executed successfully. status ["+status+"]");
			else{
				Logger.sysLog(LogValues.info, this.getClass().getName(), "Doesn't found any record to update. status ["+status+"]");
				//SendToTelephony.addMessageToQueue(((Queue)message.getJMSDestination()).getQueueName(), query);
			}
		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, this.getClass().getName(), "Caught exception while updating MakeCall status.");
			Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
		}
		finally	{
			Logger.sysLog(LogValues.debug, this.getClass().getName(), "Going to close active connection of DB.");
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se)
			{
			}
			try{
				if(conn!=null){
					this.connectionPool.disConnect(conn);
					conn = null;
				}
			}catch(Exception e){}
		}
	}

}
