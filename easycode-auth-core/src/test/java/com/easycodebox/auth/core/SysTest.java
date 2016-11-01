package com.easycodebox.auth.core;

import org.junit.Test;

import com.alibaba.druid.filter.config.ConfigTools;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

public class SysTest {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Test
	public void testDruidEncrypt() throws Exception {
		log.info(ConfigTools.encrypt("root"));
	}
	
}
