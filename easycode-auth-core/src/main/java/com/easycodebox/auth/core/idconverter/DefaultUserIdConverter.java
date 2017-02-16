package com.easycodebox.auth.core.idconverter;

import com.easycodebox.auth.core.service.user.UserService;
import com.easycodebox.auth.model.entity.user.User;
import com.easycodebox.auth.model.idconverter.AbstractUserIdConverter;

/**
 * 用户ID转换器
 * @author WangXiaoJin
 *
 */
public class DefaultUserIdConverter extends AbstractUserIdConverter {

	private UserService userService;
	
	public DefaultUserIdConverter(UserService userService) {
		this.userService = userService;
	}
	
	@Override
	public User getUserById(Object id) {
		return userService.load(id.toString());
	}
	
}
