package com.easycodebox.login.shiro.realm;

import com.easycodebox.auth.model.bo.user.UserFullBo;
import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.lang.dto.UserInfo;
import com.easycodebox.login.shiro.ShiroSecurityInfoHandler;
import com.easycodebox.login.shiro.permission.GlobalPermission;
import com.easycodebox.login.ws.UserWsService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.cas.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.jasig.cas.client.validation.TicketValidator;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

/**
 * 
 * @author WangXiaoJin
 *
 */
public class DefaultCasRealm extends CasRealm implements Serializable {

	//private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Resource
	private UserWsService userWsService;
	@Resource
	private ShiroSecurityInfoHandler securityInfoHandler;
	
	private String project;
	
	private boolean validProjectAuth = true;
	
	/**
	 * 全局模式 - 会一次性加载系统中所有的权限，然后系统中明确
	 */
	private boolean globalPermissionMode;
	
	/**
	 * 因为{@link CasRealm}的ticketValidator属性没有set方法，所以重写此属性增加setTicketValidator方法
	 */
	private TicketValidator ticketValidator;
	
	public DefaultCasRealm() {
		super();
	}
	
	/**
	 * 默认返回的key是 pri 本身，导致key太长。重写此方法，返回primary principal作为key，以减少key长度。
	 * @param principals
	 * @return
	 */
	@Override
	protected Object getAuthorizationCacheKey(PrincipalCollection principals) {
		return getAvailablePrincipal(principals);
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
		return globalPermissionMode && !included;
	}
	
	/**
	 * 重载createTicketValidator方法是为了可以自定义TicketValidator
	 */
	@Override
	protected TicketValidator createTicketValidator() {
		return ticketValidator == null ? super.createTicketValidator() : ticketValidator;
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
			if (info == null) {
				return null;
			}
			CasToken casToken = (CasToken) token;
			
			Map<String, Object> attributes = (Map<String, Object>)info.getPrincipals().asList().get(1);
			
			//后期考虑直接从CAS返回角色、权限等信息是否可行
	        UserFullBo user = userWsService.loginSuc((String)casToken.getPrincipal(), project, validProjectAuth);
	        //设置角色
	        if (user.getRoleNames() != null) {
	        	attributes.put(getRoleAttributeNames(), user.getRoleNames());
			}
	        //设置权限
	        if (user.getPermissions() != null) {
	        	attributes.put(getPermissionAttributeNames(), user.getPermissions());
			}
	        //获取当前Session
	        Session session = SecurityUtils.getSubject().getSession(false);
	        UserInfo userInfo = new UserInfo(user.getId(), 
					user.getUsername(), user.getNickname(), user.getRealname(), user.getPic(), 
					user.getStatus(), user.getGroupId(), user.getGroupName());
	        //存储用户信息
	        securityInfoHandler.storeSecurityInfo(session, userInfo);
	        //项目功能菜单 - 每个项目都可能会有不同的功能菜单，所以增加各自项目的缓存key前缀
	        session.setAttribute(project + BaseConstants.PROJECT_MENUS, treePermissions(null, user.getMenus()));
	        
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
	private static List<com.easycodebox.auth.model.entity.user.Permission> 
		treePermissions(Long parentId, List<com.easycodebox.auth.model.entity.user.Permission> all) {
		List<com.easycodebox.auth.model.entity.user.Permission> cur = new LinkedList<>();
		for(com.easycodebox.auth.model.entity.user.Permission o : all) {
			if(parentId == null ? o.getParentId() == null : parentId.equals(o.getParentId())) {
				o.setChildren(treePermissions(o.getId(), all));
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

	public TicketValidator getTicketValidator() {
		return ticketValidator;
	}

	public void setTicketValidator(TicketValidator ticketValidator) {
		this.ticketValidator = ticketValidator;
	}
	
	public String getProject() {
		return project;
	}
	
	public void setProject(String project) {
		this.project = project;
	}
	
	public boolean isValidProjectAuth() {
		return validProjectAuth;
	}
	
	public void setValidProjectAuth(boolean validProjectAuth) {
		this.validProjectAuth = validProjectAuth;
	}
}
