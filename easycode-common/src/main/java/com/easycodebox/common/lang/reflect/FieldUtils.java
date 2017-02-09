package com.easycodebox.common.lang.reflect;

import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.validate.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author WangXiaoJin
 *
 */
public class FieldUtils extends org.apache.commons.lang.reflect.FieldUtils {
	
	private static final Logger log = LoggerFactory.getLogger(FieldUtils.class);

	/**
	 * 获取指定类的所有属性(包含super class的属性)，包括private、protected、public
	 * @param clazz
	 * @return
	 */
	public static List<Field> getAllFields(Class<?> clazz) {
		return getAllFields(clazz, null, true);
	}
	
	/**
	 * 获取指定类的所有属性(包含super class的属性)，包括private、protected、public
	 * @param clazz
	 * @param excludeStaticFileds	是否排除static属性
	 * @return
	 */
	public static List<Field> getAllFields(Class<?> clazz, boolean excludeStaticFileds) {
		return getAllFields(clazz, null, excludeStaticFileds);
	}
	
	/**
	 * 获取指定类的所有属性(包含super class的属性)，包括private、protected、public
	 * @param clazz
	 * @param annotation		只获取有指定注解的属性
	 * @param excludeStaticFileds	是否排除static属性
	 * @return
	 */
	public static List<Field> getAllFields(Class<?> clazz, Class<? extends Annotation> annotation, 
			boolean excludeStaticFileds) {
		if(clazz == null) {
			return null;
		}
		
		List<Field> r = new ArrayList<>();
		Class<?> parent = clazz;
		while(parent != null) {
			for(Field f : parent.getDeclaredFields()) {
				
				if(excludeStaticFileds && (f.getModifiers() & Modifier.STATIC) != 0) {
					continue;
				}
				
				if(annotation != null && !f.isAnnotationPresent(annotation)) { 
					continue;
				}
				if(!Modifier.isPublic(f.getModifiers()))
					f.setAccessible(true);
				r.add(f);
			}
			parent = parent.getSuperclass();
		}
		return r;
	}
	
	public static String getFieldName(Method m) {
		if(m == null) {
			return null;
		}
		if(!MethodUtils.isGetterMethod(m) && !MethodUtils.isSetterMethod(m)) {
			return null;
		}
		
		StringBuilder r = new StringBuilder();
		if(MethodUtils.isIsMethod(m)) {
			r.append(m.getName().substring(2));
		} else if(MethodUtils.isGetterMethod(m)) {
			r.append(m.getName().substring(3));
		} else if(MethodUtils.isSetterMethod(m)) {
			r.append(m.getName().substring(3));
		}
		r.replace(0, 1, r.substring(0, 1).toLowerCase());
		return r.toString();
	}
	
	public static Object getField(Object target, String name) throws Exception {
		Field f = target.getClass().getDeclaredField(name);
		if (!Modifier.isPublic(f.getModifiers())) {
            f.setAccessible(true);
        }
		return f.get(target);
	}

	/**
	 * 通过反射,获得Field泛型参数的实际类型. 如: public Map<String, Buyer> names;
	 * @param field 字段
	 * @param index 泛型参数所在索引,从0开始.
	 * @return 泛型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回<code>Object.class</code>
	 */
	@SuppressWarnings("rawtypes")
	public static Class getFieldGenericType(Field field, int index){
		Type genericFieldType = field.getGenericType();
		if (genericFieldType instanceof ParameterizedType){
			ParameterizedType aType = (ParameterizedType) genericFieldType;
			Type[] fieldArgTypes = aType.getActualTypeArguments();
			if (index >= fieldArgTypes.length || index < 0){
				throw new RuntimeException("你输入的索引" + (index < 0 ? "不能小于0" : "超出了参数的总数"));
			}
			return (Class) fieldArgTypes[index];
		}
		return Object.class;
	}
	
	@SuppressWarnings("rawtypes")
	public static Class getFieldGenericType(Class clazz, String fieldName){
		return getFieldGenericType(clazz, fieldName, 0);
	}
	
	@SuppressWarnings("rawtypes")
	public static Class getFieldGenericType(Class clazz, String fieldName, int index) {
		if(clazz == null && StringUtils.isBlank(fieldName))
			return null;
		Field field = getField(clazz, fieldName, true);
		if(field == null) return null;
		return getFieldGenericType(field, index);
	}
	/**
	 * 通过反射,获得Field泛型参数的实际类型. 如: public Map<String, Buyer> names;
	 * @param field 字段
	 * @return 泛型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回<code>Object.class</code>
	 */
	@SuppressWarnings("rawtypes")
	public static Class getFieldGenericType(Field field){
		return getFieldGenericType(field, 0);
	}
	
