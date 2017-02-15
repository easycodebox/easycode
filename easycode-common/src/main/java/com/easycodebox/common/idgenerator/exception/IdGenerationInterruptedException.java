package com.easycodebox.common.idgenerator.exception;

/**
 * @author WangXiaoJin
 * 
 */
public class IdGenerationInterruptedException extends IdGenerationException {
	
	public IdGenerationInterruptedException() {
	}

	public IdGenerationInterruptedException(String s) {
		super(s);
	}

	public IdGenerationInterruptedException(Throwable t) {
		super(t);
	}

	public IdGenerationInterruptedException(String s, Throwable t) {
		super(s, t);
	}
}
