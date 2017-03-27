package com.easycodebox.auth.backend.config;

import com.easycodebox.common.web.springmvc.DefaultRequestMappingHandlerAdapter;
import com.easycodebox.common.web.springmvc.DefaultRequestMappingHandlerMapping;
import org.springframework.boot.autoconfigure.web.WebMvcRegistrations;
import org.springframework.boot.autoconfigure.web.WebMvcRegistrationsAdapter;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Spring MVC 配置
 *
 * @author WangXiaoJin
 */
@Configuration
@ComponentScan(basePackages = "com.easycodebox.idgenerator.controller")
@SuppressWarnings("Duplicates")
public class SpringMvcConfig {
	
	@Resource
	private Map properties;
	
	/**
	 * 生成JS的配置文件
	 */
	/*@Bean(initMethod = "process")
	@Profile("!" + Constants.INTEGRATION_TEST_KEY)
	public FreemarkerGenerate freemarkerGenerate(freemarker.template.Configuration configuration) {
		FreemarkerGenerate generate = new FreemarkerGenerate();
		generate.setFtlPath("/config-js.ftl");
		generate.setOutputPath("/js/config.js");
		generate.setConfiguration(configuration);
		generate.setDataModel(properties);
		return generate;
	}*/
	
	@Bean
	public WebMvcRegistrations webMvcRegistrations() {
		return new WebMvcRegistrationsAdapter() {
			@Override
			public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
				DefaultRequestMappingHandlerMapping mapping = new DefaultRequestMappingHandlerMapping();
				mapping.setControllerPostfix("Controller");
				mapping.setExcludePatterns(new String[]{
						"/**/*.js",
						"/**/*.css",
						"/imgs/**",
						"/WEB-INF/common/**",
						"/errors/**"
				});
				return mapping;
			}
			
			@Override
			public RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
				DefaultRequestMappingHandlerAdapter adapter = new DefaultRequestMappingHandlerAdapter();
				adapter.setAutoView(true);
				return adapter;
			}
		};
	}
	
}
