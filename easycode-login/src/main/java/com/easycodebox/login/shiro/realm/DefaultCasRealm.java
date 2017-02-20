package com.easycodebox.login.shiro.realm;

import com.easycodebox.auth.model.bo.user.AuthzInfoBo;
import com.easycodebox.auth.model.entity.user.User;
import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.error.ErrorContext;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.lang.dto.UserInfo;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.login.shiro.AdvancedAuthorizationInfo;
import com.easycodebox.login.shiro.ShiroSecurityInfoHandler;
import com.easycodebox.login.shiro.permission.GlobalPermission;
import com.easycodebox.login.ws.UserWsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cas.*;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.jasig.cas.client.validation.TicketValidator;

import java.io.Serializable;
import java.util.*;

/**
 * 
 * @author WangXiaoJin
 *
 */
public class DefaultCasRealm extends CasRealm implements Serializable {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private UserWsService userWsService;
	
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
	
	@Override
	protected void onInit() {
		super.onInit();
		Assert.notNull(userWsService);
		Assert.notNull(securityInfoHandler);
		Assert.notBlank(project);
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
	
	/**
	 * 获取当前project的权限
	 * @param info
	 * @return
	 */
	@Override
	protected Collection<Permission> getPermissions(AuthorizationInfo info) {
		Set<Permission> permissions = new HashSet<>();
		
		if (info != null) {
			Collection<Permission> perms;
			if (info instanceof AdvancedAuthorizationInfo) {
				AdvancedAuthorizationInfo advInfo = (AdvancedAuthorizationInfo) info;
				perms = advInfo.getObjectPermissions(project);
				if (CollectionUtils.isNotEmpty(perms)) {
					permissions.addAll(perms);
				}
				perms = resolvePermissions(advInfo.getStringPermissions(project));
				if (CollectionUtils.isNotEmpty(perms)) {
					permissions.addAll(perms);
				}
			} else {
				perms = info.getObjectPermissions();
				if (CollectionUtils.isNotEmpty(perms)) {
					permissions.addAll(perms);
				}
				perms = resolvePermissions(info.getStringPermissions());
				if (CollectionUtils.isNotEmpty(perms)) {
					permissions.addAll(perms);
				}
			}
			perms = resolveRolePermissions(info.getRoles());
			if (CollectionUtils.isNotEmpty(perms)) {
				permissions.addAll(perms);
			}
		}
		
		if (permissions.isEmpty()) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(permissions);
		}
	}
	
	@Override
	protected boolean isPermitted(Permission permission, AuthorizationInfo info) {
		if (info == null || info instanceof AdvancedAuthorizationInfo && !((AdvancedAuthorizationInfo)info).authorized(project)) {
			return false;
		}
        Collection<Permission> perms = getPermissions(info);
        //提供给GlobalPermission使用.如果included = true，说明之前已经有GlobalPermission匹配过permission，且没有permission权限
        boolean included = false;
        if (CollectionUtils.isNotEmpty(perms)) {
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
			
			User user = userWsService.load((String) casToken.getPrincipal());
			Assert.notNull(user, CodeMsg.FAIL.msg("请输入正确的用户名或密码"));
			
			AuthzInfoBo authzInfo = userWsService.authzInfo((String)casToken.getPrincipal(), project, validProjectAuth);
			if (authzInfo == null) {
				throw ErrorContext.instance("您没有权限登录此系统");
			}
	        //设置角色
	        if (authzInfo.getRoleNames() != null) {
	        	attributes.put(getRoleAttributeNames(), authzInfo.getRoleNames());
			}
	        //设置权限
	        if (authzInfo.getPermissions() != null) {
	        	attributes.put(getPermissionAttributeNames(), authzInfo.getPermissions());
			}
	        //获取当前Session
	        Session session = SecurityUtils.getSubject().getSession(false);
	        UserInfo userInfo = new UserInfo(user.getId(), 
					user.getUsername(), user.getNickname(), user.getRealname(), user.getPic(),
			user.getStatus(), user.getGroupId(), user.getGroupName());
			//存储用户信息
	        securityInfoHandler.storeSecurityInfo(session, userInfo);
	        //项目功能菜单 - 每个项目都可能会有不同的功能菜单，所以增加各自项目的缓存key前缀
	        session.setAttribute(project + BaseConstants.PROJECT_MENUS, treePermissions(null, authzInfo.getMenus()));
	        
			return info;
		} catch (CasAuthenticationException e) {
			throw e;
		} catch (Exception e) {
			log.error("Authenticated error!", e);
			throw new CasAuthenticationException("Authenticated error!", e);
		}
	}
	
