package com.easycodebox.common.log.slf4j;

import org.slf4j.Marker;

import com.easycodebox.common.lang.StringUtils;

/**
 * @author WangXiaoJin
 *
 */
public final class Logger {
	
	private org.slf4j.Logger logger;
	
	public Logger(org.slf4j.Logger logger) {
		this.logger = logger;
	}
	
	private String str(Object msg) {
		return msg == null ? "null" : msg.toString();
	}

	public String getName() {
		return logger.getName();
	}

	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	public void trace(Object msg) {
		if(isTraceEnabled())
			logger.trace(str(msg));
	}
	
	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void trace(Object msg, Object... args) {
		if(isTraceEnabled())
			logger.trace(StringUtils.format(str(msg), args));
	}
	
	public void trace(Object msg, Throwable t) {
		if(isTraceEnabled())
			logger.trace(str(msg), t);
	}
	
	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void trace(Object msg, Throwable t, Object... args) {
		if(isTraceEnabled())
			logger.trace(StringUtils.format(str(msg), args), t);
	}

	public boolean isTraceEnabled(Marker marker) {
		return logger.isTraceEnabled(marker);
	}

	public void trace(Marker marker, Object msg) {
		if(isTraceEnabled(marker))
			logger.trace(marker, str(msg));
	}

	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void trace(Marker marker, Object msg, Object... args) {
		if(isTraceEnabled(marker))
			logger.trace(marker, StringUtils.format(str(msg), args));
	}

	public void trace(Marker marker, Object msg, Throwable t) {
		if(isTraceEnabled(marker))
			logger.trace(marker, str(msg), t);
	}
	
	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void trace(Marker marker, Object msg, Throwable t, Object... args) {
		if(isTraceEnabled(marker))
			logger.trace(marker, StringUtils.format(str(msg), args), t);
	}

	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public void debug(Object msg) {
		if(isDebugEnabled())
			logger.debug(str(msg));
	}
	
	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void debug(Object msg, Object... args) {
		if(isDebugEnabled())
			logger.debug(StringUtils.format(str(msg), args));
	}
	
	public void debug(Object msg, Throwable t) {
		if(isDebugEnabled())
			logger.debug(str(msg), t);
	}
	
	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void debug(Object msg, Throwable t, Object... args) {
		if(isDebugEnabled())
			logger.debug(StringUtils.format(str(msg), args), t);
	}
	
	public boolean isDebugEnabled(Marker marker) {
		return logger.isDebugEnabled(marker);
	}

	public void debug(Marker marker, Object msg) {
		if(isDebugEnabled(marker))
			logger.debug(marker, str(msg));
	}

	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void debug(Marker marker, Object msg, Object... args) {
		if(isDebugEnabled(marker))
			logger.debug(marker, StringUtils.format(str(msg), args));
	}

	public void debug(Marker marker, Object msg, Throwable t) {
		if(isDebugEnabled(marker))
			logger.debug(marker, str(msg), t);
	}
	
	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void debug(Marker marker, Object msg, Throwable t, Object... args) {
		if(isDebugEnabled(marker))
			logger.debug(marker, StringUtils.format(str(msg), args), t);
	}
	
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}
	
	public void info(Object msg) {
		logger.info(str(msg));
	}
	
	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void info(Object msg, Object... args) {
		if(isInfoEnabled())
			logger.info(StringUtils.format(str(msg), args));
	}
	
	public void info(Object msg, Throwable t) {
		logger.info(str(msg), t);
	}
	
	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void info(Object msg, Throwable t, Object... args) {
		if(isInfoEnabled())
			logger.info(StringUtils.format(str(msg), args), t);
	}
	
	public boolean isInfoEnabled(Marker marker) {
		return logger.isInfoEnabled(marker);
	}

	public void info(Marker marker, Object msg) {
		logger.info(marker, str(msg));
	}

	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void info(Marker marker, Object msg, Object... args) {
		if(isInfoEnabled(marker))
			logger.info(marker, StringUtils.format(str(msg), args));
	}

	public void info(Marker marker, Object msg, Throwable t) {
		logger.info(marker, str(msg), t);
	}
	
	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void info(Marker marker, Object msg, Throwable t, Object... args) {
		if(isInfoEnabled(marker))
			logger.info(marker, StringUtils.format(str(msg), args), t);
	}
	
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	public void warn(Object msg) {
		logger.warn(str(msg));
	}
	
	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void warn(Object msg, Object... args) {
		if(isWarnEnabled())
			logger.warn(StringUtils.format(str(msg), args));
	}
	
	public void warn(Object msg, Throwable t) {
		logger.warn(str(msg), t);
	}
	
	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void warn(Object msg, Throwable t, Object... args) {
		if(isWarnEnabled())
			logger.warn(StringUtils.format(str(msg), args), t);
	}
	
	public boolean isWarnEnabled(Marker marker) {
		return logger.isWarnEnabled(marker);
	}

	public void warn(Marker marker, Object msg) {
		logger.warn(marker, str(msg));
	}

	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void warn(Marker marker, Object msg, Object... args) {
		if(isWarnEnabled(marker))
			logger.warn(marker, StringUtils.format(str(msg), args));
	}

	public void warn(Marker marker, Object msg, Throwable t) {
		logger.warn(marker, str(msg), t);
	}
	
	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void warn(Marker marker, Object msg, Throwable t, Object... args) {
		if(isWarnEnabled(marker))
			logger.warn(marker, StringUtils.format(str(msg), args), t);
	}
	
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	public void error(Object msg) {
		logger.error(str(msg));
	}
	
	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void error(Object msg, Object... args) {
		if(isErrorEnabled())
			logger.error(StringUtils.format(str(msg), args));
	}
	
	public void error(Object msg, Throwable t) {
		logger.error(str(msg), t);
	}
	
	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void error(Object msg, Throwable t, Object... args) {
		if(isErrorEnabled())
			logger.error(StringUtils.format(str(msg), args), t);
	}
	
	public boolean isErrorEnabled(Marker marker) {
		return logger.isErrorEnabled(marker);
	}

	public void error(Marker marker, Object msg) {
		logger.error(marker, str(msg));
	}

	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void error(Marker marker, Object msg, Object... args) {
		if(isErrorEnabled(marker))
			logger.error(marker, StringUtils.format(str(msg), args));
	}

	public void error(Marker marker, Object msg, Throwable t) {
		logger.error(marker, str(msg), t);
	}
	
	/**
	 *  两种格式都可以：<br>
	 *  aaa{0}bbb{1}ccc ==> aaa-bbb+ccc <br>
	 *  aaa{}bbb{}ccc 	==> aaa-bbb+ccc <br>
	 */
	public void error(Marker marker, Object msg, Throwable t, Object... args) {
		if(isErrorEnabled(marker))
			logger.error(marker, StringUtils.format(str(msg), args), t);
	}
	
}
