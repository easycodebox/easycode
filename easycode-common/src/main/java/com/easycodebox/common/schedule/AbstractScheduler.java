package com.easycodebox.common.schedule;

import com.easycodebox.common.processor.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author WangXiaoJin
 *
 */
public abstract class AbstractScheduler implements Scheduler, Processor {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	private boolean isLog = true;
	
	/**
	 * 执行定时器
	 */
	@Override
	public void execute() {
		if(isLog)
			log.info("Scheduler-begin: {}", getClass().getSimpleName());
		this.process();
		if(isLog)
			log.info("Scheduler-end: {}", getClass().getSimpleName());
	}

	public boolean isLog() {
		return isLog;
	}

	public void setLog(boolean isLog) {
		this.isLog = isLog;
	}
	
}
