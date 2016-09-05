package com.easycodebox.common.cache.spring;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.cache.interceptor.KeyGenerator;

import com.easycodebox.common.lang.Symbol;

/**
 * Spring Cache默认使用SimpleKeyGenerator。
 * 设计此类主要是因为Spring Cache的@Cacheable和@CacheEvict不支持同时操作做个key，因为开发经常会提供批量删除/修改的接口，
 * 那就有可以执行一个接口时同时删除一批次缓存，但一个@CacheEvict注解只会删除一个缓存。
 * 所以会出现一个现象，当你用批量删除/修改时，你只能用@CacheEvict(allEntries=true)来删除指定name cache中的所有缓存。
 * 这明显不是我们想要的结果啊，我只修改了两个实例，你非得让我把所有缓存都删除了？？？要不然就限制不提供批量操作接口？？？
 * @author WangXiaoJin
 *
 */
public class MultiKeyGenerator implements KeyGenerator {
	
	private ConcurrentHashMap<String, Integer> multiKeyIndexMap = new ConcurrentHashMap<String, Integer>(16);
	
	private final Integer NON_MULTI_KEY_ANNO = NumberUtils.INTEGER_MINUS_ONE;

	@Override
	public Object generate(Object target, Method method, Object... params) {
		String mKey = genetateMethodKey(target, method);
		if(!multiKeyIndexMap.containsKey(mKey)) {
			multiKeyIndexMap.putIfAbsent(mKey, lookupMultiIndex(target, method));
		}
		if(multiKeyIndexMap.get(mKey).equals(NON_MULTI_KEY_ANNO)) {
			for(Object param : params) {
				if(param.getClass().isArray())
					return new MultiKey((Object[])param);
				else if(param instanceof Collection)
					return new MultiKey(((Collection<?>)param).toArray());
			}
			throw new IllegalArgumentException("There is no param is Array or Collection in method(" +
						method.getName() + ").");
		}else {
			Object param = params[multiKeyIndexMap.get(mKey)];
			Object[] ps;
			if(param.getClass().isArray()) {
				ps = (Object[])param;
			}else if(param instanceof Collection) {
				ps = ((Collection<?>)param).toArray();
			}else {
				throw new IllegalArgumentException("The param with @MultiMode in method(" +
						method.getName() + ") is not Array or Collection.");
			}
			return new MultiKey(ps);
		}
	}
	
	private String genetateMethodKey(Object target, Method method) {
		return target.getClass().getName().concat(Symbol.PERIOD).concat(method.getName());
	}
	
	private Integer lookupMultiIndex(Object target, Method method) {
		Integer index = NON_MULTI_KEY_ANNO;
		Annotation[][] as = method.getParameterAnnotations();
		for(int i = 0; i < as.length; i++) {
			for(Annotation anno : as[i]) {
				if(anno instanceof MultiMode) {
					return i;
				}
			}
		}
		//查找实现类的方法上是否有注解
		if(target != null) {
			try {
				index = lookupMultiIndex(null, target.getClass().getMethod(method.getName(), 
						method.getParameterTypes()));
			} catch (NoSuchMethodException e) {
				
			} catch (SecurityException e) {
				
			}
		}
		return index;
	}
	
}
