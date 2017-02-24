package com.easycodebox.auth.backend.config;

import com.easycodebox.common.freemarker.FreemarkerGenerate;
import com.easycodebox.common.freemarker.FreemarkerProperties;
import com.easycodebox.common.web.springmvc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.util.*;

/**
 * Spring MVC 配置
 * @author WangXiaoJin
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
		"com.easycodebox.auth.backend.controller",
		"com.easycodebox.idgenerator.controller"
})
public class ServletConfig extends WebMvcConfigurerAdapter {
	
	@Autowired
	private FreemarkerProperties freemarkerProperties;
	
	@Autowired
	private ConversionService conversionService;
	
	@Autowired
	private Map properties;
	
	/**
	 * 生成JS的配置文件
	 */
	@Bean
	public FreemarkerGenerate freemarkerGenerate() {
		FreemarkerGenerate generate = new FreemarkerGenerate();
		generate.setFtlPath("/config-js.ftl");
		generate.setOutputPath("/js/config.js");
		generate.setFreemarkerProperties(freemarkerProperties);
		generate.setDataModel(properties);
		return generate;
	}
	
	@Bean
	public DefaultRequestMappingHandlerMapping defaultRequestMappingHandlerMapping() {
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
	
	@Bean
	public AcceptHeaderLocaleResolver localeResolver() {
		return new AcceptHeaderLocaleResolver();
	}
	
	/**
	 * JSP配置
	 */
	/*@Bean
	public InternalResourceViewResolver internalResourceViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/jsp/");
		resolver.setSuffix(".jsp");
		resolver.setOrder(2);
		resolver.setContentType("text/html;charset=utf-8");
		resolver.setViewClass(JstlView.class);
		return resolver;
	}*/
	
	@Bean
	public FreeMarkerConfigurer freemarkerConfig() {
		FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
		configurer.setFreemarkerProperties(freemarkerProperties);
		configurer.setFreemarkerVariables(properties);
		configurer.setTemplateLoaderPaths("/", "/WEB-INF/pages/", "classpath:/META-INF/resources/");
		configurer.setDefaultEncoding(freemarkerProperties.getDefaultEncoding());
		return configurer;
	}
	
	/**
	 * freemarker视图设置
	 */
	@Bean
	public FreeMarkerViewResolver viewResolver() {
		FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
		resolver.setSuffix(".html");
		resolver.setOrder(1);
		resolver.setContentType("text/html;charset=utf-8");
		resolver.setViewClass(FreeMarkerView.class);
		resolver.setExposeRequestAttributes(true);
		resolver.setExposeSpringMacroHelpers(true);
		resolver.setRequestContextAttribute("request");
		return resolver;
	}
	
	/*
	<!-- Support static resource -->
	<mvc:default-servlet-handler/>
	*/
	
	/**
	 * 文件上传 <p/>
	 * maxUploadSize="32505856" ==> 上传文件大小限制为31M，31*1024*1024
	 * @return
	 */
	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setDefaultEncoding("UTF-8");
		resolver.setMaxUploadSize(32505856);
		resolver.setMaxInMemorySize(4096);
		return resolver;
	}
	
	/* -------- 异常处理 ------------ */
	/*@Bean
	public FreeMarkerViewResolver errorViewResolver() {
		FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
		resolver.setPrefix("/errors/");
		resolver.setSuffix(".html");
		resolver.setOrder(2);
		resolver.setContentType("text/html;charset=utf-8");
		resolver.setViewClass(FreeMarkerView.class);
		resolver.setExposeRequestAttributes(true);
		resolver.setExposeSpringMacroHelpers(true);
		resolver.setRequestContextAttribute("request");
		return resolver;
	}
	
	@Bean
	public DefaultMappingExceptionResolver exceptionResolver() {
		Properties props = new Properties();
		props.setProperty("com.easycodebox.common.error.ErrorContext", "500");
		props.setProperty("java.lang.Exception", "500");
		DefaultMappingExceptionResolver resolver = new DefaultMappingExceptionResolver();
		resolver.setDefaultErrorView("500");
		resolver.setExceptionMappings(props);
		return resolver;
	}*/
	
	@Bean
	public DefaultRequestMappingHandlerAdapter defaultRequestMappingHandlerAdapter() {
		DefaultRequestMappingHandlerAdapter adapter = new DefaultRequestMappingHandlerAdapter();
		ConfigurableWebBindingInitializer webBindingInitializer = new ConfigurableWebBindingInitializer();
		webBindingInitializer.setConversionService(conversionService);
		adapter.setWebBindingInitializer(webBindingInitializer);
		adapter.setAutoView(true);
		
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
		stringHttpMessageConverter.setSupportedMediaTypes(MediaType.parseMediaTypes("text/plain;charset=UTF-8"));
		MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		jackson2HttpMessageConverter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/json;charset=UTF-8"));
		messageConverters.add(stringHttpMessageConverter);
		messageConverters.add(jackson2HttpMessageConverter);
				
		adapter.setMessageConverters(messageConverters);
		return adapter;
	}
	
}
