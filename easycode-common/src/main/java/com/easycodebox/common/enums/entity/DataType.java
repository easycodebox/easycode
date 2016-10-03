package com.easycodebox.common.enums.entity;

import com.easycodebox.common.enums.DetailEnum;

public enum DataType implements DetailEnum<Integer> {
	
	COMMON(0, "普通数据"),
	JSON(1, "JSON数据"),
	MIX(2, "混合数据"),
	;
	
	private Integer value;
	private String desc;
   
    private DataType(Integer value, String desc) {

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
