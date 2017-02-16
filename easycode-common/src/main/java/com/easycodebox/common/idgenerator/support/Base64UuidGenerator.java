package com.easycodebox.common.idgenerator.support;

import com.easycodebox.common.algorithm.Base64UUID;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.idgenerator.AbstractIdGenerator;

/**
 * @author WangXiaoJin
 * 
 */
public final class Base64UuidGenerator extends AbstractIdGenerator<String> {
	
	private String curVal;

	public Base64UuidGenerator() {
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
	public Base64UuidGenerator(int increment, int fetchSize, String initialVal, String currentVal,
	                           String maxVal, YesNo isCycle) {
		super(increment, fetchSize, isCycle);
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
    	Base64UuidGenerator g = new Base64UuidGenerator();
    	for(int i = 0; i < 100; i++) {
    		System.out.println(g.nextVal());
    	}
    }

}
