package com.easycodebox.jdbc;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记是否生成R资源文件
 * @author WangXiaoJin
 *
 */
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GenerateRes {

	boolean value() default true;
	
}
