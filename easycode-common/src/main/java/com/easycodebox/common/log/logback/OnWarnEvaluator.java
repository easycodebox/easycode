package com.easycodebox.common.log.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;

/**
 * @author WangXiaoJin
 *
 */
public class OnWarnEvaluator extends EventEvaluatorBase<ILoggingEvent> {

	public boolean evaluate(ILoggingEvent event) throws NullPointerException,
			EvaluationException {
		return event.getLevel().levelInt >= Level.WARN_INT;
	}

}
