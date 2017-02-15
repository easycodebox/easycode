package com.easycodebox.common.idgenerator.exception;

/**
 * @author WangXiaoJin
 * 
 */
public class BoundReachedException extends IdGenerationException {

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
