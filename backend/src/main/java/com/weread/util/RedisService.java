package com.weread.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * RedisService - Redis操作工具类
 * 封装了常用的键值操作，特别是针对验证码等带过期时间的数据。
 */
@Component
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    // Spring Boot 会自动注入 StringRedisTemplate
    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 设置键值对，并设置过期时间
     *
     * @param key 键
     * @param value 值
     * @param timeout 过期时间数值
     * @param unit 过期时间单位 (如 TimeUnit.MINUTES)
     */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取指定键的值
     *
     * @param key 键
     * @return 键对应的值 (如果不存在，返回 null)
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除指定的键
     *
     * @param key 键
     * @return 是否成功删除
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }
}