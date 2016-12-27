package com.easycodebox.common.jackson;

import com.easycodebox.common.validate.Assert;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.FilterExceptFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.SerializeExceptFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

/**
 * @author WangXiaoJin
 *
 */
public class Jacksons extends ObjectMapper {
	
	private static final long serialVersionUID = -7195120894069424979L;
	
	/**
	 *  简单JSON转换
	 */
	public static Jacksons SIMPLE = simpleSetting(instance()).global(true);
	/**
	 * 排出为null的属性。
	 * 继承SIMPLE的所有配置
	 */
	public static Jacksons NON_NULL = nonNullSetting(instance()).global(true);
	/**
	 * JSON通信使用
	 * 1、JSON的value都转化成字符窜，不管是数字、日期、字符窜本身
	 * 2、排除掉NULL值的属性
	 * 继承SIMPLE、NON_NULL的所有配置
	 */
	public static Jacksons COMMUNICATE = communicateSetting(instance()).global(true);
	
	/**
	 * 判断此Jacksons对象是否是静态全局共享变量，如果是全局共享变量，则不能为此设置新的特性配置，不然会影响全局的操作
	 */
	private boolean global = false;
	
	private Jacksons() {
		super();
    }
	
	private Jacksons(Jacksons src) {
		super(src);
    }
	
	/**
	 * 创建一个新的ObjectMapper,用于设置新特性，如在老的ObjectMapper会影响老的功能 
	 * @return
	 */
	public static Jacksons instance() {
		return new Jacksons();
	}
	
	@Override
	public Jacksons copy() {
		_checkInvalidCopy(Jacksons.class);
		return new Jacksons(this);
	}

	/**
	 * 创建新的simple对象
	 * 创建一个新的ObjectMapper,用于设置新特性
	 * @return
	 */
	public static Jacksons simple() {
		return simpleSetting(SIMPLE.copy());
	}
	
	/**
	 * 创建新的nonNull对象
	 * 创建一个新的ObjectMapper,用于设置新特性，如在老的ObjectMapper会影响老的功能 
	 * @return
	 */
	public static Jacksons nonNull() {
		return nonNullSetting(NON_NULL.copy());
	}
	
	/**
	 * 创建新的communicate对象
	 * 创建一个新的ObjectMapper,用于设置新特性，如在老的ObjectMapper会影响老的功能 
	 * @return
	 */
	public static Jacksons communicate() {
		return communicateSetting(COMMUNICATE.copy());
	}
	
	/***************************   配置函数        ***********************************************/
	public static Jacksons simpleSetting(Jacksons mapper) {
		//mapper.getSerializationConfig().with(SerializationConfig.Feature.WRITE_ENUMS_USING_TO_STRING);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		return mapper;
	}
	
	public static Jacksons nonNullSetting(Jacksons mapper) {
		simpleSetting(mapper);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		return mapper;
	}
	
	public static Jacksons communicateSetting(Jacksons mapper) {
		nonNullSetting(mapper);
		mapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
		return mapper;
	}
	
	
	/***************************   ObjectMapper扩展函数        ***********************************************/
	/**
	 * 排除指定的属性。注意：调用此方法时不能应用于应该用SIMPLE/NON_NULL/COMMUNICATE，不然会影响到全局的json转换
	 * @param propertyArray
	 * @return
	 */
	public Jacksons exclude(Class<?> dataType, String... propertyArray) {
		validate();
		HashSet<String> properties = new HashSet<>(propertyArray.length);
        Collections.addAll(properties, propertyArray);
        return exclude(dataType, properties);  
    }
	
	/**
	 * 排除指定的属性。注意：调用此方法时不能应用于应该用SIMPLE/NON_NULL/COMMUNICATE，不然会影响到全局的json转换
	 * @param properties
	 * @return
	 */
	public Jacksons exclude(Class<?> dataType, Set<String> properties) {
		validate();
        FilterProvider provider = this.getSerializationConfig().getFilterProvider();
        final SimpleFilterProvider sfp = provider instanceof SimpleFilterProvider ? (SimpleFilterProvider) provider : new SimpleFilterProvider();
        sfp.addFilter(dataType.getName(), new SerializeExceptFilter(properties)).setFailOnUnknownId(false);
        this.setFilters(sfp);
		this.setAnnotationIntrospector(new JacksonAnnotationIntrospector(){
			
			private static final long serialVersionUID = 1L;

			@Override
			public Object findFilterId(Annotated a) {
				Object filterId = super.findFilterId(a);
                return filterId == null && sfp.findPropertyFilter(a.getName(), null) != null ? a.getName() : filterId;
			}
			
		});
        return this;  
    }
	
