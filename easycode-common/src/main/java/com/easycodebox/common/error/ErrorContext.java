package com.easycodebox.common.error;

import com.easycodebox.common.enums.entity.LogLevel;
import com.easycodebox.common.error.CodeMsg.Code;
import com.easycodebox.common.filter.LogLevelException;

/**
 * @author WangXiaoJin
 *
 */
public class ErrorContext extends LogLevelException {
	
	private static final long serialVersionUID = 1053969252838403620L;

	private static final ThreadLocal<ErrorContext> LOCAL = new ThreadLocal<ErrorContext>();
	
	private CodeMsg error;
	
	protected ErrorContext(CodeMsg error, Throwable cause) {
		super(error == null ? null : error.getMsg(), cause);
		this.error = error;
	}
	
	public static ErrorContext instance() {
		return instance((CodeMsg)null, (Throwable)null);
	}
	
	/**
	 * 默认是Error.FAIL,即错误消息
	 * @param msg
	 * @return
	 */
	public static ErrorContext instance(String msg, Object... args) {
		return instance(Code.FAIL_CODE, msg, args);
	}
	
	/**
	 * @param code	可以为Error.SUC/Error.FAIL
	 * @param msg
	 * @return
	 */
	public static ErrorContext instance(final String code, final String msg, Object... args) {
		return instance(CodeMsg.NONE.codeMsg(code, msg, args));
	}
	
	public static ErrorContext instance(CodeMsg error, Object... args) {
		return instance(error, (Throwable)null, args);
	}
	
	public static ErrorContext instance(CodeMsg error, Throwable cause, Object... args) {
		ErrorContext errorContext = LOCAL.get();
		if(errorContext == null) {
			errorContext = new ErrorContext(error == null ? error : error.fillArgs(args), cause);
			LOCAL.set(errorContext);
		}
		return errorContext;
	}
	
	/**
	 * 判断当前线程有没有产生错误
	 * @return
	 */
	public static boolean hasError() {
		return LOCAL.get() != null;
	}
	
	public ErrorContext error(CodeMsg error) {
		this.error = error;
		return this;
	}
	
	public ErrorContext logLevel(LogLevel logLevel) {
		getLogLevelConfig().setLogLevel(logLevel);
		return this;
	}
	
	public ErrorContext reset() {
		super.reset();
		this.error = null;
		LOCAL.remove();
		return this;
	}

	public CodeMsg getError() {
		return error;
	}

}
