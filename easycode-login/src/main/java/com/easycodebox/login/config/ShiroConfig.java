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
import org.apache.shiro.authc.pam.AllSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cas.CasSubjectFactory;
import org.apache.shiro.config.Ini;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.config.IniFilterChainResolverFactory;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

/**
 * @author WangXiaoJin
 */
@Configuration
@Import(ShiroCacheConfig.class)
public class ShiroConfig {
	
	@Value("${project}")
	private String project;
	
	@Value("${cas.url}")
	private String casUrl;
	
	@Value("${cas.callback}")
	private String casCallback;
	
	@Value("${cas.login}")
	private String casLogin;
	
	@Value("${cas.login.callback}")
	private String casLoginCallback;
	
	@Value("${cas.logout}")
	private String casLogout;
	
	@Value("${cas.logout.callback}")
	private String casLogoutCallback;
	
	@Value("${failure.url}")
	private String failureUrl;
	
	@Value("${unauthorized.url}")
	private String unauthorizedUrl;
	
	/**
	 * Shiro权限配置文件
	 */
	@Value("${shiro.filter.file:classpath:shiro-filter.properties}")
	private String shiroFilterFile;
	
	@Autowired
	private CustomRedisCacheManager shiroCacheManager;
	
	@Autowired
	private RedisTemplateCacheStats cacheStats;
	
	@Autowired
	private UserWsService userWsService;
	
	/**
	 * 操作SecurityInfo
	 */
	@Bean
	public ShiroSecurityInfoHandler securityInfoHandler() {
		return new ShiroSecurityInfoHandler();
	}
	
	/**
	 * failureUrl: 配置验证错误时的失败页面
	 * <p/>
	 * reloginUrl: 验证错误后显示登录页面，并提示错误信息。只试用于ErrorContext异常
	 */
	@Bean
	public DefaultCasFilter casFilter() {
		DefaultCasFilter filter = new DefaultCasFilter();
		filter.setFailureUrl(failureUrl);
		filter.setReloginUrl(casLogin + "&amp;msg={0}");
		filter.setLogoutUrl(casLogout);
		return filter;
	}
	
	@Bean
	public LogoutFilter logoutFilter() {
		LogoutFilter filter = new LogoutFilter();
		filter.setRedirectUrl(casLogout + "?service=" + casLogoutCallback);
		return filter;
	}
	
	@Bean
	public DefaultPermissionsAuthorizationFilter perms() {
		return new DefaultPermissionsAuthorizationFilter();
	}
	
	@Bean
	public DefaultFormAuthenticationFilter authc() {
		return new DefaultFormAuthenticationFilter();
	}
	
	@Bean
	public SenseLoginFilter sense() {
		return new SenseLoginFilter();
	}
	
	@Bean
	public ShiroFilterFactoryBean shiroFilter() {
		ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
		factoryBean.setSecurityManager(securityManager());
		factoryBean.setLoginUrl(casLogin);
		factoryBean.setUnauthorizedUrl(unauthorizedUrl);
		//加载权限配置
		Ini ini = new Ini();
		ini.loadFromPath(shiroFilterFile);
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
		Cas30ServiceTicketValidator validator = new Cas30ServiceTicketValidator(casUrl);
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
		realm.setCasServerUrlPrefix(casUrl);
		realm.setCasService(casLoginCallback);
		realm.setRoleAttributeNames("roles");
		realm.setPermissionAttributeNames("permissions");
		realm.setProject(project);
		return realm;
	}
	
	/**
	 * 多个realm时必须所有的realm都验证通过
	 */
	@Bean
	public AllSuccessfulStrategy allSuccessfulStrategy() {
		return new AllSuccessfulStrategy();
	}
	
	@Bean
	public ModularRealmAuthenticator authenticator() {
		ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
		authenticator.setAuthenticationStrategy(allSuccessfulStrategy());
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
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}
	
	@Bean
	public DefaultWebSecurityManager securityManager() {
		DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
		manager.setSessionManager(sessionManager());
		manager.setCacheManager(securityCacheManager());
		manager.setRealm(casRealm());
		manager.setAuthenticator(authenticator());
		manager.setSubjectFactory(casSubjectFactory());
		return manager;
	}
	
	/**
	 * 让spring管理的bean支持@RequiresPermissions、 @RequiresRoles等权限验证注解
	 */
	@Bean
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		return new DefaultAdvisorAutoProxyCreator();
	}
	
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager());
		return advisor;
	}
	
	/*@Bean
	public Cas30ProxyReceivingTicketValidationFilter ticketValidationFilter() {
		Cas30ProxyReceivingTicketValidationFilter filter = new Cas30ProxyReceivingTicketValidationFilter();
		filter.setRedirectAfterValidation(true);
		filter.setServerName(casLoginCallback);
		filter.setTicketValidator(ticketValidator());
		return filter;
	}*/
}
