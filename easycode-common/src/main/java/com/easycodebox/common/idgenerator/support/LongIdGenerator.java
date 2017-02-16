package com.easycodebox.common.idgenerator.support;

import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.idgenerator.AbstractIdGenerator;
import com.easycodebox.common.idgenerator.exception.BoundReachedException;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author WangXiaoJin
 * 
 */
public final class LongIdGenerator extends AbstractIdGenerator<Long> {
	//
	private final AtomicLong curVal;
	
	public LongIdGenerator() {
		this(1, 500, 10059L, 10059L, null, YesNo.NO);
	}
	
	/**
	 * @param increment
	 * @param fetchSize
	 * @param initialVal
	 * @param maxVal	可空
	 * @param isCycle
	 */
	public LongIdGenerator(int increment, int fetchSize, Long initialVal, Long currentVal,
	                       Long maxVal, YesNo isCycle) {
		super(increment, fetchSize, isCycle);
		this.initialVal = initialVal;
		this.maxVal = maxVal == null ? Long.MAX_VALUE : maxVal;
		this.curVal = new AtomicLong(currentVal);
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
            		throw new BoundReachedException("LongIdGenerator had reached max value.");
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
    	LongIdGenerator g = new LongIdGenerator();
    	for(int i = 0; i < 100; i++) {
    		System.out.println(g.nextVal());
    	}
    }
}
