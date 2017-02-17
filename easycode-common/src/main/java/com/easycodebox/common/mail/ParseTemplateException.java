package com.easycodebox.common.mail;

import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.lang.Strings;

/**
 * @author WangXiaoJin
 */
public class ParseTemplateException extends BaseException {
    
    public ParseTemplateException() {
        super();
    }
    
    /**
     * 使用{@link Strings#format(String, Object...)}处理msg
     * @param msg
     * @param args
     */
    public ParseTemplateException(String msg, Object... args) {
        super(Strings.format(msg, args));
    }
    
    /**
     * 使用{@link Strings#format(String, Object...)}处理msg
     * @param msg
     * @param cause
     * @param args
     */
    public ParseTemplateException(String msg, Throwable cause, Object... args) {
        super(Strings.format(msg, args), cause);
    }
    
    public ParseTemplateException(Throwable cause) {
        super(cause);
    }
    
}
