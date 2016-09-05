package com.easycodebox.common;

/**
 * 在指定逻辑 之前/之后 织入逻辑
 * @author WangXiaoJin
 *
 */
public interface Weave {
	
	/**
	 * 在指定逻辑 之前 织入逻辑
	 */
	void before(Object... args);
	
	/**
	 * 在指定逻辑 之后 织入逻辑
	 */
	void after(Object... args);

}
