package com.easycodebox.common.idgenerator.support;

import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.idgenerator.exception.BoundReachedException;

/**
 * @author WangXiaoJin
 * 
 */
public final class AlphaNumericIdGenerator extends AbstractStringIdGenerator {
    
	public AlphaNumericIdGenerator() {
		this(1, 500, "a15b6", "a15b6", null, YesNo.NO);
	}
	
	/**
	 * 
	 * @param increment
	 * @param fetchSize
	 * @param initialVal
	 * @param maxVal	可空
	 * @param isCycle
	 */
	public AlphaNumericIdGenerator(int increment, int fetchSize, String initialVal, String currentVal,
	                               String maxVal, YesNo isCycle) {
		super(increment, fetchSize, isCycle);
		this.initialVal = initialVal.toLowerCase();
		this.maxVal = maxVal == null ? null : maxVal.toLowerCase();
		this.curVal = currentVal.toLowerCase();
	}
    
	@Override
    public synchronized String nextVal() {
		//如果起始值没有使用过则返回起始值
		if(hadUsedBeginVal.compareAndSet(false, true)) {
			genNum++;
			return curVal;
		}
		if(genNum >= fetchSize) return null;
		
		String next = addValue(increment);
		if(maxVal != null) {
			if(!((compare(curVal, next) < 0  && compare(next, maxVal) <= 0)
		    		|| (compare(curVal, next) > 0 && compare(next, maxVal) >= 0))) {
		    	//值超出最大范围
		    	if(isCycle == YesNo.YES)
		    		next = initialVal;
		    	else
		    		throw new BoundReachedException("AlphaNumericIdGenerator had reached max value.");
		    } 
		}
		genNum++;
		return curVal = next;
    }
	
	@Override
	public String currentVal() {
		return curVal;
	}
	
	private String addValue(int val) {
		int fragLength = 12,
			num = curVal.length()%fragLength != 0 ? 
					curVal.length()/fragLength + 1 : curVal.length()/fragLength,
			endIndex = curVal.length();
		String[] fragVals = new String[num];
		//初始化fragVals里面的值
		for(int i = num - 1; i >= 0; i--) {
			int bi = curVal.length() - fragLength*(num - i);
			bi = bi < 0 ? 0 : bi;
			fragVals[i] = curVal.substring(bi, endIndex);
			endIndex = bi;
		}
		//增加指定的值
		for(int i = fragVals.length - 1; i >= 0; i--) {
			String addedVal = Long.toString(Long.parseLong(fragVals[i], 36) + val, 36);
			int overflowLen = addedVal.length() - fragLength;
			if(overflowLen > 0) {
				String t = addedVal.substring(0, overflowLen);
				//设置低一位的值
				fragVals[i] = addedVal.substring(overflowLen);
				val = Integer.parseInt(t);
			}else {
				fragVals[i] = addedVal;
				break;
			}
		}
		String newVal = Strings.join(fragVals, "");
		int zeroNum = initialVal.length() - newVal.length();
		if(zeroNum > 0)
			newVal = Strings.repeat("0", zeroNum) + newVal;
		return newVal;
	}
	
	
	@Override
	public synchronized String nextStepVal(String curVal) {
    	//设置当前值段为 新传入的curVal~ 新nextStepVal
    	this.curVal = curVal;
    	this.genNum = 0;
    	this.hadUsedBeginVal.set(false);
		return addValue(fetchSize*increment);
	}

    public static void main(String args[]) {
    	AlphaNumericIdGenerator g = new AlphaNumericIdGenerator(1, 100
				, "000100", "000100", 
				"100100", YesNo.NO);
    	for(int i = 0; i < 100; i++) {
    		System.out.println(g.nextVal());
    	}
    }
}
