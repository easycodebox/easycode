package com.easycodebox.login.shiro.filter;

import com.easycodebox.common.BaseConstants;
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
	
	/**
	 * 标记此请求为pjax的请求参数值
	 */
	private String pjaxKey;
	/**
	 * 标记此次请求是弹出框发送的请求，controller返回callback(closeDialog(), response)格式的数据
	 */
	private String dialogReqKey = BaseConstants.DIALOG_REQ_KEY;

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
        			req.getHeader(pjaxKey == null ? BaseConstants.PJAX_KEY : pjaxKey) == null) {
    			Https.outJson(CodeMsg.NO_LOGIN, resp);
        	}
        	else if(req.getParameter(dialogReqKey) != null) {
				Callbacks.callback(Callbacks.closeDialog((String)null), null, resp);
        	}
        	else
        		saveRequestAndRedirectToLogin(request, response);
            return false;
        }
	}

	public String getPjaxKey() {
		return pjaxKey;
	}

	public void setPjaxKey(String pjaxKey) {
		this.pjaxKey = pjaxKey;
	}
	
	public String getDialogReqKey() {
		return dialogReqKey;
	}
	
	public void setDialogReqKey(String dialogReqKey) {
		this.dialogReqKey = dialogReqKey;
	}
}
