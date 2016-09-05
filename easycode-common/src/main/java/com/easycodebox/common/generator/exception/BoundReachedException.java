package com.easycodebox.common.generator.exception;

/**
 * @author WangXiaoJin
 * 
 */
public class BoundReachedException extends GenerationException {
	//
	private static final long serialVersionUID = 8459345253975296144L;

	/**
	 * 
	 */
	public BoundReachedException() {
	}

	public BoundReachedException(String s) {
		super(s);
	}

	public BoundReachedException(Throwable t) {
		super(t);
	}

	public BoundReachedException(String s, Throwable t) {
		super(s, t);
	}
}
