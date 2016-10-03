package com.easycodebox.common.enums.entity.status;

import com.easycodebox.common.enums.DetailEnum;

public enum CloseDelStatus implements DetailEnum<Integer>{
	
	OPEN(0, "启用"),
	CLOSE(1, "禁用"),
	USER_DELETE(2, "用户删除"),	//用户删除在后台可见
	SYS_DELETE(9, "系统删除");	//系统删除不应该被显示、统计
	
	private Integer value;
	private String desc;
   
    private CloseDelStatus(Integer value, String desc) {

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
