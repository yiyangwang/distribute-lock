package com.wzy.distribute.lock.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.wzy.distribute.lock.util.RedisLock;

@Service
public class ProductStoreServiceImpl implements ProductStoreService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductStoreServiceImpl.class);
	
	private static final String PRODUCT_PREFIX = "prod-";
	
	private static final Integer TIMOUT = 10 * 1000;  //超时时间
	
	private static final Integer EXPIRE_TIME = 10 * 60 * 1000;  //超时时间
	
	@Resource
	private RedisLock redisLock;
	
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	
	@Override
	public Boolean addProduct(String productId , Integer num) {
		LOGGER.info("addProduct productId : {} , num : {}   " , productId , num );
		StringBuilder sBuilder = new StringBuilder(PRODUCT_PREFIX);
		sBuilder.append(productId);
		return stringRedisTemplate.opsForValue().setIfAbsent(sBuilder.toString(), num.toString() , EXPIRE_TIME, TimeUnit.MILLISECONDS);
	}

	@Override
	public Integer queryProduct(String productId) {
		LOGGER.info("queryProduct productId : {}  " , productId );
		StringBuilder sBuilder = new StringBuilder(PRODUCT_PREFIX);
		sBuilder.append(productId);
		String currentStore = stringRedisTemplate.opsForValue().get(sBuilder.toString());
		if(StringUtils.isEmpty(currentStore)) {
			return 0;
		}
		return Integer.valueOf(currentStore);
	}
	
	@Override
	public Integer decrementProduct(String productId) {
		
		LOGGER.info("decrementProduct productId : {}  " , productId );
		
		StringBuilder sBuilder = new StringBuilder(PRODUCT_PREFIX);
		sBuilder.append(productId);
		Long result = stringRedisTemplate.opsForValue().decrement(sBuilder.toString());
		return result.intValue();
	}
	
	@Override
	public boolean decrementProductStore(String productId, Integer productQuantity) {
		LOGGER.debug("decrementProductStore  productId : {} ,  productQuantity : {} " , productId , productQuantity);
		//库存key
		String key = "dec_store_lock_" + productId;
		long time = System.currentTimeMillis() + TIMOUT;
		
		//如果加锁失败
		if(!redisLock.tryLock(key, String.valueOf(time))) {
			LOGGER.info("获取锁失败  ,  key : {}" , key);
			return false;
		}
		LOGGER.info("获取锁成功  ,  key : {}" , key);
		//查询库存
		Integer productStore = this.queryProduct(productId);
		if(productStore == null) {
			LOGGER.info("商品库存不存在 ，扣减库存失败");
			return false;
		}
		if(productStore != null && productStore > 0) {
			//扣减库存
			LOGGER.info("当前库存数为：{},开始扣减库存... " , productStore);
			this.decrementProduct(productId);
			try {
				//休眠3秒
				Thread.currentThread().sleep(1 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else {
			LOGGER.info("库存数量为 0 ，扣减库存失败");
			return false;
		}
		//释放锁
		redisLock.unlock(key, String.valueOf(time));
		return true;
	}

}
