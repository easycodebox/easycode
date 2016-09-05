package com.easycodebox.common.enums.entity.status;

import com.easycodebox.common.enums.DetailEnum;

public enum VerifyStatus implements DetailEnum<Integer> {
	
	UNVERIFY(0, "待审核"),
	VERIFIED(1, "通过"),
	REJECT(2,"驳回"),
    USER_DELETE(3, "用户已删除"),	//用户删除在后台可见
	SYS_DELETE(9, "系统删除"),	//系统删除不应该被显示、统计
	;
	
	private Integer value;
	private String desc;
   
    private VerifyStatus(Integer value, String desc) {

        this.value = value;
        this.desc = desc;
    }

    @Override
	public String toString() {
		return "{desc : '" + desc + "', value : " + value + "}";
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
