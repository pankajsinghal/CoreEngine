package com.bng.core.memCache;


public class JRedisMemCacheJsonImpl implements MemCacheJSon {

	JRedis jredis;
	public void setJredis(JRedis jredis) {
		this.jredis = jredis;
	}

	public void set(String key, Object o) {
		jredis.set(key, o);
	}

	/**
	 * expiry in second
	 */
	public void set(String key, int exp, Object o) {
		/*UserCallDetail ucd = null;
		try{ucd = (UserCallDetail) o;}catch(Exception e){}
		Logger.sysLog(LogValues.info,this.getClass().getName(),"_______setting ucd for key "+key);
		try{Logger.sysLog(LogValues.info,this.getClass().getName(),"________________________________ setting ucd for key "+key+"#######################"+ucd!=null?ucd.isaPartyCallDetail()+" , "+ucd.isbPartyCallDetail():"ucd null");}catch(Exception e)
		{
			Logger.sysLog(LogValues.error,this.getClass().getName(),"___________"+key+"    "+coreException.GetStack(e));}
		*/
		jredis.set(key, o, exp);
	}	

	public Object get(String key) {
		//System.out.println("############################# getting ucd for key "+key+"#######################");
		return jredis.get(key);
	}

	public long incr(String key) {
		return incr(key, 2000);
	}

	public long incr(String key, int by) {
		jredis.increaseExpiry(key, by);
		return 0;
	}

	public void delete(String key) {
		jredis.remove(key);
	}

}
