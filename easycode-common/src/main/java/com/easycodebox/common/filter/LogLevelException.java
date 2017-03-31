package com.easycodebox.common.filter;

import com.easycodebox.common.log.slf4j.LogLevelConfig;

/**
 * 可配置的log level异常
 * @author WangXiaoJin
 *
 */
public class LogLevelException extends RuntimeException {

	private LogLevelConfig logLevelConfig = new LogLevelConfig();
	
	public LogLevelException() {
		super();
	}

	public LogLevelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public LogLevelException(String message, Throwable cause) {
		super(message, cause);
	}

	public LogLevelException(String message) {
		super(message);
	}

	public LogLevelException(Throwable cause) {
		super(cause);
	}
	
	public LogLevelConfig getLogLevelConfig() {
		return logLevelConfig;
	}

	public void setLogLevelConfig(LogLevelConfig logLevelConfig) {
		this.logLevelConfig = logLevelConfig;
	}

}
