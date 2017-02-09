package com.easycodebox.auth.model.enums;

import com.easycodebox.common.enums.DetailEnum;


public enum MsgType implements DetailEnum<Integer> {
	
	SYSTEM(0, "系统消息"),
	USER(1, "用户消息"),
	;
	
	private final Integer value;
	private final String desc;
	
	MsgType(int value, String desc) {
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
