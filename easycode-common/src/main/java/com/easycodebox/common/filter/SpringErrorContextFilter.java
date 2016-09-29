package com.easycodebox.common.filter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.easycodebox.common.error.ExceptionHandler;
import com.easycodebox.common.lang.StringUtils;

public class SpringErrorContextFilter extends ErrorContextFilter {

	private WebApplicationContext context;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
		String exceptionHandlerBeanName = filterConfig.getInitParameter("exceptionHandlerBeanName");
		if (StringUtils.isNotBlank(exceptionHandlerBeanName)) {
			initWebContext(filterConfig.getServletContext());
			exceptionHandler = context.getBean(exceptionHandlerBeanName.trim(), ExceptionHandler.class);
		}
	}
	
	private void initWebContext(ServletContext sc) {
		if (context == null) {
			context = WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
		}
	}
	
}
