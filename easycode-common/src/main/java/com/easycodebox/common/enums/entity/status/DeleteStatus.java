package com.easycodebox.common.enums.entity.status;

import com.easycodebox.common.enums.DetailEnum;

public enum DeleteStatus implements DetailEnum<Integer>{
	
	NEW(0, "新建"),
	USER_DELETE(1, "用户已删除"),	//用户删除在后台可见
	SYS_DELETE(9, "系统删除");	//系统删除不应该被显示、统计
	
	private Integer value;
	private String desc;
   
    private DeleteStatus(Integer value, String desc) {

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
	public String getClassName() {
		return this.name();
	}

	@Override
	public Integer getValue() {
		return this.value;
	}

}
