package com.easycodebox.common.enums.entity;

import com.easycodebox.common.enums.DetailEnum;

public enum OpenClose implements DetailEnum<Integer> {
	
	OPEN(0, "启用"),
	CLOSE(1, "禁用");
	
	private Integer value;
	private String desc;
   
    OpenClose(Integer value, String desc) {

        this.value = value;
        this.desc = desc;
    }
	
	@Override
	public String getDesc() {
		return this.desc;
	}

	@Override
	public Integer getValue() {
		return this.value;
	}
	
	@Override
	public String getClassName() {
		return this.name();
	}

}
