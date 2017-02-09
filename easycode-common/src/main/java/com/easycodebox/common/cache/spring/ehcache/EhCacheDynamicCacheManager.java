package com.easycodebox.common.cache.spring.ehcache;

import net.sf.ehcache.*;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import java.util.*;

/**
 * EhCacheCacheManager默认只会加载ehcache.xml配置中的cache，不会动态new cache。
 * 所以重写了getMissingCache方法用于自动创建Cache
 * @author WangXiaoJin
 *
 */
public class EhCacheDynamicCacheManager extends EhCacheCacheManager {

	/**
	 * 标准Ehcache配置，为null时新创建的Ehcache使用CacheManager中ehcache.xml的defaultCache配置
	 */
	private CacheConfiguration stdEhcacheCfg;
	
	@Override
	protected Collection<Cache> loadCaches() {
		Status status = getCacheManager().getStatus();
		if (!Status.STATUS_ALIVE.equals(status)) {
			throw new IllegalStateException(
					"An 'alive' EhCache CacheManager is required - current cache is " + status.toString());
		}

		String[] names = getCacheManager().getCacheNames();
		Collection<Cache> caches = new LinkedHashSet<>(names.length);
		for (String name : names) {
			caches.add(new CustomEhCacheCache(getCacheManager().getEhcache(name)));
		}
		return caches;
	}
	
	@Override
	protected Cache getMissingCache(String name) {
		// Check the EhCache cache again (in case the cache was added at runtime)
		Ehcache ehcache = getCacheManager().getEhcache(name);
		if (ehcache != null) {
			return new CustomEhCacheCache(ehcache);
		}else {
			if(stdEhcacheCfg == null) {
				getCacheManager().addCacheIfAbsent(name);
				return new CustomEhCacheCache(getCacheManager().getEhcache(name));
			}else {
				net.sf.ehcache.Cache cache = new net.sf.ehcache.Cache(stdEhcacheCfg.clone().name(name));
				getCacheManager().addCacheIfAbsent(cache);
				return new CustomEhCacheCache(cache);
			}
		}
	}

	public CacheConfiguration getStdEhcacheCfg() {
		return stdEhcacheCfg;
	}

	public void setStdEhcacheCfg(CacheConfiguration stdEhcacheCfg) {
		this.stdEhcacheCfg = stdEhcacheCfg;
	}
	
}
