package com.easycodebox.cas.exception;

import javax.security.auth.login.AccountException;

public class MultipleUserException extends AccountException {

	private static final long serialVersionUID = -5855583488539787365L;

	public MultipleUserException() {
        super();
    }

    public MultipleUserException(String msg) {
        super(msg);
    }
}
