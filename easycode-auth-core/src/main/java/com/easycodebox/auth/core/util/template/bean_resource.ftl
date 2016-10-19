package com.easycodebox.auth.core.util;

import static com.easycodebox.jdbc.Property.instance;

import com.easycodebox.jdbc.Property;
import com.easycodebox.jdbc.entity.Entity;

/**
 * <b>直接运行{@link com.easycodebox.auth.core.util.template.GenerateBeanRes}类会自动生成R文件的。</b><br>
 * <p>如果是Entity类，会生成private static final Class<? extends Entity> entity = com.easycodebox.core.pojo.xxx.xxx.class
 * 和Property属性【public static final Property id = instance("id", entity)】
 * <p>如果只是普通的BO对象，则：public static final String id = "id";
 * @author WangXiaoJin
 *
 */
public class R {

[#list data as bean]
	public static class ${bean.className} {
	
	[#if bean.entity]
		private static final Class<? extends Entity> entity = ${bean.clazz};
		public static final Property 
		[#list bean.properties as prop]
			[#if prop_has_next]
			${prop} = instance("${prop}", entity),
			[#else]
			${prop} = instance("${prop}", entity);
			[/#if]
		[/#list]
	[#else]
		public static final String 
		[#list bean.properties as prop]
			[#if prop_has_next]
			${prop} = "${prop}",
			[#else]
			${prop} = "${prop}";
			[/#if]
		[/#list]
	[/#if]
	
	}
	
[/#list]
	
}
