package com.easycodebox.common.cache;

import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.lang.Symbol;
import net.sf.ehcache.*;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;

import java.io.Serializable;

/**
 * 此功能有优化空间。建议改用spring + ehcache注解功能。
 * @author WangXiaoJin
 * 
 */
@Aspect
@Deprecated
public final class CacheAspect implements Ordered, InitializingBean {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private final Integer DEFAULT_ORDER = 100;
	private Integer order;
	
	private CacheManager cacheManager;
	private Resource configLocation;
	private boolean shared = false;
	private int maxElementsInMemory = 10000;
	private int maxElementsOnDisk = 10000000;
	private MemoryStoreEvictionPolicy memoryStoreEvictionPolicy = MemoryStoreEvictionPolicy.LRU;
	private boolean overflowToDisk = true;
	private boolean eternal = false;
	private int timeToLive = 600;
	private int timeToIdle = 300;
	private boolean diskPersistent = false;
	private int diskExpiryThreadIntervalSeconds = 120;
	
	public void destroy() {
		log.info("Shutting down Cache CacheManager");
		this.cacheManager.shutdown();
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		order = order == null ? DEFAULT_ORDER : order;
		
		if(this.cacheManager != null) {
			log.info("Cache CacheManager had been Initialized");
			return;
		}
		log.info("Initializing Cache CacheManager");
		if (this.shared) {
			// Shared CacheManager singleton at the VM level.
			if (this.configLocation != null) {
				try {
					this.cacheManager = CacheManager.create(this.configLocation.getInputStream());
				} catch (Exception e) {
					log.error("load resource error.", e);
					this.cacheManager = CacheManager.create();
				}
			}else {
				this.cacheManager = CacheManager.create();
			}
		}
		else {
			// Independent CacheManager instance (the default).
			if (this.configLocation != null) {
				try {
					this.cacheManager = new CacheManager(this.configLocation.getInputStream());
				} catch (Exception e) {
					log.error("load resource error.", e);
					this.cacheManager = new CacheManager();
				}
			}else {
				this.cacheManager = new CacheManager();
			}
		}
	
	}

	@Override
	public int getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Around("@annotation(cacheable)")
	public Object cacheable(final ProceedingJoinPoint pjp, final Cacheable cacheable) throws Throwable {
		if(cacheable.cacheOperation() == CacheOperation.NOP) {
			return pjp.proceed();
		}
		String group = cacheable.group().getName(),
				cacheKey = getCacheKey(pjp);
		Cache cache = getCache(group);
		Object result = null;
		if(cacheable.cacheOperation() == CacheOperation.CACHING) {
	        Element element = cache.get(cacheKey);
	        if(element == null) {
	        	result = pjp.proceed();
	            if(result == null || result instanceof Serializable) {
	            	element = new Element(cacheKey, (Serializable)result);
	            	log.info("正在缓存{}对象。", cacheKey);
	            	cache.putIfAbsent(element);
	            }else {
	            	log.warn("{}指定方法返回对象{}不能被序列化，不能缓存。", cacheKey, result);
	            }
	        }else {
	        	log.info("从缓存中获取{}对象。", cacheKey);
	        	result = element.getObjectValue();
	        }
		}else if(cacheable.cacheOperation() == CacheOperation.FLUSH_CACHE) {
	        result = pjp.proceed();
	        for(Class<?> clazz : cacheable.flushGroup()) {
	        	String groupName = clazz.getName();
	        	if(!groupName.equals(group)) {
	        		log.info("删除缓存组{}所有的缓存对象。", groupName);
	        		getCache(group).removeAll();
	        	}
	        		
	        }
	        log.info("删除缓存组{}所有的缓存对象。", group);
	        //此处应该优化，应该可配置成只删除对应ID的数据，不应该全部删除该类型的缓存
	        cache.removeAll();
		}
		return result;
	}
	
	/** 
     *创建一个缓存对象的标识: targetName.methodName.arg0.arg1... 
     */
    private String getCacheKey(final ProceedingJoinPoint pjp) {
    	StringBuilder sb = new StringBuilder();
        sb.append(pjp.getTarget().getClass().getName()).append(Symbol.PERIOD).append(pjp.getSignature().getName());
        Object[] arguments = pjp.getArgs();
        if (arguments != null && arguments.length > 0) {
	        for (Object argument : arguments) {
		        sb.append(Symbol.PERIOD);
		        if (argument instanceof DetailEnum)
			        sb.append(((DetailEnum<?>) argument).getClassName());
			        //else if(arg instanceof Entity)
			        //arg.getClass().getAnnotation(javax.persistence.Id.class);
		        else
			        sb.append(argument);
	        }
        }
        return sb.toString();
    }
    
    private Cache getCache(String group) {
    	if(this.cacheManager == null) {
			this.cacheManager = CacheManager.getInstance();
		}
    	Cache cache = null;
		if (this.cacheManager.cacheExists(group)) {
			log.debug("获取缓存组{}对象。", group);
			cache = this.cacheManager.getCache(group);
		}
		if (cache == null) {
			log.debug("创建缓存组{}对象。", group);
			cache = createCache(group);
			Cache back = (Cache)this.cacheManager.addCacheIfAbsent(cache);
			
			//如果添加不成功，说明在此代码间隙中已经添加了
			cache = back == null ? cache : back;
		}
    	return cache;
    }
    
	private Cache createCache(String group) {
		return new Cache(
				group, this.maxElementsInMemory, this.memoryStoreEvictionPolicy,
				this.overflowToDisk, null, this.eternal, this.timeToLive, this.timeToIdle,
				this.diskPersistent, this.diskExpiryThreadIntervalSeconds, null, null, this.maxElementsOnDisk);
	}
	
	

	public void setConfigLocation(Resource configLocation) {
		this.configLocation = configLocation;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public void setMaxElementsInMemory(int maxElementsInMemory) {
		this.maxElementsInMemory = maxElementsInMemory;
	}

	public void setMaxElementsOnDisk(int maxElementsOnDisk) {
		this.maxElementsOnDisk = maxElementsOnDisk;
	}

	public void setMemoryStoreEvictionPolicy(
			MemoryStoreEvictionPolicy memoryStoreEvictionPolicy) {
		this.memoryStoreEvictionPolicy = memoryStoreEvictionPolicy;
	}

	public void setOverflowToDisk(boolean overflowToDisk) {
		this.overflowToDisk = overflowToDisk;
	}

	public void setEternal(boolean eternal) {
		this.eternal = eternal;
	}

	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}

	public void setTimeToIdle(int timeToIdle) {
		this.timeToIdle = timeToIdle;
	}

	public void setDiskPersistent(boolean diskPersistent) {
		this.diskPersistent = diskPersistent;
	}

	public void setDiskExpiryThreadIntervalSeconds(
			int diskExpiryThreadIntervalSeconds) {
		this.diskExpiryThreadIntervalSeconds = diskExpiryThreadIntervalSeconds;
	}
	

}
