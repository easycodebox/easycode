package com.easycodebox.common.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.validate.Assert;

/**
 * @author WangXiaoJin
 *
 */
public class BeanFactory {
	
	public static Object getBean(String name)  throws BeansException {
		ApplicationContext context = ApplicationContextFactory.newInstance();
		Assert.notNull(context);
		return context.getBean(name);
	}
	
	public static <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		ApplicationContext context = ApplicationContextFactory.newInstance();
		Assert.notNull(context);
		return context.getBean(name, requiredType);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> requiredType) throws BeansException {
		Object bean = null;
		try {
			bean = getBean(StringUtils.uncapitalize(requiredType.getSimpleName()));
		} catch (Exception e) {
			ApplicationContext context = ApplicationContextFactory.newInstance();
			Assert.notNull(context);
			bean = context.getBean(requiredType);
		}
		return (T)bean;
	}
	
}
