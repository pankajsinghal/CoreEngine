package com.bng.core.memCache;

public interface MemCacheJSon {
	public void set(String key, Object o);
	public void set(String key, int exp, Object o);
	public Object get(String key);
	public long incr(String key);
	public long incr(String key, int by);
	public void delete(String key);
}
