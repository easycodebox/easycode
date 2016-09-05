package com.easycodebox.common.web.springmvc;

import java.io.IOException;

import com.easycodebox.common.freemarker.ConfigurationFactory;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * 统一FreeMarker Configuration基础配置
 * @author WangXiaoJin
 *
 */
public class FreeMarkerConfigurer extends org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer {

	@Override
	protected Configuration newConfiguration() throws IOException,
			TemplateException {
		return (Configuration)ConfigurationFactory.instance().clone();
	}
	
}
