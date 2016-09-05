package com.easycodebox.common;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author WangXiaoJin
 * 
 */
public class Pair<T> {
	
	private T first;
	
	private T second;
	
	public Pair() {
		
	}
	
	public Pair(T first, T second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
		.append("first", first)
		.append("second", second).toString();
	}
	
	public synchronized T getFirst() {
		return first;
	}

	public synchronized void setFirst(T first) {
		this.first = first;
	}
	
	public synchronized T getSecond() {
		return second;
	}

	public synchronized void setSecond(T second) {
		this.second = second;
	}

	public synchronized void swap() {
		T backup = this.first;
		this.first = this.second;
		this.second = backup;
	}
	
}
