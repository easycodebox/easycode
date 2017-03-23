package com.easycodebox.auth;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.easycodebox.auth.backend.config.SpringMvcConfig;
import com.easycodebox.auth.core.config.CoreConfig;
import com.easycodebox.common.filter.ErrorContextFilter;
import com.easycodebox.common.filter.SecurityContextFilter;
import com.easycodebox.common.security.SecurityInfoHandler;
import com.easycodebox.common.sitemesh3.DefaultConfigurableSiteMeshFilter;
import com.easycodebox.login.config.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.filter.DelegatingFilterProxy;

/**
 * @author WangXiaoJin
 */
@Import({
		WsClientConfig.class, CoreConfig.class, ShiroConfig.class,
		SpringMvcConfig.class, WsServerConfig.class
})
@SpringBootApplication
public class Application {
	
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
		ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
		return bean;
	}
	
	@Bean
	public FilterRegistrationBean errorContextFilter() {
		FilterRegistrationBean bean = new FilterRegistrationBean(new ErrorContextFilter());
		bean.addInitParameter("defaultPage", "/errors/500.html");
		return bean;
	}
	
	@Bean
	public FilterRegistrationBean shiroFilterRegistration() {
		FilterRegistrationBean bean = new FilterRegistrationBean(new DelegatingFilterProxy("shiroFilter"));
		bean.addInitParameter("targetFilterLifecycle", "true");
		return bean;
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
	
	public static void main(String[] args) throws Exception {
		args = new String[] {
				"--debug",
				"--logging.level.root=DEBUG"
		};
		SpringApplication.run(Application.class, args);
	}
	
}
