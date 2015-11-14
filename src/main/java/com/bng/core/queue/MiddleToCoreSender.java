package com.bng.core.queue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.bng.core.jsonBean.Event;
import com.bng.core.utils.CoreEnums;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * 10: * The TestMessageSender class uses the injected JMSTemplate to send a
 * message 11: * to a specified Queue. In our case we're sending messages to
 * 'TestQueueTwo' 12:
 */
public class MiddleToCoreSender implements MessageSender {

	private JmsTemplate jmsTemplate;
	private int queue = 0;

	/**
	 * 21: * Sends message using JMS Template. 22: * 23:
	 * 
	 * @param message_p
	 *            the message_p 24:
	 */
	public void sendMessage(Message m) {
		String message = null;
		if (m instanceof TextMessage) {
			TextMessage tm = (TextMessage) m;
			try {
				message = tm.getText();
				Gson gson = new Gson();
				Logger.sysLog(LogValues.debug, this.getClass().getName(), "message recevied "+message);
				Event eventInfo = gson.fromJson(message, Event.class);
				String msisdn;
				if((eventInfo.getCallType() == CoreEnums.callType.Incoming.ordinal()) || (eventInfo.getCallType() == CoreEnums.callType.Outgoing.ordinal()))
					msisdn = (eventInfo.getCallType()==0)?eventInfo.getaPartyMsisdn():eventInfo.getbPartyMsisdn();
				else
					msisdn = (eventInfo.getaPartyMsisdn() != null)?eventInfo.getaPartyMsisdn():eventInfo.getbPartyMsisdn();
				int num = Integer.parseInt(msisdn.substring(
						msisdn.length() - 4, msisdn.length()));
				queue = num % QueueConnection.msisdnQueueList.size();
				jmsTemplate.send(QueueConnection.msisdnQueueList.get(queue),getMessageCreator(message));
				
			} catch (JMSException e) {
				Logger.sysLog(LogValues.error, this.getClass().getName()," Message["+ message + "] JMSException \n"+ com.bng.core.exception.coreException.GetStack(e));
			} catch (JsonSyntaxException e) {
				Logger.sysLog(LogValues.error, this.getClass().getName()," Message["+ message + "] JsonSyntaxException \n"+  com.bng.core.exception.coreException.GetStack(e));
			}catch(Exception e){
				Logger.sysLog(LogValues.error, this.getClass().getName()," Message["+ message + "] Exception \n"+  com.bng.core.exception.coreException.GetStack(e));
			}
		}

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