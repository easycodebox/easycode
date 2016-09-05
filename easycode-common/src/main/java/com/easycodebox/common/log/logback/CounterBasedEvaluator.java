package com.easycodebox.common.log.logback;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.math.NumberUtils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;

/**
 * 基于计数器来触发 <br>
 * &lt;evaluator class="com.easycodebox.common.log.logback.CounterBasedEvaluator"> <br>
 * 	&lt;limit>1024&lt;/limit> (可省略，默认：1024)<br>
 * 	&lt;level>ERROR&lt;/level>(可省略，默认：ERROR) <br>
 * &lt;/evaluator> <br>
 * @author WangXiaoJin
 *
 */
public class CounterBasedEvaluator extends EventEvaluatorBase<ILoggingEvent> {

	static int DEFAULT_LIMIT = 1024;
	private AtomicInteger counter = new AtomicInteger(NumberUtils.INTEGER_ZERO);
	private int limit = DEFAULT_LIMIT;
	private Level level = Level.ERROR;
	
	@Override
	public boolean evaluate(ILoggingEvent event) throws NullPointerException,
			EvaluationException {
		if(event.getLevel().levelInt >= level.levelInt 
				&& counter.incrementAndGet() == limit) {
			counter.set(NumberUtils.INTEGER_ZERO);
			return true;
		}
		return false;
	}

	public int getCounter() {
		return counter.get();
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}
	
}
