package com.easycodebox.common.generator.impl;

import java.util.concurrent.atomic.AtomicLong;

import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.generator.AbstractGenerator;
import com.easycodebox.common.generator.exception.BoundReachedException;
import com.easycodebox.common.lang.Strings;

/**
 * @author WangXiaoJin
 * 
 */
public final class LongGenerator extends AbstractGenerator<Long> {
	//
	private final AtomicLong curVal;
	
	public LongGenerator() {
		this(1, 500, "10059", "10059", null, YesNo.NO);
	}
	
	/**
	 * @param increment
	 * @param fetchSize
	 * @param initialVal
	 * @param maxVal	可空
	 * @param isCycle
	 */
	public LongGenerator(int increment, int fetchSize
				, String initialVal, String currentVal, 
				String maxVal, YesNo isCycle) {
		super(increment, fetchSize, initialVal, currentVal, maxVal, isCycle);
		this.initialVal = Long.parseLong(initialVal);
		this.maxVal = Strings.isBlank(maxVal) ?
				Long.MAX_VALUE : Long.parseLong(maxVal);
		this.curVal = new AtomicLong(Long.parseLong(currentVal));
	}

	@Override
	public Long nextVal() {
		//如果起始值没有使用过则返回起始值
		if(hadUsedBeginVal.compareAndSet(false, true)) {
			genNum++;
			return this.curVal.get();
		}
		if(genNum >= fetchSize) return null;
		for (;;) {
            long current = curVal.get(),
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
	public Long currentVal() {
		return curVal.longValue();
	}
	
    @Override
	public Long nextStepVal(String curVal) {
    	Long curInt = Long.parseLong(curVal);
    	//设置当前值段为 新传入的curVal~ 新nextStepVal
    	this.curVal.set(curInt);
    	this.genNum = 0;
    	this.hadUsedBeginVal.set(false);
		return curInt + fetchSize*increment;
	}
    
    public static void main(String args[]) {
    	LongGenerator g = new LongGenerator();
    	for(int i = 0; i < 100; i++) {
    		System.out.println(g.nextVal());
    	}
    }
}
