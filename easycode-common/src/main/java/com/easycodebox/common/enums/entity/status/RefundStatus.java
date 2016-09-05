package com.easycodebox.common.enums.entity.status;

import com.easycodebox.common.enums.DetailEnum;

public enum RefundStatus implements DetailEnum<Integer>{
	
	CHECK(0, "点点审核中"),
	WX_DISPOSE(1, "微信钱包处理"),
	ZFB_DISPOSE(2, "支付宝处理"),
	CFT_DISPOSE(3, "财付通处理"),
	BANK_DISPOSE(4, "银行处理中"),
	OTHER_DISPOSE(5, "其他付款方式处理中"),
	REFUND(6, "退款成功");
	
	private Integer value;
	private String desc;
   
    private RefundStatus(Integer value, String desc) {

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
