package com.easycodebox.common.generator.exception;

import org.apache.commons.lang.exception.NestableRuntimeException;

/**
 * @author WangXiaoJin
 * 
 */
public class GenerationException extends NestableRuntimeException {
	
	public GenerationException() {
	}

	public GenerationException(String s) {
		super(s);
	}

	public GenerationException(Throwable t) {
		super(t);
	}

	public GenerationException(String s, Throwable t) {
		super(s, t);
	}
}
