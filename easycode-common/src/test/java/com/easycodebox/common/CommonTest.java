package com.easycodebox.common;

import org.junit.Before;
import org.junit.Test;

import com.easycodebox.common.log.logback.LocateLogger;

public class CommonTest extends LocateLogger {
	
	@Before
	public void beforeTest() {
		try{
			
		}catch (Exception e) {
			LOG.error("run error!!!", e);
		}
	}

	@Test
    public void testLog() {
    	/*try {
    		Logger LOG = LoggerFactory.getLogger(CommonTest.class);
    		LOG.info("11111xxx {} xxxx", "A", "B");
    		LOG.info("2222222xxx {0} xxxx", new NullPointerException(), "A", "B");
    		LOG.info("33333333xxx {0} xxxx", new NullPointerException());
		} catch (Exception e) {
			LOG.error("run error!!!", e);
		}*/
    }
    
}
