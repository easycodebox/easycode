package com.easycodebox.common.error;

/**
 * 
 * @author WangXiaoJin
 *
 */
public interface ExceptionHandler {

	/**
	 * 处理异常逻辑
	 * @param throwable
	 */
	void handle(Throwable throwable);
	
}
