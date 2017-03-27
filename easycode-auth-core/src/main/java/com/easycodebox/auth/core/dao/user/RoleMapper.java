package com.easycodebox.auth.core.dao.user;

import com.easycodebox.auth.model.entity.user.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author WangXiaoJin
 *
 */
@Mapper
public interface RoleMapper {

	List<Role> listOpenedByUserId(String userId);
	
	Integer[] listOpenedRoleIdsByUserId(String userId);
	
	List<Role> listOpenedByGroupId(int groupId);
	
}