	/**
	 * 请用其他工具类。如：org.apache.commons.beanutils.PropertyUtils
	 * @return
	 */
	@Deprecated
	public static Class<?> getBeanPropertyType(Class<?> clazz, String name) {
		Assert.notNull(clazz);
		Assert.notBlank(name);
		Method method = null;
        String methodName = "get" + StringUtils.capitalize(name);
        boolean accessField;
        try {
			method = clazz.getMethod(methodName);
		} catch (NoSuchMethodException e) {
			log.debug(clazz.getName() + " has no {0} method.", methodName);
		} catch (SecurityException e) {
			log.debug(" SecurityException.", e);
		}
        if(method == null) {
        	methodName = "is" + StringUtils.capitalize(name);
        	try {
				method = clazz.getMethod(methodName);
			} catch (NoSuchMethodException e) {
				log.debug(clazz.getName() + " has no {0} method.", methodName);
			} catch (SecurityException e) {
				log.debug(" SecurityException.", e);
			}
        }
        if(method != null) {
        	try {
				return method.getReturnType();
			} catch (Exception e) {
				log.debug(" execute {0} method in class {1}.", methodName, clazz.getName());
				accessField = true;
			} 
        }else
        	accessField = true;
        if(accessField) {
        	 try {
                 Field field = clazz.getDeclaredField(name);
                 return field.getType();
             } catch (Exception e1) {
             	throw new BaseException("call mehtod setBeanField error.", e1);
             }
        }
        throw new BaseException("There is no property named {0}.", name);
	}
	
	/**
	 * 获取javabean的属性。请用其他工具类。如：org.apache.commons.beanutils.PropertyUtils
	 * @param target
	 * @param name
	 */
	@Deprecated
	public static Object getBeanProperty(Object target, String name) {
        if (target == null || StringUtils.isBlank(name)) {
        	throw new BaseException("call getBeanProperty method error. params error.");
        }
        Class<?> clazz = target.getClass();
        Method method = null;
        String methodName = "get" + StringUtils.capitalize(name);
        boolean accessField;
        try {
			method = clazz.getMethod(methodName);
		} catch (NoSuchMethodException e) {
			log.debug(clazz.getName() + " has no {0} method.", methodName);
		} catch (SecurityException e) {
			log.debug(" SecurityException.", e);
		}
        if(method == null) {
        	methodName = "is" + StringUtils.capitalize(name);
        	try {
				method = clazz.getMethod(methodName);
			} catch (NoSuchMethodException e) {
				log.debug(clazz.getName() + " has no {0} method.", methodName);
			} catch (SecurityException e) {
				log.debug(" SecurityException.", e);
			}
        }
        if(method != null) {
        	try {
				return method.invoke(target);
			} catch (Exception e) {
				log.debug(" execute {0} method in class {1}.", methodName, clazz.getName());
				accessField = true;
			} 
        }else
        	accessField = true;
        if(accessField) {
        	 try {
                 Field field = clazz.getDeclaredField(name);
                 if (!Modifier.isPublic(field.getModifiers())) {
                     field.setAccessible(true);
                 }
                 return field.get(target);
             } catch (Exception e1) {
             	throw new BaseException("call mehtod getBeanProperty error.", e1);
             }
        }
        throw new BaseException("call mehtod getBeanProperty error.");
    }
	
	/**
	 * 设置javabean的属性
	 * 请用其他工具类。如：org.apache.commons.beanutils.PropertyUtils
	 * @param target 
	 * @param name	属性名
	 * @param value	设置的属性值
	 */
	@Deprecated
	public static void setBeanProperty(Object target, String name, Object value) {
		Class<?> type = getBeanPropertyType(target.getClass(), name);
		setBeanProperty(target, name, type, value);
	}
	
	/**
	 * 设置javabean的属性
	 * 请用其他工具类。如：org.apache.commons.beanutils.PropertyUtils
	 * @param target 
	 * @param name	属性名
	 * @param type	属性类型
	 * @param value	设置的属性值
	 */
	@Deprecated
	public static void setBeanProperty(Object target, String name, 
			Class<?> type, Object value) {
        if (target == null || StringUtils.isBlank(name)) {
        	throw new BaseException("call setBeanField method error. params error.");
        }
        Class<?> clazz = target.getClass();
        String setMethod = "set" + StringUtils.capitalize(name);
        try {
            Method method = clazz.getMethod(setMethod, type);
            method.invoke(target, value);
        } catch (Exception e) {
        	log.debug(clazz.getName() + " execute " + setMethod + " error.", e);
            try {
                Field field = clazz.getDeclaredField(name);
                if (!Modifier.isPublic(field.getModifiers())) {
                    field.setAccessible(true);
                }
                field.set(target, value);
            } catch (Exception e1) {
            	throw new BaseException("call mehtod setBeanField error.", e1);
            }
        }
    }
	
}
