package com.easycodebox.common.enums;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;

import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.file.Resources;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

/**
 * @author WangXiaoJin
 *
 */
public class EnumClassFactory implements InitializingBean {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	private static Map<String, Class<? extends Enum<?>>> enums = new ConcurrentHashMap<>();
	
	private Class<? extends Enum<?>>[] annotatedClasses;
	private String[] packagesToScan;
	
	public static Class<? extends Enum<?>> newInstance(String enumName) {
		return enums.get(enumName.toUpperCase());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() throws Exception {
		if(annotatedClasses != null && annotatedClasses.length > 0) {
			for(Class<? extends Enum<?>> clazz : annotatedClasses) {
				addEnum(clazz);
				log.debug("load enum class {0}", clazz);
			}
		}
		List<Class<?>> classes = Resources.scanClass(packagesToScan);
		for(Class<?> clazz : classes) {
			if(Enum.class.isAssignableFrom(clazz)) {
				addEnum((Class<? extends Enum<?>>)clazz);
				log.debug("load enum class {0}", clazz);
			}
		}
	}
	
	private void addEnum(Class<? extends Enum<?>> clazz) {
		String key = clazz.getSimpleName().toUpperCase();
		if(enums.containsKey(key))
			throw new BaseException("Enum key {0} already exists.Do not repeat to add.", key);
		enums.put(key, clazz);
	}
	
	public Class<?>[] getAnnotatedClasses() {
		return annotatedClasses;
	}

	public void setAnnotatedClasses(Class<? extends Enum<?>>[] annotatedClasses) {
		this.annotatedClasses = annotatedClasses;
	}

	public String[] getPackagesToScan() {
		return packagesToScan;
	}

	public void setPackagesToScan(String[] packagesToScan) {
		this.packagesToScan = packagesToScan;
	}
	
}
