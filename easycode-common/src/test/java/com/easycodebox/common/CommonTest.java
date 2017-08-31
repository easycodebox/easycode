package com.easycodebox.common;

import com.easycodebox.common.log.logback.LocateLogger;
import org.junit.Before;

public class CommonTest extends LocateLogger {
	
	@Before
	public void beforeTest() {
		try{
			
		}catch (Exception e) {
			log.error("run error!!!", e);
		}
	}

}
