package com.bng.core.queue;

import java.util.ArrayList;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.bng.core.cdr.CdrMessageListener;
import com.bng.core.parser.Engine;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

public class QueueConnection {
	private PooledConnectionFactory pooledConnectionFactory;
	private Connection connection;
	private Session session;
	private DefaultMessageListenerContainer defaultMessageListenerContainer;
	private CdrMessageListener cdrMessageListener;
//	private ArrayList<DefaultMessageListenerContainer> defaultMessageListenerContainersMiddleQueue = new ArrayList<DefaultMessageListenerContainer>();
	private ArrayList<DefaultMessageListenerContainer> defaultMessageListenerContainersMsisdnQueue = new ArrayList<DefaultMessageListenerContainer>();
	private ArrayList<DefaultMessageListenerContainer> defaultMessageListenerContainersCdrQueue = new ArrayList<DefaultMessageListenerContainer>();
	private ObjectFactory<DefaultMessageListenerContainer> defaultMessageListenerContainerFactory;
	private ObjectFactory<Engine> engineFactory;
//	private ObjectFactory<OriginalToMiddleSender> originalToMiddleSenderFactory;
	private ObjectFactory<MiddleToCoreSender> middleToCoreSenderFactory;

//	protected static ArrayList<String> middleQueueList = new ArrayList<String>();
	protected static ArrayList<String> msisdnQueueList = new ArrayList<String>();
	protected static ArrayList<String> telephonyQueueList = new ArrayList<String>();
	protected static ArrayList<String> cdrQueueList = new ArrayList<String>();

//	private int middleQueueInitialLength;
	private int msisdnQueueInitialLength;
	private int telephonyQueueInitialLength;
	private int cdrQueueInitialLength;

	private String originalQueueNamePrefixString;
//	private String middleQueueNamePrefixString;
	private String msisdnQueueNamePrefixString;
	private String telephonyQueueNamePrefixString;
	private String cdrQueueNamePrefixString;

	public void init() {
		try {
			connection = pooledConnectionFactory.createConnection();
			session = connection.createSession(false,
					Session.DUPS_OK_ACKNOWLEDGE);

			updateDefaultQueue();
			updateDefaultConsumer();
		} catch (JMSException e) {
			Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
		} catch (Exception e) {
			Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
		}
	}

	public void destroy(){
		
//		defaultMessageListenerContainer.destroy();
//		for(DefaultMessageListenerContainer defaultMessageListenerContainer : defaultMessageListenerContainersMiddleQueue)
//			defaultMessageListenerContainer.destroy();
		for(DefaultMessageListenerContainer defaultMessageListenerContainer : defaultMessageListenerContainersMsisdnQueue)
			defaultMessageListenerContainer.destroy();
		for(DefaultMessageListenerContainer defaultMessageListenerContainer : defaultMessageListenerContainersCdrQueue)
			defaultMessageListenerContainer.destroy();
		try {
			session.close();
		} catch (JMSException e) {
			Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
		}
		try {
			connection.close();
		} catch (JMSException e) {
			Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
		}
		pooledConnectionFactory.clear();
	}
	
	private void updateDefaultQueue() {
//		for (int i = 0; i < middleQueueInitialLength; i++) {
//			middleQueueList.add(middleQueueNamePrefixString + i);
//		}
		for (int i = 0; i < msisdnQueueInitialLength; i++) {
			msisdnQueueList.add(msisdnQueueNamePrefixString + i);
		}
		for (int i = 0; i < telephonyQueueInitialLength; i++) {
			telephonyQueueList.add(telephonyQueueNamePrefixString + i);
		}
		for (int i = 0; i < cdrQueueInitialLength; i++) {
			cdrQueueList.add(cdrQueueNamePrefixString + i);
		}
	}

