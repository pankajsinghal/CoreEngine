package com.bng.core.queue;

import javax.jms.Message;
import javax.jms.MessageListener;

import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

public class DefaultMessageListener implements MessageListener {
	MessageSender sender;

	/**
	 * Implementation of <code>MessageListener</code>.
	 */
	public void onMessage(Message message) {
		try {
			sender.sendMessage(message);
		} catch (Exception e) {
			Logger.sysLog(LogValues.error, this.getClass().getName(),e.getMessage());
		}
	}

	public MessageSender getSender() {
		return sender;
	}

	public void setSender(MessageSender sender) {
		this.sender = sender;
	}

}
