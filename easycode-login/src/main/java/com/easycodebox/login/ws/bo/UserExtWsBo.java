package com.easycodebox.login.ws.bo;

import java.util.List;

public class UserExtWsBo extends UserWsBo {

	private static final long serialVersionUID = -3600978665605684188L;

	private String roleNames;
	
	private String roleIds;
	
	/**
	 * 所有的权限
	 */
	private String operations;
	
	/**
	 * 菜单项
	 */
	private List<OperationWsBo> menus;

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

	public String getOperations() {
		return operations;
	}

	public void setOperations(String operations) {
		this.operations = operations;
	}

	public List<OperationWsBo> getMenus() {
		return menus;
	}

	public void setMenus(List<OperationWsBo> menus) {
		this.menus = menus;
	}

}
