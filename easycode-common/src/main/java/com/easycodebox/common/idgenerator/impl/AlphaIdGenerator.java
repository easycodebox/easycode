package com.easycodebox.common.idgenerator.impl;

import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.idgenerator.exception.BoundReachedException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author WangXiaoJin
 * 
 */
public final class AlphaIdGenerator extends AbstractStringIdGenerator {
	
	public static char[] alphas = {
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 
			'o', 'p', 'q', 'r', 's', 't', 
			'u', 'v', 'w', 'x', 'y', 'z' 
	};
	
	public static Map<Character, Integer> properties = new HashMap<>();
	
	static {
		for(int i = 0; i < alphas.length; i++) {
			properties.put(alphas[i], i);
		}
	}
    
	public AlphaIdGenerator() {
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
	public AlphaIdGenerator(int increment, int fetchSize, String initialVal, String currentVal,
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
		    		throw new BoundReachedException("AlphaIdGenerator had reached max value.");
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
		//进一位的基数
		int carryNum = alphas.length,
			remainVal = val;	
		char[] frags = new char[curVal.length() > 33 ? curVal.length() + 1 : 33 + 1];
		//当前值
		char[] curValFrags = curVal.toCharArray();
		int i;
		for(i = 1; i <= frags.length; i++) {
			//当前索引位 需要计算的值
			Integer calVal = remainVal%carryNum,
				charIndex = curValFrags.length - i < 0 ? null : properties.get(curValFrags[curValFrags.length - i]),
				total = calVal + (charIndex == null ? -1 : charIndex);	//如果charIndex==null 则增加的值需要从a开始，所以charIndex传-1值
			if(total >= carryNum) {
				frags[frags.length - i] = alphas[total - carryNum];
				remainVal = remainVal/carryNum + 1;
			}else {
				frags[frags.length - i] = alphas[total];
				remainVal = remainVal/carryNum;
			}
			if(remainVal == 0 && curValFrags.length - i <= 0)
				break;
		}
		return new String(frags, frags.length - i, i);
	}
	
	public static String intToAlpha(int val) {
		char buf[] = new char[33];
		int radix = alphas.length;
		boolean negative = (val < 0);
		int charPos = 32;
		if (!negative) 
			val = -val;
		while (val <= -radix) {
		    buf[charPos--] = alphas[-(val % radix)];
		    val = val / radix;
		}
		buf[charPos] = alphas[-val];
		if (negative) {
		    buf[--charPos] = '-';
		}
		return new String(buf, charPos, (33 - charPos));
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
    	AlphaIdGenerator g = new AlphaIdGenerator(3, 100
				, "ab", "a", 
				"azz", YesNo.NO);
    	for(int i = 0; i < 100; i++) {
    		System.out.println(g.nextVal());
    	}
    }
}
