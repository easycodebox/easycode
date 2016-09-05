package com.easycodebox.common.cache.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 因Spring Cache没有提供timeToLive选项功能，所以拓展了此功能
 * @author WangXiaoJin
 *
 */
@Retention(RetentionPolicy.RUNTIME)  
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface Expire {
	
	/**
	 * 没有过期限制
	 */
	public static final int NO_LIMIT = -1;
	
	/**
	 * 自创建缓存起能存活多少秒
	 * @return
	 */
	int timeToLive() default NO_LIMIT;
	
	/**
	 * 两次访问最大的间隔时间，超过则过期
	 * @return
	 */
	int timeToIdle() default NO_LIMIT;
	
}
