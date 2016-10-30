package com.easycodebox.common.freemarker;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.processor.StaticValue;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.core.TemplateDateFormatFactory;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * @author WangXiaoJin
 *
 */
public class ConfigurationFactory {
	
	//private static final Logger log = LoggerFactory.getLogger(ConfigurationFactory.class);
	
	@StaticValue("${freemarker.default_encoding}")
	public static String DEFAULT_ENCODING = "UTF-8";
	
	@StaticValue("${freemarker.locale}")
	public static String LOCALE = "zh_CN";
	
	@StaticValue("${freemarker.loader_path}")
	public static String LOADER_PATH = Symbol.SLASH;
	
	@StaticValue("${freemarker.template_update_delay}")
	public static String TEMPLATE_UPDATE_DELAY = "3600";
	
	@StaticValue("${freemarker.classic_compatible}")
	public static String CLASSIC_COMPATIBLE = "true";

	@StaticValue("${freemarker.datetime_format}")
	public static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	@StaticValue("${freemarker.date_format}")
	public static String DATE_FORMAT = "yyyy-MM-dd";
	
	@StaticValue("${freemarker.time_format}")
	public static String TIME_FORMAT = "HH:mm:ss";
	
	@StaticValue("${freemarker.number_format}")
	public static String NUMBER_FORMAT = "#.##";
	
	@StaticValue("${freemarker.tag_syntax}")
	public static String TAG_SYNTAX = "auto_detect";
	
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
	 * @return
	 * @throws TemplateException
	 */
	public static Configuration instance() throws TemplateException {
		if (INSTANCE == null) {
			synchronized (ConfigurationFactory.class) {
				if (INSTANCE == null) {
					INSTANCE = produce(null);
				}
			}
		}
		return INSTANCE;
	}
	
	/**
	 * 返回web项目的Configuration，加载路径为项目的根路径
	 * @param servletContext
	 * @return
	 * @throws TemplateException
	 */
	public static Configuration instance(ServletContext servletContext) throws TemplateException {
		if (servletContext == null) return instance();
		if (WEB_INSTANCE == null) {
			synchronized (ConfigurationFactory.class) {
				if (WEB_INSTANCE == null) {
					WEB_INSTANCE = produce(servletContext);
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
	
	/**
	 * 传ServletContext参数，同时创建WebappTemplateLoader的Configuration
	 * @param servletContext
	 * @throws TemplateException
	 */
	public static void setServletContext(ServletContext servletContext) throws TemplateException {
		instance(servletContext);
	}
	
	private static Configuration produce(ServletContext servletContext) throws TemplateException {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_24);
		cfg.setDefaultEncoding(DEFAULT_ENCODING);
		cfg.setSetting("locale", LOCALE);
		cfg.setTemplateLoader(servletContext == null ? 
				new ClassTemplateLoader(ConfigurationFactory.class, LOADER_PATH)
				: new WebappTemplateLoader(servletContext, LOADER_PATH));
		cfg.setSetting("template_update_delay", TEMPLATE_UPDATE_DELAY);
		cfg.setSetting("classic_compatible", CLASSIC_COMPATIBLE);
		cfg.setSetting("number_format", NUMBER_FORMAT);
		cfg.setSetting("tag_syntax", TAG_SYNTAX);
		
		/*
		cfg.setSetting("datetime_format", DATETIME_FORMAT);
		cfg.setSetting("date_format", DATE_FORMAT);
		cfg.setSetting("time_format", TIME_FORMAT);
		*/
		//因classic_compatible=true且对象的Date类型属性为null时，表达式${obj.birth?datetime}会抛异常，所以自定义格式化
		Map<String, TemplateDateFormatFactory> customDateFormats = new HashMap<>();
		customDateFormats.put("date", JavaTemplateDateFormatFactory.INSTANCE);
		cfg.setCustomDateFormats(customDateFormats);
		cfg.setDateTimeFormat("@date " + DATETIME_FORMAT);
		cfg.setDateFormat("@date " + DATE_FORMAT);
		cfg.setTimeFormat("@date " + TIME_FORMAT);
		
		return cfg;
	}
	
}
