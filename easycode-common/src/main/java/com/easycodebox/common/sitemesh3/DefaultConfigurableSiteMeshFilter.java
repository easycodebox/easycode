package com.easycodebox.common.sitemesh3;

import com.easycodebox.common.CommonProperties;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.Symbol;
import org.apache.commons.lang.ArrayUtils;
import org.sitemesh.config.ConfigurableSiteMeshFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * SiteMesh集成pjax
 * @author WangXiaoJin
 *
 */
public class DefaultConfigurableSiteMeshFilter extends ConfigurableSiteMeshFilter {
	
	/**
	 * 是否启用pjax校验
	 */
	private boolean pjax;
	
	private CommonProperties commonProperties;
	
	/**
	 * 是否启用装饰器的参数名
	 */
	private String decoratedKey;
	
	/**
	 * 忽略装饰器的参数值，默认为["false", "0"]
	 */
	private String[] noDecVals;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
		String pjax = filterConfig.getInitParameter("pjax"),
				decoratedKey = filterConfig.getInitParameter("decoratedKey"),
				noDecVals = filterConfig.getInitParameter("noDecVals");
		
		if (Strings.isNotBlank(pjax)) {
			this.pjax = Boolean.parseBoolean(pjax.trim());
		} else {
			this.pjax = true;
		}
		
		if (Strings.isNotBlank(decoratedKey)) {
			this.decoratedKey = decoratedKey.trim();
		}
		
		if (Strings.isNotBlank(noDecVals)) {
			this.noDecVals = noDecVals.trim().split(Symbol.COMMA);
		} else {
			this.noDecVals = new String[]{ "false", "0" };
		}
		if (commonProperties == null) {
			commonProperties = (CommonProperties) filterConfig.getServletContext().getAttribute(CommonProperties.DEFAULT_NAME);
			commonProperties = commonProperties == null ? CommonProperties.instance() : commonProperties;
		}
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		if (pjax) {
			HttpServletRequest request = (HttpServletRequest)servletRequest;
			if (request.getHeader(commonProperties.getPjaxKey()) != null) {
				filterChain.doFilter(servletRequest, servletResponse);
				return;
			}
		}
		//判断该请求是否使用装饰器的拦截器
		if (decoratedKey != null && servletRequest.getParameter(decoratedKey) != null 
				&& ArrayUtils.contains(noDecVals, servletRequest.getParameter(decoratedKey))) {
			filterChain.doFilter(servletRequest, servletResponse);
		} else {
			super.doFilter(servletRequest, servletResponse, filterChain);
		}
	}
	
	public CommonProperties getCommonProperties() {
		return commonProperties;
	}
	
	public void setCommonProperties(CommonProperties commonProperties) {
		this.commonProperties = commonProperties;
	}
}
