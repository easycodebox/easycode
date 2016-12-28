package com.easycodebox.login.shiro.cache.spring;

import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.validate.Assert;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.DefaultRedisCachePrefix;
import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationUtils;

import java.util.*;

/**
 * 如果{@link org.springframework.data.redis.cache.RedisCacheManager}设置了自定义的{@link RedisCachePrefix}时，
 * 此类也需要设置相同的{@link RedisCachePrefix},不然此类的接口获取的信息不准确。
 * @author WangXiaoJin
 */
public class RedisTemplateCacheStats<K, V> implements CacheStats {

    private boolean usePrefix;
    private RedisCachePrefix cachePrefix = new DefaultRedisCachePrefix();

    @Override
    public int size(Cache cache) {
        Set<K> keys = keys(cache);
        return keys.size();
    }

    @Override
    public Set<K> keys(final Cache cache) {
        Assert.notNull(cache);
        RedisTemplate template = (RedisTemplate) cache.getNativeCache();
        Set<byte[]> rawKeys = (Set<byte[]>) template.execute(new RedisCallback<Set<byte[]>>() {

            public Set<byte[]> doInRedis(RedisConnection connection) {
                String key = (isUsePrefix() ? cache.getName() : Symbol.EMPTY) + Symbol.ASTERISK;
                return connection.keys(key.getBytes());
            }
        }, true);

        if (template.getKeySerializer() == null || rawKeys == null) return (Set<K>) rawKeys;

        Set<byte[]> keys = new LinkedHashSet<>();
        for (byte[] rawKey : rawKeys) {
            if (rawKey != null && isUsePrefix() && cachePrefix != null) {
                byte[] prefix = cachePrefix.prefix(cache.getName());
                keys.add(Arrays.copyOfRange(rawKey, prefix.length, rawKey.length));
            } else {
                keys.add(rawKey);
            }
        }
        return SerializationUtils.deserialize(keys, template.getKeySerializer());
    }

    @Override
    public Collection<V> values(Cache cache) {
        Set<K> keys = keys(cache);
        Collection<V> vals = new LinkedList<>();
        for (K key : keys) {
            Cache.ValueWrapper valueWrapper = cache.get(key);
            vals.add(valueWrapper == null ? null : (V) valueWrapper.get());
        }
        return vals;
    }

    public boolean isUsePrefix() {
        return usePrefix;
    }

    public void setUsePrefix(boolean usePrefix) {
        this.usePrefix = usePrefix;
    }

    public RedisCachePrefix getCachePrefix() {
        return cachePrefix;
    }

    public void setCachePrefix(RedisCachePrefix cachePrefix) {
        this.cachePrefix = cachePrefix;
    }
}
