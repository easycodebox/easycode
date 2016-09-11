package com.easycodebox.jdbc.config;

import java.util.List;

import javax.persistence.Entity;

import org.springframework.beans.factory.InitializingBean;

import com.easycodebox.common.file.Resources;

/**
 * @author WangXiaoJin
 * 
 */
public class ConfigPojoBean implements InitializingBean {

	private Class<?>[] annotatedClasses;
	private String[] packagesToScan;

	@Override
	public void afterPropertiesSet() throws Exception {
		if(annotatedClasses != null && annotatedClasses.length > 0) {
			for(Class<?> clazz : annotatedClasses) 
				Configuration.addAnnotatedClass(clazz);
		}
		
		List<Class<?>> classes = Resources.scanClass(packagesToScan);
		for(Class<?> clazz : classes) {
			if(clazz.getAnnotation(Entity.class) != null)
				Configuration.addAnnotatedClass(clazz);
		}
		Configuration.initTablesAssociate();
	}

	public Class<?>[] getAnnotatedClasses() {
		return annotatedClasses;
	}

	public void setAnnotatedClasses(Class<?>[] annotatedClasses) {
		this.annotatedClasses = annotatedClasses;
	}

	public String[] getPackagesToScan() {
		return packagesToScan;
	}

	public void setPackagesToScan(String[] packagesToScan) {
		this.packagesToScan = packagesToScan;
	}

}
