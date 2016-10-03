package com.easycodebox.common.enums.entity;

import com.easycodebox.common.enums.DetailEnum;


public enum PhoneType implements DetailEnum<Integer> {
	
	IPHONE(1, "IPHONE"), 
	ANDROID(2, "ANDROID"),
	IPAD(4,"IPAD"),
	OTHER(3, "OTHER");
	
	private final Integer value;
	private final String desc;
	
	private PhoneType(int value, String desc) {
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
