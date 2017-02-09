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

import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.net.Https;

/**
 * 把http://www.xx.com  转成 http://www.xx.com/
 * 为了解决tomcat7以上版本，servlet url-pattern 为 "/*"，且访问的路径为http://www.xx.com时，
 * 在谷歌和IE下是获取不到当前的session的，因为session的cookie是保存在http://www.xx.com/路径下，
 * 所以需要此过滤器在根路径后面加上斜线，以能获取到当前的session信息。
 * 当url-pattern 为 "/"时不会出现上述问题，因为此时为默认servlet，tomcat会自己转换。
 * 在tomcat6下是不会出现上述问题的。
 * @author WangXiaoJin
 * 
 */
public class CorrectBasePathFilter implements Filter {

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		if(req.getRequestURI().equals(req.getContextPath())) {
			
			((HttpServletResponse)response).sendRedirect(Https.getBasePath(req) + Symbol.SLASH);
		}else
			chain.doFilter(request, response);

	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

}
