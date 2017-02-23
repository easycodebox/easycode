package com.easycodebox.common.web.springmvc;

import com.easycodebox.common.freemarker.ConfigurationFactory;
import com.easycodebox.common.freemarker.FreemarkerProperties;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * 统一FreeMarker Configuration基础配置
 * @author WangXiaoJin
 *
 */
public class FreeMarkerConfigurer extends org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer {

	private FreemarkerProperties freemarkerProperties = FreemarkerProperties.instance();
	
	@Override
	protected Configuration newConfiguration() throws IOException,
			TemplateException {
		return (Configuration)ConfigurationFactory.instance(freemarkerProperties).clone();
	}
	
	public FreemarkerProperties getFreemarkerProperties() {
		return freemarkerProperties;
	}
	
	public void setFreemarkerProperties(FreemarkerProperties freemarkerProperties) {
		this.freemarkerProperties = freemarkerProperties;
	}
}
