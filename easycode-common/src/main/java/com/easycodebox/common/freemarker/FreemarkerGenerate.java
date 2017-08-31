package com.easycodebox.common.freemarker;

import com.easycodebox.common.processor.Processor;
import com.easycodebox.common.validate.Assert;
import freemarker.template.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.*;

import java.io.*;

/**
 * 快速构建Freemarker生成文件功能类
 * @author WangXiaoJin
 *
 */
public class FreemarkerGenerate implements Processor, ResourceLoaderAware {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private ResourceLoader resourceLoader = new DefaultResourceLoader();
	
	private Configuration configuration;
	
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
		Assert.notNull(configuration);
		Assert.notBlank(ftlPath);
		Assert.notBlank(outputPath);
		Writer out = null;
		try {
			log.info("Start generate file '{}' by freemarker.", outputPath);
			Template tpl = configuration.getTemplate(ftlPath);
			Resource resource = resourceLoader.getResource(outputPath);
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resource.getFile()), encoding));
			tpl.process(dataModel, out);
			log.info("Generate file '{}' successfully by freemarker.", outputPath);
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
	
	public Configuration getConfiguration() {
		return configuration;
	}
	
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
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
	
	public Object getDataModel() {
		return dataModel;
	}
	
	public void setDataModel(Object dataModel) {
		this.dataModel = dataModel;
	}
}
