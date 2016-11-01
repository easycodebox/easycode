package com.easycodebox.auth.core;

import org.junit.Test;

import com.easycodebox.jdbc.res.GenerateBeanRes;

public class SysTest {
	
	@Test
	public void testGenerateRes() {
		GenerateBeanRes gen = new GenerateBeanRes();
		gen.setBasePackages(new String[] {
				"com/easycodebox/auth/model/entity",
				"com/easycodebox/auth/model/bo"
		});
		gen.setOutputFile("src/main/java/com/easycodebox/auth/model/util/R.java");
		gen.generate();
	}
	
}
