package com.easycodebox.auth.core.util.aop.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.easycodebox.auth.core.enums.ModuleType;
import com.easycodebox.common.enums.entity.LogLevel;

/**
 * 记录日志注解
 * @author WangXiaoJin
 *
 */
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.METHOD)
@Inherited
public @interface Log {

	LogLevel level() default LogLevel.INFO;
	
	String title() default "";
	
	ModuleType moduleType();
	
}
