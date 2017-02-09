package com.easycodebox.common.lang.reflect;

import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author WangXiaoJin
 *
 */
public class MethodUtils extends org.apache.commons.lang.reflect.MethodUtils {

	/**
     * 判断clazz中是否有指定的方法
     * @param clazz
     * @param method
     * @param parameterTypes
     * @return
     */
    public static boolean hasMethod(Class<?> clazz, String method, 
    		Class<?>... parameterTypes) {
    	if(clazz == null) return false;
    	try {
			clazz.getMethod(method, parameterTypes);
		} catch (Exception e) {
			return false;
		}
		return true;
    }
    
	public static boolean isIsMethod(Method method) {
		return method != null && method.getName().startsWith("is")
				&& method.getParameterTypes().length == 0 && method.getReturnType().equals(boolean.class);
	}
	
	public static boolean isGetterMethod(Method method) {
		return method != null && (isIsMethod(method) || method.getName().startsWith("get")
				&& method.getParameterTypes().length == 0 && !ClassUtils.isVoid(method.getReturnType()));
	}
	
	public static boolean isSetterMethod(Method method) {
		return method != null && method.getName().startsWith("set")
				&& method.getParameterTypes().length == 1 && ClassUtils.isVoid(method.getReturnType());
	}

	public static Method findGetterMethod(Class<?> clazz, Field field) {
		StringBuilder sb = new StringBuilder(field.getName());
		sb.replace(0, 1, sb.substring(0, 1).toUpperCase());
		sb.insert(0, "get");
		
		Method r = findPublicMethod(clazz, sb.toString(), new Class<?>[]{});
		if(r == null) {
			if(field.getType().equals(boolean.class)) {
				sb.replace(0, 3, "is");
			}
			r = findPublicMethod(clazz, sb.toString(), new Class<?>[]{});
		}
		return r;
	}
	
	public static Method findGetterMethod(Class<?> clazz, String fieldName) {
		StringBuilder sb = new StringBuilder(fieldName);
		sb.replace(0, 1, sb.substring(0, 1).toUpperCase());
		sb.insert(0, "get");
		
		Method r = findPublicMethod(clazz, sb.toString(), new Class<?>[]{});
		if(r == null) {
			try {
				Field field = clazz.getField(fieldName);
				if(field.getType().equals(boolean.class)) {
					sb.replace(0, 3, "is");
				}
				r = findPublicMethod(clazz, sb.toString(), new Class<?>[]{});
			} catch (NoSuchFieldException | SecurityException ignored) {
				
			}
		}
		return r;
	}
	
	public static Method findSetterMethod(Class<?> clazz, Field field) {
		//
		StringBuilder sb = new StringBuilder(field.getName());
		sb.replace(0, 1, sb.substring(0, 1).toUpperCase());
		sb.insert(0, "set");
		
		//
		Class<?> type = field.getType();
		if(type.isPrimitive()) {
			type = ClassUtils.primitiveToWrapper(type);
		}
		return findPublicMethod(clazz, sb.toString(), new Class<?>[]{type});
	}
	
	public static Method getPublicMethod(Class<?> clazz, String name, Class<?> signatures[]) 
	throws NoSuchMethodException {
		Method r = findPublicMethod(clazz, name, signatures);
		if (r == null) {
			throw new NoSuchMethodException(clazz.getName() + "." + name + ArrayUtils.toString(signatures));
		}
		return r;
	}
	
