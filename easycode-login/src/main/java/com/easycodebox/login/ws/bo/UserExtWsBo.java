package com.easycodebox.login.ws.bo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserExtWsBo extends UserWsBo {

	private static final long serialVersionUID = -3600978665605684188L;

	private Set<String> roleNames;
	
	private Integer[] roleIds;
	
	private Map<String, Boolean> allOsMap;
	
	private List<OperationWsBo> menus;

	public Set<String> getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(Set<String> roleNames) {
		this.roleNames = roleNames;
	}

	public Integer[] getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(Integer[] roleIds) {
		this.roleIds = roleIds;
	}

	public Map<String, Boolean> getAllOsMap() {
		return allOsMap;
	}

	public void setAllOsMap(Map<String, Boolean> allOsMap) {
		this.allOsMap = allOsMap;
	}

	public List<OperationWsBo> getMenus() {
		return menus;
	}

	public void setMenus(List<OperationWsBo> menus) {
		this.menus = menus;
	}

}
