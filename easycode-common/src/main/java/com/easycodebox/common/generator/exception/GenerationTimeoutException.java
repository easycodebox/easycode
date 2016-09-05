package com.easycodebox.common.generator.exception;

/**
 * @author WangXiaoJin
 * 
 */
public class GenerationTimeoutException extends GenerationException {
	//
	private static final long serialVersionUID = 8771768600181059960L;

	/**
	 * 
	 */
	public GenerationTimeoutException() {
	}

	public GenerationTimeoutException(String s) {
		super(s);
	}

	public GenerationTimeoutException(Throwable t) {
		super(t);
	}

	public GenerationTimeoutException(String s, Throwable t) {
		super(s, t);
	}
}
