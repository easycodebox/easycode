package com.easycodebox.common.idgenerator.exception;

import org.apache.commons.lang.exception.NestableRuntimeException;

/**
 * @author WangXiaoJin
 * 
 */
public class IdGenerationException extends NestableRuntimeException {
	
	public IdGenerationException() {
	}

	public IdGenerationException(String s) {
		super(s);
	}

	public IdGenerationException(Throwable t) {
		super(t);
	}

	public IdGenerationException(String s, Throwable t) {
		super(s, t);
	}
}
