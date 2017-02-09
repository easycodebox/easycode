package com.easycodebox.common.enums.entity;

import com.easycodebox.common.enums.DetailEnum;


public enum RequestMethod implements DetailEnum<Integer> {
	
	GET(1, "GET"),
	POST(2, "POST"),
	PUT(3, "PUT"),
	HEAD(2, "HEAD");
	
	private final Integer value;
	private final String desc;
	
	RequestMethod(int value, String desc) {
		this.value = value;
		this.desc = desc;
	}
	
	public Integer getValue() {
		return this.value;
	}

	public String getDesc() {
		return this.desc;
	}
	
	@Override
	public String getClassName() {
		return this.name();
	}
}
