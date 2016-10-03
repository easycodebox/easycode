package com.easycodebox.common.enums.entity;

import com.easycodebox.common.enums.DetailEnum;

public enum ProjectEnv implements DetailEnum<String> {
	
	DEV("DEV", "开发环境"),
	TEST("TEST", "测试环境"),
	PRE("PRE", "预发环境"),
	PROD("PROD", "生产环境");
	
	private final String value;
	private final String desc;
	
	private ProjectEnv(String value, String desc) {
		this.value = value;
		this.desc = desc;
	}
	
	public String getValue() {
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
