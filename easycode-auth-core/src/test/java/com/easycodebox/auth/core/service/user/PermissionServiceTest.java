package com.easycodebox.auth.core.service.user;

import com.easycodebox.auth.core.AbstractTest;
import com.easycodebox.auth.model.entity.user.Permission;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class PermissionServiceTest extends AbstractTest {

	@Autowired
	private PermissionService permissionService;
	
	@Test
	public void testLoad() {
		Permission data = permissionService.load(101000000L, null, null);
		assertNotNull("获取权限信息失败", data);
	}
	
}
