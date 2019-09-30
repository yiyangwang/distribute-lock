package com.wzy.distribute.lock.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wzy.distribute.lock.util.RedisLock;

@RestController
@RequestMapping("/lock")
public class LockController {

	private static final Logger LOGGER = LoggerFactory.getLogger(LockController.class);
	
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	
	@Resource
	private  RedisLock redisLock;
	
	
	private static final Integer PRODUCT_TIME_OUT = 100 * 1000;
	
	
	
	@RequestMapping(value = "/trylock/{key}/{value}" ,method = RequestMethod.GET)
    public String  trylock(@PathVariable("key") String key, @PathVariable("value") String value) {
		LOGGER.info("加锁：{}  --> {}" ,  key , value);
		Boolean  flag = redisLock.tryLock(key, value);
		if(flag) {
			return "加锁成功";
		}else {
			return "加锁失败";
		}
    }
	
	
	
}
