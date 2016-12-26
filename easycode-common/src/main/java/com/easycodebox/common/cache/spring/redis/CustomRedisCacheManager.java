package com.easycodebox.common.cache.spring.redis;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisOperations;

import java.util.Collection;

/**
 * 重写{@link RedisCacheManager}是因为想使用自定义的{@link CustomRedisCache}
 * @author WangXiaoJin
 */
public class CustomRedisCacheManager extends RedisCacheManager {

    public CustomRedisCacheManager(RedisOperations redisOperations) {
        super(redisOperations);
    }

    public CustomRedisCacheManager(RedisOperations redisOperations, Collection<String> cacheNames) {
        super(redisOperations, cacheNames);
    }

    protected RedisCache createCache(String cacheName) {
        long expiration = computeExpiration(cacheName);
        return new CustomRedisCache(cacheName, (isUsePrefix() ? getCachePrefix().prefix(cacheName) : null), getRedisOperations(), expiration);
    }

}
