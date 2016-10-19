package com.easycodebox.login.shiro.permission;

import java.io.Serializable;

import org.apache.shiro.authz.Permission;

/**
 * isPermitted接口代表当前用户是否有此权限。<br>
 * 提供给非严格模式的权限验证（权限系统中没有出现被验证的权限时则放行,适合敏捷开发）<br>
 * <p>
 * 如果url规则中配置  <code>/** = authc,perms</code>，则所有的请求都会被拦截进行权限验证，当我有部分请求不需要perms验证，
 * 只需要authc验证时，不得不定义一种类似的规则 <code>/permit/** = authc</code>来实现。这样url地址就无缘无故多了个<code>/permit</code>。 
 * <p>
 * 由于以上问题导致了开发时增加了复杂度，使用此接口即解决上诉问题：实现此接口的权限只是标记权限系统中有这么个权限，至于该用户是否有此权限，依据isPermitted接口来判断。
 * 即虽然你配置了<code>/** = authc,perms</code>，但是只有出现在权限系统中的权限才会被验证，
 * 没有在权限系统中出现的权限直接放行。
 * @author WangXiaoJin
 *
 */
public interface GlobalPermission extends Permission, Serializable {

	/**
	 * 标记用户是否拥有此权限
	 * @return
	 */
	boolean isPermitted();
	
}
