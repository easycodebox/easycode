package com.easycodebox.login.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author WangXiaoJin
 */
@ConfigurationProperties(prefix = "login")
public class LoginProperties {
	
	private String project;
	
	private String authWsUrl;
	
	private String casUrl;
	
	private String casCallback;
	
	private String casLogin;
	
	private String casLoginCallback;
	
	private String casLogout;
	
	private String casLogoutCallback;
	
	private String failureUrl;
	
	private String unauthorizedUrl;
	
	/**
	 * Shiro权限配置文件
	 */
	private String shiroFilterFile = "classpath:shiro-filter.properties";
	
	public String getProject() {
		return project;
	}
	
	public void setProject(String project) {
		this.project = project;
	}
	
	public String getAuthWsUrl() {
		return authWsUrl;
	}
	
	public void setAuthWsUrl(String authWsUrl) {
		this.authWsUrl = authWsUrl;
	}
	
	public String getCasUrl() {
		return casUrl;
	}
	
	public void setCasUrl(String casUrl) {
		this.casUrl = casUrl;
	}
	
	public String getCasCallback() {
		return casCallback;
	}
	
	public void setCasCallback(String casCallback) {
		this.casCallback = casCallback;
	}
	
	public String getCasLogin() {
		return casLogin;
	}
	
	public void setCasLogin(String casLogin) {
		this.casLogin = casLogin;
	}
	
	public String getCasLoginCallback() {
		return casLoginCallback;
	}
	
	public void setCasLoginCallback(String casLoginCallback) {
		this.casLoginCallback = casLoginCallback;
	}
	
	public String getCasLogout() {
		return casLogout;
	}
	
	public void setCasLogout(String casLogout) {
		this.casLogout = casLogout;
	}
	
	public String getCasLogoutCallback() {
		return casLogoutCallback;
	}
	
	public void setCasLogoutCallback(String casLogoutCallback) {
		this.casLogoutCallback = casLogoutCallback;
	}
	
	public String getFailureUrl() {
		return failureUrl;
	}
	
	public void setFailureUrl(String failureUrl) {
		this.failureUrl = failureUrl;
	}
	
	public String getUnauthorizedUrl() {
		return unauthorizedUrl;
	}
	
	public void setUnauthorizedUrl(String unauthorizedUrl) {
		this.unauthorizedUrl = unauthorizedUrl;
	}
	
	public String getShiroFilterFile() {
		return shiroFilterFile;
	}
	
	public void setShiroFilterFile(String shiroFilterFile) {
		this.shiroFilterFile = shiroFilterFile;
	}
}
