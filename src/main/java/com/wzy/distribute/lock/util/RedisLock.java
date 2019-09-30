package com.wzy.distribute.lock.util;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisLock {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisLock.class);

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	
	//获取尝试时间  key过期时间
	private final long EXPIRE_TIME = 30 *  1000;
	
	
	/**
	 * @param key  锁的key
	 * @param value  时间戳+过期时间，表示 key的截止时间，但不会自动删除，需要业务判断
	 * @return
	 */
	public Boolean tryLock(String key , String value) {
		//如果没有获取到，就判断是否库存为0，不为0就继续等待
		if (stringRedisTemplate.opsForValue().setIfAbsent(key, value, EXPIRE_TIME, TimeUnit.MILLISECONDS)) {
            return true;
        }
		String currentValue = stringRedisTemplate.opsForValue().get(key);
		if(StringUtils.isNotEmpty(currentValue) && Long.valueOf(currentValue) < System.currentTimeMillis()) {
			//获取上一个锁的时间 如果高并发的情况可能会出现已经被修改的问题  所以多一次判断保证线程的安全
			String oldValue = stringRedisTemplate.opsForValue().getAndSet(key, value);
			if(StringUtils.isNotEmpty(oldValue) && oldValue.equals(currentValue)) {
				return true;
			}
		}
		return  false;
	}
	
	/**
	 * 解除锁的操作
	 * @param key
     * @param value
	 * */
	
	public void unlock(String key , String value){
		String currentValue = stringRedisTemplate.opsForValue().get(key);
		try {
			if(StringUtils.isNotEmpty(currentValue) && currentValue.equals(value)) {
				//当前线程只能删除自己的锁
				LOGGER.info( "释放锁 key ,  {} --> {}" , key , value );
				stringRedisTemplate.opsForValue().getOperations().delete(key);
			}
		}catch(Exception e) {
			LOGGER.info( "删除锁失败 key ,  {} --> {}" , key , value );
			e.printStackTrace();
		}
	}
	
	
	
	
}
