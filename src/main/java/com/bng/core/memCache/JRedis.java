package com.bng.core.memCache;


public interface JRedis {
	public Object get(Object key);
	public void remove(Object key);
	public void increaseExpiry(Object key, int expiry_seconds);
	public void set(Object key, Object value);
	public void set(Object key, Object value, int expiry_seconds);
}
