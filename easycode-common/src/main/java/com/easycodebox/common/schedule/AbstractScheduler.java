package com.easycodebox.common.schedule;

import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.processor.Processor;

/**
 * @author WangXiaoJin
 *
 */
public abstract class AbstractScheduler implements Scheduler, Processor {
	
	protected final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private boolean isLog = true;
	
	/**
	 * 执行定时器
	 */
	@Override
	public void execute() {
		if(isLog)
			LOG.info("Scheduler-begin: {0}", getClass().getSimpleName());
		this.process();
		if(isLog)
			LOG.info("Scheduler-end: {0}", getClass().getSimpleName());
	}

	public boolean isLog() {
		return isLog;
	}

	public void setLog(boolean isLog) {
		this.isLog = isLog;
	}
	
}
