package com.bng.core.queue;

import java.util.concurrent.DelayQueue;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.bng.core.jsonBean.Event;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.Utility;

public class Timer {

	private final static DelayQueue<TimerObject> queue =  new DelayQueue<TimerObject>();
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	private TimerQueueExaminer timerQueueExaminer;
	
	public static TimerObject removedTimerObject;
	
	public Timer(TimerQueueExaminer timerQueueExaminer) {
		this.timerQueueExaminer = timerQueueExaminer;
	}

	public void init() {
		Logger.sysLog(LogValues.info, Timer.class.getName(),"initialize starting.");
		threadPoolTaskExecutor.execute(timerQueueExaminer);
		Logger.sysLog(LogValues.info, Timer.class.getName(),"initialized completed.");
		Logger.sysLog(LogValues.info, Timer.class.getName(),"Active threads : " + threadPoolTaskExecutor.getActiveCount());
	}

	public static String startTimer(Event event, int timeout, String queueName) {
		Logger.sysLog(LogValues.info, Timer.class.getName(), "new event received = "+Utility.convertObjectToJsonStr(event)+", timeout = "+timeout);
		Event eventNew = Utility.convertJsonStrToObject(Utility.convertObjectToJsonStr(event), Event.class);
		TimerObject timerObject = generateTimerObject(eventNew, timeout, queueName);
		Logger.sysLog(LogValues.info, Timer.class.getName(),"registered timer object : "+timerObject.toString());
		String id = startTimer(timerObject);
		Logger.sysLog(LogValues.info, Timer.class.getName(), "new element added in queue: "+queue.toString() +"with id: "+id);
		return id;
	}

	protected static String startTimer(TimerObject timerObject) {
			queue.put(timerObject);
			//Logger.sysLog(LogValues.info, Timer.class.getName(), "seccussfully received. returning id  = "+ timerObject.getId());
			return Long.toString(timerObject.getId());
	}

	private static synchronized TimerObject generateTimerObject(Event event, int timeout,
			String queueName) {
		TimerObject timerObject = new TimerObject();
		timerObject.setId(System.currentTimeMillis());
		timerObject.setEvent(event);
		timerObject.setTimeOut(timeout*1000);
		timerObject.setQueueName(queueName);
		timerObject.setStartTime(timerObject.getId());
		timerObject.setEndTime(timerObject.getStartTime() + timerObject.getTimeOut());
		Logger.sysLog(LogValues.info, Timer.class.getName(), "seccussfully generated id  = "+ timerObject.getId());
		return timerObject;
	}

	protected static TimerObject expiredTimer() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Logger.sysLog(LogValues.error, Timer.class.getName(), com.bng.core.exception.coreException.GetStack(e));
		}
		return null;
	}

	public static int stopTimer(long id) {
		Logger.sysLog(LogValues.info, Timer.class.getName(), "entered stop timer.");
		long endTime = System.currentTimeMillis();
		TimerObject timerObject = new TimerObject();
		timerObject.setId(id);
		Logger.sysLog(LogValues.info, Timer.class.getName(),"current elements in queue: "+queue.toString());
		boolean status = queue.remove(timerObject);
		Logger.sysLog(LogValues.info, Timer.class.getName(),"removing status : "+status);
		if (status){
			Logger.sysLog(LogValues.info, Timer.class.getName(), id+" removed successfully.");
			int interval = (int)((endTime - removedTimerObject.getStartTime())/1000);
			Logger.sysLog(LogValues.info, Timer.class.getName(), "returning interval = " + interval);
			Logger.sysLog(LogValues.info, Timer.class.getName(),"elements after removal in queue: "+queue.toString());
			return interval;
		}
		Logger.sysLog(LogValues.info, Timer.class.getName(), "returning interval = 0.");
		return 0;
	}
	
	public ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
		return threadPoolTaskExecutor;
	}

	public void setThreadPoolTaskExecutor(
			ThreadPoolTaskExecutor threadPoolTaskExecutor) {
		this.threadPoolTaskExecutor = threadPoolTaskExecutor;
	}
}
