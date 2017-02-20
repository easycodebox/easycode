package com.easycodebox.auth.model.bo.user;

import com.easycodebox.auth.model.entity.user.Permission;
import com.easycodebox.jdbc.GenerateRes;

import java.io.Serializable;
import java.util.List;

@GenerateRes(false)
public class AuthzInfoBo implements Serializable {
	
	private String userId;
	
	private String roleNames;
	
	private String roleIds;
	
	/**
	 * 所有的权限
	 */
	private String permissions;
	
	/**
	 * 菜单项
	 */
	private List<Permission> menus;
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(String roleNames) {
		this.roleNames = roleNames;
	}

	public String getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String roleIds) {
		this.roleIds = roleIds;
	}

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

	public List<Permission> getMenus() {
		return menus;
	}

	public void setMenus(List<Permission> menus) {
		this.menus = menus;
	}

}
