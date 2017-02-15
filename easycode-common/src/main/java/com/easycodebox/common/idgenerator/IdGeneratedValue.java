package com.easycodebox.common.idgenerator;

import java.lang.annotation.*;

/**
 * @author WangXiaoJin
 */
@Retention(RetentionPolicy.RUNTIME)  
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface IdGeneratedValue {
	
	Class<? extends IdGeneratorType> type();
	
	String key();
	
	Strategy strategy() default Strategy.ENUM;
	
	enum Strategy {
		/**
		 * {@link IdGeneratedValue#type()}为枚举类型，{@link IdGeneratedValue#key()}对应枚举值
		 */
		ENUM,
		/**
		 * 获取生成ID策略是通过静态方法获取，静态方法名为{@link IdGeneratedValue#key()}
		 */
		STATIC_METHOD
	}
	
}
