package com.bng.core.queue.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bng.core.queue.QueueConnection;

@Controller
public class QueueManager {

	@Autowired
	private QueueConnection QueueConnection;

/*	public void setQueueConnection(QueueConnection queueConnection) {
		this.queueConnection = queueConnection;
	}*/

//	@RequestMapping(value="/queue/{queueName}/consumer/{count}", method = RequestMethod.GET)
//	public String addConsumer(@PathVariable("queueName") String queueName, @PathVariable("count") int count) {
//		this.QueueConnection.addConsumers(queueName, count);
//		return "ok";
//	}

	
	@RequestMapping(value="/queue/level/{queueLevel}",method = RequestMethod.GET)
	public String addQueue(@PathVariable("queueLevel") int queueLevel) {
		this.QueueConnection.addQueue(queueLevel);
		return "ok";
	}
}
