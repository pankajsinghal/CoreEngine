package com.bng.core.queue;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.httpclient.NameValuePair;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

public class SendToTelephony {

	private static LinkedBlockingQueue<NameValuePair> messages = new LinkedBlockingQueue<NameValuePair>();
	private int threadCount = 1;
	private SendMessageToTelephonyThread sendMessageToTelephonyThread;

	private ThreadPoolTaskExecutor taskExecutor;

	public SendToTelephony(
			SendMessageToTelephonyThread sendMessageToTelephonyThread) {
		this.sendMessageToTelephonyThread = sendMessageToTelephonyThread;
	}

	public static void addMessageToQueue(String queueName, String message) {
		try {
			NameValuePair nameValuePair = new NameValuePair(queueName, message);
			messages.put(nameValuePair);
			Logger.sysLog(LogValues.info, SendToTelephony.class.getName(),"Event successfuly added to telephony q ="
					+ queueName + " , nameValuePair ="
					+ nameValuePair.toString());
			nameValuePair = null;
		} catch (InterruptedException e) {
			Logger.sysLog(LogValues.error, SendToTelephony.class.getName(), com.bng.core.exception.coreException.GetStack(e));
		}
	}

	public static NameValuePair getMessageForQueue() {
		try {
			return messages.take();
		} catch (InterruptedException e) {
			Logger.sysLog(LogValues.error, SendToTelephony.class.getName(), com.bng.core.exception.coreException.GetStack(e));
		}
		return null;
	}

	public void init() {
		for (int i = 0; i < threadCount; i++) {
			sendMessageToTelephonyThread.setId(i);
			taskExecutor.execute(sendMessageToTelephonyThread);
		}
		Logger.sysLog(LogValues.debug, this.getClass().getName(),"Active threads : " + taskExecutor.getActiveCount());
	}

	public void destroy() {
		Logger.sysLog(LogValues.debug, this.getClass().getName(),"Thread destruction need to be implemented.");
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public ThreadPoolTaskExecutor getTaskExecutor() {
		return taskExecutor;
	}

	public void setTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

}
