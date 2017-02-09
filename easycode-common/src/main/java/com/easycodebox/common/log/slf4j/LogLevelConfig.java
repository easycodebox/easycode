package com.easycodebox.common.log.slf4j;

import com.easycodebox.common.enums.entity.LogLevel;

import java.io.Serializable;

/**
 * 可配置的log level
 * @author WangXiaoJin
 *
 */
public class LogLevelConfig implements Serializable {

	/**
	 * 输出的日志级别
	 */
	private LogLevel logLevel = LogLevel.ERROR;
	
	/**
	 * 打印日志
	 * @return
	 */
	public void log(Logger log, Object msg, Object... args) {
		this.log(log, msg, (Throwable)null, args);
	}
	
	/**
	 * 打印日志
	 * @return
	 */
	public void log(Logger log, Object msg, Throwable t, Object... args) {
		switch(this.logLevel) {
			
		case TRACE: 
			log.trace(msg, t, args);
			break;
		case DEBUG: 
			log.debug(msg, t, args);
			break;
		case INFO:
			log.info(msg, t, args);
			break;
		case WARN:
			log.warn(msg, t, args);
			break;
		case ERROR:
			log.error(msg, t, args);
			break;
		default: 
			log.error(msg, t, args);
			break;
			
		}
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}
	
}
