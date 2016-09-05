package com.easycodebox.common.freemarker;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.context.ServletContextAware;

import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.processor.Processor;
import com.easycodebox.common.validate.Assert;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 快速构建Freemarker生成文件功能类
 * @author WangXiaoJin
 *
 */
public class FreemarkerGenerate implements Processor, ResourceLoaderAware, ServletContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(FreemarkerGenerate.class);
	
	private ResourceLoader resourceLoader = new DefaultResourceLoader();
	
	private ServletContext servletContext;
	
	/**
	 * 使用的模板是否基于classPath
	 */
	private boolean classPathTpl = true;
	
	/**
	 * 模板路径
	 */
	private String ftlPath;
	/**
	 * 输出路径
	 */
	private String outputPath;
	
	private String encoding = "UTF-8";
	
	private Properties properties;
	
	@Override
	public void process() {
		Assert.notBlank(ftlPath);
		Assert.notBlank(outputPath);
		Writer out = null;
		try {
			LOG.info("Start generate file '{0}' by freemarker.", outputPath);
			Configuration cfg = ConfigurationFactory.instance(classPathTpl ? null : servletContext);
			Template tpl = cfg.getTemplate(ftlPath);
			Resource resource = resourceLoader.getResource(outputPath);
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resource.getFile()), encoding));
			tpl.process(properties, out);
			LOG.info("Generate file '{0}' successfully by freemarker.", outputPath);
		} catch (TemplateException | IOException e) {
			LOG.error("Freemarker generate file error.", e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
	
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public String getFtlPath() {
		return ftlPath;
	}

	public void setFtlPath(String ftlPath) {
		this.ftlPath = ftlPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isClassPathTpl() {
		return classPathTpl;
	}

	public void setClassPathTpl(boolean classPathTpl) {
		this.classPathTpl = classPathTpl;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

}
