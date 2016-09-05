package com.easycodebox.common.log.logback;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.math.NumberUtils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;

/**
 * 基于时间间隔触发，即两次触发的最小时间间隔 <br>
 * &lt;evaluator class="com.easycodebox.common.log.logback.CounterBasedEvaluator"> <br>
 * 	&lt;interval>60&lt;/interval> (可省略，默认：60秒)<br>
 * 	&lt;level>ERROR&lt;/level>(可省略，默认：ERROR) <br>
 * &lt;/evaluator> <br>
 * @author WangXiaoJin
 *
 */
public class TimeIntervalEvaluator extends EventEvaluatorBase<ILoggingEvent> {

	//默认60秒
	static int DEFAULT_INTERVAL = 60;
	private AtomicBoolean using = new AtomicBoolean();
	private long last = NumberUtils.INTEGER_ZERO;
	private int interval = DEFAULT_INTERVAL;
	private Level level = Level.ERROR;
	
	@Override
	public boolean evaluate(ILoggingEvent event) throws NullPointerException,
			EvaluationException {
		long current = System.currentTimeMillis()/1000;
		if(event.getLevel().levelInt >= level.levelInt 
				&& current - last > interval
				&& using.compareAndSet(false, true)) {
			last = current;
			using.set(false);
			return true;
		}
		return false;
	}

	
	public int getInterval() {
		return interval;
	}

	/**
	 * 单位为秒
	 * @param interval
	 */
	public void setInterval(int interval) {
		this.interval = interval;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}
	
}
