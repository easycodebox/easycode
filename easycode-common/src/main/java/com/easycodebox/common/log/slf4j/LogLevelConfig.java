package com.easycodebox.common.log.slf4j;

import com.easycodebox.common.enums.entity.LogLevel;
import org.slf4j.Logger;

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
	public void log(Logger log, String msg, Object... args) {
		switch(this.logLevel) {
			
		case TRACE: 
			log.trace(msg, args);
			break;
		case DEBUG: 
			log.debug(msg, args);
			break;
		case INFO:
			log.info(msg, args);
			break;
		case WARN:
			log.warn(msg, args);
			break;
		case ERROR:
			log.error(msg, args);
			break;
		default: 
			log.error(msg, args);
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
