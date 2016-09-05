package com.easycodebox.common.cache.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表明此参数作为Spring Cache批量key
 * 此注解只能标注参数类型为Array、Collection
 * @author WangXiaoJin
 *
 */
@Retention(RetentionPolicy.RUNTIME)  
@Target({ElementType.PARAMETER})
@Inherited
public @interface MultiMode {
	
}
