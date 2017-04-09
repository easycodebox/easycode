package com.easycodebox.login.shiro.cache.spring;

import com.easycodebox.common.validate.Assert;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import java.util.Collection;
import java.util.Set;

/**
 * @author WangXiaoJin
 */
public class SpringCache<K, V> implements Cache<K, V> {

    private org.springframework.cache.Cache cache;

    private CacheStats cacheStats;

    public SpringCache(org.springframework.cache.Cache cache, CacheStats cacheStats) {
        Assert.notNull(cache);
        this.cache = cache;
        this.cacheStats = cacheStats;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(K key) throws CacheException {
        try {
            org.springframework.cache.Cache.ValueWrapper val = cache.get(key);
            return val == null ? null : (V) val.get();
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public V put(K key, V value) throws CacheException {
        try {
            V previous = get(key);
            cache.put(key, value);
            return previous;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public V remove(K key) throws CacheException {
        try {
            V previous = get(key);
            cache.evict(key);
            return previous;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public void clear() throws CacheException {
        try {
            cache.clear();
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public int size() {
        return cacheStats == null ? -1 : cacheStats.size(cache);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<K> keys() {
        return cacheStats == null ? null : cacheStats.keys(cache);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<V> values() {
        return cacheStats == null ? null : cacheStats.values(cache);
    }

}
