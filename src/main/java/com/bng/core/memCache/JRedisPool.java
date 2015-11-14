package com.bng.core.memCache;

import java.util.concurrent.LinkedBlockingQueue;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.bng.core.exception.coreException;
import com.bng.core.utils.LogValues;
import com.bng.core.utils.Logger;
import com.bng.core.utils.Util;

public class JRedisPool {
	private JedisPool jedisPool;
	private JedisPoolConfig jedisPoolConfig;

	public void setJedisPoolConfig(JedisPoolConfig jedisPoolConfig) {
		this.jedisPoolConfig = jedisPoolConfig;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	private static LinkedBlockingQueue<Jedis> queue = new LinkedBlockingQueue<Jedis>();
	//private Logger logger = Logger.getLogger(getClass());
	
	public void init(){
		Util.sysLog(LogValues.debug, this.getClass().getName(), "INTIALIZING REDIS CONNECTIONS");
		for(int i=0;i<this.jedisPoolConfig.getMaxActive();i++){
			try {
				JRedisPool.queue.put(this.jedisPool.getResource());
			} catch (InterruptedException e) {
			}
		}
	}
	
	public Jedis getConnection(){
		Jedis jedis = null;
		try {
			jedis = queue.take();
		} catch (InterruptedException e) {
			Logger.sysLog(LogValues.debug, this.getClass().getName(), "exception "+coreException.GetStack(e));
		}
		return jedis;
	}
	
	public void disconnect(Jedis jedis){
		try {
			queue.put(jedis);
		} catch (InterruptedException e) {
			Logger.sysLog(LogValues.debug, this.getClass().getName(), "exception "+coreException.GetStack(e));
		}
		//notify();
		Util.sysLog(LogValues.debug, this.getClass().getName(), "ADDING THE JEDIS CONNECTION TO LOCAL QUEUE");
	}
	
	/**
	 * to be implemented from bean destory method
	 */
	public void destroy(){
		Util.sysLog(LogValues.debug, this.getClass().getName(),"____________________________________________Shutting down thread closing all connections_____________________________________");
		for(int i=0;i<queue.size();i++){
			try {
				queue.take().disconnect();
			} catch (InterruptedException e) {
			}
		}
	}
}
