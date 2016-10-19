package com.easycodebox.jdbc.mybatis;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.BeanWrapper;
import org.apache.ibatis.reflection.wrapper.CollectionWrapper;
import org.apache.ibatis.reflection.wrapper.MapWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

import com.easycodebox.common.enums.EnumClassFactory;
import com.easycodebox.common.lang.StringUtils;

/**
 * 此类是为了解决MyBatis的XML文件中SQL语句出现枚举值而定义的
 * 例子:	SELECT * FROM u_operation o WHERE o.status != #{OpenClose.CLOSE} 
 * 		ORDER BY o.sort DESC, o.createTime DESC
 * 		会把#{OpenClose.CLOSE} 自动转换成对应的枚举值
 * 注意：当MAPPER文件中statementType="STATEMENT" 时，此类不会生效。建议使用替代类DelegateSqlSource
 * 使用配置：在Mybatis配置中增加<objectWrapperFactory type="com.easycodebox.common.mybatis.DefaultObjectWrapperFactory" />
 * @author WangXiaoJin
 *
 */
@Deprecated
public class DefaultObjectWrapperFactory implements ObjectWrapperFactory {

	@Override
	public boolean hasWrapperFor(Object object) {
		return true;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
		ObjectWrapper wrapper = null;
		if (object instanceof ObjectWrapper) {
			wrapper = (ObjectWrapper) object;
		} else if (object instanceof Map) {
			wrapper = new MapWrapper(metaObject, (Map) object);
		} else if (object instanceof Collection) {
			wrapper = new CollectionWrapper(metaObject, (Collection) object);
		} else {
			wrapper = new BeanWrapper(metaObject, object);
		}
		return new DefaultObjectWrapper(wrapper, object);
	}
	
	public static class DefaultObjectWrapper implements ObjectWrapper {
		
		private ObjectWrapper delegate;
		private Object object;
		
		public DefaultObjectWrapper(ObjectWrapper delegate, Object object) {
			this.delegate = delegate;
			this.object = object;
		}

		@Override
		public Object get(PropertyTokenizer prop) {
			return delegate.hasGetter(prop.getIndexedName()) ? delegate.get(prop) : getObjectVal(prop.getIndexedName());
		}

		@Override
		public void set(PropertyTokenizer prop, Object value) {
			delegate.set(prop, value);
		}

		@Override
		public String findProperty(String name, boolean useCamelCaseMapping) {
			return delegate.findProperty(name, useCamelCaseMapping);
		}

		@Override
		public String[] getGetterNames() {
			return delegate.getGetterNames();
		}

		@Override
		public String[] getSetterNames() {
			return delegate.getSetterNames();
		}

		@Override
		public Class<?> getSetterType(String name) {
			return delegate.getSetterType(name);
		}

		@Override
		public Class<?> getGetterType(String name) {
			return delegate.hasGetter(name) ? delegate.getGetterType(name) : getEnumVal(name).getClass();
		}

		@Override
		public boolean hasSetter(String name) {
			return delegate.hasSetter(name);
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private Object getObjectVal(String name) {
			if(object instanceof Class && ((Class)object).isEnum()) {
				try {
					return Enum.valueOf((Class)object, name);
				} catch (Exception e) {
					
				}
			}else {
				return EnumClassFactory.newInstance(name);
			}
			return null;
		}
		
		/**
		 * 校验name是否是获取枚举值
		 * name = OpenClose.CLOSE
		 * @param name
		 * @return
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private Enum getEnumVal(String name) {
			if(StringUtils.isNotBlank(name)) {
				String[] frags = name.split("\\.");
				if(frags.length == 2) {
					Class<? extends Enum<?>> enumClass 
							= (Class<? extends Enum<?>>)EnumClassFactory.newInstance(frags[0]);
					try {
						return Enum.valueOf((Class)enumClass, frags[1]);
					} catch (Exception e) {
						
					}
				}
			}
			return null;
		}

		@Override
		public boolean hasGetter(String name) {
			return delegate.hasGetter(name) || getEnumVal(name) != null;
		}

		@Override
		public MetaObject instantiatePropertyValue(String name,
				PropertyTokenizer prop, ObjectFactory objectFactory) {
			return delegate.instantiatePropertyValue(name, prop, objectFactory);
		}

		@Override
		public boolean isCollection() {
			return delegate.isCollection();
		}

		@Override
		public void add(Object element) {
			delegate.add(element);
		}

		@Override
		public <E> void addAll(List<E> element) {
			delegate.addAll(element);
		}
		
	}

}
