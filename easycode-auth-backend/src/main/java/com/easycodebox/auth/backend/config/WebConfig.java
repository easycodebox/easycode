package com.easycodebox.auth.backend.config;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.easycodebox.common.filter.ErrorContextFilter;
import com.easycodebox.common.filter.SecurityContextFilter;
import com.easycodebox.common.security.SecurityInfoHandler;
import com.easycodebox.common.sitemesh3.DefaultConfigurableSiteMeshFilter;
import org.springframework.boot.web.servlet.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Filter/Servlet/Listener配置
 * @author WangXiaoJin
 */
@Configuration
public class WebConfig {
	
	/**
	 * druid监控Filter
	 * @return
	 */
	@Bean
	public FilterRegistrationBean webStatFilter() {
		FilterRegistrationBean bean = new FilterRegistrationBean(new WebStatFilter());
		bean.addInitParameter("exclusions", "/css/*,/imgs/*,/js/*,/errors/*,/druid/*");
		return bean;
	}
	
	/**
	 * druid监控页面
	 * @return
	 */
	@Bean
	public ServletRegistrationBean statViewServlet() {
		return new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
	}
	
	@Bean
	public FilterRegistrationBean errorContextFilter() {
		FilterRegistrationBean bean = new FilterRegistrationBean(new ErrorContextFilter());
		bean.addInitParameter("defaultPage", "/errors/500.html");
		return bean;
	}
	
	@Bean
	public DelegatingFilterProxyRegistrationBean shiroFilterRegistration() {
		return new DelegatingFilterProxyRegistrationBean("shiroFilter");
	}
	
	/**
	 * Security Info拦截器
	 */
	@Bean
	public FilterRegistrationBean securityFilter(SecurityInfoHandler securityInfoHandler) {
		SecurityContextFilter filter = new SecurityContextFilter();
		filter.setSecurityInfoHandler(securityInfoHandler);
		return new FilterRegistrationBean(filter);
	}
	
	@Bean
	public FilterRegistrationBean sitemesh() {
		FilterRegistrationBean bean = new FilterRegistrationBean(new DefaultConfigurableSiteMeshFilter());
		bean.addInitParameter("decoratedKey", "decorated");
		bean.addInitParameter("configFile", "sitemesh3.xml");
		return bean;
	}
	
}
