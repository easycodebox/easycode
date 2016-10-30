package com.easycodebox.auth.core.service.user;

import org.junit.Test;

import com.easycodebox.auth.core.service.user.OperationService;
import com.easycodebox.auth.core.util.test.BaseTest;

public class OperationServiceTest extends BaseTest<OperationService> {

	@Test
	public void testListOperationsOfRoles() {
		log.info(bean.listOperationsOfRoles(new Integer[]{1}, 2, null));
	}
	
	@Test
	public void testListOperationsOfUser() {
		log.info(bean.listOperationsOfUser("1", 1, null));
	}
	
	@Test
	public void testListOperationIds() {
		//log.info(bean.listOperationIds("a15p2", null));
	}
	
	@Test
	public void testPage() {
		try {
			log.info(bean.page("  cc  ", "  ", null, null, null, null, 4, 15));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
