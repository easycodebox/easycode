package com.easycodebox.common.idconverter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * IdConverter注册器
 * @author WangXiaoJin
 */
public class IdConverterRegistry {
	
	private String defaultModule;
	
	private ConcurrentMap<String, IdConverter> maps = new ConcurrentHashMap<>(4);
	
	public IdConverter getIdConverter(String module) {
		return getIdConverter(module, false);
	}
	
	/**
	 * 当module参数为null或者没有找到指定module的IdConverter，则返回defaultModule对应的IdConverter
	 * @param module
	 * @param useDefaultModule
	 * @return
	 */
	public IdConverter getIdConverter(String module, boolean useDefaultModule) {
		IdConverter converter = null;
		if (module != null) {
			converter = maps.get(module);
		}
		if (converter == null && useDefaultModule && defaultModule != null) {
			converter = maps.get(defaultModule);
		}
		return converter;
	}
	
	public void register(Map<String, IdConverter> map) {
		if (map != null) {
			maps.putAll(map);
		}
	}
	
	public void setConverterMap(Map<String, IdConverter> map) {
		register(map);
	}
	
	/**
	 * 注册转换器
	 * @param module
	 * @param converter
	 */
	public IdConverter register(String module, IdConverter converter) {
		return maps.put(module, converter);
	}
	
	public String getDefaultModule() {
		return defaultModule;
	}
	
	public void setDefaultModule(String defaultModule) {
		this.defaultModule = defaultModule;
	}
	
}
