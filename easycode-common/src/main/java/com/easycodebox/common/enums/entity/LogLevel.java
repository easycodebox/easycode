package com.easycodebox.common.enums.entity;

import com.easycodebox.common.enums.DetailEnum;


public enum LogLevel implements DetailEnum<Integer> {
	
	TRACE(0, "追踪"),
	DEBUG(1, "调试"),
	INFO(2, "提示"),
	WARN(3, "警告"),
	ERROR(4, "错误"),
	FATAL(5, "严重错误");
	
	private final Integer value;
	private final String desc;
	
	private LogLevel(int value, String desc) {
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
	public String toString() {
		return "{desc : '" + desc + "', value : " + value + "}";
	}
	
	@Override
	public String getClassName() {
		return this.name();
	}
}
