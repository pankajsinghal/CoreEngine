package com.bng.core.queue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.httpclient.NameValuePair;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

public class SendMessageToTelephonyThread implements Runnable {

	private int id;
	private JmsTemplate jmsTemplate;
	

	public void run() {
		while (true) {
			Logger.sysLog(LogValues.debug, this.getClass().getName(),"Thread [" + Thread.currentThread().getId()
					+ "] waiting for execution.");
			NameValuePair message = SendToTelephony.getMessageForQueue();
			if (message != null)
				sendMessage(message);
		}

	}

	public void sendMessage(NameValuePair message) {

		int queue = (int) (Math.random() * QueueConnection.telephonyQueueList
				.size());
		Logger.sysLog(LogValues.debug, this.getClass().getName(),"Thread[" + id + "] Queue["
				+ QueueConnection.telephonyQueueList.get(queue) + "] Message["
				+ message.toString() + "]");
		if (message.getName().isEmpty())
			jmsTemplate.send(QueueConnection.telephonyQueueList.get(queue),
					getMessageCreator(message.getValue()));
		else
			jmsTemplate.send(message.getName(),
					getMessageCreator(message.getValue()));

		Logger.sysLog(LogValues.info, this.getClass().getName(),"Sent message: Thread[" + id + "] Queue["
				+ QueueConnection.telephonyQueueList.get(queue) + "] Message["
				+ message.toString() + "]");
	}

	private MessageCreator getMessageCreator(final String message) {
		return new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(message);
			}
		};
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
}
