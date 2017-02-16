package com.easycodebox.auth.core.service.sys;

import com.easycodebox.idgenerator.service.GeneratorService;
import org.junit.Test;

import com.easycodebox.auth.core.util.test.BaseTest;

public class GeneratorServiceTest extends BaseTest<GeneratorService> {
	
	@Test
    public void testGenerator() {
    	try {
    		//log.info(bean.batchAdd());
		} catch (Exception e) {
			log.error("run error!!!", e);
		}
    }
    
}
