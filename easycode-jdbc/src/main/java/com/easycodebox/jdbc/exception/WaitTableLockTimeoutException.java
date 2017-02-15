package com.easycodebox.jdbc.exception;

import com.easycodebox.common.error.BaseException;

/**
 * @author WangXiaoJin
 * 
 */
public class WaitTableLockTimeoutException extends BaseException {

	public WaitTableLockTimeoutException() {
		super();
	}

	/**
	 * msg格式：“属性{0}不能为空。”,从0开始
	 * @param msg
	 * @param args
	 */
	public WaitTableLockTimeoutException(String msg, Object... args) {
		super(msg, args);
	}
	
	/**
	 * msg格式：“属性{0}不能为空。”
	 * @param msg
	 * @param cause
	 * @param args
	 */
	public WaitTableLockTimeoutException(String msg, Throwable cause, Object... args) {
        super(msg, cause, args);
    }
	
	public WaitTableLockTimeoutException(Throwable cause) {
		super(cause);
	}
	
}
