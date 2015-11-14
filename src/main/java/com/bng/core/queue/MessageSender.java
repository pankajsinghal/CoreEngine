package com.bng.core.queue;

import javax.jms.Message;

public interface MessageSender {
	public void sendMessage(Message message);
}
