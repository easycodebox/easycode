package com.easycodebox.common.lang;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author WangXiaoJin
 * 
 */
public abstract class CollectionUtils extends org.apache.commons.collections.CollectionUtils {

	/**
	 * 
	 */
	@SafeVarargs
	public static <T> Set<T> toSet(T... values) {
		// Precondition checking
		if(values == null) {
			return new HashSet<T>(0);
		}
		
		//
		Set<T> r = new HashSet<T>(values.length);
		for(T t : values) {
			r.add(t);
		}
		return r;
	}
	
	@SafeVarargs
	public static <T> List<T> toList(T... values) {
		// Precondition checking
		if(values == null) {
			return new ArrayList<T>(0);
		}
		
		//
		List<T> r = new ArrayList<T>(values.length);
		for(T t : values) {
			r.add(t);
		}
		return r;
	}
}
