package com.easycodebox.auth.core.config;

import com.easycodebox.common.CommonProperties;
import com.easycodebox.common.error.CodeMsg.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.*;

import java.util.*;

/**
 * 属性配置
 * @author WangXiaoJin
 */
@Configuration
@SuppressWarnings("Duplicates")
public class PropertyConfig {
	
	@Autowired
	private Environment environment;
	
	
	/**
	 * 因{@link PropertySourcesPlaceholderConfigurer}实现了{@link BeanFactoryPostProcessor}接口且类上有{@link Configuration}，
	 * 所以方法必须是{@code static}
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholder() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
		configurer.setIgnoreResourceNotFound(true);
		configurer.setTrimValues(true);
		configurer.setProperties(customProperties());
		return configurer;
	}
	
	/**
	 * 把环境变量拷贝到map中，供其他类使用。通过{@link PropertySource}加载的属性文件，最终生成Properties类，
	 * 而此类是线程安全的，性能会有一定的损耗，应在只读的场景下转成非线程安全的Map
	 */
	@Bean
	@SuppressWarnings("unchecked")
	public Map properties() {
		Map props = new HashMap();
		if (environment instanceof ConfigurableEnvironment) {
			ConfigurableEnvironment configEnv = (ConfigurableEnvironment) environment;
			for (org.springframework.core.env.PropertySource<?> source : configEnv.getPropertySources()) {
				if (source instanceof EnumerablePropertySource) {
					EnumerablePropertySource eps = (EnumerablePropertySource) source;
					for (String key : eps.getPropertyNames()) {
						if (!props.containsKey(key)) {
							props.put(key, eps.getProperty(key));
						}
					}
				}
			}
		}
		//增加自定义的属性资源
		Properties custom = customProperties();
		for (Object key : custom.keySet()) {
			props.put(key, custom.get(key));
		}
		return Collections.unmodifiableMap(props);
	}
	
	/**
	 * 返回自定义的属性资源
	 * @return
	 */
	private static Properties customProperties() {
		Properties props = new Properties();
		props.setProperty("code.suc", Code.SUC_CODE);
		props.setProperty("code.fail", Code.FAIL_CODE);
		props.setProperty("code.no.login", Code.NO_LOGIN_CODE);
		return props;
	}
	
	@Bean
	public CoreProperties coreProperties() {
		return CoreProperties.instance();
	}
	
	@Bean
	@ConfigurationProperties(prefix = "common")
	public CommonProperties commonProperties() {
		return CommonProperties.instance();
	}
	
}
