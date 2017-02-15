package com.easycodebox.common.idgenerator.impl;

import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.idgenerator.AbstractIdGenerator;
import com.easycodebox.common.lang.Strings;

import java.util.UUID;

/**
 * @author WangXiaoJin
 * 
 */
public final class UuidGenerator extends AbstractIdGenerator<String> {

	private String curVal;
	
	public UuidGenerator() {
		this(1, 500, "1", "1", null, YesNo.NO);
	}
	
	/**
	 * 
	 * @param increment
	 * @param fetchSize
	 * @param initialVal
	 * @param maxVal	可空
	 * @param isCycle
	 */
	public UuidGenerator(int increment, int fetchSize, String initialVal, String currentVal,
	                     String maxVal, YesNo isCycle) {
		super(increment, fetchSize, isCycle);
		this.initialVal = initialVal;
		this.maxVal = Strings.isBlank(maxVal) ? maxVal : null;
	}
	
	@Override
	public String nextVal() {
		return curVal = UUID.randomUUID().toString().replace("-", "");
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
    	UuidGenerator g = new UuidGenerator();
    	for(int i = 0; i < 100; i++) {
    		System.out.println(g.nextVal());
    	}
    }
}
