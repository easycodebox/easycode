package com.easycodebox.common.cache.spring.ehcache;

import net.sf.ehcache.Ehcache;

import org.springframework.cache.ehcache.EhCacheCache;

import com.easycodebox.common.cache.spring.MultiKey;

/**
 * 扩展了批量删除缓存
 * @author WangXiaoJin
 *
 */
public class CustomEhCacheCache extends EhCacheCache {

	public CustomEhCacheCache(Ehcache ehcache) {
		super(ehcache);
	}

	@Override
	public void evict(Object key) {
		if(key instanceof MultiKey) {
			for(Object item : ((MultiKey)key).getParams()) {
				super.evict(item);
			}
		}else {
			super.evict(key);
		}
	}

}
