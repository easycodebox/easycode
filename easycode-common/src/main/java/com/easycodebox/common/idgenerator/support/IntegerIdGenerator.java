package com.easycodebox.common.idgenerator.support;

import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.idgenerator.AbstractIdGenerator;
import com.easycodebox.common.idgenerator.exception.BoundReachedException;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author WangXiaoJin
 * 
 */
public final class IntegerIdGenerator extends AbstractIdGenerator<Integer> {
	
	private final AtomicInteger curVal;
	
	public IntegerIdGenerator() {
		this(1, 500, 359, 359, null, YesNo.NO);
	}
	
	/**
	 * 
	 * @param increment
	 * @param fetchSize
	 * @param initialVal
	 * @param maxVal	可空
	 * @param isCycle
	 */
	public IntegerIdGenerator(int increment, int fetchSize, Integer initialVal, Integer currentVal,
	                          Integer maxVal, YesNo isCycle) {
		super(increment, fetchSize, isCycle);
		this.initialVal = initialVal;
		this.maxVal = maxVal == null ? Integer.MAX_VALUE : maxVal;
		this.curVal = new AtomicInteger(currentVal);
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
            		throw new BoundReachedException("IntegerIdGenerator had reached max value.");
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
    	IntegerIdGenerator g = new IntegerIdGenerator();
    	for(int i = 0; i < 100; i++) {
    		System.out.println(g.nextVal());
    	}
    }
}
