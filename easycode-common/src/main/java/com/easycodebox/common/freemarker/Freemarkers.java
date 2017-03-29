package com.easycodebox.common.freemarker;

import freemarker.cache.ClassTemplateLoader;
import freemarker.core.TemplateDateFormatFactory;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author WangXiaoJin
 */
public class Freemarkers {
	
	/**
	 * 创建默认配置的{@link Configuration}实例
	 * @return
	 */
	public static Configuration simpleCfg() throws TemplateException {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_24);
		cfg.setDefaultEncoding("UTF-8");
		cfg.setSetting("locale", "zh_CN");
		//cfg.setSetting("template_update_delay", "3600");
		cfg.setSetting("classic_compatible", "true");
		cfg.setSetting("number_format", "#.##");
		cfg.setSetting("tag_syntax", "auto_detect");
		cfg.setTemplateLoader(new ClassTemplateLoader(Freemarkers.class, "/"));
		
		//因classic_compatible=true且对象的Date类型属性为null时，表达式${obj.birth?datetime}会抛异常，所以自定义格式化
		Map<String, TemplateDateFormatFactory> customDateFormats = new HashMap<>();
		customDateFormats.put("date", JavaTemplateDateFormatFactory.INSTANCE);
		cfg.setCustomDateFormats(customDateFormats);
		
		cfg.setSetting("datetime_format", "@date yyyy-MM-dd HH:mm:ss");
		cfg.setSetting("date_format", "@date yyyy-MM-dd");
		cfg.setSetting("time_format", "@date HH:mm:ss");
		return cfg;
	}
	
}
