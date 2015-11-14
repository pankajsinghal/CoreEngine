package com.bng.core.memCache;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

import com.bng.core.exception.coreException;
import com.bng.core.utils.Utility;

public class JRedisImpl implements JRedis{

	private JRedisPool jRedisPool;

	public void setjRedisPool(JRedisPool jRedisPool) {
		this.jRedisPool = jRedisPool;
	}

	private Logger logger = Logger.getLogger(getClass());
	
	public Object get(Object key) {
		Jedis j = this.jRedisPool.getConnection();
		Object o = null;
		try {
			byte [] b = j.get(Utility.object2byte(key));
			if(b !=null){ 
				o = Utility.byte2object(b);
			}
		} catch (Exception e) {
			logger.error(coreException.GetStack(e));
		}
		try{
			this.jRedisPool.disconnect(j);
		}catch (Exception e) {
			logger.error(coreException.GetStack(e));
		}
		return o;
	}

	public void set(Object key, Object value) {
		Jedis j = this.jRedisPool.getConnection();
		try {
			j.set(Utility.object2byte(key), Utility.object2byte(value));
		} catch (Exception e) {
			logger.error(coreException.GetStack(e));
		}
		try{
			this.jRedisPool.disconnect(j);
		}catch (Exception e) {
			logger.error(coreException.GetStack(e));
		}
	}

	public void remove(Object key) {
		Jedis j = this.jRedisPool.getConnection();
		try {
			logger.debug("["+key+"] key removed " +j.del(Utility.object2byte(key)));
		} catch (Exception e) {
			logger.error(coreException.GetStack(e));
		}
		try{
			this.jRedisPool.disconnect(j);
		}catch (Exception e) {
			logger.error(coreException.GetStack(e));
		}
	}

	public void set(Object key, Object value, int expiry_seconds) {
		Jedis j = this.jRedisPool.getConnection();
		try {
			j.setex(Utility.object2byte(key), expiry_seconds,Utility.object2byte(value));
		} catch (Exception e) {
			logger.error(coreException.GetStack(e));
		}
		try{
			this.jRedisPool.disconnect(j);
		}catch (Exception e) {
			logger.error(coreException.GetStack(e));
		}
	}

	public void increaseExpiry(Object key, int expiry_seconds) {
		Jedis j = this.jRedisPool.getConnection();
		try {
			j.expire(Utility.object2byte(key), expiry_seconds);
		} catch (Exception e) {
			logger.error(coreException.GetStack(e));
		}
		try{
			this.jRedisPool.disconnect(j);
		}catch (Exception e) {
			logger.error(coreException.GetStack(e));
		}
	}
}
