package com.easycodebox.auth.core.service.user;

import java.util.List;


/**
 * @author WangXiaoJin
 *
 */
public interface RoleProjectService {
	
	/**
	 * 指定的角色和项目编号是否存在对应关系，如果projectNo指定的项目不存在或已被禁用，则return false
	 * @param roleIds
	 * @param projectNo
	 * @return
	 */
	boolean permit(Integer[] roleIds, String projectNo);
	
	/**
	 * 获取指定角色拥有的项目ID
	 * @param roleId
	 * @return
	 */
	List<Integer> getProjectIdsByRoleId(Integer roleId);
	
	/**
	 * 更新角色和项目对应关系表
	 * @param roleId
	 * @param projectIds
	 * @return
	 */
	int updateRoleProjectByRoleId(Integer roleId, Integer[] projectIds);
	
	/**
	 * 物理删除
	 * @param roleId
	 * @return
	 */
	int removePhyByRoleId(Integer roleId);
	
	int removePhyByProjectIds(Integer[] projectIds);
	
}
