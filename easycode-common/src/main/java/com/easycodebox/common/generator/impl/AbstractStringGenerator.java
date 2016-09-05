package com.easycodebox.common.generator.impl;

import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.generator.AbstractGenerator;
import com.easycodebox.common.lang.StringUtils;

/**
 * @author WangXiaoJin
 * 
 */
public abstract class AbstractStringGenerator extends AbstractGenerator<String> {
	
	protected String curVal;
	
    protected Integer size;
    
    protected AbstractStringGenerator(int increment, int fetchSize
			, String initialVal, String currentVal, 
			String maxVal, YesNo isCycle) {
		super(increment, fetchSize, initialVal, currentVal, maxVal, isCycle);
	};
	
    /**
     * 比较两个字符窜 a>b返回正数 a==b返回0 a<b返回负数 
     * @param a
     * @param b
     * @return
     */
    public int compare(String a, String b) {
    	String tmpa = filterZero(a);
		String tmpb = filterZero(b);
    	if(tmpa == null && tmpb != null)
    		return -1;
    	else if(tmpa == null && tmpb == null)
    		return 0;
    	else if(tmpa != null && tmpb == null)
    		return 1;
    	else {
    		if(tmpa.length() < tmpb.length())
    			return -1;
    		else if(tmpa.length() == tmpb.length()) {
    			return tmpa.compareToIgnoreCase(tmpb);
    		}else 
    			return 1;
    	}
    }
    
    /**
     * 过滤字符窜前面的0
     * @param val
     * @return
     */
    public String filterZero(String val) {
    	if(StringUtils.isNotBlank(val)) 
    		return val.replaceFirst("^0+", "");
    	else
    		return val;
    }
    
}
