package com.easycodebox.common.error;

import com.easycodebox.common.lang.Strings;

/**
 * @author WangXiaoJin
 * 
 */
public class BaseException extends RuntimeException {

	private static final long serialVersionUID = 8710681426899865033L;
	
	public BaseException() {
		super();
	}

	/**
	 * msg格式：“属性{0}不能为空。”,从0开始
	 * @param msg
	 * @param args
	 */
	public BaseException(String msg, Object... args) {
		super(Strings.format(msg, args));
	}
	
	/**
	 * msg格式：“属性{0}不能为空。”
	 * @param msg
	 * @param cause
	 * @param args
	 */
	public BaseException(String msg, Throwable cause, Object... args) {
        super(Strings.format(msg, args), cause);
    }
	
	public BaseException(Throwable cause) {
		super(cause);
	}
	
}
