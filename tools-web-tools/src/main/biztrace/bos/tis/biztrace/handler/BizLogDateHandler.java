package bos.tis.biztrace.handler;

import java.util.Set;

import redis.clients.jedis.Jedis;
import bos.tis.biztrace.redis.AbstractRedisHandler;
import bos.tis.biztrace.utils.RunConfig;

public class BizLogDateHandler extends AbstractRedisHandler{
	
	private Jedis jedis ;
	private Set<String> dateList = null;
	public static final BizLogDateHandler instance = new BizLogDateHandler() ;
		
	public Set<String> getUnanalyzedBizLogDate(){
		try{
			jedis = jedisPool.getResource() ;
			dateList = jedis.smembers(RunConfig.KP_UNANALYZED_DATE);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			jedis.close();
		}
		
		return dateList;
	}
	
	public void deleteAnalyzedBizLogDate(Object[] analyzedDateArry){
		try{
			jedis = jedisPool.getResource() ;
			for(int i=0;i<analyzedDateArry.length;i++){
				jedis.srem(RunConfig.KP_UNANALYZED_DATE,analyzedDateArry[i].toString());
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			jedis.close();
		}
	}
	
	public Set<String> getResolvedBizLogDate(){
		try{
			jedis = jedisPool.getResource() ;
			dateList = jedis.smembers(RunConfig.KP_SET_RESOLVED_DATE);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			jedis.close();
		}
		return dateList;
	}
}
