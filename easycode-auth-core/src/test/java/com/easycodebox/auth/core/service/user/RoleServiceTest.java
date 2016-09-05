package com.easycodebox.auth.core.service.user;

import org.junit.Test;

import com.alibaba.druid.filter.config.ConfigTools;
import com.easycodebox.auth.core.pojo.user.Role;
import com.easycodebox.auth.core.service.user.RoleService;
import com.easycodebox.auth.core.util.test.BaseTest;
import com.easycodebox.common.enums.entity.status.CloseStatus;

public class RoleServiceTest extends BaseTest<RoleService> {

	@Test
	public void testLoad() {
		LOG.info(bean.load(3859));
	}
	
	@Test
	public void testUpdate() {
		Role role = new Role();
		role.setId(3859);
		role.setName("管理员");
		role.setSort(9);
		role.setStatus(CloseStatus.OPEN);
		role.setDescription("xxx");
		LOG.info(bean.update(role));
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(ConfigTools.encrypt("root"));
	}
	
}
