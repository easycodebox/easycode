package com.easycodebox.login.shiro.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.cas.CasFilter;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.BeansException;

import com.easycodebox.common.Weave;
import com.easycodebox.common.error.ErrorContext;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.spring.BeanFactory;

/**
 * @author WangXiaoJin
 *
 */
public class DefaultCasFilter extends CasFilter {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private String weaveName;
	private Weave weave;

	private String failureUrl;
	private String reloginUrl;
	private String logoutUrl;
	
	@Override
	protected boolean executeLogin(ServletRequest request,
			ServletResponse response) throws Exception {
		AuthenticationToken token = createToken(request, response);
		if (token == null) {
			String msg = "createToken method implementation returned null. A valid non-null AuthenticationToken "
					+ "must be created in order to execute a login attempt.";
			throw new IllegalStateException(msg);
		}
		try {
			Subject subject = getSubject(request, response);
			subject.login(token);
			return onLoginSuccess(token, subject, request, response);
		} catch (Exception e) {
			return onLoginFailure(token, e, request, response);
		}
	}

	@Override
	protected boolean onLoginSuccess(AuthenticationToken token,
			Subject subject, ServletRequest request, ServletResponse response)
			throws Exception {
		loginSucWeave(subject, request, response);
		return super.onLoginSuccess(token, subject, request, response);
	}
	
    @SuppressWarnings({ "rawtypes", "unchecked" })
	protected boolean onLoginFailure(AuthenticationToken token, Exception exception, ServletRequest request,
                                     ServletResponse response) {
        Subject subject = getSubject(request, response);
        
        if (subject.isAuthenticated() || subject.isRemembered()) {
        	 subject.logout();
        }
        
        String url = null;
        Map queryParams = null;
        if(exception instanceof ErrorContext
        		|| (exception.getCause() != null && exception.getCause() instanceof ErrorContext)) {
        	url = logoutUrl;
        	queryParams = new HashMap(1);
        	ErrorContext error = exception instanceof ErrorContext 
        			? (ErrorContext)exception : (ErrorContext)exception.getCause();
        	try {
				queryParams.put("service", StringUtils.format(reloginUrl, 
						URLEncoder.encode(URLEncoder.encode(error.getMessage(), "UTF-8"), "UTF-8")));
			} catch (UnsupportedEncodingException e) {
				log.error("URLEncoder params error : {0}", error.getMessage(), e);
			}
        }else {
        	url = failureUrl;
        }
        
        try {
            WebUtils.issueRedirect(request, response, url, queryParams);
        } catch (IOException e) {
        	log.error("Cannot redirect to failure url : {0}", failureUrl, e);
        }
        return false;
    }
	
	private void loginSucWeave(Subject subject, ServletRequest request, ServletResponse response) {
		if(weave == null && weaveName != null) {
			Object weave = null;
			try {
				weave = BeanFactory.getBean(weaveName);
			} catch (BeansException e) {
				
			}
			this.weave = weave == null ? null : (Weave)weave;
		}
		if(weave != null) {
			//此处不需要捕获异常
			weave.after(subject, request, response);
		}
	}

	public void setWeaveName(String weaveName) {
		this.weaveName = weaveName;
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
