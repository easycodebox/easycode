package com.easycodebox.common.sitemesh3;

import com.easycodebox.common.CommonProperties;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.lang.reflect.Classes;
import org.apache.commons.lang.ArrayUtils;
import org.sitemesh.config.ConfigurableSiteMeshFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * SiteMesh集成pjax
 * @author WangXiaoJin
 *
 */
public class DefaultConfigurableSiteMeshFilter extends ConfigurableSiteMeshFilter {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
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
			commonProperties = CommonProperties.instance();
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
	
	/**
	 * 当集成Spring Boot后加载不了configFile，所以增加了ClassLoader加载configFile的备选方案
	 * @param filterConfig
	 * @param configFilePath
	 * @return
	 * @throws ServletException
	 */
	@Override
	protected Element loadConfigXml(FilterConfig filterConfig, String configFilePath) throws ServletException {
		Element element = super.loadConfigXml(filterConfig, configFilePath);
		if (element == null) {
			try {
				InputStream stream = Classes.getClassLoader().getResourceAsStream(configFilePath);
				if (stream == null) {
					log.info("No config file present - using ClassLoader. Tried: {}", configFilePath);
					return null;
				}
				try {
					log.info("Loading SiteMesh 3 config file from ClassLoader {}", configFilePath);
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder documentBuilder = factory.newDocumentBuilder();
					Document document = documentBuilder.parse(stream);
					return document.getDocumentElement();
				} catch (SAXException e) {
					throw new ServletException("Could not parse " + configFilePath + " (loaded by ClassLoader)", e);
				} finally {
					stream.close();
				}
			} catch (ParserConfigurationException e) {
				throw new ServletException("Could not initialize DOM parser", e);
			} catch (IOException e) {
				throw new ServletException(e);
			}
		}
		return element;
	}
	
	public CommonProperties getCommonProperties() {
		return commonProperties;
	}
	
	public void setCommonProperties(CommonProperties commonProperties) {
		this.commonProperties = commonProperties;
	}
}
