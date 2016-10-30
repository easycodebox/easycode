package com.easycodebox.common.schedule.quartz;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.Scheduler;

import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.spring.BeanFactory;

/**
 * quartz 调度 监听器
 * @author WangXiaoJin
 * 
 */
public class ScheduleListener implements ServletContextListener {
	
	private final Logger log = LoggerFactory.getLogger(ScheduleListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Scheduler scheduler = BeanFactory.getBean(Scheduler.class);
		try {
			if(scheduler != null && scheduler.isStarted()) {
				scheduler.shutdown(true);
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			log.error("shut down Scheduler error.", e);
		}
	}


}
