package com.bng.core.parser;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.bng.core.exception.coreException;
import com.bng.core.jsonBean.ServiceInfo;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.Utility;


public class XmlParserMessageListener implements MessageListener {

	private Controller controller;
	
	/**
	 * Implementation of <code>MessageListener</code>.
	 */
	public void onMessage(Message message) {
		String textMessage = null;
		Logger.sysLog(LogValues.info, this.getClass().getName(),"xml parser message received");
		if (message instanceof TextMessage)
			try {
				textMessage = ((TextMessage) message).getText();
			} catch (JMSException e) {
				Logger.sysLog(LogValues.error, this.getClass().getName(), coreException.GetStack(e));
			}

		if (textMessage == null)
			Logger.sysLog(LogValues.info, this.getClass().getName(),"textmessage null");
		else 
		{
			if(textMessage.contains("\"classname\":\"serviceInfo\""))
			{	
				ServiceInfo serviceInfo = Utility.convertJsonStrToObject(textMessage, ServiceInfo.class);	
				Logger.sysLog(LogValues.info,this.getClass().getName(),"json = "+textMessage);
				Utility.modifyServiceMap(serviceInfo);	
				
			}
			else
				controller.Unmarshaller(textMessage);
		}
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

}
