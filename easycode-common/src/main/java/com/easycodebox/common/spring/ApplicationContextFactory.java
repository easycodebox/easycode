package com.easycodebox.common.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author WangXiaoJin
 * 
 */
public class ApplicationContextFactory implements ApplicationContextAware {

	private static ApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		ApplicationContextFactory.context = context;
	}

	public static ApplicationContext newInstance() {
		return context;
	}

	

}
