package com.easycodebox.login.shiro.cache.spring;

import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.validate.Assert;
import org.springframework.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

/**
 * @author WangXiaoJin
 */
public class RedisTemplateCacheStats<K, V> implements CacheStats {

    private boolean usePrefix = false;

    @Override
    public int size(Cache cache) {
        Set<K> keys = keys(cache);
        return keys.size();
    }

    @Override
    public Set<K> keys(Cache cache) {
        Assert.notNull(cache);
        RedisTemplate template = (RedisTemplate) cache.getNativeCache();
        return template.keys(( isUsePrefix() ? cache.getName() : Symbol.EMPTY ) + "*");
    }

    @Override
    public Collection<V> values(Cache cache) {
        Set<K> keys = keys(cache);
        Collection<V> vals = new LinkedList<>();
        for (K key: keys) {
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
}
