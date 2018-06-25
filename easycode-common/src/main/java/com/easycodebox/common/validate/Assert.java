package com.easycodebox.common.validate;

import com.easycodebox.common.enums.entity.LogLevel;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.error.ErrorContext;
import com.easycodebox.common.lang.Collections;
import com.easycodebox.common.lang.Strings;

import java.util.Collection;
import java.util.Map;

public class Assert {
	
	/**
	 * 抛{@link IllegalArgumentException}异常
	 */
	public static void throwException(String msg) throws IllegalArgumentException {
		throw new IllegalArgumentException(msg);
	}
	
	/**
	 * 抛{@link IllegalArgumentException}异常
	 */
	public static void throwException(String msg, Object... args) throws IllegalArgumentException {
		throw new IllegalArgumentException(args != null && args.length > 0 ? Strings.format(msg, args) : msg);
	}
	
	/**
	 * 抛{@link ErrorContext}异常
	 */
	private static void throwError(CodeMsg error) throws ErrorContext {
		throw ErrorContext.instance(error).logLevel(LogLevel.WARN);
	}
	
	public static boolean isTrue(boolean exp) {
		return isTrue(exp, "表达式必须等于true");
	}
	
	public static boolean isTrue(boolean exp, String message) {
		if (!exp) throwException(message);
		return true;
	}
	
	public static boolean isTrue(boolean exp, String message, Object... args) {
		if (!exp) throwException(message, args);
		return true;
	}
	
	public static boolean isTrue(boolean exp, CodeMsg error) {
		if (!exp) throwError(error);
		return true;
	}

	public static boolean isFalse(boolean exp) {
		return isFalse(exp, "表达式必须等于false");
	}
	
	public static boolean isFalse(boolean exp, String message) {
		if (exp) throwException(message);
		return false;
	}
	
	public static boolean isFalse(boolean exp, String message, Object... args) {
		if (exp) throwException(message, args);
		return false;
	}
	
	public static boolean isFalse(boolean exp, CodeMsg error) {
		if (exp) throwError(error);
		return false;
	}
	
	public static <T> T isNull(T object) {
		return isNull(object, "参数必须为null");
	}
	
	public static <T> T isNull(T object, String message) {
		if (object != null) throwException(message);
		return null;
	}
	
	public static <T> T isNull(T object, String message, Object... args) {
		if (object != null) throwException(message, args);
		return null;
	}
	
	public static <T> T isNull(T object, CodeMsg error) {
		if (object != null) throwError(error);
		return null;
	}
	
	public static <T> T notNull(T object) {
		return notNull(object, "参数不能为null");
	}
	
	public static <T> T notNull(T object, String message) {
		if (object == null) throwException(message);
		return object;
	}
	
	public static <T> T notNull(T object, String message, Object... args) {
		if (object == null) throwException(message, args);
		return object;
	}
	
	public static <T> T notNull(T object, CodeMsg error) {
		if (object == null) throwError(error);
		return object;
	}
	
	public static String isBlank(String text) {
		return isBlank(text, "参数应为空或空格字符");
	}
	
	public static String isBlank(String text, String message) {
		if (Strings.isNotBlank(text)) throwException(message);
		return text;
	}
	
	public static String isBlank(String text, String message, Object... args) {
		if (Strings.isNotBlank(text)) throwException(message, args);
		return text;
	}
	
	public static String isBlank(String text, CodeMsg error) {
		if (Strings.isNotBlank(text)) throwError(error);
		return text;
	}
	
	public static String notBlank(String text) {
		return notBlank(text, "参数不能为空或空格字符");
	}
	
	public static String notBlank(String text, String message) {
		if (Strings.isBlank(text)) throwException(message);
		return text;
	}
	
	public static String notBlank(String text, String message, Object... args) {
		if (Strings.isBlank(text)) throwException(message, args);
		return text;
	}
	
	public static String notBlank(String text, CodeMsg error) {
		if (Strings.isBlank(text)) throwError(error);
		return text;
	}
	
	public static String notContain(String textToSearch, String substring) {
		return notContain(textToSearch, substring,
				"字符窜[" + textToSearch + "] 不能包含子字符窜[" + substring + "]");
	}
	
	public static String notContain(String textToSearch, String substring, String message) {
		notNull(textToSearch);
		notNull(substring);
		if (textToSearch.contains(substring)) throwException(message);
		return textToSearch;
	}
	
	public static String notContain(String textToSearch, String substring, String message, Object... args) {
		notNull(textToSearch);
		notNull(substring);
		if (textToSearch.contains(substring)) throwException(message, args);
		return textToSearch;
	}
	
	public static String notContain(String textToSearch, String substring, CodeMsg error) {
		notNull(textToSearch);
		notNull(substring);
		if (textToSearch.contains(substring)) throwError(error);
		return textToSearch;
	}
	
	public static <T> T[] notEmpty(T[] array) {
		return notEmpty(array, "数组参数不能为空");
	}
	
	public static <T> T[] notEmpty(T[] array, String message) {
		if (array == null || array.length == 0) throwException(message);
		return array;
	}
	
	public static <T> T[] notEmpty(T[] array, String message, Object... args) {
		if (array == null || array.length == 0) throwException(message, args);
		return array;
	}
	