	private void updateDefaultConsumer() throws JMSException {

		// for original queue
		defaultMessageListenerContainer = defaultMessageListenerContainerFactory
				.getObject();
		defaultMessageListenerContainer.setDestination(session
				.createQueue(originalQueueNamePrefixString));
		DefaultMessageListener messageListener = (DefaultMessageListener) defaultMessageListenerContainer
				.getMessageListener();
//		OriginalToMiddleSender originalToMiddleSender = originalToMiddleSenderFactory
//				.getObject();
		MiddleToCoreSender middleToCoreSender = middleToCoreSenderFactory
				.getObject();
		messageListener.setSender(middleToCoreSender);
		defaultMessageListenerContainer.setMessageListener(messageListener);
		defaultMessageListenerContainer.start();

		// for middle queues
//		for (int i = 0; i < middleQueueInitialLength; i++) {
//			defaultMessageListenerContainer = defaultMessageListenerContainerFactory
//					.getObject();
//			defaultMessageListenerContainer.setDestination(session
//					.createQueue(middleQueueList.get(i)));
//			messageListener = (DefaultMessageListener) defaultMessageListenerContainer
//					.getMessageListener();
//			MiddleToCoreSender middleToCoreSender = middleToCoreSenderFactory
//					.getObject();
//			messageListener.setSender(middleToCoreSender);
//			defaultMessageListenerContainer.setMessageListener(messageListener);
//			defaultMessageListenerContainer.start();
//			defaultMessageListenerContainersMiddleQueue
//					.add(defaultMessageListenerContainer);
//
//		}
		// for msisdn queues
		for (int i = 0; i < msisdnQueueInitialLength; i++) {
			defaultMessageListenerContainer = defaultMessageListenerContainerFactory
					.getObject();
			defaultMessageListenerContainer.setDestination(session
					.createQueue(msisdnQueueList.get(i)));
			Engine engine = engineFactory.getObject();
			defaultMessageListenerContainer.setMessageListener(engine);
			defaultMessageListenerContainer.start();
			defaultMessageListenerContainersMsisdnQueue
					.add(defaultMessageListenerContainer);
		}
		// for cdr queues (default queue size is 1)
		for (int i = 0; i < cdrQueueInitialLength; i++) {
			defaultMessageListenerContainer = defaultMessageListenerContainerFactory
					.getObject();
			defaultMessageListenerContainer.setDestination(session
					.createQueue(cdrQueueList.get(i)));
			defaultMessageListenerContainer
					.setMessageListener(cdrMessageListener);
			defaultMessageListenerContainer.start();
			defaultMessageListenerContainersCdrQueue
					.add(defaultMessageListenerContainer);
		}

	}

	/**
	 * level: 1 = middle queues level: 2 = msisdn queues
	 */
	public void addQueue(int level) {
		try {
			switch (level) {
			case 1:

//				addMiddleQueue();
				break;
			case 2:
				addMsisdnQueue();
				break;
			default:
				break;
			}

		} catch (JMSException e) {
			Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
		}
	}

//	private void addMiddleQueue() throws JMSException {
//		int newindex = middleQueueList.size();
//		Logger.sysLog(LogValues.info, this.getClass().getName(),"newindex : " + newindex);
//		middleQueueList.add(middleQueueNamePrefixString + newindex);
//
//		Logger.sysLog(LogValues.info, this.getClass().getName(),"middleQueueList.get(newindex) : "
//				+ middleQueueList.get(newindex));
//
//		defaultMessageListenerContainer = defaultMessageListenerContainerFactory
//				.getObject();
//		defaultMessageListenerContainer.setDestination(session
//				.createQueue(middleQueueList.get(newindex)));
//		DefaultMessageListener messageListener = (DefaultMessageListener) defaultMessageListenerContainer
//				.getMessageListener();
//		MiddleToCoreSender middleToCoreSender = middleToCoreSenderFactory
//				.getObject();
//		messageListener.setSender(middleToCoreSender);
//		defaultMessageListenerContainer.setMessageListener(messageListener);
//		defaultMessageListenerContainer.start();
//		defaultMessageListenerContainersMiddleQueue
//				.add(defaultMessageListenerContainer);
//
//	}

	private void addMsisdnQueue() throws JMSException {
		for (int i = 0; i < msisdnQueueList.size(); i++) {
			Logger.sysLog(LogValues.info, this.getClass().getName(),"again print : " + msisdnQueueList.get(i));

		}
		int newindex = msisdnQueueList.size();
		msisdnQueueList.add(msisdnQueueNamePrefixString + newindex);

		defaultMessageListenerContainer = defaultMessageListenerContainerFactory
				.getObject();
		defaultMessageListenerContainer.setDestination(session
				.createQueue(msisdnQueueList.get(newindex)));
		Engine engine = engineFactory.getObject();
		defaultMessageListenerContainer.setMessageListener(engine);
		defaultMessageListenerContainer.start();
		defaultMessageListenerContainersMsisdnQueue
				.add(defaultMessageListenerContainer);

	}

//	public void addConsumers(String queueName, int count) {
//		int index = middleQueueList.indexOf(new String(queueName));
//		defaultMessageListenerContainer = defaultMessageListenerContainersMiddleQueue
//				.get(index);
//		defaultMessageListenerContainer.setConcurrentConsumers(count);
//	}

	public PooledConnectionFactory getPooledConnectionFactory() {
		return pooledConnectionFactory;
	}

	public void setPooledConnectionFactory(
			PooledConnectionFactory pooledConnectionFactory) {
		this.pooledConnectionFactory = pooledConnectionFactory;
	}

