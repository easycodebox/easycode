package com.easycodebox.common.validate;

import java.util.Collection;
import java.util.Map;

import com.easycodebox.common.enums.entity.LogLevel;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.error.ErrorContext;
import com.easycodebox.common.lang.CollectionUtils;
import com.easycodebox.common.lang.StringUtils;

public class Assert {

	/**
	 * 当exp = true 时，抛出异常
	 * @param exp
	 */
	private static void throwException(boolean exp, String message, Object... args) {
		if(exp) throw new IllegalArgumentException(StringUtils.format(message, args));
	}
	
	private static void throwError(boolean exp, String code, String message, Object... args) {
		if(exp) throw ErrorContext.instance(code, StringUtils.format(message, args)).logLevel(LogLevel.WARN);
	}
	
	public static void isTrue(boolean expression) {
		isTrue(expression, "表达式必须等于true");
	}
	
	public static void isTrue(boolean expression, String message, Object... args) {
		throwException(!expression, message, args);
	}
	
	public static void isTrue(boolean expression, CodeMsg error, Object... args) {
		isTrue(expression, error.getCode(), error.getMsg(), args);
	}
	
	public static void isTrue(boolean expression, String code, String message, Object... args) {
		throwError(!expression, code, message, args);
	}

	public static void isFalse(boolean expression) {
		isFalse(expression, "表达式必须等于false");
	}
	
	public static void isFalse(boolean expression, String message, Object... args) {
		throwException(expression, message, args);
	}
	
	public static void isFalse(boolean expression, CodeMsg error, Object... args) {
		isFalse(expression, error.getCode(), error.getMsg(), args);
	}
	
	public static void isFalse(boolean expression, String code, String message, Object... args) {
		throwError(expression, code, message, args);
	}
	
	public static void isNull(Object object) {
		isNull(object, "参数必须为null");
	}
	
	public static void isNull(Object object, String message, Object... args) {
		throwException(object != null, message, args);
	}
	
	public static void isNull(Object object, CodeMsg error, Object... args) {
		isNull(object, error.getCode(), error.getMsg(), args);
	}
	
	public static void isNull(Object object, String code, String message, Object... args) {
		throwError(object != null, code, message, args);
	}
	
	public static void notNull(Object object) {
		notNull(object, "参数不能为null");
	}
	
	public static void notNull(Object object, String message, Object... args) {
		throwException(object == null, message, args);
	}
	
	public static void notNull(Object object, CodeMsg error, Object... args) {
		notNull(object, error.getCode(), error.getMsg(), args);
	}
	
	public static void notNull(Object object, String code, String message, Object... args) {
		throwError(object == null, code, message, args);
	}
	
	public static void isBlank(String text) {
		isBlank(text, "参数应为空或空格字符");
	}
	
	public static void isBlank(String text, String message, Object... args) {
		throwException(!StringUtils.isBlank(text), message, args);
	}
	
	public static void isBlank(String text, CodeMsg error, Object... args) {
		isBlank(text, error.getCode(), error.getMsg(), args);
	}
	
	public static void isBlank(String text, String code, String message, Object... args) {
		throwError(!StringUtils.isBlank(text), code, message, args);
	}
	
	public static void notBlank(String text) {
		notBlank(text, "参数不能为空或空格字符");
	}
	
	public static void notBlank(String text, String message, Object... args) {
		throwException(StringUtils.isBlank(text), message, args);
	}
	
	public static void notBlank(String text, CodeMsg error, Object... args) {
		notBlank(text, error.getCode(), error.getMsg(), args);
	}
	
	public static void notBlank(String text, String code, String message, Object... args) {
		throwError(StringUtils.isBlank(text), code, message, args);
	}
	
	public static void notContain(String textToSearch, String substring) {
		notContain(textToSearch, substring,
				"字符窜[" + textToSearch + "] 不能包含子字符窜[" + substring + "]");
	}
	
	public static void notContain(String textToSearch, String substring, String message, Object... args) {
		notNull(textToSearch);
		notNull(substring);
		throwException(textToSearch.indexOf(substring) != -1, message, args);
	}
	
	public static void notContain(String textToSearch, String substring, CodeMsg error, Object... args) {
		notContain(textToSearch, substring, error.getCode(), error.getMsg(), args);
	}
	
	public static void notContain(String textToSearch, String substring, 
			String code, String message, Object... args) {
		notNull(textToSearch);
		notNull(substring);
		throwError(textToSearch.indexOf(substring) != -1, code, message, args);
	}
	
	public static void notEmpty(Object[] array) {
		notEmpty(array, "数组参数不能为空");
	}
	
	public static void notEmpty(Object[] array, String message, Object... args) {
		throwException(array == null || array.length == 0, message, args);
	}
	
