package com.easycodebox.common.filter;

import com.easycodebox.common.security.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author WangXiaoJin
 * 
 */
public class SecurityContextFilter implements Filter {

	private SecurityInfoHandler<?, ?> securityInfoHandler;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
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
	public void destroy() {
		
	}
	
	public SecurityInfoHandler<?, ?> getSecurityInfoHandler() {
		return securityInfoHandler;
	}

	public void setSecurityInfoHandler(SecurityInfoHandler<?, ?> securityInfoHandler) {
		this.securityInfoHandler = securityInfoHandler;
	}
	
}
