package com.easycodebox.jdbc.exception;

import com.easycodebox.common.error.BaseException;

/**
 * @author WangXiaoJin
 */
public class ParseGeneratedValueException extends BaseException {
	
	public ParseGeneratedValueException() {
		super();
	}
	
	public ParseGeneratedValueException(String msg, Object... args) {
		super(msg, args);
	}
	
	public ParseGeneratedValueException(String msg, Throwable cause, Object... args) {
		super(msg, cause, args);
	}
	
	public ParseGeneratedValueException(Throwable cause) {
		super(cause);
	}
}