	public static void notEmpty(Object[] array, CodeMsg error, Object... args) {
		notEmpty(array, error.getCode(), error.getMsg(), args);
	}
	
	public static void notEmpty(Object[] array, String code, String message, Object... args) {
		throwError(array == null || array.length == 0, code, message, args);
	}

	public static void noNullElements(Object[] array) {
		noNullElements(array, "数组参数不应有空值");
	}
	
	public static void noNullElements(Object[] array, String message, Object... args) {
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] == null) {
					throwException(true, message, args);
				}
			}
		}
	}
	
	public static void noNullElements(Object[] array, CodeMsg error, Object... args) {
		noNullElements(array, error.getCode(), error.getMsg(), args);
	}
	
	public static void noNullElements(Object[] array, String code, String message, Object... args) {
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] == null) {
					throwError(true, code, message, args);
				}
			}
		}
	}
	
	/**
	 * 判断数组array的长度是否等于length
	 * @param array
	 * @param length
	 * @throws IllegalArgumentException 当数组array的长度是不等于length,或者array==null
	 */
	public static void length(Object[] array, int length) {
		length(array, length, "数组参数长度应等于" + length);
	}
	
	/**
	 * 判断数组array的长度是否等于length
	 * @param array
	 * @param length
	 * @throws IllegalArgumentException 当数组array的长度是不等于length,或者array==null
	 */
	public static void length(Object[] array, int length, String message, Object... args) {
		throwException(array == null || array.length != length, message, args);
	}
	
	public static void length(Object[] array, int length, CodeMsg error, Object... args) {
		length(array, length, error.getCode(), error.getMsg(), args);
	}
	
	public static void length(Object[] array, int length, String code, String message, Object... args) {
		throwError(array == null || array.length != length, code, message, args);
	}
	
	public static void notEmpty(Collection<?> collection) {
		notEmpty(collection, "集合参数至少有一个值");
	}
	
	public static void notEmpty(Collection<?> collection, String message, Object... args) {
		throwException(CollectionUtils.isEmpty(collection), message, args);
	}
	
	public static void notEmpty(Collection<?> collection, CodeMsg error, Object... args) {
		notEmpty(collection, error.getCode(), error.getMsg(), args);
	}
	
	public static void notEmpty(Collection<?> collection, String code, String message, Object... args) {
		throwError(CollectionUtils.isEmpty(collection), code, message, args);
	}
	
	public static void notEmpty(Map<?, ?> map) {
		notEmpty(map, "map至少有一个值");
	}

	public static void notEmpty(Map<?, ?> map, String message, Object... args) {
		throwException(map == null || map.isEmpty(), message, args);
	}
	
	public static void notEmpty(Map<?, ?> map, CodeMsg error, Object... args) {
		notEmpty(map, error.getCode(), error.getMsg(), args);
	}
	
	public static void notEmpty(Map<?, ?> map, String code, String message, Object... args) {
		throwError(map == null || map.isEmpty(), code, message, args);
	}
	
	public static void isInstanceOf(Class<?> clazz, Object obj) {
		isInstanceOf(clazz, obj, "类 [" + (obj != null ? obj.getClass().getName() : "null") +
				"] 必须是 " + clazz + "的一个实例");
	}
	
	public static void isInstanceOf(Class<?> type, Object obj, String message, Object... args) {
		notNull(type, "type参数不能为空值");
		throwException(!type.isInstance(obj), message, args);
	}
	
	public static void isInstanceOf(Class<?> clazz, Object obj, CodeMsg error, Object... args) {
		isInstanceOf(clazz, obj, error.getCode(), error.getMsg(), args);
	}

	public static void isInstanceOf(Class<?> type, Object obj, String code, String message, Object... args) {
		notNull(type, "type参数不能为空值");
		throwError(!type.isInstance(obj), code, message, args);
	}

	@SuppressWarnings("rawtypes")
	public static void isAssignable(Class superType, Class subType) {
		isAssignable(superType, subType, subType + " is not assignable to " + superType);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void isAssignable(Class superType, Class subType, String message, Object... args) {
		notNull(superType, "superType参数不能为空值");
		throwException(subType == null || !superType.isAssignableFrom(subType), message, args);
	}
	
	@SuppressWarnings("rawtypes")
	public static void isAssignable(Class superType, Class subType, CodeMsg error, Object... args) {
		isAssignable(superType, subType, error.getCode(), error.getMsg(), args);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void isAssignable(Class superType, Class subType, String code, String message, Object... args) {
		notNull(superType, "superType参数不能为空值");
		throwError(subType == null || !superType.isAssignableFrom(subType), code, message, args);
	}
	

}
