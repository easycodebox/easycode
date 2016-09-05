package com.easycodebox.common.filter;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.net.ParameterRequestWrapper;

public class CharacterEncodingFilter implements Filter {
	
	private static final Logger LOG = LoggerFactory.getLogger(CharacterEncodingFilter.class);
	
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
		if(StringUtils.isNotBlank(feStr))
			this.forceEncoding = Boolean.parseBoolean(feStr);
		this.attributeKey = StringUtils.isBlank(filterConfig.getFilterName()) ?
				CharacterEncodingFilter.class.getName() + ".filter" : filterConfig.getFilterName();
		String dgp = filterConfig.getInitParameter("decodeGetParams");
		if(StringUtils.isNotBlank(dgp))
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
							&& StringUtils.isNotBlank(req.getQueryString())) {
						HashMap<String, String[]> params = new HashMap<String, String[]>(request.getParameterMap());
						Iterator<String> keys = params.keySet().iterator();
						while(keys.hasNext()) {
							String key = (String)keys.next();
							String[] values = params.get(key);
							if(values == null) continue;
							for(int i = 0; i < values.length; i++) 
								values[i] = URLDecoder.decode(values[i], encoding);
						}
						ParameterRequestWrapper wrapperRequest = new ParameterRequestWrapper(req, params);
						request = wrapperRequest;
					}
				}
				chain.doFilter(request, response);
			} catch(IOException e) {
				LOG.warn("Encoding URL error, {0}.", req.getRequestURL().append("?").append(req.getQueryString()));
				throw e;
			} finally {
				request.removeAttribute(attributeKey);
			}
		}
		
	}
	
	@Override
	public void destroy() {
		
	}

}
