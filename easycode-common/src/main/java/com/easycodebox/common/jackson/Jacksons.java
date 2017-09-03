package com.easycodebox.common.jackson;

import com.easycodebox.common.Wrapper;
import com.easycodebox.common.validate.Assert;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import java.io.IOException;
import java.util.Set;

/**
 * @author WangXiaoJin
 */
public class Jacksons extends Wrapper<ObjectMapper> {
	
	private static volatile Jacksons instance;
	
	/**
	 * 被包裹实例{@link #object}是否已包含了自定义{@link JacksonAnnotationIntrospector}动态生成FilterId
	 */
	@SuppressWarnings("FieldCanBeLocal")
	private boolean hasFilterIdGenerator = false;
	
	public Jacksons(ObjectMapper object) {
		super(object);
		Assert.notNull(object);
	}
	
	/**
	 * 返回共享实例
	 * @return
	 */
	public static Jacksons instance() {
		if (instance == null) {
			synchronized (Jacksons.class) {
				if (instance == null) {
					instance = new Jacksons(defaultObjectMapper());
				}
			}
		}
		return instance;
	}
	
	/**
	 * 设置共享的实例 - 此方法最好是在项目启动时就调用，如果共享实例已存在则抛异常
	 * @return
	 */
	public static Jacksons setSharedInstance(Jacksons sharedInstance) {
		Assert.isNull(instance, "The shared instance has been seted.");
		synchronized (Jacksons.class) {
			Assert.isNull(instance, "The shared instance has been seted.");
			instance = sharedInstance;
		}
		return instance;
	}
	
	private static ObjectMapper defaultObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
		mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
		mapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		return mapper;
	}
	
	/**
	 * 排除指定的属性
	 */
	public Jacksons exclude(Class<?> dataType, String... properties) {
		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(properties);
		return definePropertyFilter(dataType, filter);
    }
	
	/**
	 * 排除指定的属性
	 */
	public Jacksons exclude(Class<?> dataType, Set<String> properties) {
		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(properties);
		return definePropertyFilter(dataType, filter);
    }
	
	/**
	 * 包含指定的属性
	 */
	public Jacksons include(Class<?> dataType, String... properties) {
		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(properties);
		return definePropertyFilter(dataType, filter);
    }
	
	/**
	 * 包含指定的属性
	 */
	public Jacksons include(Class<?> dataType, Set<String> properties) {
		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(properties);
		return definePropertyFilter(dataType, filter);
    }
	
	/**
	 * 自定义指定类型的序列化规则
	 */
	public <T> Jacksons addJsonSerializer(Class<? extends T> type, JsonSerializer<T> jsonSerializer) {
		ObjectMapper copy = object.copy();
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(type, jsonSerializer);
		copy.registerModule(simpleModule);
		return new Jacksons(copy);
	}
	
	public String toJson(Object value) throws JsonProcessingException {
    	return object.writeValueAsString(value);
	}
	
	/**
	 * @param value		JSON字符窜
	 * @param valueType	需要转换成的类型
	 */
	public <T> T toBean(String value, Class<T> valueType) throws IOException {
		return object.readValue(value, valueType);
	}
	
	/**
	 * @param value		JSON字符窜
	 * @param valueType	需要转换成的类型
	 */
	public <T> T toBean(String value, TypeReference<T> valueType) throws IOException {
    	return object.readValue(value, valueType);
	}
	
	private String generateFilterId(Class<?> clazz) {
		return clazz.getName() + "#auto";
	}
	
	private Jacksons definePropertyFilter(Class<?> dataType, SimpleBeanPropertyFilter filter) {
		ObjectMapper copy = object.copy();
		SerializationConfig config = copy.getSerializationConfig();
		SimpleFilterProvider filterProvider;
		if (config.getFilterProvider() instanceof SimpleFilterProvider) {
			filterProvider = (SimpleFilterProvider) config.getFilterProvider();
		} else {
			filterProvider = new SimpleFilterProvider();
			config = config.withFilters(filterProvider);
		}
		filterProvider.addFilter(generateFilterId(dataType), filter).setFailOnUnknownId(false);
		if (!hasFilterIdGenerator) {
			config = config.withAppendedAnnotationIntrospector(new JacksonAnnotationIntrospector(){
				@Override
				public Object findFilterId(Annotated a) {
					Object filterId = super.findFilterId(a);
					String autoFilterId = generateFilterId(a.getRawType());
					return filterId == null && filterProvider.findPropertyFilter(autoFilterId, null) != null ? autoFilterId : filterId;
				}
			});
		}
		copy.setConfig(config);
		return new Jacksons(copy);
	}
}
