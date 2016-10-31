package com.easycodebox.auth.core;

import org.junit.Test;

import com.alibaba.druid.filter.config.ConfigTools;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.jdbc.res.GenerateBeanRes;

public class SysTest {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Test
	public void testDruidEncrypt() throws Exception {
		log.info(ConfigTools.encrypt("root"));
	}
	
	@Test
	public void testGenerateRes() {
		GenerateBeanRes gen = new GenerateBeanRes();
		gen.setBasePackages(new String[] {
				"com/easycodebox/auth/core/pojo",
				"com/easycodebox/auth/core/bo"
		});
		gen.setOutputFile("src/main/java/com/easycodebox/auth/core/util/R.java");
		gen.generate();
	}
	
}
