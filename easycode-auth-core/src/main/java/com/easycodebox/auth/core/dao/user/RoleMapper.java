package com.easycodebox.auth.core.dao.user;

import java.util.List;

import com.easycodebox.auth.core.pojo.user.Role;

/**
 * @author WangXiaoJin
 *
 */
public interface RoleMapper {

	List<Role> listOpenedByUserId(String userId);
	
	Integer[] listOpenedRoleIdsByUserId(String userId);
	
	List<Role> listOpenedByGroupId(int groupId);
	
}
