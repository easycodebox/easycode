package com.easycodebox.common.enums.entity.status;

import com.easycodebox.common.enums.DetailEnum;

public enum UserStatus implements DetailEnum<Integer>{
	
	NO_ACTIVE(0, "未激活"),
	ACTIVED(1, "已激活"),
	CLOSED(2, "禁用");
	
	private Integer value;
	private String desc;
   
    private UserStatus(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

	@Override
	public String getDesc() {
		return desc;
	}

	@Override
	public Integer getValue() {
		return value;
	}
	
	@Override
	public String getClassName() {
		return this.name();
	}

}
