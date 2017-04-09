package com.easycodebox.login.shiro.filter;

import org.apache.shiro.util.ThreadContext;

import javax.servlet.*;
import java.io.IOException;
import java.util.Map;

/**
 * 删除{@link ThreadContext}中需要删除的线程变量
 * @author WangXiaoJin
 */
public class ThreadContextFilter implements Filter {
	
	/**
	 * 标记是否可删除
	 */
	public static final String REMOVABLE_KEY_PREFIX = "REMOVABLE-";
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} finally {
			Map<Object, Object> resources = ThreadContext.getResources();
			if (resources != null) {
				for (Object key : resources.keySet()) {
					if (key != null && key instanceof String) {
						if (((String) key).startsWith(REMOVABLE_KEY_PREFIX)) {
							ThreadContext.remove(key);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void destroy() {
		
	}
	
}
