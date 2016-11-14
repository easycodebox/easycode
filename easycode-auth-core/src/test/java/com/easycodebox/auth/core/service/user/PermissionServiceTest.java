package com.easycodebox.auth.core.service.user;

import org.junit.Test;

import com.easycodebox.auth.core.service.user.PermissionService;
import com.easycodebox.auth.core.util.test.BaseTest;

public class PermissionServiceTest extends BaseTest<PermissionService> {

	@Test
	public void testListPermissionsOfRoles() {
		log.info(bean.listPermissionsOfRoles(new Integer[]{1}, 2, null));
	}
	
	@Test
	public void testListPermissionsOfUser() {
		log.info(bean.listPermissionsOfUser("1", 1, null));
	}
	
	@Test
	public void testListPermissionIds() {
		//log.info(bean.listPermissionIds("a15p2", null));
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
