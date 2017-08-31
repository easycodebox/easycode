package com.easycodebox.common.log.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import com.easycodebox.common.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.net.URL;

/**
 * @author WangXiaoJin
 *
 */
public abstract class LocateLogger {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	private final String CLASS_PREFIX = "classpath:";

	/**
	 * 自定义加载日志文件，初始化日志框架
	 * @param logbackUrl	日志配置文件路径
	 * @throws FileNotFoundException
	 * @throws JoranException
	 */
	protected void locate(String logbackUrl) throws FileNotFoundException, JoranException {
		if(Strings.isBlank(logbackUrl)) return;
		logbackUrl = (logbackUrl = logbackUrl.trim()).startsWith(CLASS_PREFIX) 
				? logbackUrl : CLASS_PREFIX + logbackUrl;
		URL url = ResourceUtils.getURL(logbackUrl);
        LoggerContext loggerContext = (LoggerContext)StaticLoggerBinder.getSingleton().getLoggerFactory();
        loggerContext.reset();
        new ContextInitializer(loggerContext).configureByResource(url);
	}
	
}
