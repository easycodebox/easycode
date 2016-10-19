package com.easycodebox.common.enums.entity.status;

import com.easycodebox.common.enums.DetailEnum;

public enum VerifyStatus implements DetailEnum<Integer> {
	
	UNVERIFY(0, "待审核"),
	REJECTED(1,"驳回"),
	PASSED(2, "通过"),
	;
	
	private Integer value;
	private String desc;
   
    private VerifyStatus(Integer value, String desc) {

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
