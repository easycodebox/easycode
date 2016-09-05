package com.easycodebox.common.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存历史请求URL <br>
 * 1、当用到弹出框 新增、修改后需要刷新列表页面时，需要在列表controller方法上加上此注解 <br>
 * 2、当页面上有返回按钮，返回上一次缓存的地址时，在需要缓存的controller方法上加上此注解<br>
 * <b>注：新版通过浏览器端sessionStorage实现此功能</b> 
 * @author WangXiaoJin
 *
 */
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.METHOD)
@Inherited
@Deprecated
public @interface CacheHisUri {
	
	
}
