package com.easycodebox.auth.core.util.test;

import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.easycodebox.common.lang.reflect.ClassUtils;
import com.easycodebox.common.log.logback.LocateLogger;
import com.easycodebox.common.spring.ApplicationContextFactory;
import com.easycodebox.common.spring.BeanFactory;

public class BaseTest<T> extends LocateLogger {
	
	private static final ReentrantLock lock = new ReentrantLock();
	
	protected T bean;
	
	public BaseTest() {
		initContext();
		initBean();
	}
	
	@SuppressWarnings("resource")
	public void initContext() {
		/**
		 * 当同时执行N个test方法时，此类会被执行N次，但这N个实例会共享Spring上下文环境，所以下面的方法只能执行一次
		 */
		if(ApplicationContextFactory.newInstance() == null) {
			lock.lock();
			try {
				if (ApplicationContextFactory.newInstance() == null) {
					try{
						new ClassPathXmlApplicationContext("core.xml");
						System.out.println("-===========================-");
					}catch (Exception e) {
						LOG.error("run error!!!", e);
					}
				}
			} finally {
				lock.unlock();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void initBean() {
		try {
			bean = (T)BeanFactory.getBean(ClassUtils.getSuperClassGenricType(getClass()));
		} catch (Exception e) {
			if(e.getClass() != NoSuchBeanDefinitionException.class)
				LOG.error("run error!!!", e);
		}
	}

}
