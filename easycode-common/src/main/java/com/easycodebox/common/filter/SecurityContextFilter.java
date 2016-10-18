package com.easycodebox.common.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.easycodebox.common.security.SecurityContext;
import com.easycodebox.common.security.SecurityContexts;
import com.easycodebox.common.security.SecurityUtils;
import com.easycodebox.common.security.SecurityUtils.SecurityInfoHandler;

/**
 * @author WangXiaoJin
 * 
 */
public class SecurityContextFilter implements Filter {

	private SecurityInfoHandler<?, ?> securityInfoHandler = SecurityUtils.SESSION_SECURITY_INFO_HANDLER;
	
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			SecurityContext<?> context = securityInfoHandler.newSecurityContext(null, 
					(HttpServletRequest)request, (HttpServletResponse)response);
			SecurityContexts.setCurSecurityContext(context);
			chain.doFilter(request, response);
		} finally {
			SecurityContexts.resetSecurityContext();
		}
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

	public SecurityInfoHandler<?, ?> getSecurityInfoHandler() {
		return securityInfoHandler;
	}

	public void setSecurityInfoHandler(SecurityInfoHandler<?, ?> securityInfoHandler) {
		this.securityInfoHandler = securityInfoHandler;
	}

}
