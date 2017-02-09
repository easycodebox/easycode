package com.easycodebox.common.tag;

import com.easycodebox.common.spring.BeanFactory;
import com.easycodebox.common.validate.Assert;

import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author WangXiaoJin
 * 
 */
public class ConvertIdTag extends TagExt {
	
	/**
	 * ID转换器
	 */
	private static IdConverterMap converterMap;
	
	private String module;
	/**
	 * 因父类已有id属性，所以用cid - convert id
	 */
	private Object cid;
	private String prop;
	
	@Override
	protected void init() {
		super.init();
		module = prop = null;
		cid = null;
		if (converterMap == null) {
			converterMap = BeanFactory.getBean(IdConverterMap.class);
		}
	}
	
	@Override
	public int doStartTag() throws JspException {
		try {
			String data = converterMap.convert(module, cid, prop);
			if(data != null)
				pageContext.getOut().append(data);
		} catch (IOException e) {
			log.error("IOException.", e);
		}
		return super.doStartTag();
	}

	/**
	 * ID转换成value的转换器Map
	 * @author WangXiaoJin
	 *
	 */
	public interface IdConverterMap {
		
		/**
		 * 
		 * @param module （可选） ID所属的模块，null表明用默认模块，实现类需要考虑此情况
		 * @param id 
		 * @param prop （可选） 某些情况下需要提供对象的属性名，特别是提供不同的属性名显示不同值的场景
		 * @return
		 */
		String convert(String module, Object id, String prop);
		
	}
	
	/**
	 * ID转换器Map默认实现
	 * @author WangXiaoJin
	 *
	 */
	public static class DefaultIdConverterMap implements IdConverterMap {

		private String defaultModule;

		private ConcurrentMap<String, IdConverter> maps = new ConcurrentHashMap<>(4);
		
		@Override
		public String convert(String module, Object id, String prop) {
			if (module == null)
				module = defaultModule;
			IdConverter converter = maps.get(module);
			Assert.notNull(converter, "Can't find corresponding module : {0}.", module);
			Object val = converter.convert(id, prop);
			return val == null ? null : val.toString();
		}
		
		/**
		 * 设置转换器Map
		 * @param map
		 */
		public void setConverterMap(Map<String, IdConverter> map) {
			if (map != null) {
				maps.putAll(map);
			}
		}
		
		/**
		 * 添加转换器
		 * @param key
		 * @param converter
		 */
		public IdConverter addIdConverter(String key, IdConverter converter) {
			return maps.put(key, converter);
		}

		public String getDefaultModule() {
			return defaultModule;
		}

		public void setDefaultModule(String defaultModule) {
			this.defaultModule = defaultModule;
		}
		
	}
	
	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public Object getCid() {
		return cid;
	}

	public void setCid(Object cid) {
		this.cid = cid;
	}

	public String getProp() {
		return prop;
	}

	public void setProp(String prop) {
		this.prop = prop;
	}
	
}
