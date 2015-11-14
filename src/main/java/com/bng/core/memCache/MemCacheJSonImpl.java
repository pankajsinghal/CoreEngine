package com.bng.core.memCache;


public class MemCacheJSonImpl implements MemCacheJSon{

    //private static final Logger logger = Logger.getLogger(MemCacheJSonImpl.class);
    //@Autowired
    MemCache memCache;
    
    public void setMemCache(MemCache memCache) {
        this.memCache = memCache;
    }
        
        
	public void set(String key, int exp, Object o) {
		memCache.set(key, exp, o);
	}
	public long incr(String key, int by) {
		return memCache.incr(key, by,9600);
	}

	public void delete(String key) {
		memCache.delete(key);
	}

	public void set(String key, Object o) {
		set(key, 9600, o);
	}
	
	public void set(String key,String o) {
		set(key, 9600, o);
	}

	public long incr(String key) {
		return incr(key, 9600);
	}

	public Object get(String key) {
		return memCache.get(key);
	}
}
