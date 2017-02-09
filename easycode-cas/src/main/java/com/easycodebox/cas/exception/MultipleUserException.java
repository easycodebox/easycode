package com.easycodebox.cas.exception;

import javax.security.auth.login.AccountException;

public class MultipleUserException extends AccountException {

	public MultipleUserException() {
        super();
    }

    public MultipleUserException(String msg) {
        super(msg);
    }
}
