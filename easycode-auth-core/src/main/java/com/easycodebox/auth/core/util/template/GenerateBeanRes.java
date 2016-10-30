package com.easycodebox.auth.core.util.template;

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
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;


/**
 * @author WangXiaoJin
 *
 */
public class GenerateBeanRes {
	
	private static final Logger log = LoggerFactory.getLogger(GenerateBeanRes.class);
	
	/**
	 * 要生成资源文件的Bean对象所在的路径
	 */
	private static final String[] basePackages = {
		"com/easycodebox/auth/core/pojo",
		"com/easycodebox/auth/core/bo"
	};
	
	private static final String 
			TEMPLATE = "bean_resource.ftl",
			OUTPUT_FILE = "src/main/java/com/easycodebox/auth/core/util/R.java";
	
	private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
	
	public static void main(String[] args) {
		generate();
	}
	
	/**
	 * 生成资源文件
	 */
	public static void generate() {
		Resource[] rs = new Resource[0];
		int lastIndex = rs.length;
		try {
			for(String basePackage : basePackages) {
				String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
						basePackage + Symbol.SLASH + DEFAULT_RESOURCE_PATTERN;
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
			cfg.setTemplateLoader(new ClassTemplateLoader(GenerateBeanRes.class, ""));
			
			//设置包装器，并将对象包装为数据模型
			Template tpl = cfg.getTemplate(TEMPLATE, "UTF-8");
			Map<String, List<BeanData>> root = new LinkedHashMap<>();
			root.put("data", processRes2BeanData(rs));
			File outPutFile = new File(OUTPUT_FILE);
			if(!outPutFile.getParentFile().exists()) {
				outPutFile.getParentFile().mkdirs();
			}
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
	
	private static List<BeanData> processRes2BeanData(Resource[] rs) {
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
	
	public static class BeanData {
		
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
	
}
