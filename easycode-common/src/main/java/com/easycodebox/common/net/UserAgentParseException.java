package com.easycodebox.common.net;


/**
 * Catches scenarios where user agent cannot be parsed.
 * @author Glen Smith (glen@bytecode.com.au)
 */
public class UserAgentParseException extends RuntimeException {

	public UserAgentParseException(String message) {
        super(message);
    }

}
