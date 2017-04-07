package com.easycodebox.upload.config;

import com.easycodebox.common.CommonProperties;
import com.easycodebox.common.error.ErrorContext;
import com.easycodebox.common.web.callback.Callbacks;
import com.easycodebox.common.web.springmvc.DefaultRequestMappingHandlerAdapter;
import com.easycodebox.common.web.springmvc.DefaultRequestMappingHandlerMapping;
import freemarker.ext.jsp.TaglibFactory.ClasspathMetaInfTldSource;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModelException;
import org.springframework.beans.BeansException;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.web.*;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Spring MVC 配置
 *
 * @author WangXiaoJin
 */
@Configuration
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
	
	/**
	 * 自定义ErrorAttributes，存储{@link com.easycodebox.common.error.CodeMsg} 属性
	 */
	@Bean
	public ErrorAttributes errorAttributes() {
		return new DefaultErrorAttributes() {
			@Override
			public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
				Map<String, Object> attributes = super.getErrorAttributes(requestAttributes, includeStackTrace);
				Throwable error = getError(requestAttributes);
				if (error instanceof ErrorContext) {
					ErrorContext ec = (ErrorContext) error;
					if (ec.getError() != null) {
						attributes.put("code", ec.getError().getCode());
						attributes.put("msg", ec.getError().getMsg());
						attributes.put("data", ec.getError().getData());
					}
				}
				return attributes;
			}
		};
	}
	
	/**
	 * 增加直接访问/error/目录下页面功能
	 */
	@Bean
	public BasicErrorController errorController(ErrorAttributes errorAttributes, ServerProperties serverProperties,
	                                            List<ErrorViewResolver> errorViewResolvers) {
		return new BasicErrorController(errorAttributes, serverProperties.getError(), errorViewResolvers) {
			
			@Autowired
			private CommonProperties commonProperties;
			
			@RequestMapping(path = "/{status}", produces = "text/html")
			public String status(@PathVariable String status) {
				return "error/" + status;
			}
			
			@Override
			@RequestMapping(produces = "text/html")
			public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
				if(request.getParameter(commonProperties.getDialogReqKey()) != null) {
					Map<String, Object> attributes = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.TEXT_HTML));
					String code = (String) attributes.get("code");
					String msg = (String) attributes.get("msg");
					Object data = attributes.get("data");
					Callbacks.callback(Callbacks.none(code, msg, data), null, response);
					return null;
				}
				return super.errorHtml(request, response);
			}
			
		};
	}
	
}
