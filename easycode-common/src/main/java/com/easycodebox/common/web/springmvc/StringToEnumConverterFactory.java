package com.easycodebox.common.web.springmvc;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import com.easycodebox.common.enums.Enums;
import com.easycodebox.common.validate.Assert;

/**
 * @author WangXiaoJin
 *
 */
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum<?>> {

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends Enum<?>> Converter<String, T> getConverter(
			Class<T> targetType) {
		Class<?> enumType = targetType;
		while(enumType != null && !enumType.isEnum()) {
			enumType = enumType.getSuperclass();
		}
		Assert.notNull(enumType, "The target type does not refer to an enum", targetType);
		return new StringToDetailEnumConverter(enumType);
	}
	
	public static class StringToDetailEnumConverter<T extends Enum<?>> 
			implements Converter<String, T> {
		
		private Class<T> enumType;
		
		public StringToDetailEnumConverter(Class<T> enumType) {
			this.enumType = enumType;
		}
		
		@Override
		public T convert(String source) {
			Assert.isTrue(enumType.isEnum(), 
					"{0} class is not enum, so can not convert {1} to enum.", 
					enumType, source);
			return Enums.deserialize(enumType, source, true);
		}
		
	}

}
