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

import com.easycodebox.common.lang.dto.UserInfo;
import com.easycodebox.common.net.HttpUtils;
import com.easycodebox.common.security.SecurityContext;
import com.easycodebox.common.security.SecurityContexts;
import com.easycodebox.common.security.SecurityUtils;

/**
 * @author WangXiaoJin
 * 
 */
public class SecurityContextFilter implements Filter {

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;
		SecurityContexts.setCurSecurityContext(getSecurityContext(req, res));
		try{
			chain.doFilter(request, response);
		}finally{
			SecurityContexts.resetSecurityContext();
		}
		
	}
	
	private SecurityContext<UserInfo> getSecurityContext(HttpServletRequest request,
				HttpServletResponse response) {
		UserInfo user = SecurityUtils.getUser(request.getSession(false));
		SecurityContext<UserInfo> context = new SecurityContext<UserInfo>();
		if(user != null) {
			context.setSecurity(user);
		}
		context.setIp(HttpUtils.getIpAddr(request));
		context.setSessionId(request.getSession(true).getId());
		context.setRequest(request);
		context.setResponse(response);
		context.setUserAgent(request.getHeader("User-Agent"));
		return context;
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

}
