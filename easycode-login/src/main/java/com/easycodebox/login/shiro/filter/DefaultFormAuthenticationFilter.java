package com.easycodebox.login.shiro.filter;

import com.easycodebox.common.CommonProperties;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.net.Https;
import com.easycodebox.common.web.callback.Callbacks;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 此类替代了shiro的authc的拦截器
 * @author WangXiaoJin
 *
 */
public class DefaultFormAuthenticationFilter extends FormAuthenticationFilter {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private CommonProperties commonProperties = CommonProperties.instance();
	
	@Override
	protected boolean onAccessDenied(ServletRequest request,
			ServletResponse response) throws Exception {
		if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
            	log.trace("Login submission detected.  Attempting to execute login.");
                return executeLogin(request, response);
            } else {
            	log.trace("Login page view.");
                //allow them to see the login page ;)
                return true;
            }
        } else {
        	log.trace("Attempting to access a path which requires authentication.  Forwarding to the " +
                        "Authentication url [{0}]", getLoginUrl());
        	HttpServletRequest req = (HttpServletRequest)request;
        	HttpServletResponse resp = (HttpServletResponse)response;
        	
        	if(Https.isAjaxRequest(req) &&
        			req.getHeader(commonProperties.getPjaxKey()) == null) {
    			Https.outJson(CodeMsg.NO_LOGIN, resp);
        	}
        	else if(req.getParameter(commonProperties.getDialogReqKey()) != null) {
				Callbacks.callback(Callbacks.closeDialog((String)null), null, resp);
        	}
        	else
        		saveRequestAndRedirectToLogin(request, response);
            return false;
        }
	}
	
	public CommonProperties getCommonProperties() {
		return commonProperties;
	}
	
	public void setCommonProperties(CommonProperties commonProperties) {
		this.commonProperties = commonProperties;
	}
}
