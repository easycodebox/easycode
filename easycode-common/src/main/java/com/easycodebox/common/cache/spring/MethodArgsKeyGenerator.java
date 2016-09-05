package com.easycodebox.common.cache.spring;

import java.lang.reflect.Method;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;

/**
 * Spring Cache默认使用SimpleKeyGenerator。
 * 此KeyGenerator会把MethodName最为key的一部分
 * @author WangXiaoJin
 *
 */
public class MethodArgsKeyGenerator implements KeyGenerator {

	@Override
	public Object generate(Object target, Method method, Object... params) {
		params = ArrayUtils.add(params, 0, method.getName());
		if (params.length == 1) {
			return params[0];
		}
		return new SimpleKey(params);
	}
	
}
