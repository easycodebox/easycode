package classes_test;

import static com.easycodebox.common.jdbc.Property.instance;

import com.easycodebox.common.biz.pojo.Entity;
import com.easycodebox.common.jdbc.Property;

public class R {

	public static class Generator {
	
		private static final Class<? extends Entity> entity = com.easycodebox.core.pojo.sys.Generator.class;
		public static final Property 
			generatorType = instance("generatorType", entity),
			initialVal = instance("initialVal", entity),
			currentVal = instance("currentVal", entity),
			maxVal = instance("maxVal", entity),
			fetchSize = instance("fetchSize", entity),
			increment = instance("increment", entity),
			isCycle = instance("isCycle", entity),
			creator = instance("creator", entity),
			createTime = instance("createTime", entity),
			modifier = instance("modifier", entity),
			modifyTime = instance("modifyTime", entity);
	
	}
	
	public static class ShopBo {
	
		public static final String 
			id = "id",
			name = "name",
			status = "status",
			address = "address",
			person = "person";
	
	}
	
	
}
