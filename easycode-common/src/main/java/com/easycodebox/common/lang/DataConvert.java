package com.easycodebox.common.lang;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.beanutils.BeanUtils;

import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.lang.reflect.ClassUtils;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

/**
 * 数据类型转换工具 <br>
 * 注：后期会转用第三方工具，使用之前综合比较下。可能会使用org.apache.commons.beanutils.ConvertUtils.convert()。
 * @author WangXiaoJin
 * 
 */
public class DataConvert {
	
	private static final Logger log = LoggerFactory.getLogger(DataConvert.class);
	
	public static final String separator = ",";
	
	/**
	 * 转换数组类型 <br>
	 * 注：后期会转用第三方工具，使用之前综合比较下。可能会使用org.apache.commons.beanutils.ConvertUtils.convert()。
	 * @param <T>
	 * @param obj  为aa,bb,cc 类型
	 * @param clazz
	 * @return
	 */
	public static <T> T[] convertArray(String obj, Class<T[]> clazz, String separator) throws BaseException {
		if(StringUtils.isBlank(obj)) throw new BaseException("obj is null.");
		if(StringUtils.isBlank(separator))
			separator = DataConvert.separator;
		String[] s = obj.replaceAll("[\\[\\]]", "").split(separator);
		return convertArray(s, clazz);
	}
	
	/**
	 * 注：后期会转用第三方工具，使用之前综合比较下。可能会使用org.apache.commons.beanutils.ConvertUtils.convert()。
	 * @param obj
	 * @param clazz
	 * @return
	 * @throws BaseException
	 */
	public static <T> T[] convertArray(String obj, Class<T[]> clazz) throws BaseException {
		return convertArray(obj, clazz, separator);
	}
	
	/**
	 * 注：后期会转用第三方工具，使用之前综合比较下。可能会使用org.apache.commons.beanutils.ConvertUtils.convert()。
	 * @param objs
	 * @param clazz
	 * @return
	 * @throws BaseException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] convertArray(String[] objs, Class<T[]> clazz) throws BaseException {
		if(objs == null) return null;
		if((Object)clazz == (Object)Object[].class)
			return (T[])objs;
		T[] copy = (T[]) Array.newInstance(clazz.getComponentType(), objs.length);
        int i = 0;  				
		for(String temp : objs) 
			copy[i++] = (T)convertType(temp, clazz.getComponentType());
		return copy;
	}
	
	/**
	 * 注：后期会转用第三方工具，使用之前综合比较下。可能会使用org.apache.commons.beanutils.ConvertUtils.convert()。
	 * @param obj
	 * @param clazz
	 * @return
	 * @throws BaseException
	 */
	public static <T> T convertType(String obj, Class<T> clazz)  throws BaseException {
		return convertType(obj, clazz, null);
	}
	
