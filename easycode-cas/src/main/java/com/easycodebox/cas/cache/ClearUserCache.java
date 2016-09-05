package com.easycodebox.cas.cache;

/**
 * @author WangXiaoJin
 *
 */
public interface ClearUserCache {

	/**
	 * 清除User相关的缓存
	 * @return
	 */
	boolean clear(String userId) throws Exception;
	
}
