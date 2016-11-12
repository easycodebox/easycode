package com.easycodebox.common.sitemesh3;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.sitemesh.config.ConfigurableSiteMeshFilter;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.lang.StringUtils;

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
	
	private String pjaxKey;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
		String pjax = filterConfig.getInitParameter("pjax"),
				pjaxKey = filterConfig.getInitParameter("pjaxKey");
		
		if (StringUtils.isNotBlank(pjax)) {
			this.pjax = Boolean.parseBoolean(pjax.trim());
		} else {
			this.pjax = true;
		}
		
		if (StringUtils.isNotBlank(pjaxKey)) {
			this.pjaxKey = pjaxKey.trim();
		} else {
			this.pjaxKey = BaseConstants.pjaxKey;
		}
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		if (pjax) {
			HttpServletRequest request = (HttpServletRequest)servletRequest;
			if (request.getHeader(pjaxKey) != null) {
				filterChain.doFilter(servletRequest, servletResponse);
				return;
			}
		}
		super.doFilter(servletRequest, servletResponse, filterChain);
	}

}
