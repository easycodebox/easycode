package com.easycodebox.common.log;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.easycodebox.common.lang.Symbol;

/**
 * 
 * 功能和ch.qos.logback.ext.spring.web.LogbackConfigListener类一样。为了让log的配置文件获取当前项目的根路径。
 * @author WangXiaoJin
 *
 */
public class LogConfigListener implements ServletContextListener {

	private static final String WEB_APP_ROOT_KEY = "webAppRootKey";
	
	private static final String DEFAULT_WEB_APP_ROOT_KEY = "webapp.root";
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext sc = sce.getServletContext();
		String key = sc.getInitParameter(WEB_APP_ROOT_KEY),
				root = sc.getRealPath(Symbol.SLASH);
		key = key == null ? DEFAULT_WEB_APP_ROOT_KEY : key;
		
		String oldValue = System.getProperty(key);
		if (oldValue != null && !oldValue.equals(root)) {
			throw new IllegalStateException(
				"Web app root system property already set to different value: '" +
				key + "' = [" + oldValue + "] instead of [" + root + "] - " +
				"Choose unique values for the 'webAppRootKey' context-param in your web.xml files!");
		}
		System.setProperty(key, root);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		String key = sce.getServletContext().getInitParameter(WEB_APP_ROOT_KEY);
		key = key == null ? DEFAULT_WEB_APP_ROOT_KEY : key;
		System.getProperties().remove(key);
	}

}
