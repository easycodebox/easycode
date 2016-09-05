package com.easycodebox.common.generator.impl;

import com.easycodebox.common.algorithm.Base64UUID;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.generator.AbstractGenerator;

/**
 * @author WangXiaoJin
 * 
 */
public final class Base64UUIDGenerator extends AbstractGenerator<String> {
	
	private String curVal;

	public Base64UUIDGenerator() {
		this(1, 500, "0", "0", null, YesNo.NO);
	}
	
	/**
	 * 
	 * @param increment
	 * @param fetchSize
	 * @param initialVal
	 * @param maxVal	可空
	 * @param isCycle
	 */
	public Base64UUIDGenerator(int increment, int fetchSize
				, String initialVal, String currentVal, 
				String maxVal, YesNo isCycle) {
		super(increment, fetchSize, initialVal, currentVal, maxVal, isCycle);
		this.initialVal = initialVal;
		this.maxVal = maxVal;
		this.curVal = currentVal;
	}
	
	@Override
	public String nextVal() {
		return curVal = Base64UUID.compressUUID();
	}
	
	@Override
	public String currentVal() {
		return curVal;
	}
	
	@Override
	public String nextStepVal(String curVal) {
		return null;
	}
    
    public static void main(String args[]) {
    	Base64UUIDGenerator g = new Base64UUIDGenerator();
    	for(int i = 0; i < 100; i++) {
    		System.out.println(g.nextVal());
    	}
    }

}
