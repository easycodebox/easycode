package com.easycodebox.auth.core.service.user;

import com.easycodebox.auth.core.AbstractTest;
import com.easycodebox.auth.model.entity.user.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class UserServiceTest extends AbstractTest {
	
	@Autowired
	private UserService userService;
	
	@Test
	public void testLoad() {
		User data = userService.load("1");
		assertNotNull("获取用户信息失败", data);
	}
	
	@Test
	public void testUpdateNickname() {
		int data = userService.updateNickname("newNickname", "a15b7");
		assertTrue("更新用户昵称失败", data > 0);
	}
	
}
