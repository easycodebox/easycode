package com.easycodebox.login.shiro.cache.spring;

import com.easycodebox.common.validate.Assert;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/**
 * @author WangXiaoJin
 */
public class SpringCacheManager implements CacheManager {

    private org.springframework.cache.CacheManager cacheManager;

    private CacheStats cacheStats;

    public SpringCacheManager(org.springframework.cache.CacheManager cacheManager) {
        Assert.notNull(cacheManager);
        this.cacheManager = cacheManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        return new SpringCache(cacheManager.getCache(name), cacheStats);
    }

    public org.springframework.cache.CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(org.springframework.cache.CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public CacheStats getCacheStats() {
        return cacheStats;
    }

    public void setCacheStats(CacheStats cacheStats) {
        this.cacheStats = cacheStats;
    }

}
