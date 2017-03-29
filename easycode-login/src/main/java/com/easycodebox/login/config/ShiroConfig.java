package com.easycodebox.login.config;

import com.easycodebox.common.cache.spring.redis.CustomRedisCacheManager;
import com.easycodebox.login.shiro.ShiroSecurityInfoHandler;
import com.easycodebox.login.shiro.cache.spring.RedisTemplateCacheStats;
import com.easycodebox.login.shiro.cache.spring.SpringCacheManager;
import com.easycodebox.login.shiro.filter.*;
import com.easycodebox.login.shiro.permission.UrlWildcardPermissionResolver;
import com.easycodebox.login.shiro.realm.DefaultCasRealm;
import com.easycodebox.login.ws.UserWsService;
import org.apache.commons.collections.MapUtils;
import org.apache.shiro.authc.pam.*;
import org.apache.shiro.cas.CasSubjectFactory;
import org.apache.shiro.config.Ini;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.config.IniFilterChainResolverFactory;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.*;

/**
 * @author WangXiaoJin
 */
@Configuration
@EnableConfigurationProperties(LoginProperties.class)
public class ShiroConfig {
	
	@Autowired
	private LoginProperties loginProperties;
	
	@Autowired
	private CustomRedisCacheManager shiroCacheManager;
	
	@Autowired
	private RedisTemplateCacheStats cacheStats;
	
	/**
	 * 此处不能换成{@link Autowired}，因为{@link Autowired}会根据类型来进行匹配，而easycode-auth-core包中
	 * 有一个{@code UserWsServiceImpl}类，所以会直接依赖core包里的实例，
	 * 不会依赖{@link WsClientConfig#userWsService()}实例，这和Spring的实例初始化顺序有关。就算你用了
	 * {@link Autowired}、{@link org.springframework.beans.factory.annotation.Qualifier}组合也不能解决上述问题。
	 */
	@Resource
	private UserWsService userWsService;
	
	@Autowired(required = false)
	private AuthenticationStrategy authenticationStrategy;
	
	@Autowired
	private List<Realm> realms;
	
	/**
	 * 操作SecurityInfo
	 */
	@Bean
	public ShiroSecurityInfoHandler securityInfoHandler() {
		return new ShiroSecurityInfoHandler();
	}
	
	@Bean
	public ShiroFilterFactoryBean shiroFilter() {
		ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
		
		//设置Filter映射
		LinkedHashMap<String, Filter> filterMap = new LinkedHashMap<>();
		DefaultCasFilter casFilter = new DefaultCasFilter();
		casFilter.setFailureUrl(loginProperties.getFailureUrl()); //配置验证错误时的失败页面
		casFilter.setReloginUrl(loginProperties.getCasLogin() + "&msg={0}"); //验证错误后显示登录页面，并提示错误信息。只试用于ErrorContext异常
		casFilter.setLogoutUrl(loginProperties.getCasLogout());
		filterMap.put("casFilter", casFilter);
		LogoutFilter logoutFilter = new LogoutFilter();
		logoutFilter.setRedirectUrl(loginProperties.getCasLogout() + "?service=" + loginProperties.getCasLogoutCallback());
		filterMap.put("logoutFilter", logoutFilter);
		filterMap.put("perms", new DefaultPermissionsAuthorizationFilter());
		filterMap.put("authc", new DefaultFormAuthenticationFilter());
		filterMap.put("sense", new SenseLoginFilter());
		factoryBean.setFilters(filterMap);
		
		factoryBean.setSecurityManager(securityManager());
		factoryBean.setLoginUrl(loginProperties.getCasLogin());
		factoryBean.setUnauthorizedUrl(loginProperties.getUnauthorizedUrl());
		//加载权限配置
		Ini ini = new Ini();
		ini.loadFromPath(loginProperties.getShiroFilterFile());
		//did they explicitly state a 'urls' section?  Not necessary, but just in case:
		Ini.Section section = ini.getSection(IniFilterChainResolverFactory.URLS);
		if (MapUtils.isEmpty(section)) {
			//no urls section.  Since this _is_ a urls chain definition property, just assume the
			//default section contains only the definitions:
			section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
		}
		factoryBean.setFilterChainDefinitionMap(section);
		return factoryBean;
	}
	
	@Bean
	public EnterpriseCacheSessionDAO sessionDAO() {
		EnterpriseCacheSessionDAO sessionDAO = new EnterpriseCacheSessionDAO();
		sessionDAO.setActiveSessionsCacheName("sessionsCache");
		return sessionDAO;
	}
	
	/**
	 * 1. 修改Session保存到Cookie中的Key值，JSESSIONID 修改为 sid，解决使用Web容器Session时出现302重定向循环问题。
		 如果你想查看项目中有没有使用Web容器的Session，你可以把sid改成JSESSIONID，这样就会出现302重定向循环问题。
		 原因：因为项目中使用了Shiro的Native Session，如果你用到Web容器的Session时（即调用了request.getSession()
		 或request.getSession(true)），Web容器会根据JSESSIONID的Cookie值去容器里找，没有找到则创建一个新的
		 Web Session然后把此Session Id存入到JSESSIONID的Cookie中。这样就会更新Shiro之前保存的JSESSIONID Cookie值，
		 所以会出现302重定向循环。
		 
		 2. 还有一种情况必须改成此Key值：当你本地启动了easycode-cas项目，且你的项目和cas项目共用同一个域名（比如：localhost），
		 只是通过端口号来区分访问哪个项目。这种情况下你第一次登录系统会报错，跳转到500页面，再次请求项目不会出现500页面。这是因为
		 CAS登录校验成功后，会访问本项目的/login地址，此请求中会包含JSESSIONID的cookie中，这是CAS项目生成的cookie，
		 由于CAS项目和本项目用的域是相同的，所以本项目可以拿到此JSESSIONID的cookie值去shiro的session manager中获取
		 对应的Session。这个JSESSIONID对应的session是CAS的Web容器生成的，Shiro获取不到对应的Session就抛出异常了。
		 提示：所以如果你启动的项目和CAS共用同一域，cookie key值就不能定义成JSESSIONID，除非CAS的session也是通过Shiro生成
		 的，且为互相共享Session。
	 */
	@Bean
	public SimpleCookie sessionIdCookie() {
		SimpleCookie cookie = new SimpleCookie();
		cookie.setName("sid");
		return cookie;
	}
	
