package com.bng.core.queue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.bng.core.jsonBean.Event;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.Utility;


public class TimerQueueExaminer  implements Runnable{
	private JmsTemplate jmsTemplate;

	public void run() {
		TimerObject timerObject;
		while(true) {
			timerObject = Timer.expiredTimer();
			send(timerObject);
		}
	}

	private void send(TimerObject timerObject) {
		//Logger.sysLog(LogValues.info, TimerQueueExaminer.class.getName(), "message sending started to qid "+timerObject.getId()+"q name = "+timerObject.getQueueName());
		jmsTemplate.send(timerObject.getQueueName(), getMessageCreator(timerObject.getEvent()));
		Logger.sysLog(LogValues.info, TimerQueueExaminer.class.getName(), "message sending done.");
	}

	private MessageCreator getMessageCreator(final Event event) 
	{
		return new MessageCreator() 
		{
			public Message createMessage(Session session) throws JMSException {

				String message = Utility.convertObjectToJsonStr(event);
				Logger.sysLog(LogValues.info, TimerQueueExaminer.class.getName(), "about to add mesage = "+message);
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