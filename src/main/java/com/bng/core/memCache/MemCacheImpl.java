package com.bng.core.memCache;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.BulkFuture;

import org.apache.log4j.Logger;

public class MemCacheImpl implements MemCache 
{
    private static final Logger logger = Logger.getLogger(MemCacheImpl.class.getName());

    //@Autowired
    private MemcachedClient memcachedClient;   

    public void setMemcachedClient(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
    }
    
    public Future<Boolean> set(String key, int exp, Object o) {
        //logger.debug("memCacheClient is alive "+memcachedClient.isAlive() +" in set is default");
        return memcachedClient.set(key, exp, o);
    }

    public Future<Boolean> add(String key, int exp, Object o) {
        return set(key, exp, o);
    }

    public Future<Long> asyncDecr(String key, int time) {
        return memcachedClient.asyncDecr(key, time);
    }

    public Object asyncGet(String key) throws Exception {
        //logger.debug("memcache is alive ["+memcachedClient.isAlive()+"] fetching key ["+key+"]");
        return memcachedClient.asyncGet(key).get(10, TimeUnit.SECONDS);
    }

    public BulkFuture<Map<String, Object>> asyncGetBulk(Collection<String> keys) {
        return memcachedClient.asyncGetBulk(keys);
    }

    public Object get(String key){
    	return memcachedClient.get(key);
    }

    public long decr(String key, int by) {
        return memcachedClient.decr(key, by);
    }

    public long decr(String key, int by, long def) {
        return memcachedClient.decr(key, by, def);
    }

    public Future<Long> asyncIncr(String key, int time) {
        return memcachedClient.asyncIncr(key, time);
    }

    public Future<Boolean> delete(String key) {
        return memcachedClient.delete(key);
    }

    public void shutdown() {
        logger.info("Shutdown not required in case of spring context");
        memcachedClient.shutdown();
    }
    public long incr(String key, int by) {
        return memcachedClient.incr(key, by);
    }

    public long incr(String key, int by, long def) {
        return memcachedClient.incr(key, by, def);
    }

    public Collection<SocketAddress> getAvailableServers() {
        return memcachedClient.getAvailableServers();
    }

    public Collection<SocketAddress> getUnavailableServers() {
        return memcachedClient.getUnavailableServers();
    }

    public BulkFuture<Map<String, Object>> asyncGetBulk(String keys) {
        return memcachedClient.asyncGetBulk(keys);
    }

}
