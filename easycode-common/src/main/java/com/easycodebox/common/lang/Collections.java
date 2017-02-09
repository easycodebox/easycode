package com.easycodebox.common.lang;

import java.util.*;

/**
 * @author WangXiaoJin
 * 
 */
public abstract class Collections extends org.apache.commons.collections.CollectionUtils {

	@SafeVarargs
	public static <T> Set<T> toSet(T... values) {
		// Precondition checking
		if(values == null) {
			return new HashSet<>(0);
		}
		
		Set<T> r = new HashSet<>(values.length);
		java.util.Collections.addAll(r, values);
		return r;
	}
	
	@SafeVarargs
	public static <T> List<T> toList(T... values) {
		// Precondition checking
		if(values == null) {
			return new ArrayList<>(0);
		}
		
		//
		List<T> r = new ArrayList<>(values.length);
		java.util.Collections.addAll(r, values);
		return r;
	}
}