	/**
	 * 包含指定的属性。注意：调用此方法时不能应用于应该用SIMPLE/NON_NULL/COMMUNICATE，不然会影响到全局的json转换
	 * @param propertyArray
	 * @return
	 */
	public Jacksons include(Class<?> dataType, String... propertyArray) {
		validate();
		HashSet<String> properties = new HashSet<>(propertyArray.length);
        Collections.addAll(properties, propertyArray);
        return include(dataType, properties);  
    }
	
	/**
	 * 包含指定的属性。注意：调用此方法时不能应用于应该用SIMPLE/NON_NULL/COMMUNICATE，不然会影响到全局的json转换
	 * @param properties
	 * @return
	 */
	public Jacksons include(Class<?> dataType, Set<String> properties) {
		validate();
        FilterProvider provider = this.getSerializationConfig().getFilterProvider();
        final SimpleFilterProvider sfp = provider instanceof SimpleFilterProvider ? (SimpleFilterProvider) provider : new SimpleFilterProvider();
		sfp.addFilter(dataType.getName(), new FilterExceptFilter(properties)).setFailOnUnknownId(false);
        this.setFilters(sfp);
		this.setAnnotationIntrospector(new JacksonAnnotationIntrospector(){

			private static final long serialVersionUID = 1L;

			@Override
			public Object findFilterId(Annotated a) {
				Object filterId = super.findFilterId(a);
				return filterId == null && sfp.findPropertyFilter(a.getName(), null) != null ? a.getName() : filterId;
			}

		});
        return this;
    }
	
	/**
	 * 当类型为clazz时
	 * 序列化数据时始终以传入的JsonSerializer序列化，并不会用系统自带的。
	 * 此方法可以不受限制的序列化任意对象
	 * @param jsonSerializer
	 * @return
	 */
	public <T> Jacksons addJsonSerializer(Class<? extends T> type, JsonSerializer<T> jsonSerializer) {
		validate();
		SimpleSerializers sers = new SimpleSerializers();
		sers.addSerializer(type, jsonSerializer);
		_serializerFactory = _serializerFactory.withAdditionalSerializers(sers);
		return this;
	}
	
	/**
	 * 对象快速转换成字符窜,性能稍微高于直接调用Jacksons.writeValueAsString(value)
	 * 如果想性能更优，则使用Streaming API
	 * @param value
	 * @return
	 * @throws JsonProcessingException 
	 */
	public String toJson(Object value) throws JsonProcessingException {
    	return value == null ? "null" 
    			: this.writerWithView(value.getClass()).writeValueAsString(value);
	}
	
	/**
	 * 字符窜快速转换成对象,性能稍微高于直接调用Jacksons.this.readValue(content, valueType)
	 * 如果想性能更优，则使用Streaming API
	 * @param value		JSON字符窜
	 * @param valueType	需要转换成的类型
	 * @return
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	public <T> T toBean(String value, Class<T> valueType) throws JsonProcessingException, IOException {
    	return this.reader(valueType).readValue(value);  
	}
	
	/**
	 * 字符窜快速转换成对象,性能稍微高于直接调用Jacksons.this.readValue(content, valueType)
	 * 如果想性能更优，则使用Streaming API
	 * @param value		JSON字符窜
	 * @param valueType	需要转换成的类型
	 * @return
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	public <T> T toBean(String value, TypeReference<T> valueType) throws JsonProcessingException, IOException {
    	return this.reader(valueType).readValue(value);  
	}
	
	private void validate() {
		Assert.isFalse(global, "全局共享变量不能配置新特性");
	}
	
	private Jacksons global(boolean global) {
		this.global = global;
		return this;
	}


	
	
	
	/************* 重写ObjectMapper函数，为了不让通用对象不能修改特性    ****************/
	
