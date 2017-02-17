package com.easycodebox.common.mail;

import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.lang.Strings;

/**
 * @author WangXiaoJin
 */
public class GenerateCoupleMailException extends BaseException {
    
    public GenerateCoupleMailException() {
        super();
    }
    
    /**
     * 使用{@link Strings#format(String, Object...)}处理msg
     * @param msg
     * @param args
     */
    public GenerateCoupleMailException(String msg, Object... args) {
        super(Strings.format(msg, args));
    }
    
    /**
     * 使用{@link Strings#format(String, Object...)}处理msg
     * @param msg
     * @param cause
     * @param args
     */
    public GenerateCoupleMailException(String msg, Throwable cause, Object... args) {
        super(Strings.format(msg, args), cause);
    }
    
    public GenerateCoupleMailException(Throwable cause) {
        super(cause);
    }
    
}
