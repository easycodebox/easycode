package com.easycodebox.auth.core.config;

import com.easycodebox.auth.core.config.CoreProperties;
import com.easycodebox.common.CommonProperties;
import com.easycodebox.common.Named;
import com.easycodebox.common.filter.SecurityContextFilter;
import com.easycodebox.common.freemarker.FreemarkerProperties;
import com.easycodebox.common.security.SecurityInfoHandler;
import com.easycodebox.common.servlet.ServletContextAttrRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Servlet 配置 <br/>
 * 因为此配置为通用配置，所以放在core包里
 * @author WangXiaoJin
 */
@Configuration
public class ServletConfig {
	
	/**
	 * 配置Bean注册进ServletContext Attributes
	 */
	@Bean
	public ServletContextAttrRegistry servletContextAttrRegistry(CommonProperties commonProperties,
			FreemarkerProperties freemarkerProperties, CoreProperties coreProperties) {
		ServletContextAttrRegistry registry = new ServletContextAttrRegistry();
		registry.setNameds(new Named[] {commonProperties, freemarkerProperties, coreProperties});
		return registry;
	}
	
	/**
	 * Security Info拦截器
	 */
	@Bean
	public SecurityContextFilter securityFilter(SecurityInfoHandler securityInfoHandler) {
		SecurityContextFilter filter = new SecurityContextFilter();
		filter.setSecurityInfoHandler(securityInfoHandler);
		return filter;
	}
	
}