	@Override
	public ObjectMapper enableDefaultTyping() {
		validate();
		return super.enableDefaultTyping();
	}

	@Override
	public ObjectMapper enableDefaultTyping(DefaultTyping dti) {
		validate();
		return super.enableDefaultTyping(dti);
	}

	@Override
	public ObjectMapper enableDefaultTyping(DefaultTyping applicability,
			As includeAs) {
		validate();
		return super.enableDefaultTyping(applicability, includeAs);
	}

	@Override
	public ObjectMapper enableDefaultTypingAsProperty(
			DefaultTyping applicability, String propertyName) {
		validate();
		return super.enableDefaultTypingAsProperty(applicability, propertyName);
	}

	@Override
	public ObjectMapper disableDefaultTyping() {
		validate();
		return super.disableDefaultTyping();
	}

	@Override
	public ObjectMapper configure(MapperFeature f, boolean state) {
		validate();
		return super.configure(f, state);
	}

	@Override
	public ObjectMapper enable(MapperFeature... f) {
		validate();
		return super.enable(f);
	}

	@Override
	public ObjectMapper disable(MapperFeature... f) {
		validate();
		return super.disable(f);
	}

	@Override
	public ObjectMapper configure(SerializationFeature f, boolean state) {
		validate();
		return super.configure(f, state);
	}

	@Override
	public ObjectMapper enable(SerializationFeature f) {
		validate();
		return super.enable(f);
	}

	@Override
	public ObjectMapper enable(SerializationFeature first,
			SerializationFeature... f) {
		validate();
		return super.enable(first, f);
	}

	@Override
	public ObjectMapper disable(SerializationFeature f) {
		validate();
		return super.disable(f);
	}

	@Override
	public ObjectMapper disable(SerializationFeature first,
			SerializationFeature... f) {
		validate();
		return super.disable(first, f);
	}

	@Override
	public ObjectMapper configure(DeserializationFeature f, boolean state) {
		validate();
		return super.configure(f, state);
	}

	@Override
	public ObjectMapper enable(DeserializationFeature feature) {
		validate();
		return super.enable(feature);
	}

	@Override
	public ObjectMapper enable(DeserializationFeature first,
			DeserializationFeature... f) {
		validate();
		return super.enable(first, f);
	}

	@Override
	public ObjectMapper disable(DeserializationFeature feature) {
		validate();
		return super.disable(feature);
	}

	@Override
	public ObjectMapper disable(DeserializationFeature first,
			DeserializationFeature... f) {
		validate();
		return super.disable(first, f);
	}

	@Override
	public ObjectMapper configure(Feature f, boolean state) {
		validate();
		return super.configure(f, state);
	}

	@Override
	public ObjectMapper configure(
			com.fasterxml.jackson.core.JsonGenerator.Feature f, boolean state) {
		validate();
		return super.configure(f, state);
	}

	@Override
	public ObjectMapper enable(Feature... features) {
		validate();
		return super.enable(features);
	}

	@Override
	public ObjectMapper enable(
			com.fasterxml.jackson.core.JsonGenerator.Feature... features) {
		validate();
		return super.enable(features);
	}

	@Override
	public ObjectMapper disable(Feature... features) {
		validate();
		return super.disable(features);
	}

	@Override
	public ObjectMapper disable(
			com.fasterxml.jackson.core.JsonGenerator.Feature... features) {
		validate();
		return super.disable(features);
	}

	@Override
	public ObjectMapper registerModule(Module module) {
		validate();
		return super.registerModule(module);
	}

	@Override
	public ObjectMapper registerModules(Module... modules) {
		validate();
		return super.registerModules(modules);
	}

	@Override
	public ObjectMapper registerModules(Iterable<Module> modules) {
		validate();
		return super.registerModules(modules);
	}

	@Override
	public ObjectMapper setSerializerFactory(SerializerFactory f) {
		validate();
		return super.setSerializerFactory(f);
	}

	@Override
	public ObjectMapper setSerializerProvider(DefaultSerializerProvider p) {
		validate();
		return super.setSerializerProvider(p);
	}

