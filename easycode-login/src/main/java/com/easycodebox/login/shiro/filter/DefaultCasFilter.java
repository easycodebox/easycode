package com.easycodebox.login.shiro.filter;

import com.easycodebox.common.error.ErrorContext;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.cas.CasFilter;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 捕获executeLogin方法抛出的{@link ErrorContext}异常，把特定类的异常信息显示在Cas登录页面上。
 * @author WangXiaoJin
 */
public class DefaultCasFilter extends CasFilter {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private String failureUrl;
	private String reloginUrl;
	private String logoutUrl;
	
	@Override
	@SuppressWarnings("unchecked")
	protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException exception, ServletRequest request,
	                                 ServletResponse response) {
		Subject subject = getSubject(request, response);
		
		if (subject.isAuthenticated() || subject.isRemembered()) {
			subject.logout();
		}
		
		String url;
		Map queryParams = null;
		if (exception.getCause() != null && exception.getCause() instanceof ErrorContext) {
			url = logoutUrl;
			queryParams = new HashMap(1);
			ErrorContext error = (ErrorContext) exception.getCause();
			try {
				queryParams.put("service", Strings.format(reloginUrl,
						URLEncoder.encode(URLEncoder.encode(error.getMessage(), "UTF-8"), "UTF-8")));
			} catch (UnsupportedEncodingException e) {
				log.error("URLEncoder params error : {0}", error.getMessage(), e);
			}
		} else {
			url = failureUrl;
		}
		
		try {
			WebUtils.issueRedirect(request, response, url, queryParams);
		} catch (IOException e) {
			log.error("Cannot redirect to failure url : {0}", failureUrl, e);
		}
		return false;
	}
	
	public void setFailureUrl(String failureUrl) {
		this.failureUrl = failureUrl;
	}
	
	public void setReloginUrl(String reloginUrl) {
		this.reloginUrl = reloginUrl;
	}
	
	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}
	
}
