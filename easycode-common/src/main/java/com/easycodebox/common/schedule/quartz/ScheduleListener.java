package com.easycodebox.common.schedule.quartz;

import com.easycodebox.common.spring.BeanFactory;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * quartz 调度 监听器
 * @author WangXiaoJin
 * 
 */
public class ScheduleListener implements ServletContextListener {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

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
