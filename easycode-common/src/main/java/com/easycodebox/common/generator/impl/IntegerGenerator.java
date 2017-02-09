package com.easycodebox.common.generator.impl;

import java.util.concurrent.atomic.AtomicInteger;

import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.generator.AbstractGenerator;
import com.easycodebox.common.generator.exception.BoundReachedException;
import com.easycodebox.common.lang.Strings;

/**
 * @author WangXiaoJin
 * 
 */
public final class IntegerGenerator extends AbstractGenerator<Integer> {
	
	private final AtomicInteger curVal;
	
	public IntegerGenerator() {
		this(1, 500, "359", "359", null, YesNo.NO);
	}
	
	/**
	 * 
	 * @param increment
	 * @param fetchSize
	 * @param initialVal
	 * @param maxVal	可空
	 * @param isCycle
	 */
	public IntegerGenerator(int increment, int fetchSize
				, String initialVal, String currentVal, 
				String maxVal, YesNo isCycle) {
		super(increment, fetchSize, initialVal, currentVal, maxVal, isCycle);
		this.initialVal = Integer.parseInt(initialVal);
		this.maxVal = Strings.isBlank(maxVal) ?
				Integer.MAX_VALUE : Integer.parseInt(maxVal);
		this.curVal = new AtomicInteger(Integer.parseInt(currentVal));
	}

	@Override
	public Integer nextVal() {
		//如果起始值没有使用过则返回起始值
		if(hadUsedBeginVal.compareAndSet(false, true)) {
			genNum++;
			return this.curVal.get();
		}
		if(genNum >= fetchSize) return null;
		for (;;) {
            int current = curVal.get(),
            	next = current + increment;
            if(!((current < next && next <= maxVal)
            		|| (current > next && next >= maxVal))) {
            	//值超出最大范围
            	if(isCycle == YesNo.YES)
            		next = initialVal;
            	else
            		throw new BoundReachedException("IntegerGenerator had reached max value.");
            } 
        	if(curVal.compareAndSet(current, next)){
        		genNum++;
        		return next;
        	}
        }
	}
	
	@Override
	public Integer currentVal() {
		return curVal.intValue();
	}
	
    @Override
	public Integer nextStepVal(String curVal) {
    	Integer curInt = Integer.parseInt(curVal);
    	//设置当前值段为 新传入的curVal~ 新nextStepVal
    	this.curVal.set(curInt);
    	this.genNum = 0;
    	this.hadUsedBeginVal.set(false);
		return curInt + fetchSize*increment;
	}

	public static void main(String args[]) {
    	IntegerGenerator g = new IntegerGenerator();
    	for(int i = 0; i < 100; i++) {
    		System.out.println(g.nextVal());
    	}
    }
}
