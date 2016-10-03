package com.easycodebox.common.enums.entity.status;

import com.easycodebox.common.enums.DetailEnum;

public enum CloseStatus implements DetailEnum<Integer>{
	
	OPEN(0, "启用"),
	CLOSE(1, "禁用"),
	DELETE(9, "系统删除");
	
	private Integer value;
	private String desc;
   
    private CloseStatus(Integer value, String desc) {

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
