package com.easycodebox.login.util;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.net.HttpUtils;
import com.easycodebox.common.web.callback.Callbacks;

/**
 * 此类替代了shiro的authc的拦截器
 * @author WangXiaoJin
 *
 */
public class DefaultFormAuthenticationFilter extends FormAuthenticationFilter {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultFormAuthenticationFilter.class);

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
        	HttpServletRequest req = (HttpServletRequest)request;
        	HttpServletResponse resp = (HttpServletResponse)response;
        	
        	if(HttpUtils.isAjaxRequest(req)) {
    			HttpUtils.outJson(CodeMsg.NO_LOGIN, resp);
        	}
        	else if(req.getParameter(BaseConstants.DIALOG_REQ) != null) {
				Callbacks.callback(Callbacks.closeDialog((String)null), null, resp);
        	}
        	else
        		saveRequestAndRedirectToLogin(request, response);
            return false;
        }
	}

}
