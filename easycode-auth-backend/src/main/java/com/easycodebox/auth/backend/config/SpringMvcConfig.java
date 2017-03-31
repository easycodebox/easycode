package com.easycodebox.auth.backend.config;

import com.easycodebox.common.web.springmvc.DefaultRequestMappingHandlerAdapter;
import com.easycodebox.common.web.springmvc.DefaultRequestMappingHandlerMapping;
import freemarker.ext.jsp.TaglibFactory.ClasspathMetaInfTldSource;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModelException;
import org.springframework.beans.BeansException;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.web.*;
import org.springframework.context.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Spring MVC 配置
 *
 * @author WangXiaoJin
 */
@Configuration
@ComponentScan(basePackages = "com.easycodebox.idgenerator.controller")
@SuppressWarnings("Duplicates")
public class SpringMvcConfig {
	
	@Bean
	public WebMvcRegistrations webMvcRegistrations() {
		return new WebMvcRegistrationsAdapter() {
			@Override
			public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
				DefaultRequestMappingHandlerMapping mapping = new DefaultRequestMappingHandlerMapping();
				mapping.setControllerPostfix("Controller");
				mapping.setExcludePatterns(new String[]{
						"/js/**",
						"/css/**",
						"/imgs/**"
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
	
	/**
	 * 修改MetaInfTldSources加载Taglib规则 - 默认加载/WEB-INF/lib下面的jar <br/>
	 * 集成Spring Boot后没有WEB-INF目录，所以找不到原有的tld文件，修改成{@link ClasspathMetaInfTldSource}
	 * 来加载所有的tld文件
	 * @return
	 */
	@Bean
	public static BeanPostProcessor freeMarkerConfigPostProcessor(final Map properties) {
		return new BeanPostProcessor() {
			@Override
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
				return bean;
			}
			
			@Override
			public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
				if (bean instanceof FreeMarkerConfig) {
					FreeMarkerConfig config = (FreeMarkerConfig) bean;
					//修改MetaInfTldSources加载Taglib规则 - 默认加载/WEB-INF/lib下面的jar
					List<ClasspathMetaInfTldSource> tldSources = Collections.singletonList(new ClasspathMetaInfTldSource(Pattern.compile(".*")));
					config.getTaglibFactory().setMetaInfTldSources(tldSources);
					//设置Freemarker全局变量
					try {
						config.getConfiguration().setAllSharedVariables(new SimpleHash(properties, config.getConfiguration().getObjectWrapper()));
					} catch (TemplateModelException e) {
						throw new NotWritablePropertyException(FreeMarkerConfig.class, "configuration",
								"Invoke Configuration setAllSharedVariables method error.", e);
					}
				}
				return bean;
			}
		};
	}
	
	@Bean
	public BasicErrorController errorController(ErrorAttributes errorAttributes, ServerProperties serverProperties,
	                                            List<ErrorViewResolver> errorViewResolvers) {
		return new BasicErrorController(errorAttributes, serverProperties.getError(), errorViewResolvers) {
			
			@RequestMapping(path = "/{status}", produces = "text/html")
			public String status(@PathVariable String status) {
				return "error/" + status;
			}
			
		};
	}
	
}
