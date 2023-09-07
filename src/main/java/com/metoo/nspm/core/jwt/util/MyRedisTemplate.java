package com.metoo.nspm.core.jwt.util;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

public class MyRedisTemplate {

    private final Log log = LogFactory.getLog(MyRedisTemplate.class);

    /**
     * 过期时长
     */
    private final Long DURATION = 1 * 24 * 60 * 60 * 1000L;

    @Resource
    private RedisTemplate redisTemplate;

    private ValueOperations<String, String> valueOperations;

    @PostConstruct
    public void init() {
        RedisSerializer redisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(redisSerializer);
        redisTemplate.setValueSerializer(redisSerializer);
        redisTemplate.setHashKeySerializer(redisSerializer);
        redisTemplate.setHashValueSerializer(redisSerializer);
        valueOperations = redisTemplate.opsForValue();
    }


    public void set(String key, String value) {
        valueOperations.set(key, value, DURATION, TimeUnit.MILLISECONDS);
//        log.info("key={}, value is: {} into redis cache", key, value);
    }


    public String get(String key) {
        String redisValue = valueOperations.get(key);
//        log.info("get from redis, value is: {}", redisValue);
        return redisValue;
    }


    public boolean delete(String key) {
        boolean result = redisTemplate.delete(key);
//        log.info("delete from redis, key is: {}", key);
        return result;
    }


    public Long getExpireTime(String key) {
        return valueOperations.getOperations().getExpire(key);
    }
}
