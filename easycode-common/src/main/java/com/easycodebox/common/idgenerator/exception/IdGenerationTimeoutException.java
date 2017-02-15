package com.easycodebox.common.idgenerator.exception;

/**
 * @author WangXiaoJin
 * 
 */
public class IdGenerationTimeoutException extends IdGenerationException {
	
	public IdGenerationTimeoutException() {
	}

	public IdGenerationTimeoutException(String s) {
		super(s);
	}

	public IdGenerationTimeoutException(Throwable t) {
		super(t);
	}

	public IdGenerationTimeoutException(String s, Throwable t) {
		super(s, t);
	}
}
