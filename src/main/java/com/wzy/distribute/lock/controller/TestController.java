package com.wzy.distribute.lock.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    @GetMapping("/")
    public String index(String key, String value) {
       return  "hello";
    }

    @RequestMapping(value = "/set" ,method = RequestMethod.GET)
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    @RequestMapping(value = "/get" , method = RequestMethod.GET)
    public String show(String key) {
    	String result = stringRedisTemplate.opsForValue().get(key);
        return result;
    }

    @RequestMapping("/set2")
    public void set2(String key, String value) {
        // 可选，自定义序列化方法
        stringRedisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<String> redisSerializer = redisTemplate.getStringSerializer();
                byte[] serializeKey = redisSerializer.serialize(key);
                byte[] serializeValue = redisSerializer.serialize(value);
                return redisConnection.setNX(serializeKey, serializeValue);
            }
        });
    }
}