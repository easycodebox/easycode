package com.easycodebox.common.freemarker;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.core.TemplateDateFormatFactory;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

/**
 * @author WangXiaoJin
 *
 */
public class ConfigurationFactory {
	
	//private static final Logger log = LoggerFactory.getLogger(ConfigurationFactory.class);
	
	/**
	 * 相对于项目的classes路径而言
	 */
	private volatile static Configuration INSTANCE = null;
	/**
	 * 相当于web项目的根路径
	 */
	private volatile static Configuration WEB_INSTANCE = null;
	
	/**
	 * 返回classes为加载器的Configuration,根路径为classes路径
	 * @param props 可以传null，为null时默认配置使用{@link FreemarkerProperties#instance()}
	 * @return
	 * @throws TemplateException
	 */
	public static Configuration instance(FreemarkerProperties props) throws TemplateException {
		if (INSTANCE == null) {
			synchronized (ConfigurationFactory.class) {
				if (INSTANCE == null) {
					INSTANCE = produce(props, null);
				}
			}
		}
		return INSTANCE;
	}
	
	/**
	 * 返回web项目的Configuration，加载路径为项目的根路径
	 * @param props 可以传null，为null时默认配置使用{@link FreemarkerProperties#instance()}
	 * @param servletContext
	 * @return
	 * @throws TemplateException
	 */
	public static Configuration instance(FreemarkerProperties props, ServletContext servletContext) throws TemplateException {
		if (servletContext == null) return instance(props);
		if (WEB_INSTANCE == null) {
			synchronized (ConfigurationFactory.class) {
				if (WEB_INSTANCE == null) {
					WEB_INSTANCE = produce(props, servletContext);
				}
			}
		}
		return WEB_INSTANCE;
	}
	
	/**
	 * 获取WebappTemplateLoader的Configuration，当WEB_INSTANCE = null时，抛NullPointerException
	 * @return
	 * @throws NullPointerException
	 */
	public static Configuration webInstance() throws NullPointerException {
		if(WEB_INSTANCE == null) 
			throw new NullPointerException("Web Configuration instance is null.");
		return WEB_INSTANCE;
	}
	
	private static Configuration produce(FreemarkerProperties props,
	                                     ServletContext servletContext) throws TemplateException {
		props = props == null ? FreemarkerProperties.instance() : props;
		
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_24);
		cfg.setDefaultEncoding(props.getDefaultEncoding());
		cfg.setSetting("locale", props.getLocale());
		cfg.setTemplateLoader(servletContext == null ? 
				new ClassTemplateLoader(ConfigurationFactory.class, props.getLoaderPath())
				: new WebappTemplateLoader(servletContext, props.getLoaderPath()));
		cfg.setSetting("template_update_delay", props.getTemplateUpdateDelay());
		cfg.setSetting("classic_compatible", props.getClassicCompatible());
		cfg.setSetting("number_format", props.getNumberFormat());
		cfg.setSetting("tag_syntax", props.getTagSyntax());
		
		/*
		cfg.setSetting("datetime_format", DATETIME_FORMAT);
		cfg.setSetting("date_format", DATE_FORMAT);
		cfg.setSetting("time_format", TIME_FORMAT);
		*/
		//因classic_compatible=true且对象的Date类型属性为null时，表达式${obj.birth?datetime}会抛异常，所以自定义格式化
		Map<String, TemplateDateFormatFactory> customDateFormats = new HashMap<>();
		customDateFormats.put("date", JavaTemplateDateFormatFactory.INSTANCE);
		cfg.setCustomDateFormats(customDateFormats);
		cfg.setDateTimeFormat("@date " + props.getDatetimeFormat());
		cfg.setDateFormat("@date " + props.getDateFormat());
		cfg.setTimeFormat("@date " + props.getTimeFormat());
		
		return cfg;
	}
	
}
