package com.bng.core.queue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

/**
 * 10: * The TestMessageSender class uses the injected JMSTemplate to send a
 * message 11: * to a specified Queue. In our case we're sending messages to
 * 'TestQueueTwo' 12:
 */
public class OriginalToMiddleSender implements MessageSender {

	private JmsTemplate jmsTemplate;
	private int queue = 0;

	/**
	 * 21: * Sends message using JMS Template. 22: * 23:
	 * 
	 * @param message_p
	 *            the message_p 24:
	 */
	public void sendMessage(Message m) {
//		String message = null;
//		if (m instanceof TextMessage) {
//			TextMessage tm = (TextMessage) m;
//			try {
//				message = tm.getText();
//				queue = (int) (Math.random() * QueueConnection.middleQueueList
//						.size());
//				Logger.sysLog(LogValues.info, this.getClass().getName(),"\n[" + this.getClass().getName() + "] - Message["
//						+ message + "]");
//				jmsTemplate.send(QueueConnection.middleQueueList.get(queue),
//						getMessageCreator(message));
//
//			} catch (JMSException e) {
//				// TODO Auto-generated catch block
//				Logger.sysLog(LogValues.error, this.getClass().getName(),"exception : " + this.getClass().getName());
//				Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
//			}
//		}
	}

	private MessageCreator getMessageCreator(final String message) {
		return new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(message);
			}
		};
	}

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

}