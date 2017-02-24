package com.easycodebox.common.freemarker;

import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.processor.Processor;
import com.easycodebox.common.validate.Assert;
import freemarker.template.*;
import org.apache.commons.io.IOUtils;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.*;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.*;

/**
 * 快速构建Freemarker生成文件功能类
 * @author WangXiaoJin
 *
 */
public class FreemarkerGenerate implements Processor, ResourceLoaderAware, ServletContextAware {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private ResourceLoader resourceLoader = new DefaultResourceLoader();
	
	private ServletContext servletContext;
	
	private FreemarkerProperties freemarkerProperties = FreemarkerProperties.instance();
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
	
	private Object dataModel;
	
	@Override
	public Object process() {
		Assert.notBlank(ftlPath);
		Assert.notBlank(outputPath);
		Writer out = null;
		try {
			log.info("Start generate file '{0}' by freemarker.", outputPath);
			Configuration cfg = ConfigurationFactory.instance(freemarkerProperties, classPathTpl ? null : servletContext);
			Template tpl = cfg.getTemplate(ftlPath);
			Resource resource = resourceLoader.getResource(outputPath);
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resource.getFile()), encoding));
			tpl.process(dataModel, out);
			log.info("Generate file '{0}' successfully by freemarker.", outputPath);
		} catch (TemplateException | IOException e) {
			log.error("Freemarker generate file error.", e);
		} finally {
			IOUtils.closeQuietly(out);
		}
		return null;
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
	
	public Object getDataModel() {
		return dataModel;
	}
	
	public void setDataModel(Object dataModel) {
		this.dataModel = dataModel;
	}
	
	public FreemarkerProperties getFreemarkerProperties() {
		return freemarkerProperties;
	}
	
	public void setFreemarkerProperties(FreemarkerProperties freemarkerProperties) {
		this.freemarkerProperties = freemarkerProperties;
	}
}
