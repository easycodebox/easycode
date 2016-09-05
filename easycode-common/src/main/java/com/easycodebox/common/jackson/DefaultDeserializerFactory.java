package com.easycodebox.common.jackson;

import com.easycodebox.common.enums.DetailEnum;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

/**
 * 此类创建之初是为了实现DetailEnum的反序列化用的，即为了使用DetailEnumDeserializer。
 * 后期经过一番搜索，发现可以实现ContextualDeserializer接口获取实际类型，固废弃此功能类。
 * @author WangXiaoJin
 * 
 */
@Deprecated
public class DefaultDeserializerFactory extends BeanDeserializerFactory {

	private static final long serialVersionUID = -2726900213010448694L;

	/**
	 * @param config
	 */
	public DefaultDeserializerFactory(DeserializerFactoryConfig config) {
		super(config);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public JsonDeserializer<?> createEnumDeserializer(
			DeserializationContext ctxt, JavaType type, BeanDescription beanDesc)
			throws JsonMappingException {
		final DeserializationConfig config = ctxt.getConfig();
		final Class<?> enumClass = type.getRawClass();
		// 23-Nov-2010, tatu: Custom deserializer?
		JsonDeserializer<?> deser = _findCustomEnumDeserializer(enumClass,
				config, beanDesc);
		if (deser == null) {
			
			/**
			 * add by WangXiaoJin 处理DetailEnum类型
			 */
			if(DetailEnum.class.isAssignableFrom(enumClass)) 
	        	return new DetailEnumDeserializer((Class<DetailEnum<?>>)enumClass);
			
			// [JACKSON-193] May have @JsonCreator for static factory method:
			for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
				if (ctxt.getAnnotationIntrospector().hasCreatorAnnotation(
						factory)) {
					int argCount = factory.getParameterCount();
					if (argCount == 1) {
						Class<?> returnType = factory.getRawReturnType();
						// usually should be class, but may be just plain
						// Enum<?> (for Enum.valueOf()?)
						if (returnType.isAssignableFrom(enumClass)) {
							deser = EnumDeserializer.deserializerForCreator(
									config, enumClass, factory);
							break;
						}
					}
					throw new IllegalArgumentException("Unsuitable method ("
							+ factory
							+ ") decorated with @JsonCreator (for Enum type "
							+ enumClass.getName() + ")");
				}
			}
			// [JACKSON-749] Also, need to consider @JsonValue, if one found
			if (deser == null) {
				deser = new EnumDeserializer(constructEnumResolver(enumClass,
						config, beanDesc.findJsonValueMethod()));
			}
		}

		// and then new with 2.2: ability to post-process it too (Issue#120)
		if (_factoryConfig.hasDeserializerModifiers()) {
			for (BeanDeserializerModifier mod : _factoryConfig
					.deserializerModifiers()) {
				deser = mod.modifyEnumDeserializer(config, type, beanDesc,
						deser);
			}
		}
		return deser;
	}

}