	public static <T> T[] notEmpty(T[] array, CodeMsg error) {
		if (array == null || array.length == 0) throwError(error);
		return array;
	}

	public static <T> T[] noNullElements(T[] array) {
		return noNullElements(array, "数组参数不应有空值");
	}
	
	public static <T> T[] noNullElements(T[] array, String message) {
		if (array != null) {
			for (T anArray : array) {
				if (anArray == null) {
					throwException(message);
				}
			}
		}
		return array;
	}
	
	public static <T> T[] noNullElements(T[] array, String message, Object... args) {
		if (array != null) {
			for (Object anArray : array) {
				if (anArray == null) {
					throwException(message, args);
				}
			}
		}
		return array;
	}
	
	public static <T> T[] noNullElements(T[] array, CodeMsg error) {
		if (array != null) {
			for (Object anArray : array) {
				if (anArray == null) {
					throwError(error);
				}
			}
		}
		return array;
	}
	
	/**
	 * 判断数组array的长度是否等于length
	 * @param array
	 * @param length
	 * @throws IllegalArgumentException 当数组array的长度是不等于length,或者array==null
	 */
	public static <T> T[] length(T[] array, int length) {
		return length(array, length, "数组参数长度应等于" + length);
	}
	
	/**
	 * 判断数组array的长度是否等于length
	 * @param array
	 * @param length
	 * @throws IllegalArgumentException 当数组array的长度是不等于length,或者array==null
	 */
	public static <T> T[] length(T[] array, int length, String message) {
		if (array == null || array.length != length) throwException(message);
		return array;
	}
	
	/**
	 * 判断数组array的长度是否等于length
	 * @param array
	 * @param length
	 * @throws IllegalArgumentException 当数组array的长度是不等于length,或者array==null
	 */
	public static <T> T[] length(T[] array, int length, String message, Object... args) {
		if (array == null || array.length != length) throwException(message, args);
		return array;
	}
	
	public static <T> T[] length(T[] array, int length, CodeMsg error) {
		if (array == null || array.length != length) throwError(error);
		return array;
	}
	
	public static <T> Collection<T> notEmpty(Collection<T> collection) {
		return notEmpty(collection, "集合参数至少有一个值");
	}
	
	public static <T> Collection<T> notEmpty(Collection<T> collection, String message) {
		if (Collections.isEmpty(collection)) throwException(message);
		return collection;
	}
	
	public static <T> Collection<T> notEmpty(Collection<T> collection, String message, Object... args) {
		if (Collections.isEmpty(collection)) throwException(message, args);
		return collection;
	}
	
	public static <T> Collection<T> notEmpty(Collection<T> collection, CodeMsg error) {
		if (Collections.isEmpty(collection)) throwError(error);
		return collection;
	}
	
	public static <K, V> Map<K, V> notEmpty(Map<K, V> map) {
		return notEmpty(map, "map至少有一个值");
	}

	public static <K, V> Map<K, V> notEmpty(Map<K, V> map, String message) {
		if (map == null || map.isEmpty()) throwException(message);
		return map;
	}
	
	public static <K, V> Map<K, V> notEmpty(Map<K, V> map, String message, Object... args) {
		if (map == null || map.isEmpty()) throwException(message, args);
		return map;
	}
	
	public static <K, V> Map<K, V> notEmpty(Map<K, V> map, CodeMsg error) {
		if (map == null || map.isEmpty()) throwError(error);
		return map;
	}
	
	public static <T> Class<T> isInstanceOf(Class<T> clazz, Object obj) {
		return isInstanceOf(clazz, obj, "类 [" + (obj != null ? obj.getClass().getName() : "null") +
				"] 必须是 " + clazz + "的一个实例");
	}
	
	public static <T> Class<T> isInstanceOf(Class<T> type, Object obj, String message) {
		notNull(type, "type参数不能为空值");
		if (!type.isInstance(obj)) throwException(message);
		return type;
	}
	
	public static <T> Class<T> isInstanceOf(Class<T> type, Object obj, String message, Object... args) {
		notNull(type, "type参数不能为空值");
		if (!type.isInstance(obj)) throwException(message, args);
		return type;
	}
	
	public static <T> Class<T> isInstanceOf(Class<T> type, Object obj, CodeMsg error) {
		notNull(type, "type参数不能为空值");
		if (!type.isInstance(obj)) throwError(error);
		return type;
	}

	public static <T, S> Class<T> isAssignable(Class<T> superType, Class<S> subType) {
		return isAssignable(superType, subType, subType + " is not assignable to " + superType);
	}
	
	public static <T, S> Class<T> isAssignable(Class<T> superType, Class<S> subType, String message) {
		notNull(superType, "superType参数不能为空值");
		if (subType == null || !superType.isAssignableFrom(subType)) throwException(message);
		return superType;
	}
	
	public static <T, S> Class<T> isAssignable(Class<T> superType, Class<S> subType, String message, Object... args) {
		notNull(superType, "superType参数不能为空值");
		if (subType == null || !superType.isAssignableFrom(subType)) throwException(message, args);
		return superType;
	}
	
	public static <T, S> Class<T> isAssignable(Class<T> superType, Class<S> subType, CodeMsg error) {
		notNull(superType, "superType参数不能为空值");
		if (subType == null || !superType.isAssignableFrom(subType)) throwError(error);
		return superType;
	}

}
