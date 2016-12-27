package com.easycodebox.login.shiro.cache.spring;

import org.springframework.cache.Cache;

import java.util.Collection;
import java.util.Set;

/**
 * 扩展Spring Cache 中缺少 Shiro Cache的统计接口
 * @author WangXiaoJin
 */
public interface CacheStats<K, V> {

    /**
     * Returns the number of entries in the cache.
     *
     * @return the number of entries in the cache.
     */
    int size(Cache cache);

    /**
     * Returns a view of all the keys for entries contained in this cache.
     *
     * @return a view of all the keys for entries contained in this cache.
     */
    Set<K> keys(Cache cache);

    /**
     * Returns a view of all of the values contained in this cache.
     *
     * @return a view of all of the values contained in this cache.
     */
    Collection<V> values(Cache cache);

}
