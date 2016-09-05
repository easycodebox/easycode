package com.easycodebox.common.spring;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.util.StringValueResolver;

/**
 * 重新定义可读取属性的类 <br>
 * 注：此类已被废弃，可以通过Properties和Placeholder分离达到同样的效果
 * @author WangXiaoJin
 *
 */
@Deprecated
public class PropertyPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer {

	private StringValueResolver valueResolver;
	
	@Override
	protected void doProcessProperties(
			ConfigurableListableBeanFactory beanFactoryToProcess,
			StringValueResolver valueResolver) {
		this.valueResolver = valueResolver;
		super.doProcessProperties(beanFactoryToProcess, valueResolver);
	}

	public String get(String key) {
		try {
			return valueResolver.resolveStringValue(this.placeholderPrefix + key + this.placeholderSuffix);
		} catch (Exception e) {
			return null;
		}
	}
	
}
