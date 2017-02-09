package com.easycodebox.common.file.exception;

/**
 * @author WangXiaoJin
 * 
 */
public class NonEnlargedException extends RuntimeException {

	public NonEnlargedException() {
		super();
	}

	public NonEnlargedException(String s) {
		super(s);
	}
	
	public NonEnlargedException(Throwable cause) {
        super(cause);
    }
	
	public NonEnlargedException(String s, Throwable cause) {
        super(s, cause);
    }
	
}
