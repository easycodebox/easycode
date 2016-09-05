package com.easycodebox.common.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.easycodebox.common.lang.dto.Entity;


/**
 * 建议改用spring提供的cache功能
 * @author WangXiaoJin
 * 
 */
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.METHOD)
@Inherited
@Deprecated
public @interface Cacheable {

	Class<? extends Entity> group();
	
	CacheOperation cacheOperation() default CacheOperation.NOP;
	
	/**
	 * 当CacheOperation = FLUSH_CACHE 时，除了清除group()的数据还要清除flushGroup()的数据
	 * @return
	 */
	Class<? extends Entity>[] flushGroup() default {};
	
}