	@Override
	public ObjectMapper setMixIns(Map<Class<?>, Class<?>> sourceMixins) {
		validate();
		return super.setMixIns(sourceMixins);
	}

	@Override
	public ObjectMapper addMixIn(Class<?> target, Class<?> mixinSource) {
		validate();
		return super.addMixIn(target, mixinSource);
	}

	@Override
	public void setVisibilityChecker(VisibilityChecker<?> vc) {
		validate();
		super.setVisibilityChecker(vc);
	}

	@Override
	public ObjectMapper setVisibility(PropertyAccessor forMethod,
			Visibility visibility) {
		validate();
		return super.setVisibility(forMethod, visibility);
	}

	@Override
	public ObjectMapper setSubtypeResolver(SubtypeResolver str) {
		validate();
		return super.setSubtypeResolver(str);
	}

	@Override
	public ObjectMapper setAnnotationIntrospector(AnnotationIntrospector ai) {
		validate();
		return super.setAnnotationIntrospector(ai);
	}

	@Override
	public ObjectMapper setAnnotationIntrospectors(
			AnnotationIntrospector serializerAI,
			AnnotationIntrospector deserializerAI) {
		validate();
		return super.setAnnotationIntrospectors(serializerAI, deserializerAI);
	}

	@Override
	public ObjectMapper setPropertyNamingStrategy(PropertyNamingStrategy s) {
		validate();
		return super.setPropertyNamingStrategy(s);
	}

	@Override
	public ObjectMapper setSerializationInclusion(Include incl) {
		validate();
		return super.setSerializationInclusion(incl);
	}

	@Override
	public ObjectMapper setDefaultTyping(TypeResolverBuilder<?> typer) {
		validate();
		return super.setDefaultTyping(typer);
	}

	@Override
	public void registerSubtypes(Class<?>... classes) {
		validate();
		super.registerSubtypes(classes);
	}

	@Override
	public void registerSubtypes(NamedType... types) {
		validate();
		super.registerSubtypes(types);
	}

	@Override
	public ObjectMapper setTypeFactory(TypeFactory f) {
		validate();
		return super.setTypeFactory(f);
	}

	@Override
	public ObjectMapper setNodeFactory(JsonNodeFactory f) {
		validate();
		return super.setNodeFactory(f);
	}

	@Override
	public ObjectMapper addHandler(DeserializationProblemHandler h) {
		validate();
		return super.addHandler(h);
	}

	@Override
	public ObjectMapper setConfig(DeserializationConfig config) {
		validate();
		return super.setConfig(config);
	}

	@Override
	public void setFilters(FilterProvider filterProvider) {
		validate();
		super.setFilters(filterProvider);
	}

	@Override
	public ObjectMapper setBase64Variant(Base64Variant v) {
		validate();
		return super.setBase64Variant(v);
	}

	@Override
	public ObjectMapper setConfig(SerializationConfig config) {
		validate();
		return super.setConfig(config);
	}

	@Override
	public ObjectMapper setDateFormat(DateFormat dateFormat) {
		validate();
		return super.setDateFormat(dateFormat);
	}

	@Override
	public Object setHandlerInstantiator(HandlerInstantiator hi) {
		validate();
		return super.setHandlerInstantiator(hi);
	}

	@Override
	public ObjectMapper setInjectableValues(InjectableValues injectableValues) {
		validate();
		return super.setInjectableValues(injectableValues);
	}

	@Override
	public ObjectMapper setLocale(Locale l) {
		validate();
		return super.setLocale(l);
	}

	@Override
	public ObjectMapper setTimeZone(TimeZone tz) {
		validate();
		return super.setTimeZone(tz);
	}

	@Override
	public void acceptJsonFormatVisitor(Class<?> type,
			JsonFormatVisitorWrapper visitor) throws JsonMappingException {
		validate();
		super.acceptJsonFormatVisitor(type, visitor);
	}

	@Override
	public void acceptJsonFormatVisitor(JavaType type,
			JsonFormatVisitorWrapper visitor) throws JsonMappingException {
		validate();
		super.acceptJsonFormatVisitor(type, visitor);
	}
	
}
