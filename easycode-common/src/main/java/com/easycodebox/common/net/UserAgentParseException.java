package com.easycodebox.common.net;


/**
 * Catches scenarios where user agent cannot be parsed.
 * @author Glen Smith (glen@bytecode.com.au)
 */
public class UserAgentParseException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3973085846409047724L;

	public UserAgentParseException(String message) {
        super(message);
    }

}
