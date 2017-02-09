package com.easycodebox.common.lang;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @author WangXiaoJin
 * 
 */
public class DataTypeUtils {
	
	/**
	 * data==null return true;
	 * @param data
	 * @return
	 */
	public static boolean isBasicType(Object data) {
		return data == null || isBasicType(data.getClass());
	}
	
	/**
	 * data==null return true;
	 * @param clazz
	 * @return
	 */
	public static boolean isBasicType(Class<?> clazz) {
		if(clazz == null
				|| CharSequence.class.isAssignableFrom(clazz)
				|| Number.class.isAssignableFrom(clazz) 
				|| Boolean.class.isAssignableFrom(clazz)
				|| Character.class.isAssignableFrom(clazz)
				|| Date.class.isAssignableFrom(clazz)
				|| clazz.isArray()
				|| Collection.class.isAssignableFrom(clazz)
				|| Map.class.isAssignableFrom(clazz)
				|| clazz.isEnum()){
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		Object a = new Date();
		System.out.println(a);
	}
	
}
