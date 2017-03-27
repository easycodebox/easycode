package com.easycodebox.auth.core.config;

import com.easycodebox.common.CommonProperties;
import com.easycodebox.common.error.CodeMsg.Code;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.*;

import java.util.*;

/**
 * 属性配置
 * @author WangXiaoJin
 */
@Configuration
@EnableConfigurationProperties(CoreProperties.class)
public class PropertyConfig {
	
	/**
	 * 返回默认配置
	 * @return
	 */
	public static Map<String, Object> defaultProperties() {
		Map<String, Object> props = new HashMap<>();
		props.put("code.suc", Code.SUC_CODE);
		props.put("code.fail", Code.FAIL_CODE);
		props.put("code.no.login", Code.NO_LOGIN_CODE);
		return props;
	}
	
	@Bean
	@SuppressWarnings("unchecked")
	public Map properties(Environment environment) {
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
		return Collections.unmodifiableMap(props);
	}
	
	@Bean
	@ConfigurationProperties(prefix = "common")
	public CommonProperties commonProperties() {
		return CommonProperties.instance();
	}
	
}
