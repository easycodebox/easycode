package com.easycodebox.login.shiro.permission;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.util.AntPathMatcher;

/**
 * 
 * @author WangXiaoJin
 *
 */
public class UrlWildcardPermissionResolver implements PermissionResolver {

	private String dividerToken;
	
	@Override
	public Permission resolvePermission(String permissionString) {
		return new UrlWildcardPermission(permissionString, dividerToken, new AntPathMatcher());
	}

	public String getDividerToken() {
		return dividerToken;
	}

	public void setDividerToken(String dividerToken) {
		this.dividerToken = dividerToken;
	}

}
