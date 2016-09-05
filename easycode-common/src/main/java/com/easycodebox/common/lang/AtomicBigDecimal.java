package com.easycodebox.common.lang;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author WangXiaoJin
 * 
 */
public class AtomicBigDecimal extends AtomicReference<BigDecimal>{

	private static final long serialVersionUID = 1L;
	
	public AtomicBigDecimal() {
		super(BigDecimal.ZERO);
	}
	
	public AtomicBigDecimal(BigDecimal initialValue) {
		super(initialValue);
	}
	
	public BigDecimal addAndGet(BigDecimal delta) {
		for (;;) {
			BigDecimal current = get();
			BigDecimal next = current.add(delta);
            if (compareAndSet(current, next))
                return next;
        }
	}
	
}
