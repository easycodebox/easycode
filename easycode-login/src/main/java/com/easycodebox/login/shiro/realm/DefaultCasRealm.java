package com.easycodebox.login.shiro.realm;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.cas.CasAuthenticationException;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Value;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.lang.dto.UserInfo;
import com.easycodebox.login.shiro.ShiroSecurityInfoHandler;
import com.easycodebox.login.shiro.permission.GlobalPermission;
import com.easycodebox.login.ws.UserWsService;
import com.easycodebox.login.ws.bo.OperationWsBo;
import com.easycodebox.login.ws.bo.UserExtWsBo;

/**
 * 
 * @author WangXiaoJin
 *
 */
public class DefaultCasRealm extends CasRealm implements Serializable {

	private static final long serialVersionUID = 2888923134019023168L;

	//private static final Logger LOG = LoggerFactory.getLogger(DefaultCasRealm.class);
	
	public static final String PRINCIPAL_USER_ID_KEY = "id";
	public static final String PRINCIPAL_ROLES_KEY = "roles";
	public static final String PRINCIPAL_PERMISSIONS_KEY = "operations";
	
	@Resource
	private UserWsService userWsService;
	@Resource
	private ShiroSecurityInfoHandler securityInfoHandler;
	
	@Value("${project}")
	private String projectNo;
	@Value("${valid_project_auth:true}")
	private boolean validProjectAuth;
	
	/**
	 * 全局模式 - 会一次性加载系统中所有的权限，然后系统中明确
	 */
	private boolean globalPermissionMode;
	
	public DefaultCasRealm() {
		setName("defaultCasRealm");
	}

	@Override
	protected boolean isPermitted(Permission permission, AuthorizationInfo info) {
        Collection<Permission> perms = getPermissions(info);
        //提供给GlobalPermission使用.如果included = true，说明之前已经有GlobalPermission匹配过permission，且没有permission权限
        boolean included = false;
        if (perms != null && !perms.isEmpty()) {
            for (Permission perm : perms) {
            	if (perm instanceof GlobalPermission) {
            		GlobalPermission gp = (GlobalPermission)perm;
            		//GlobalPermission包含permission
            		if (perm.implies(permission)) {
            			if (gp.isPermitted()) {
            				return true;
						} else {
							included = true;
						}
					}
				} else if (perm.implies(permission)) {
                    return true;
                }
            }
        }
        //如果是系统一次性加载所有权限的模式且系统中不包含此验证permission则返回true，意思就是此permission不受权限管控，直接放行
        if (globalPermissionMode && !included) {
			return true;
		}
		return false;
    }
	
	/**
	 * 重写认证 Authenticates a user and retrieves its information.
	 * @param token the authentication token
	 * @throws AuthenticationException  if there is an error during authentication.
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {

		try {
			SimpleAuthenticationInfo info = (SimpleAuthenticationInfo)super.doGetAuthenticationInfo(token);
			Map<String, String> attributes = (Map<String, String>)info.getPrincipals().asList().get(1);
			
			//后期考虑直接从CAS返回角色、权限等信息是否可行
	        String userId = attributes.get(PRINCIPAL_USER_ID_KEY);
	        UserExtWsBo user = userWsService.loginSuc(userId, projectNo, validProjectAuth);
	        //设置角色
	        if (user.getRoleNames() != null) {
	        	attributes.put(PRINCIPAL_ROLES_KEY, user.getRoleNames());
			}
	        //设置权限
	        if (user.getOperations() != null) {
	        	attributes.put(PRINCIPAL_PERMISSIONS_KEY, user.getOperations());
			}
	        //获取当前Session
	        Session session = SecurityUtils.getSubject().getSession(false);
	        UserInfo userInfo = new UserInfo(user.getId(), 
					user.getUsername(), user.getNickname(), user.getRealname(), user.getPic(), 
					user.getStatus(), user.getGroupId(), user.getGroupName());
	        //存储用户信息
	        securityInfoHandler.storeSecurityInfo(session, userInfo);
	        //存储菜单
	        session.setAttribute(BaseConstants.LEFT_MENU_KEY, treeOperations(null, user.getMenus()));
	        
			return info;
		} catch (CasAuthenticationException e) {
			throw e;
		} catch (Exception e) {
			throw new CasAuthenticationException("Authenticated error!", e);
		}
	}
	
	/**
	 * 为权限列表组装成树形结构
	 * @param parentId
	 * @param all	所有的权限
	 * @return
	 */
	private static List<OperationWsBo> treeOperations(Long parentId, List<OperationWsBo> all) {
		List<OperationWsBo> cur = new LinkedList<OperationWsBo>();
		for(OperationWsBo o : all) {
			if(parentId == null ? o.getParentId() == null : parentId.equals(o.getParentId())) {
				o.setChildren(treeOperations(o.getId(), all));
				cur.add(o);
			}
		}
		return cur;
	}

	public boolean isGlobalPermissionMode() {
		return globalPermissionMode;
	}

	public void setGlobalPermissionMode(boolean globalPermissionMode) {
		this.globalPermissionMode = globalPermissionMode;
	}
	
}
