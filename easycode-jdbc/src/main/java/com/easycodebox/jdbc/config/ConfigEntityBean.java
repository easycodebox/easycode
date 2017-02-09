package com.easycodebox.jdbc.config;

import com.easycodebox.common.file.Resources;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import java.util.List;

/**
 * @author WangXiaoJin
 * 
 */
public class ConfigEntityBean {

	private Class<?>[] annotatedClasses;
	private String[] packagesToScan;

	@PostConstruct
	public void init() throws Exception {
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
