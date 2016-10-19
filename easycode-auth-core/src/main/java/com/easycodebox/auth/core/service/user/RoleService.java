package com.easycodebox.auth.core.service.user;

import java.util.List;

import com.easycodebox.auth.core.pojo.user.Role;
import com.easycodebox.common.enums.entity.OpenClose;
import com.easycodebox.common.lang.dto.DataPage;

/**
 * @author WangXiaoJin
 *
 */
public interface RoleService {
	
	/**
	 * 角色列表
	 * @param eqName 角色名等于eqName的角色，不是like
	 * @return
	 */
	List<Role> list(OpenClose status, String eqName);
	
	/**
	 * 角色详情
	 * @param id
	 * @return
	 */
	Role load(Integer id);
	
	/**
	 * 新增角色
	 * @param role
	 * @return	应该实现返回数据能获取到主键
	 */
	Role add(Role role);
	
	/**
	 * 修改角色
	 * @param role
	 * @return
	 */
	int update(Role role);
	
	/**
	 * 逻辑删除角色
	 * @param ids
	 * @return
	 */
	int remove(Integer[] ids);
	
	/**
	 * 物理删除角色
	 * @param ids
	 * @return
	 */
	int removePhy(Integer[] ids);
	
	/**
	 * 开启、关闭角色
	 * @param ids
	 * @param status
	 * @return
	 */
	int openClose(Integer[] ids, OpenClose status);
	
	/**
	 * 角色分页
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	DataPage<Role> page(String name, OpenClose status, int pageNo, int pageSize);
	
	/**
	 * 只列出已开放的角色
	 */
	List<Role> listOpenedByUserId(String userId);
	
	/**
	 * 只列出已开放的角色
	 */
	Integer[] listOpenedRoleIdsByUserId(String userId);
	
	/**
	 * 只列出已开放的角色
	 */
	List<Role> listOpenedByGroupId(int groupId);
	
	/**
	 * 列出所有的已开放角色，如果指定的组织拥有该角色，则isOwn标记为yes
	 */
	List<Role> listAllByGroupId(int groupId);
	
	/**
	 * 列出所有的已开放角色，如果指定的用户拥有该角色，则isOwn标记为yes
	 */
	List<Role> listAllByUserId(String userId);
	
	/**
	 * 为指定的组织设置角色
	 */
	void installRolesOfGroup(int groupId, Integer[] roleIds);
	
	/**
	 * 为指定的用户设置角色
	 */
	void installRolesOfUser(String userId, Integer[] roleIds);
	
	boolean existName(String name, Integer excludeId);
	
}
