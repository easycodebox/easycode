package com.easycodebox.auth.backend.config;

import com.easycodebox.common.freemarker.FreemarkerGenerate;
import com.easycodebox.common.freemarker.FreemarkerProperties;
import com.easycodebox.common.web.springmvc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Spring MVC 配置
 * @author WangXiaoJin
 */
@Configuration
@ComponentScan(basePackages = {
		"com.easycodebox.auth.backend.controller",
		"com.easycodebox.idgenerator.controller"
})
public class ServletConfig extends DelegatingWebMvcConfiguration {
	
	@Autowired
	private FreemarkerProperties freemarkerProperties;
	
	@Autowired
	private FormattingConversionService conversionService;
	
	@Resource
	private Map properties;
	
	/**
	 * 生成JS的配置文件
	 */
	@Bean(initMethod = "process")
	public FreemarkerGenerate freemarkerGenerate() {
		FreemarkerGenerate generate = new FreemarkerGenerate();
		generate.setFtlPath("/config-js.ftl");
		generate.setOutputPath("/js/config.js");
		generate.setFreemarkerProperties(freemarkerProperties);
		generate.setDataModel(properties);
		return generate;
	}
	
	@Override
	protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
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
	 * freemarker的配置 <p/>
	 * 因为依赖easycode-idgenerator模块，用到了此模块的模板，所以增加了【classpath:/META-INF/resources/】loaderPath。 <p/>
	 * 如果模板统一存于相同路径下，则使用FreeMarkerViewResolver的suffix属性配置。
	 */
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
	@Override
	protected void configureViewResolvers(ViewResolverRegistry registry) {
		super.configureViewResolvers(registry);
		FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
		resolver.setSuffix(".html");
		resolver.setOrder(1);
		resolver.setContentType("text/html;charset=utf-8");
		resolver.setViewClass(FreeMarkerView.class);
		resolver.setExposeRequestAttributes(true);
		resolver.setExposeSpringMacroHelpers(true);
		resolver.setRequestContextAttribute("request");
		registry.viewResolver(resolver);
	}
	
	/**
	 * 启用默认Servlet
	 */
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
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
	
	@Override
	protected RequestMappingHandlerAdapter createRequestMappingHandlerAdapter() {
		DefaultRequestMappingHandlerAdapter adapter = new DefaultRequestMappingHandlerAdapter();
		adapter.setAutoView(true);
		return adapter;
	}
	
	@Bean
	@Override
	public FormattingConversionService mvcConversionService() {
		addFormatters(conversionService);
		return conversionService;
	}
	
	@Override
	protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
		stringHttpMessageConverter.setSupportedMediaTypes(MediaType.parseMediaTypes("text/plain;charset=UTF-8"));
		MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		jackson2HttpMessageConverter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/json;charset=UTF-8"));
		converters.add(stringHttpMessageConverter);
		converters.add(jackson2HttpMessageConverter);
	}
	
}