	public static Method findPublicMethod(Class<?> clazz, String name, Class<?> signatures[]) {
		// If the method takes no arguments, we can the following optimization 
		// which avoids the expensive call to getMethods().
		if (signatures.length == 0) {
			try {
				return clazz.getMethod(name, signatures);
			} catch (NoSuchMethodException e) {
				return null;
			} catch (SecurityException ignored) {
			}
		}
		
		List<Method> methods = new ArrayList<>();
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(name)) {
				if (matchArguments(signatures, method.getParameterTypes(), false)) {
					methods.add(method);
				}
			}
		}
		
		if (methods.size() == 0) {
			return null;
		} else if (methods.size() == 1) {
			return methods.get(0);
		} else {
			for(Method method : methods) {
				if (matchArguments(signatures, method.getParameterTypes(), true)) {
					return method;
				}
			}
			return getMostSpecificMethod(methods, signatures);
		}
	}
	
	private static Method getMostSpecificMethod(List<Method> methods, Class<?> signatures[]) {
		//
		int maxMatches = 0;
		Method method = null;
		for(Method m : methods){
			//
			int matches = 0;
			Class<?> paramTypes[] = m.getParameterTypes();
			for (int i = 0; i < signatures.length; i++) {
				Class<?> paramType = paramTypes[i];
				if (paramType.isPrimitive() && !signatures[i].isPrimitive()) {
					paramType = ClassUtils.primitiveToWrapper(paramType);
				}
				if (signatures[i] == paramType) {
					matches++;
				}
			}
			
			if (matches == 0 && maxMatches == 0) {
				if (method == null) {
					method = m;
				} else {
					// If the current method parameters is higher in the inheritance hierarchy then replace it.
					if (!matchArguments(method.getParameterTypes(), m.getParameterTypes(), false)) {
						method = m;
					}
				}
			} else if (matches > maxMatches) {
				maxMatches = matches;
				method = m;
			} else if (matches == maxMatches) { // Ambiguous method
				method = null;
			}
		}
		return method;
	}
	
	private static boolean matchArguments(Class<?> signatures[], Class<?> paramTypes[], boolean explicit) {
		//
		if(signatures.length != paramTypes.length) {
			return false;
		}
		
		//
		for (int j = 0; j < signatures.length; j++) {
			//
			Class<?> paramType = paramTypes[j];
			if (paramType.isPrimitive() && !signatures[j].isPrimitive()) {
				paramType = ClassUtils.primitiveToWrapper(paramType);
			}
			
			//
			if (explicit) {
				if (signatures[j] != paramType) { // Test each element for equality
					return false;
				}
			} else { // Consider null an instance of all classes.
				if (signatures[j] != null && !(paramType.isAssignableFrom(signatures[j]))) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 通过反射,获得方法返回值泛型参数的实际类型. 如: public Map<String, Buyer> getNames(){}
	 * @param method 方法
	 * @param index 泛型参数所在索引,从0开始.
	 * @return 泛型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回<code>Object.class</code>
	 */
	@SuppressWarnings("rawtypes")
	public static Class getMethodGenericReturnType(Method method, int index){
		Type returnType = method.getGenericReturnType();
		if (returnType instanceof ParameterizedType){
			ParameterizedType type = (ParameterizedType)returnType;
			Type[] typeArguments = type.getActualTypeArguments();
			if (index >= typeArguments.length || index < 0){
				throw new RuntimeException("你输入的索引" + (index < 0 ? "不能小于0" : "超出了参数的总数"));
			}
			return (Class)typeArguments[index];
		}
		return Object.class;
	}

	/**
	 * 通过反射,获得方法返回值第一个泛型参数的实际类型. 如: public Map<String, Buyer> getNames(){}
	 * @param method 方法
	 * @return 泛型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回<code>Object.class</code>
	 */
	@SuppressWarnings("rawtypes")
	public static Class getMethodGenericReturnType(Method method){
		return getMethodGenericReturnType(method, 0);
	}

	/**
	 * 通过反射,获得方法输入参数第index个输入参数的所有泛型参数的实际类型. 如: public void add(Map<String, Buyer> maps, List<String> names){}
	 * @param method 方法
	 * @param index 第几个输入参数
	 * @return 输入参数的泛型参数的实际类型集合, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回空集合
	 */
	@SuppressWarnings("rawtypes")
	public static List<Class> getMethodGenericParameterTypes(Method method, int index){
		List<Class> results = new ArrayList<>();
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		if (index >= genericParameterTypes.length || index < 0){
			throw new RuntimeException("你输入的索引" + (index < 0 ? "不能小于0" : "超出了参数的总数"));
		}
		Type genericParameterType = genericParameterTypes[index];
		if (genericParameterType instanceof ParameterizedType){
			ParameterizedType aType = (ParameterizedType) genericParameterType;
			Type[] parameterArgTypes = aType.getActualTypeArguments();
			for (Type parameterArgType : parameterArgTypes){
				Class parameterArgClass = (Class) parameterArgType;
				results.add(parameterArgClass);
			}
			return results;
		}
		return results;
	}

	/**
	 * 通过反射,获得方法输入参数第一个输入参数的所有泛型参数的实际类型. 如: public void add(Map<String, Buyer> maps, List<String> names){}
	 * @param method 方法
	 * @return 输入参数的泛型参数的实际类型集合, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回空集合
	 */
	@SuppressWarnings("rawtypes")
	public static List<Class> getMethodGenericParameterTypes(Method method){
		return getMethodGenericParameterTypes(method, 0);
	}

	
}