	/**
	 * 返回{@link AdvancedAuthorizationInfo}对象以精细控制权限
	 * @param principals
	 * @return
	 */
	@Override
	protected AuthorizationInfo getAuthorizationInfo(PrincipalCollection principals) {
		if (principals == null) {
			return null;
		}
		AdvancedAuthorizationInfo info = null;
		
		if (log.isTraceEnabled()) {
			log.trace("Retrieving AuthorizationInfo for principals [" + principals + "]");
		}
		
		Cache<Object, AuthorizationInfo> cache = getAvailableAuthorizationCache();
		if (cache != null) {
			if (log.isTraceEnabled()) {
				log.trace("Attempting to retrieve the AuthorizationInfo from cache.");
			}
			Object key = getAuthorizationCacheKey(principals);
			info = (AdvancedAuthorizationInfo) cache.get(key);
			if (log.isTraceEnabled()) {
				if (info == null) {
					log.trace("No AuthorizationInfo found in cache for principals [" + principals + "]");
				} else {
					log.trace("AuthorizationInfo found in cache for principals [" + principals + "]");
				}
			}
		}
		//缓存中没有AdvancedAuthorizationInfo数据或者AdvancedAuthorizationInfo没有初始化过project权限信息则调用doGetAuthorizationInfo获取权限数据
		if (info == null || !info.initedProjectPermission(project)) {
			if (info == null) {
				// Call template method if the info was not found in a cache
				info = (AdvancedAuthorizationInfo) doGetAuthorizationInfo(principals);
				//添加此用户是否有访问当前project的权限
				info.addAuthority(project, true);
			} else {
				//缓存中已经存在 info 数据，只需要初始化 project 中的权限信息
				AuthzInfoBo authzInfo = userWsService.authzInfo((String)getAvailablePrincipal(principals), project, validProjectAuth);
				//添加此用户是否有访问当前project的权限
				info.addAuthority(project, authzInfo != null);
				if (authzInfo != null) {
					//添加用户权限
					info.addStringPermissions(project, Strings.split2List(authzInfo.getPermissions(), Symbol.COMMA));
					//获取当前Session
					Session session = SecurityUtils.getSubject().getSession(false);
					//项目功能菜单 - 每个项目都可能会有不同的功能菜单，所以增加各自项目的缓存key前缀
					session.setAttribute(project + BaseConstants.PROJECT_MENUS, treePermissions(null, authzInfo.getMenus()));
				}
			}
			// If the info is not null and the cache has been created, then cache the authorization info.
			if (info != null && cache != null) {
				if (log.isTraceEnabled()) {
					log.trace("Caching authorization info for principals: [" + principals + "].");
				}
				Object key = getAuthorizationCacheKey(principals);
				cache.put(key, info);
			}
		}
		return info;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		AuthorizationInfo simpleInfo = super.doGetAuthorizationInfo(principals);
		AdvancedAuthorizationInfo info = new AdvancedAuthorizationInfo();
		if (CollectionUtils.isNotEmpty(simpleInfo.getRoles())) {
			info.addRoles(simpleInfo.getRoles());
		}
		if (CollectionUtils.isNotEmpty(simpleInfo.getObjectPermissions())) {
			info.addObjectPermissions(project, simpleInfo.getObjectPermissions());
		}
		if (CollectionUtils.isNotEmpty(simpleInfo.getStringPermissions())) {
			info.addStringPermissions(project, simpleInfo.getStringPermissions());
		}
		return info;
	}
	
