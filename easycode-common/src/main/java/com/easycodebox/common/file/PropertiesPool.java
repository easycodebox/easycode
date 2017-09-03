package com.easycodebox.common.file;

import com.easycodebox.common.validate.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author WangXiaoJin
 * 
 */
public class PropertiesPool {
	
	private static final Logger log = LoggerFactory.getLogger(PropertiesPool.class);
	
	private static final String[] properties;
	private static final String[] xmls;
	private static Properties propertyFile = new Properties();
	
	private static final ReentrantLock lock = new ReentrantLock();
	private static Set<String> files = new HashSet<>();
	

	static {
		properties = new String[]{
			//"base.properties"
		};
		xmls = new String[]{
			
		};
		init();
	}
	
	private PropertiesPool(){
		
	}
	
	private static void init(){
		for(String fileStr : properties) {
			loadPropertiesFile(fileStr);
		}
		for(String xmlStr : xmls) {
			loadXMLFile(xmlStr);
		}
	}
	
	/**
	 * 加载properties文件
	 * @param resource 文件路径  相对项目而言
	 */
	public static void loadPropertiesFile(String resource) {
		loadPropertiesFile(resource, false, false);
	}
	
	/**
	 * 加载properties文件
	 * @param resource 文件路径
	 * @param absolute 表明resource是否是系统的绝对路径
	 * @param force 当指定文件加载过一次后，是否再次强制加载，并覆盖原有值
	 */
	public static void loadPropertiesFile(String resource, boolean absolute, boolean force) {
		Assert.notBlank(resource, "fileName can't be blank");
		lock.lock();
		try {
			boolean contains = files.contains(resource);
			if(force || !contains) {
				try {
					if(absolute)
						PropertiesUtils.loadAbsoluteFile(propertyFile, resource);
					else
						PropertiesUtils.loadFile(propertyFile, resource);
				} catch (IOException e) {
					log.error("Load file ({}) to Properties error!!", resource, e);
				}
				if(!contains)
					files.add(resource);
			}
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * 加载xml文件
	 * @param resource 文件路径  相对项目而言
	 */
	public static void loadXMLFile(String resource) {
		loadXMLFile(resource, false, false);
	}
	
	/**
	 * 加载xml文件
	 * @param resource 文件路径
	 * @param absolute 表明resource是否是系统的绝对路径
	 * @param force 当指定文件加载过一次后，是否再次强制加载，并覆盖原有值
	 */
	public static void loadXMLFile(String resource, boolean absolute, boolean force) {
		Assert.notBlank(resource, "fileName can't be blank");
		lock.lock();
		try {
			boolean contains = files.contains(resource);
			if(force || !contains) {
				try {
					if(absolute)
						PropertiesUtils.loadAbsoluteXmlFile(propertyFile, resource);
					else
						PropertiesUtils.loadXmlFile(propertyFile, resource);
				} catch (Exception e) {
					log.error("Load xml file ({}) to Properties error!!", resource, e);
				}
				if(!contains)
					files.add(resource);
			}
		} finally {
			lock.unlock();
		}
	}
	
	public static Object get(Object key) {
		return propertyFile.get(key);
	}
	
	public static Object get(Object key, String defaultValue) {
		Object val = propertyFile.get(key);
		if(val == null) val = defaultValue;
		return val;
	}
	
	public static String getProperty(String key) {
		return propertyFile.getProperty(key);
	}
	
	public static String getProperty(String key, String defaultValue) {
		return propertyFile.getProperty(key, defaultValue);
	}
	
	public static void addProperties(Properties properties) {
		if(properties != null) {
			propertyFile.putAll(properties);
		}
	}
	
	public static Properties getProperties() {
		return propertyFile;
	}
	
	public static void main(String[] args) {
		Properties propertyFile = new Properties();
		propertyFile.put("name", "1");
		propertyFile.put("name", "3");
		System.out.println(propertyFile.get("name"));
	}

}
