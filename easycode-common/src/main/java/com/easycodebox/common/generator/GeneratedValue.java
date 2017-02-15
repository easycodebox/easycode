package com.easycodebox.common.generator;

import java.lang.annotation.*;

/**
 * @author WangXiaoJin
 */
@Retention(RetentionPolicy.RUNTIME)  
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface GeneratedValue {
	
	Class<? extends GeneratorType> type();
	
	String key();
	
	Strategy strategy() default Strategy.ENUM;
	
	enum Strategy {
		/**
		 * {@link GeneratedValue#type()}为枚举类型，{@link GeneratedValue#key()}对应枚举值
		 */
		ENUM,
		/**
		 * 获取生成ID策略是通过静态方法获取，静态方法名为{@link GeneratedValue#key()}
		 */
		STATIC_METHOD
	}
	
}
