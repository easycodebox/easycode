package com.easycodebox.common.enums.entity;

import com.easycodebox.common.enums.DetailEnum;

public enum YesNo implements DetailEnum<Integer> {
	
	YES(1, "是"),
	NO(0, "否");
	
	private Integer value;
	private String desc;
   
    private YesNo(Integer value, String desc) {

        this.value = value;
        this.desc = desc;
    }

    @Override
	public String toString() {
		return "{desc : '" + desc + "', value : " + value + "}";
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
