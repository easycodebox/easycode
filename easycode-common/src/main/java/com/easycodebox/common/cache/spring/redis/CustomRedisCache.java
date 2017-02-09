package com.easycodebox.common.cache.spring.redis;

import com.easycodebox.common.cache.spring.MultiKey;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.core.RedisOperations;

/**
 * 扩展了批量删除缓存
 * @author WangXiaoJin
 */
public class CustomRedisCache extends RedisCache {

    public CustomRedisCache(String name, byte[] prefix, RedisOperations<?, ?> redisOperations, long expiration) {
        super(name, prefix, redisOperations, expiration);
    }

    @Override
    public void evict(Object key) {
        if (key instanceof MultiKey) {
            for (Object item : ((MultiKey) key).getParams()) {
                super.evict(item);
            }
        } else {
            super.evict(key);
        }
    }
}
