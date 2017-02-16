package com.easycodebox.auth.core.service.sys;

import com.easycodebox.idgenerator.service.IdGeneratorService;
import org.junit.Test;

import com.easycodebox.auth.core.util.test.BaseTest;

public class IdGeneratorServiceTest extends BaseTest<IdGeneratorService> {
	
	@Test
    public void testGenerator() {
    	try {
    		//log.info(bean.batchAdd());
		} catch (Exception e) {
			log.error("run error!!!", e);
		}
    }
    
}
