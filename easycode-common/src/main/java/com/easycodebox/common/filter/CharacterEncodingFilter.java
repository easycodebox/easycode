package com.easycodebox.common.filter;

import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.net.ParameterRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;

public class CharacterEncodingFilter implements Filter {
	
	private String encoding;
	private boolean forceEncoding = false;
	private String attributeKey;
	/**
	 * 默认对get请求的参数译码
	 */
	private boolean decodeGetParams = false;


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.encoding = filterConfig.getInitParameter("encoding");
		String feStr = filterConfig.getInitParameter("forceEncoding");
		if(Strings.isNotBlank(feStr))
			this.forceEncoding = Boolean.parseBoolean(feStr);
		this.attributeKey = Strings.isBlank(filterConfig.getFilterName()) ?
				CharacterEncodingFilter.class.getName() + ".filter" : filterConfig.getFilterName();
		String dgp = filterConfig.getInitParameter("decodeGetParams");
		if(Strings.isNotBlank(dgp))
			this.decodeGetParams = Boolean.parseBoolean(dgp);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		boolean encoded = request.getAttribute(attributeKey) != null;
		if(encoded) {
			chain.doFilter(request, response);
		}else {
			request.setAttribute(attributeKey, Boolean.TRUE);
			HttpServletRequest req = (HttpServletRequest)request;
			try {
				if (this.encoding != null && (this.forceEncoding || request.getCharacterEncoding() == null)) {
					request.setCharacterEncoding(this.encoding);
					if (this.forceEncoding) {
						response.setCharacterEncoding(this.encoding);
					}
					if(decodeGetParams && "GET".equals(req.getMethod()) 
							&& Strings.isNotBlank(req.getQueryString())) {
						HashMap<String, String[]> params = new HashMap<>(request.getParameterMap());
						for (String key : params.keySet()) {
							String[] values = params.get(key);
							if (values == null) continue;
							for (int i = 0; i < values.length; i++)
								values[i] = URLDecoder.decode(values[i], encoding);
						}
						request = new ParameterRequestWrapper(req, params);
					}
				}
				chain.doFilter(request, response);
			} finally {
				request.removeAttribute(attributeKey);
			}
		}
		
	}
	
	@Override
	public void destroy() {
		
	}

}
