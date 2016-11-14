package com.easycodebox.auth.model.entity.user;

import javax.persistence.*;

import com.easycodebox.jdbc.entity.AbstractCreateEntity;

/**
 * 角色权限 - 角色与权限的对应关系
 * @author WangXiaoJin
 *
 */
@Entity
@Table(name="u_role_permission")
public class RolePermission extends AbstractCreateEntity {

	private static final long serialVersionUID = 5454155825314635342L;
	
	/**
	 * 角色ID
	 */
	@Id
	private Integer roleId;
	
	/**
	 * 权限ID
	 */
	@Id
	private Long permissionId;
	

	@ManyToOne
	@JoinColumn(name="permissionId") 
	private Permission permission;
	
	@ManyToOne
	@JoinColumn(name="roleId") 
	private Role role;
	
	public RolePermission(){
	
	}

	public RolePermission(Integer roleId, Long permissionId) {
		this.roleId = roleId;
		this.permissionId = permissionId;
	}
	
	public Integer getRoleId() {
		return roleId;
	}
	
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	
	public Long getPermissionId() {
		return permissionId;
	}
	
	public void setPermissionId(Long permissionId) {
		this.permissionId = permissionId;
	}
	
	public void setPermission(Permission permission){
		this.permission = permission;
	}
	
	public Permission getPermission() {
		return permission;
	}
	public void setRole(Role role){
		this.role = role;
	}
	
	public Role getRole() {
		return role;
	}

}

