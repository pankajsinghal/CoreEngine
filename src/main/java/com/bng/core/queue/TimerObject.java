package com.bng.core.queue;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.bng.core.jsonBean.Event;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;

public class TimerObject implements Delayed {
	private long id;
	private Event event;
	private long startTime;
	private long timeOut;
	private long endTime;
	private String queueName;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	@Override
	public boolean equals(Object obj) {
		TimerObject timerObject = (TimerObject) obj;
		if (timerObject.getId() == id) {
			Logger.sysLog(LogValues.info, this.getClass().getName(),"removed id: " + id + "\nreturning true");
			Timer.removedTimerObject = timerObject;
			return true;
		} else
			return false;
	}

	public long getDelay(TimeUnit unit) {
		return unit.convert((endTime - System.currentTimeMillis()),
				TimeUnit.MILLISECONDS);

	}

	public int compareTo(Delayed o) {
		return Long.valueOf(endTime).compareTo(((TimerObject) o).getEndTime());
	}

	/*@Override
	public String toString() {
		String s ="id : "+ id + ",event: "+event.toString() +",startTime: "+startTime + ",timeOut: "+timeOut+",endTime: "+endTime +",queueName: "+queueName;

		return s;
	}*/
	
}