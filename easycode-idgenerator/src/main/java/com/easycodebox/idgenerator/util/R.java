package com.easycodebox.idgenerator.util;

import com.easycodebox.jdbc.Property;
import com.easycodebox.jdbc.entity.Entity;

import static com.easycodebox.jdbc.Property.instance;

/**
 * <b>直接运行{@link com.easycodebox.jdbc.res.GenerateBeanRes}类会自动生成R文件的。</b>
 * <p>如果是Entity类，会生成private static final Class<? extends Entity> entity = com.easycodebox.core.pojo.xxx.xxx.class
 * 和Property属性【public static final Property id = instance("id", entity)】
 * <p>如果只是普通的BO对象，则：public static final String id = "id";
 * @author WangXiaoJin
 *
 */
public class R {

	public static class IdGenerator {
	
		private static final Class<? extends Entity> entity = com.easycodebox.idgenerator.entity.IdGenerator.class;
		public static final Property 
			id = instance("id", entity),
			initialVal = instance("initialVal", entity),
			currentVal = instance("currentVal", entity),
			maxVal = instance("maxVal", entity),
			fetchSize = instance("fetchSize", entity),
			increment = instance("increment", entity),
			isCycle = instance("isCycle", entity),
			creator = instance("creator", entity),
			createTime = instance("createTime", entity),
			modifier = instance("modifier", entity),
			modifyTime = instance("modifyTime", entity),
			creatorName = instance("creatorName", entity),
			modifierName = instance("modifierName", entity);
	
	}
	
}