	/**
	 * globalSessionTimeout: Session Timeout 30分钟
	 */
	@Bean
	public DefaultWebSessionManager sessionManager() {
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setGlobalSessionTimeout(1800000);
		sessionManager.setSessionDAO(sessionDAO());
		sessionManager.setSessionIdCookie(sessionIdCookie());
		sessionManager.setSessionValidationSchedulerEnabled(false);
		return sessionManager;
	}
	
	@Bean
	public SpringCacheManager securityCacheManager() {
		SpringCacheManager cacheManager = new SpringCacheManager(shiroCacheManager);
		cacheManager.setCacheStats(cacheStats);
		return cacheManager;
	}
	
	/**
	 * String类型转换成Permission类型
	 */
	@Bean
	public UrlWildcardPermissionResolver urlWildcardPermissionResolver() {
		return new UrlWildcardPermissionResolver();
	}
	
	@Bean
	public Cas30ServiceTicketValidator ticketValidator() {
		Cas30ServiceTicketValidator validator = new Cas30ServiceTicketValidator(loginProperties.getCasUrl());
		validator.setEncoding("UTF-8");
		return validator;
	}
	
	/**
	 * casService: 客户端的回调地址设置，必须和下面的shiro-cas过滤器拦截的地址一致
	 */
	@Bean
	public DefaultCasRealm casRealm() {
		DefaultCasRealm realm = new DefaultCasRealm();
		realm.setPermissionResolver(urlWildcardPermissionResolver());
		realm.setTicketValidator(ticketValidator());
		realm.setUserWsService(userWsService);
		realm.setSecurityInfoHandler(securityInfoHandler());
		realm.setName("cas");
		realm.setGlobalPermissionMode(true);
		realm.setCasServerUrlPrefix(loginProperties.getCasUrl());
		realm.setCasService(loginProperties.getCasLoginCallback());
		realm.setRoleAttributeNames("roles");
		realm.setPermissionAttributeNames("permissions");
		realm.setProject(loginProperties.getProject());
		return realm;
	}
	
	/**
	 * 默认使用{@link AllSuccessfulStrategy}策略，即必须全部验证通过
	 */
	@Bean
	public ModularRealmAuthenticator authenticator() {
		AuthenticationStrategy auth = authenticationStrategy == null ? new AllSuccessfulStrategy() : authenticationStrategy;
		ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
		authenticator.setAuthenticationStrategy(auth);
		return authenticator;
	}
	
	/**
	 * 如果要实现cas的remember me的功能，需要用到下面这个bean，并设置到securityManager的subjectFactory中
	 */
	@Bean
	public CasSubjectFactory casSubjectFactory() {
		return new CasSubjectFactory();
	}
	
	@Bean
	public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}
	
	@Bean
	public DefaultWebSecurityManager securityManager() {
		realms = realms == null ? new ArrayList<Realm>() : realms;
		boolean existCasRealm = false;
		for (Realm realm : realms) {
			if (realm == casRealm()) {
				existCasRealm = true;
				break;
			}
		}
		if (!existCasRealm) {
			realms.add(0, casRealm());
		}
		
		DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
		//authenticator必须在realm前面设值，因为setRealm时会有条件的设置authenticator里的realm
		manager.setAuthenticator(authenticator());
		manager.setSubjectFactory(casSubjectFactory());
		manager.setCacheManager(securityCacheManager());
		manager.setSessionManager(sessionManager());
		manager.setRealms(realms);
		return manager;
	}
	
	/* ------------------------  BEGIN  --------------------------------------- */
	/*
		让spring管理的bean支持@RequiresPermissions、 @RequiresRoles等权限验证注解
		使用此功能时需要注意：项目中同时使用DefaultAdvisorAutoProxyCreator、<aop:config />、<aop:aspectj-autoproxy/>
		时，可能会出现Double Proxy和代理混乱的情况，使用之前请充分测试，尽量只用一个。
		easycode项目没用到基于注解的权限控制，都是走Url控制的，所以此功能我没有全面测试。
	 */
	/*@Bean
	@DependsOn("lifecycleBeanPostProcessor")
	public static DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		return new DefaultAdvisorAutoProxyCreator();
	}
	
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager());
		return advisor;
	}*/
	/* ------------------------  END  --------------------------------------- */
	
	/*@Bean
	public Cas30ProxyReceivingTicketValidationFilter ticketValidationFilter() {
		Cas30ProxyReceivingTicketValidationFilter filter = new Cas30ProxyReceivingTicketValidationFilter();
		filter.setRedirectAfterValidation(true);
		filter.setServerName(casLoginCallback);
		filter.setTicketValidator(ticketValidator());
		return filter;
	}*/
}
