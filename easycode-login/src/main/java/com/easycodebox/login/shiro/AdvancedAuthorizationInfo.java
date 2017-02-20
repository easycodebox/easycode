package com.easycodebox.login.shiro;

import com.easycodebox.common.validate.Assert;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;

import java.util.*;

/**
 * 提供了针对不同的project返回不同的Permission集合，传统的AuthorizationInfo里面包含了当前用户所有的权限，并没有做精细划分。
 * 用传统的方法时，登录成功后必须把所有的权限都取出来保存至AuthorizationInfo，如果权限信息量很大时会降低网络传输速度，
 * 还会造成数据浪费，比如登录从始至终只操作的A系统，你却把B、C系统的权限一起返回过来，这些B、C系统的权限根本没用到。
 * 另外你还需要控制多个系统不能出现重名的权限。
 * <p/>
 * 鉴于上述原因，增加了AdvancedAuthorizationInfo来精细控制权限
 *
 * @author WangXiaoJin
 */
public class AdvancedAuthorizationInfo implements AuthorizationInfo {
	
	protected Set<String> roles;
	
	/**
	 * key: project
	 */
	protected Map<String, Set<String>> stringPermissionMap;
	
	/**
	 * key: project
	 */
	protected Map<String, Set<Permission>> objectPermissionMap;
	
	/**
	 * key: project
	 */
	protected Map<String, Boolean> authorizedMap;
	
	public AdvancedAuthorizationInfo() {
	}
	
	public AdvancedAuthorizationInfo(Set<String> roles) {
		this.roles = roles;
	}
	
	@Override
	public Set<String> getRoles() {
		return roles;
	}
	
	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
	
	public void addRole(String role) {
		if (this.roles == null) {
			this.roles = new HashSet<>();
		}
		this.roles.add(role);
	}
	
	public void addRoles(Collection<String> roles) {
		if (this.roles == null) {
			this.roles = new HashSet<>();
		}
		this.roles.addAll(roles);
	}
	
	/**
	 * 判断是否初始化过指定project权限
	 * @param project
	 * @return
	 */
	public boolean initedProjectPermission(String project) {
		return stringPermissionMap != null && stringPermissionMap.containsKey(project)
				|| objectPermissionMap != null && objectPermissionMap.containsKey(project);
	}
	
	/**
	 * 用户是否有权限访问此project
	 * @param project
	 * @return
	 */
	public Boolean authorized(String project) {
		return authorizedMap != null && authorizedMap.get(project);
	}
	
	/**
	 * 添加项目授权信息
	 * @param project
	 * @param authorized 标记用户是否有权限访问此project
	 */
	public void addAuthority(String project, boolean authorized) {
		if (authorizedMap == null) {
			authorizedMap = new HashMap<>();
		}
		authorizedMap.put(project, authorized);
	}
	
	@Override
	public Set<String> getStringPermissions() {
		if (stringPermissionMap == null) return null;
		Set<String> data = new HashSet<>();
		for (Set<String> permissions : stringPermissionMap.values()) {
			data.addAll(permissions);
		}
		return data;
	}
	
	public Set<String> getStringPermissions(String project) {
		return stringPermissionMap == null ? null : stringPermissionMap.get(project);
	}
	
	public void addStringPermission(String project, String permission) {
		checkStringPermissionMap(project);
		this.stringPermissionMap.get(project).add(permission);
	}
	
	public void addStringPermissions(String project, Collection<String> permissions) {
		checkStringPermissionMap(project);
		this.stringPermissionMap.get(project).addAll(permissions);
	}
	
	@Override
	public Set<Permission> getObjectPermissions() {
		if (objectPermissionMap == null) return null;
		Set<Permission> data = new HashSet<>();
		for (Set<Permission> permissions : objectPermissionMap.values()) {
			data.addAll(permissions);
		}
		return data;
	}
	
	public Set<Permission> getObjectPermissions(String project) {
		return objectPermissionMap == null ? null : objectPermissionMap.get(project);
	}
	
	public void addObjectPermission(String project, Permission permission) {
		if (permission == null) return;
		checkObjectPermissionMap(project);
		this.objectPermissionMap.get(project).add(permission);
	}
	
	public void addObjectPermissions(String project, Collection<Permission> permissions) {
		checkObjectPermissionMap(project);
		this.objectPermissionMap.get(project).addAll(permissions);
	}
	
	/**
	 * 检查stringPermissionMap对应的{@code project}数据有没有初始化
	 * @param project 项目名
	 */
	private void checkStringPermissionMap(String project) {
		Assert.notBlank(project);
		if (stringPermissionMap == null) {
			stringPermissionMap = new HashMap<>();
		}
		if (!stringPermissionMap.containsKey(project)) {
			stringPermissionMap.put(project, new HashSet<String>());
		}
	}
	
	/**
	 * 检查objectPermissionMap对应的{@code project}数据有没有初始化
	 * @param project 项目名
	 */
	private void checkObjectPermissionMap(String project) {
		Assert.notBlank(project);
		if (objectPermissionMap == null) {
			objectPermissionMap = new HashMap<>();
		}
		if (!objectPermissionMap.containsKey(project)) {
			objectPermissionMap.put(project, new HashSet<Permission>());
		}
	}
	
	public Map<String, Set<String>> getStringPermissionMap() {
		return Collections.unmodifiableMap(stringPermissionMap);
	}
	
	public void setStringPermissionMap(Map<String, Set<String>> stringPermissionMap) {
		this.stringPermissionMap = stringPermissionMap;
	}
	
	public Map<String, Set<Permission>> getObjectPermissionMap() {
		return Collections.unmodifiableMap(objectPermissionMap);
	}
	
	public void setObjectPermissionMap(Map<String, Set<Permission>> objectPermissionMap) {
		this.objectPermissionMap = objectPermissionMap;
	}
	
	public Map<String, Boolean> getAuthorizedMap() {
		return authorizedMap;
	}
	
	public void setAuthorizedMap(Map<String, Boolean> authorizedMap) {
		this.authorizedMap = authorizedMap;
	}
}
