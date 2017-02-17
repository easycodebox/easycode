package com.easycodebox.common.error;

import com.easycodebox.common.lang.Strings;

/**
 * @author WangXiaoJin
 * 
 */
public class BaseException extends RuntimeException {

	public BaseException() {
		super();
	}

	/**
	 * 使用{@link Strings#format(String, Object...)}处理msg
	 * @param msg
	 * @param args
	 */
	public BaseException(String msg, Object... args) {
		super(Strings.format(msg, args));
	}
	
	/**
	 * 使用{@link Strings#format(String, Object...)}处理msg
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
