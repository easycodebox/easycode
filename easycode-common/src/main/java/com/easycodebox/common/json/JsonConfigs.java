package com.easycodebox.common.json;

import java.util.Date;

import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

/**
 * @author WangXiaoJin
 * 
 */
public class JsonConfigs extends JsonConfig {

	
	/**
	 * 为JsonConfig添加去除null属性的功能
	 * @param jc 为null时，自动创建一个默认的JsonConfig
	 * @return
	 */
	public JsonConfigs filterNullProp() {
		//去除字段为null值得属性
		this.setJsonPropertyFilter(new PropertyFilter(){

			@Override
			public boolean apply(Object source, String name, Object value) {
				if(value == null)
					return true;
				return false;
			}
			
		});
		return this;
	}
	
	/**
	 * 向JsonConfig注册[Java -> JSON] 处理Date类型的值
	 * @return
	 */
	public JsonConfigs dateJsonValue() {
		this.registerJsonValueProcessor(Date.class, 
				new DateJsonValueProcessor()
		);
		return this;
	}
	
	/**
	 * 向JsonConfig注册[Java -> JSON] 处理Date类型的值
	 * @param dataFormat yyyy-MM-dd
	 * @return
	 */
	public JsonConfigs dateJsonValue(String dataFormat) {
		if(dataFormat == null) return dateJsonValue();
		this.registerJsonValueProcessor(Date.class,
				new DateJsonValueProcessor(dataFormat) 
		);
		return this;
	}
	
	/**
	 * 向JsonConfig注册[Java -> JSON] 处理Date对象
	 * @return
	 */
	public JsonConfigs dateJsonBean() {
		this.registerJsonBeanProcessor(Date.class, 
				new DateJsonBeanProcessor()
		);
		return this;
	}
	
	/**
	 * 向JsonConfig注册[Java -> JSON] 处理Date对象
	 * @param dataFormat yyyy-MM-dd
	 * @return
	 */
	public JsonConfigs dateJsonBean(String dataFormat) {
		if(dataFormat == null) return dateJsonBean();
		this.registerJsonBeanProcessor(Date.class, 
				new DateJsonBeanProcessor(dataFormat) 
		);
		return this;
	}
	
	
}
