package com.easycodebox.common.freemarker;

import com.easycodebox.common.lang.Strings;
import freemarker.core.TemplateDateFormatFactory;
import freemarker.template.Configuration;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

import java.util.HashMap;
import java.util.Map;

/**
 * 因classic_compatible=true且对象的Date类型属性为null时，表达式${obj.birth?datetime}会抛异常，
 * 所以增加{@link JavaTemplateDateFormatFactory}自定义格式化日期类
 * @author WangXiaoJin
 */
public class ConfigurationPostProcessor implements BeanPostProcessor, Ordered {
	
	public static final String DEFAULT_FORMAT_KEY = "date";
	
	private int order = Ordered.HIGHEST_PRECEDENCE;
	
	private String formatKey;
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof Configuration) {
			Configuration cfg = (Configuration) bean;
			Map<String, TemplateDateFormatFactory> formats = (Map<String, TemplateDateFormatFactory>) cfg.getCustomDateFormats();
			if (MapUtils.isEmpty(formats)) {
				formats = new HashMap<>();
			}
			formats.put(Strings.isEmpty(formatKey) ? DEFAULT_FORMAT_KEY : formatKey, JavaTemplateDateFormatFactory.INSTANCE);
			cfg.setCustomDateFormats(formats);
		}
		return bean;
	}
	
	@Override
	public int getOrder() {
		return order;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	public String getFormatKey() {
		return formatKey;
	}
	
	public void setFormatKey(String formatKey) {
		this.formatKey = formatKey;
	}
}
