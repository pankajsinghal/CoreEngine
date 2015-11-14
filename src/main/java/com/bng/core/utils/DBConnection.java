package com.bng.core.utils;

import com.bng.core.queue.SendToTelephony;


public class DBConnection {

	private static String dbQueueName = "dbdirectQuery";
//	private JmsTemplate jmsTemplate;
//
//	public void setJmsTemplate(JmsTemplate jmsTemplate) {
//		this.jmsTemplate = jmsTemplate;
//	}
//
//	private static MessageCreator getMessageCreator(final String message) {
//		return new MessageCreator() {
//			public Message createMessage(Session session) throws JMSException {
//				return session.createTextMessage(message);
//			}
//		};
//	}
//
//	private static JmsTemplate jmsTemplate2 = null;
//
//	public void init(){
//		this.jmsTemplate2 = this.jmsTemplate;
//	}

	public static void insertObdStatus(String query){
		//jmsTemplate2.send(dbQueueName,DBConnection.getMessageCreator(query));
		SendToTelephony.addMessageToQueue(DBConnection.dbQueueName, query);
		/*Connection conn = null;
		Statement stmt = null;
		try
		{
			Logger.sysLog(LogValues.info, DBConnection.class.getName(), "Query = "+query);
			conn = this.connectionPool.getConnection();
			stmt = conn.createStatement();
			int status = stmt.executeUpdate(query);
			if(status > 0 )
				Logger.sysLog(LogValues.info, DBConnection.class.getName(), "Updated query executed successfully.");
			else
				Logger.sysLog(LogValues.info, DBConnection.class.getName(), "Doesn't found any record to update.");
		}
		catch(Exception e)
		{
			Logger.sysLog(LogValues.error, DBConnection.class.getName(), "Caught exception while updating MakeCall status.");
			Logger.sysLog(LogValues.error, DBConnection.class.getName(), coreException.GetStack(e));
		}
		finally	{
			Logger.sysLog(LogValues.debug, DBConnection.class.getName(), "Going to close active connection of DB.");
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se)
			{
			}
			if(conn!=null){
				this.connectionPool.disConnect(conn);
				conn = null;
			}
		}*/
	}
}
