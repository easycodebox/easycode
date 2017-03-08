package com.easycodebox.auth.core;

import com.alibaba.druid.filter.config.ConfigTools;
import org.junit.Test;

public class ToolsTest {
	
	/**
	 * druid加密密码
	 */
	@Test
	public void testDruidEncrypt() throws Exception {
		System.out.println((ConfigTools.encrypt("root")));
	}
	
}