	/**
	 * 转换数据类型包括数组 <br>
	 * 注：后期会转用第三方工具，使用之前综合比较下。可能会使用org.apache.commons.beanutils.ConvertUtils.convert()。
	 * @param <T>
	 * @param obj
	 * @param clazz
	 * @return
	 * @throws BaseException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T convertType(String obj, Class<T> clazz, String separator)  throws BaseException {
		if(clazz == null) throw new BaseException("you except convert type is null.");
		if(obj == null) return null;
		if(String.class.isAssignableFrom(clazz)) return (T)obj;
		//去除字符窜首尾的单引号和双引号
		obj = obj.trim().replaceAll("^['\"]*([^'\"]*)['\"]*$", "$1");
		T t = null;
		if(clazz.isArray()) {
			if(StringUtils.isBlank(separator))
				separator = DataConvert.separator;
			String[] os = obj.replaceAll("[\\[\\]]", "").split(separator);
			t = (T)Array.newInstance(clazz.getComponentType(), os.length);
	        int i = 0;  				
			for(String temp : os) 
				Array.set(t, i++, convertType(temp, clazz.getComponentType()));
		}else{
			clazz = ClassUtils.primitiveToWrapper(clazz);
			if(Number.class.isAssignableFrom(clazz)) {
				try {
					Constructor<T> c = clazz.getConstructor(String.class);
					t = c.newInstance(obj);
				} catch (Exception e) {
					//throw new BaseException("convert value error in obtainInfoVal method.");
				} 
			}else if(DetailEnum.class.isAssignableFrom(clazz)) {
				boolean handled = false;
				Object handledVal = obj;
				for(T e : clazz.getEnumConstants()) {
					DetailEnum enumVal = (DetailEnum)e;
					Object val = enumVal.getValue();
					if(!handled && !val.getClass().isAssignableFrom(String.class)) {
						handledVal = convertType(obj, val.getClass());
					}
					if(handledVal.equals(val)) {
						t = e;
						break;
					}
				}
			}else if(Enum.class.isAssignableFrom(clazz)) {
				boolean suc = false;
				for(T e : clazz.getEnumConstants()) {
					Enum en = (Enum)e;
					String name = en.name();
					if(name.equals(obj)) {
						t = e;
						suc = true;
						break;
					}
				}
				if(!suc && obj.matches("[0-9]+")) {
					for(T e : clazz.getEnumConstants()) {
						Enum en = (Enum)e;
						int index = en.ordinal();
						if(Integer.parseInt(obj) == index) {
							t = e;
							suc = true;
							break;
						}
					}
				}
			}else if(Date.class.isAssignableFrom(clazz)) {
				if(StringUtils.isBlank(obj))
					t = null;
				else if(StringUtils.isNumeric(obj))
					t = (T)new Date(Long.parseLong(obj));
				else
					t = (T)DateUtils.parse(obj); 
			}else if(Calendar.class.isAssignableFrom(clazz)) {
				t = (T)DateUtils.parse2Calenar(obj.toString()); 
			}else if(Boolean.class.isAssignableFrom(clazz)) {
				t = (T)new Boolean(obj);
			}else {
				t = (T)obj;
			}
		}
		return t;
	}
	
	/**
	 * 数组或者集合组装成string格式 <br>
	 * 注：后期会转用第三方工具，使用之前综合比较下。可能会使用org.apache.commons.beanutils.ConvertUtils.convert()。
	 * @param <T>
	 * @param data
	 * @param separator
	 * @param prefix
	 * @param postfix
	 * @param forceOneVal 是否强制只取集合里面的对象一个属性值
	 * @param props 需要的封装的属性名
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static <T> String arrayCollection2Str(Object data, String separator, 
			String prefix, String postfix, boolean forceOneVal, String... props ) {
		if(data == null) return Symbol.EMPTY;
		if(!data.getClass().isArray()
				&& !(data instanceof Collection))
			throw new IllegalArgumentException("data is not array or collection");
		StringBuilder sb = new StringBuilder();
		if(prefix != null)
			sb.append(prefix);
		if(data.getClass().isArray()) {
			int length = Array.getLength(data);
			for(int i = 0; i < length; i++) {
				Object o = Array.get(data, i);
				if(o.getClass().isArray()
						|| o instanceof Collection)
					sb.append(arrayCollection2Str(o, separator, prefix, postfix, forceOneVal, props));
				else {
					sb.append(Object2String(o, forceOneVal, props));
				}
				if(i < length - 1) 
					sb.append(separator);
			}
		}else if(data instanceof Collection) {
			Collection c = (Collection)data;
			Iterator it = c.iterator();
			for(int i = 0; it.hasNext(); i++) {
				Object o = it.next();
				if(o.getClass().isArray()
						|| o instanceof Collection)
					sb.append(arrayCollection2Str(o, separator, prefix, postfix, forceOneVal, props));
				else {
					sb.append(Object2String(o, forceOneVal, props));
				}
				if(i < c.size() - 1)
					sb.append(separator);
			}
		}
		if(postfix != null)
			sb.append(postfix);
		return sb.toString();
	}
	
	/**
	 * 注：后期会转用第三方工具，使用之前综合比较下。可能会使用org.apache.commons.beanutils.ConvertUtils.convert()。
	 * @param data
	 * @param separator
	 * @param prefix
	 * @param postfix
	 * @param forceOneVal
	 * @param props
	 * @return
	 */
	public static <T> String array2String(T[] data, String separator, 
			String prefix, String postfix, boolean forceOneVal, String... props) {
		if(data == null) return Symbol.EMPTY;
		StringBuilder sb = new StringBuilder();
		if(prefix != null)
			sb.append(prefix);
		for(int i = 0; i < data.length; i++) {
			sb.append(Object2String(data[i], forceOneVal, props));
			if(i < data.length - 1)
				sb.append(separator);
		}
		if(postfix != null)
			sb.append(postfix);
		return sb.toString();
	}
	
	/**
	 * 注：后期会转用第三方工具，使用之前综合比较下。可能会使用org.apache.commons.beanutils.ConvertUtils.convert()。
	 * @param data 不能为集合
	 * @param forceOneVal 是否强制只取集合里面的对象一个属性值
	 * @param props	包含的属性
	 * @return
	 */
	public static String Object2String(Object data, boolean forceOneVal, String... props) {
		if(data == null) return Symbol.EMPTY;
		if(data.getClass().isArray()
				|| data instanceof Collection)
			throw new IllegalArgumentException("data 参数不能为集合");
		StringBuilder sb = new StringBuilder();
		if(forceOneVal) {
			if(!DataTypeUtils.isBasicType(data)
					&& props != null
					&& props.length > 0
					&& props[0] != null) {
				try { 
					sb.append(BeanUtils.getProperty(data, props[0]));
				} catch (Exception e) {
					log.error("获取属性值错误", e);
				}
			}else
				sb.append(data.toString());
		}else {
			if(DataTypeUtils.isBasicType(data)) {
				sb.append(data.toString());
			}else {
				sb.append(Symbol.L_BRACE);
				if(props != null
						&& props.length > 0
						&& props[0] != null) {
					for(int j = 0; j < props.length; j++) {
						try {
							sb.append(props[j]).append(Symbol.COLON)
								.append(BeanUtils.getProperty(data, props[j]));
							if(j < props.length - 1)
								sb.append(Symbol.COMMA);
						} catch (Exception e) {
							log.error("获取属性值错误", e);
						}
					}
				}
				sb.append(Symbol.R_BRACE);
			}
		}
		return sb.toString();
	}
	
}
