package com.easycodebox.auth.core.service.user;

import com.easycodebox.auth.core.AbstractTest;
import com.easycodebox.auth.model.entity.user.Permission;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class PermissionServiceTest extends AbstractTest {

	@Autowired
	private PermissionService permissionService;
	
	@Test
	public void testListPermissionsOfRoles() {
		List<Permission> data = permissionService.listPermissionsOfRoles(new Integer[]{1}, 2, null);
		assertTrue(data.size() > 0);
	}
	
}
