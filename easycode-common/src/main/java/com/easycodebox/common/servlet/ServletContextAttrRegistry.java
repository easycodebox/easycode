package com.easycodebox.common.servlet;

import com.easycodebox.common.Init;
import com.easycodebox.common.Named;
import com.easycodebox.common.validate.Assert;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.web.context.ServletContextAware;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

/**
 * 注册attribute到ServletContext
 * @author WangXiaoJin
 */
public class ServletContextAttrRegistry implements ServletContextAware, Init {
	
	private ServletContext servletContext;
	
	private Named[] nameds;
	
	@Override
	@PostConstruct
	public void init() throws Exception {
		Assert.notNull(servletContext, "servletContext is null.");
		if (ArrayUtils.isNotEmpty(nameds)) {
			for (Named named : nameds) {
				servletContext.setAttribute(named.getName(), named);
			}
		}
	}
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public Named[] getNameds() {
		return nameds;
	}
	
	public void setNameds(Named[] nameds) {
		this.nameds = nameds;
	}
	
}
