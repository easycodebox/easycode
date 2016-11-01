package com.easycodebox.jdbc.res;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.asm.ClassReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.easycodebox.common.freemarker.ConfigurationFactory;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.lang.reflect.ClassUtils;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.jdbc.GenerateRes;
import com.easycodebox.jdbc.entity.Entity;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author WangXiaoJin
 *
 */
public class GenerateBeanRes {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * 要生成资源文件的Bean对象所在的路径
	 */
	private String[] basePackages;
	
	private String resourcePattern = "**/*.class";
	
	private String template = "bean_resource.ftl";
	
	private String outputFile;
	
	/**
	 * 生成的R文件package名时，忽略的前缀信息
	 */
	private String[] ignorePrefixes = {"src/main/java/"};
	
	private TemplateLoader templateLoader;
	
	/**
	 * 生成资源文件
	 */
	public void generate() {
		Resource[] rs = new Resource[0];
		int lastIndex = rs.length;
		try {
			for(String basePackage : basePackages) {
				String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
						basePackage + Symbol.SLASH + resourcePattern;
				Resource[] tmp = new PathMatchingResourcePatternResolver()
					.getResources(packageSearchPath);
				if(tmp.length > 0) {
					lastIndex = rs.length;
					rs = Arrays.copyOf(rs, rs.length + tmp.length);
					System.arraycopy(tmp, 0, rs, lastIndex, tmp.length);
				}
			}
			
		} catch (IOException e) {
			log.error("generate value-object resource error.", e);
		}
		
		Writer out = null;
		try {
			Configuration cfg = ConfigurationFactory.instance();
			//方法1
			/*URL url = GenerateBeanRes.class.getResource("");
			File tlFile = new File(url.toURI());
			cfg.setDirectoryForTemplateLoading(tlFile);*/
			
			//方法2
			/*URL url = GenerateBeanRes.class.getResource("");
			cfg.setTemplateLoader(new FileTemplateLoader(new File(url.toURI())));*/
			
			//方法3
			cfg.setTemplateLoader(templateLoader == null ? new ClassTemplateLoader(GenerateBeanRes.class, Symbol.EMPTY) : templateLoader);
			
			//设置包装器，并将对象包装为数据模型
			Template tpl = cfg.getTemplate(template, "UTF-8");
			Map<String, Object> root = new LinkedHashMap<>();
			root.put("data", processRes2BeanData(rs));
			File outPutFile = new File(outputFile);
			if(!outPutFile.getParentFile().exists()) {
				outPutFile.getParentFile().mkdirs();
			}
			//设置R文件的package name
			String basePathOfOutput = new File(Symbol.EMPTY).getCanonicalPath();
			String packageName = outPutFile.getCanonicalPath().replaceFirst("\\Q" + basePathOfOutput + File.separator + "\\E", Symbol.EMPTY);
			for (String prefix : ignorePrefixes) {
				prefix =  FilenameUtils.separatorsToSystem(prefix);
				if (packageName.startsWith(prefix)) {
					packageName = packageName.substring(prefix.length());
				}
			}
			packageName = FilenameUtils.getPathNoEndSeparator(packageName).replaceAll("[\\\\/]", Symbol.PERIOD);
			root.put("packageName", packageName);
			
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outPutFile),"UTF-8"));
			if(log.isInfoEnabled()) {
				log.info("=== ********* Begin generate template *********** =====");
			}
			tpl.process(root, out);
			if(log.isInfoEnabled()) {
				log.info("=== ********* End generate template *********** =====");
			}
		} catch (IOException e) {
			log.error("generate value-object resource error.", e);
		} catch (TemplateException e) {
			log.error("generate value-object resource error.", e);
		}finally {
			IOUtils.closeQuietly(out);
		}
		
	}
	
	private List<BeanData> processRes2BeanData(Resource[] rs) {
		Assert.notEmpty(rs);
		List<BeanData> beanDatas = new ArrayList<>(rs.length);
		try {
			for(int i = 0; i < rs.length; i++) {
				Resource r = rs[i];
				if(r.isReadable()) {
					BeanData data = new BeanData();
					ClassReader cr = new ClassReader(r.getInputStream());
					String className = cr.getClassName();
					Class<?> clazz = ClassUtils.getClass(className
							.replace(Symbol.SLASH, Symbol.PERIOD));
					if (clazz.isAnnotation() || clazz.isAnonymousClass() || clazz.isArray() || clazz.isEnum()) {
						continue;
					}
					GenerateRes res = clazz.getAnnotation(GenerateRes.class);
					if (res != null && !res.value()) {
						continue;
					}
					List<String> properties = new ArrayList<String>();
					data.setClazz(clazz.getName() + ".class");
					data.setClassName(clazz.getSimpleName());
					if(ClassUtils.isAssignable(clazz, Entity.class))
						data.setEntity(true);
					else
						data.setEntity(false);
					for(Class<?> curClazz = clazz; 
							curClazz != null && curClazz != Object.class; 
							curClazz = curClazz.getSuperclass()) {
						Field[] fields = curClazz.getDeclaredFields();
						for(Field f : fields) {
							if(Modifier.isFinal(f.getModifiers())
									|| Modifier.isStatic(f.getModifiers())) 
								continue;
							properties.add(f.getName());
						}
					}
					if (properties.size() == 0) {
						continue;
					}
					data.setProperties(properties);
					if(log.isInfoEnabled()) {
						log.info("==== Add {0} class to resources.", clazz);
					}
					beanDatas.add(data);
				}
			}
		} catch (IOException e) {
			log.error("generate value-object resource error.", e);
		} catch (ClassNotFoundException e) {
			log.error("generate value-object resource error.", e);
		}
		return beanDatas;
	}
	
	public class BeanData {
		
		private String clazz;
		private String className;
		private boolean entity;
		private List<String> properties;
		
		public String getClazz() {
			return clazz;
		}
		
		public void setClazz(String clazz) {
			this.clazz = clazz;
		}
		
		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public boolean isEntity() {
			return entity;
		}

		public void setEntity(boolean entity) {
			this.entity = entity;
		}

		public List<String> getProperties() {
			return properties;
		}
		
		public void setProperties(List<String> properties) {
			this.properties = properties;
		}
		
	}

	public String[] getBasePackages() {
		return basePackages;
	}

	public void setBasePackages(String[] basePackages) {
		this.basePackages = basePackages;
	}

	public String getResourcePattern() {
		return resourcePattern;
	}

	public void setResourcePattern(String resourcePattern) {
		this.resourcePattern = resourcePattern;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public String[] getIgnorePrefixes() {
		return ignorePrefixes;
	}

	public void setIgnorePrefixes(String[] ignorePrefixes) {
		this.ignorePrefixes = ignorePrefixes;
	}

	public TemplateLoader getTemplateLoader() {
		return templateLoader;
	}

	public void setTemplateLoader(TemplateLoader templateLoader) {
		this.templateLoader = templateLoader;
	}
	
}
