package com.easycodebox.login.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

/**
 * 判断请求地址中是否包含ticket参数，有就直接执行登录逻辑
 * @author WangXiaoJin
 *
 */
public class SenseLoginFilter extends FormAuthenticationFilter {

	private static final Logger LOG = LoggerFactory.getLogger(SenseLoginFilter.class);

	private static final String TICKET_PARAMETER = "ticket";
	
	@Override
	protected boolean isAccessAllowed(ServletRequest request,
			ServletResponse response, Object mappedValue) {
		Subject subject = getSubject(request, response);
		return subject.isAuthenticated() || request.getParameter(TICKET_PARAMETER) == null;
	}
	
	@Override
	protected boolean onAccessDenied(ServletRequest request,
			ServletResponse response) throws Exception {
		if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
            	LOG.trace("Login submission detected.  Attempting to execute login.");
                return executeLogin(request, response);
            } else {
            	LOG.trace("Login page view.");
                //allow them to see the login page ;)
                return true;
            }
        } else {
        	LOG.trace("Attempting to access a path which requires authentication.  Forwarding to the " +
                        "Authentication url [{0}]", getLoginUrl());
    		saveRequestAndRedirectToLogin(request, response);
            return false;
        }
	}
	
}
