package com.easycodebox.common.enums.entity;

import com.easycodebox.common.enums.DetailEnum;

public enum Gender implements DetailEnum<Integer> {
	
	MALE(0, "男"),
	FEMALE(1, "女");
	
	private Integer value;
	private String desc;
   
    private Gender(Integer value, String desc) {

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
