package com.bng.core.memCache;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import net.spy.memcached.internal.BulkFuture;

public interface MemCache {
	public Future<Boolean> set(String key, int exp, Object o);
	public Future<Boolean> add(String key, int exp, Object o);
	public Future<Long> asyncDecr(String key, int time);
	public Object asyncGet(String key) throws Exception;
	public BulkFuture<Map<String, Object>> asyncGetBulk(Collection<String> keys);
	public Object get(String key);
	public long decr(String key, int by);
	public long decr(String key, int by, long def);
	public Future<Long> asyncIncr(String key, int time);
	public Future<Boolean> delete(String key);
	public void shutdown();
	public long incr(String key, int by);
	public long incr(String key, int by , long def);
	public Collection<SocketAddress> getAvailableServers();
	public Collection<SocketAddress> getUnavailableServers();
	public BulkFuture<Map<String, Object>> asyncGetBulk(String keys);
}
