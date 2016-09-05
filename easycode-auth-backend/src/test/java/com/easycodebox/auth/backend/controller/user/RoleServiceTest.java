package com.easycodebox.auth.backend.controller.user;

import org.junit.Test;

import com.easycodebox.auth.core.service.user.RoleService;
import com.easycodebox.auth.core.util.test.BaseTest;

public class RoleServiceTest extends BaseTest<RoleService> {

	@Test
	public void testLoad() {
		LOG.info(bean.load(859));
	}
	
}