	public ObjectFactory<DefaultMessageListenerContainer> getDefaultMessageListenerContainerFactory() {
		return defaultMessageListenerContainerFactory;
	}

	public void setDefaultMessageListenerContainerFactory(
			ObjectFactory<DefaultMessageListenerContainer> defaultMessageListenerContainerFactory) {
		this.defaultMessageListenerContainerFactory = defaultMessageListenerContainerFactory;
	}

//	public ObjectFactory<OriginalToMiddleSender> getOriginalToMiddleSenderFactory() {
//		return originalToMiddleSenderFactory;
//	}
//
//	public void setOriginalToMiddleSenderFactory(
//			ObjectFactory<OriginalToMiddleSender> originalToMiddleSenderFactory) {
//		this.originalToMiddleSenderFactory = originalToMiddleSenderFactory;
//	}

	public ObjectFactory<MiddleToCoreSender> getMiddleToCoreSenderFactory() {
		return middleToCoreSenderFactory;
	}

	public void setMiddleToCoreSenderFactory(
			ObjectFactory<MiddleToCoreSender> middleToCoreSenderFactory) {
		this.middleToCoreSenderFactory = middleToCoreSenderFactory;
	}

	public ObjectFactory<Engine> getEngineFactory() {
		return engineFactory;
	}

	public void setEngineFactory(ObjectFactory<Engine> engineFactory) {
		this.engineFactory = engineFactory;
	}

//	public int getMiddleQueueInitialLength() {
//		return middleQueueInitialLength;
//	}
//
//	public void setMiddleQueueInitialLength(int middleQueueInitialLength) {
//		this.middleQueueInitialLength = middleQueueInitialLength;
//	}

	public int getMsisdnQueueInitialLength() {
		return msisdnQueueInitialLength;
	}

	public void setMsisdnQueueInitialLength(int msisdnQueueInitialLength) {
		this.msisdnQueueInitialLength = msisdnQueueInitialLength;
	}

	public int getTelephonyQueueInitialLength() {
		return telephonyQueueInitialLength;
	}

	public void setTelephonyQueueInitialLength(int telephonyQueueInitialLength) {
		this.telephonyQueueInitialLength = telephonyQueueInitialLength;
	}

	public int getCdrQueueInitialLength() {
		return cdrQueueInitialLength;
	}

	public void setCdrQueueInitialLength(int cdrQueueInitialLength) {
		this.cdrQueueInitialLength = cdrQueueInitialLength;
	}

	public String getOriginalQueueNamePrefixString() {
		return originalQueueNamePrefixString;
	}

	public void setOriginalQueueNamePrefixString(
			String originalQueueNamePrefixString) {
		this.originalQueueNamePrefixString = originalQueueNamePrefixString;
	}

//	public String getMiddleQueueNamePrefixString() {
//		return middleQueueNamePrefixString;
//	}
//
//	public void setMiddleQueueNamePrefixString(String middleQueueNamePrefixString) {
//		this.middleQueueNamePrefixString = middleQueueNamePrefixString;
//	}

	public String getMsisdnQueueNamePrefixString() {
		return msisdnQueueNamePrefixString;
	}

	public void setMsisdnQueueNamePrefixString(String msisdnQueueNamePrefixString) {
		this.msisdnQueueNamePrefixString = msisdnQueueNamePrefixString;
	}

	public String getTelephonyQueueNamePrefixString() {
		return telephonyQueueNamePrefixString;
	}

	public void setTelephonyQueueNamePrefixString(
			String telephonyQueueNamePrefixString) {
		this.telephonyQueueNamePrefixString = telephonyQueueNamePrefixString;
	}
	
	public String getCdrQueueNamePrefixString() {
		return cdrQueueNamePrefixString;
	}

	public void setCdrQueueNamePrefixString(String cdrQueueNamePrefixString) {
		this.cdrQueueNamePrefixString = cdrQueueNamePrefixString;
	}
	
	public static ArrayList<String> getCdrQueueList(){
		return cdrQueueList;
	}

	public CdrMessageListener getCdrMessageListener() {
		return cdrMessageListener;
	}

	public void setCdrMessageListener(CdrMessageListener cdrMessageListener) {
		this.cdrMessageListener = cdrMessageListener;
	}


	/*
	 * public ObjectFactory<Engine> getCoreMessageListenerFactory() { return
	 * coreMessageListenerFactory; }
	 * 
	 * public void setCoreMessageListenerFactory( ObjectFactory<Engine>
	 * coreMessageListenerFactory) { this.coreMessageListenerFactory =
	 * coreMessageListenerFactory; }
	 */

}
