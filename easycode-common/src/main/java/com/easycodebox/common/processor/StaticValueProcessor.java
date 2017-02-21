package com.easycodebox.common.processor;

import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.file.Resources;
import com.easycodebox.common.lang.DataConvert;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.reflect.Fields;
import com.easycodebox.common.validate.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 根据properties属性值刷入Class的静态属性中，如果Class静态属性有初始化值，且properties中没有，则会反刷进properties中
 * @author WangXiaoJin
 *
 */
public class StaticValueProcessor implements Processor {
	
	//private final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * 匹配${name}
	 */
	private final Pattern pattern = Pattern.compile("^\\s*\\$\\s*\\{\\s*([\\w\\._\\-]+)\\s*\\}\\s*$");
	
	private String[] packagesToScan;
	private Class[] classes;

	private Properties properties;
	
	@Override
	public Object process() {
		Assert.notNull(properties);
		try {
			List<Class<?>> pkgClasses = Resources.scanClass(packagesToScan);
			for(Class<?> clazz : pkgClasses) {
				processStaticValue(clazz);
			}
			if(classes != null) {
				for(Class clazz : classes) {
					processStaticValue(clazz);
				}
			}
		} catch (Exception e) {
			throw new BaseException(e);
		}
		return null;
	}
	
	private void processStaticValue(Class<?> clazz) throws IllegalAccessException {
		Field[] fields = clazz.getFields();
		if(fields == null) 
			return;
		for(Field field : fields) {
			if(!Modifier.isStatic(field.getModifiers()))
				continue;
			StaticValue value = field.getAnnotation(StaticValue.class);
			if(value != null) {
				String str = value.value();
				if(Strings.isNotBlank(str)) {
					Matcher m = pattern.matcher(str);
					if(m.find()) {
						String key = m.group(1),
							val = properties.getProperty(key);
						if(val != null) {
							Fields.writeStaticField(field,
									DataConvert.convertType(val, field.getType()));
						}else {
							Object originalVal = field.get(null);
							if(originalVal != null) {
								properties.setProperty(key, originalVal.toString());
							}
						}
					}
				}
			}
		}
	}
	
	public String[] getPackagesToScan() {
		return packagesToScan;
	}

	public void setPackagesToScan(String[] packagesToScan) {
		this.packagesToScan = packagesToScan;
	}
	
	public Class[] getClasses() {
		return classes;
	}
	
	public void setClasses(Class[] classes) {
		this.classes = classes;
	}
	
	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

}
