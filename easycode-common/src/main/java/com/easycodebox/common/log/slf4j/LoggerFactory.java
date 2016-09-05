package com.easycodebox.common.log.slf4j;

import org.slf4j.ILoggerFactory;

/**
 * @author WangXiaoJin
 *
 */
public final class LoggerFactory {

	private LoggerFactory() {

	}

	public static Logger getLogger(String name) {
		return new Logger(org.slf4j.LoggerFactory.getLogger(name));
	}

	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	public static ILoggerFactory getILoggerFactory() {
		return org.slf4j.LoggerFactory.getILoggerFactory();
	}
}