	/**
	 * 因为{@link AuthorizingRealm#getAvailableAuthorizationCache()}方法为 private，当前类的{@link #getAuthorizationInfo}
	 * 方法不能没权限访问，所以把{@link AuthorizingRealm#getAvailableAuthorizationCache()}方法Copy一份
	 * @return
	 */
	protected Cache<Object, AuthorizationInfo> getAvailableAuthorizationCache() {
		Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
		if (cache == null && isAuthorizationCachingEnabled()) {
			cache = getAuthorizationCacheLazy();
		}
		return cache;
	}
	
	/**
	 * 因为{@link AuthorizingRealm#getAuthorizationCacheLazy()}方法为 private，当前类的{@link #getAuthorizationInfo}
	 * 方法不能没权限访问，所以把{@link AuthorizingRealm#getAuthorizationCacheLazy()}方法Copy一份
	 * @return
	 */
	private Cache<Object, AuthorizationInfo> getAuthorizationCacheLazy() {
		if (getAuthorizationCache() == null) {
			if (log.isDebugEnabled()) {
				log.debug("No authorizationCache instance set.  Checking for a cacheManager...");
			}
			
			CacheManager cacheManager = getCacheManager();
			if (cacheManager != null) {
				String cacheName = getAuthorizationCacheName();
				if (log.isDebugEnabled()) {
					log.debug("CacheManager [" + cacheManager + "] has been configured.  Building " +
							"authorization cache named [" + cacheName + "]");
				}
				setAuthorizationCache(cacheManager.<Object, AuthorizationInfo>getCache(cacheName));
			} else {
				if (log.isInfoEnabled()) {
					log.info("No cache or cacheManager properties have been set.  Authorization cache cannot " +
							"be obtained.");
				}
			}
		}
		return getAuthorizationCache();
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
	
	/**
	 * 此方法 Copy 自父类，没有改变任何代码
	 * @param stringPerms
	 * @return
	 */
	private Collection<Permission> resolvePermissions(Collection<String> stringPerms) {
		Collection<Permission> perms = Collections.emptySet();
		PermissionResolver resolver = getPermissionResolver();
		if (resolver != null && !CollectionUtils.isEmpty(stringPerms)) {
			perms = new LinkedHashSet<>(stringPerms.size());
			for (String strPermission : stringPerms) {
				Permission permission = getPermissionResolver().resolvePermission(strPermission);
				perms.add(permission);
			}
		}
		return perms;
	}
	
	/**
	 * 此方法 Copy 自父类，没有改变任何代码
	 * @param roleNames
	 * @return
	 */
	private Collection<Permission> resolveRolePermissions(Collection<String> roleNames) {
		Collection<Permission> perms = Collections.emptySet();
		RolePermissionResolver resolver = getRolePermissionResolver();
		if (resolver != null && !CollectionUtils.isEmpty(roleNames)) {
			perms = new LinkedHashSet<>(roleNames.size());
			for (String roleName : roleNames) {
				Collection<Permission> resolved = resolver.resolvePermissionsInRole(roleName);
				if (!CollectionUtils.isEmpty(resolved)) {
					perms.addAll(resolved);
				}
			}
		}
		return perms;
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
	
	public UserWsService getUserWsService() {
		return userWsService;
	}
	
	public void setUserWsService(UserWsService userWsService) {
		this.userWsService = userWsService;
	}
	
	public ShiroSecurityInfoHandler getSecurityInfoHandler() {
		return securityInfoHandler;
	}
	
	public void setSecurityInfoHandler(ShiroSecurityInfoHandler securityInfoHandler) {
		this.securityInfoHandler = securityInfoHandler;
	}
}
