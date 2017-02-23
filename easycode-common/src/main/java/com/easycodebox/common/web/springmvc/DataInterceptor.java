package com.easycodebox.common.web.springmvc;

import com.easycodebox.common.config.CommonProperties;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.net.Https;
import com.easycodebox.common.web.CacheHisUris;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 给 Controller 中 model 增加可重用数据，以便在JSP、Freemarker中使用这些数据
 * @author WangXiaoJin
 * 
 */
public class DataInterceptor extends HandlerInterceptorAdapter {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private boolean basePath = true;
	private final String BASE_PATH_KEY = "basePath";

	private boolean imgUrl = true;
	private final String IMG_URL_KEY = "imgUrl";
	
	private CommonProperties commonProperties;

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		if (!(handler instanceof HandlerMethod))
			return;
		HandlerMethod handlerMethod = (HandlerMethod) handler;

		if (modelAndView != null) {
			commonProperties = commonProperties == null ? CommonProperties.instance() : commonProperties;
			
			if (basePath) {
				modelAndView.addObject(BASE_PATH_KEY, commonProperties.getBasePath() == null
						?  Https.getBasePath(request) : commonProperties.getBasePath());
			}

			if (imgUrl) {
				if (Strings.isBlank(commonProperties.getImgUrl())) {
					log.warn("Has no config IMG_URL constant.");
				} else
					modelAndView.addObject(IMG_URL_KEY, commonProperties.getImgUrl());
			}

		}

		/*
		  新版通过浏览器端实现此功能
		 */
		if (cacheHisUri) {
			CacheHisUris.cacheHisUri(handlerMethod.getMethod(), request, commonProperties.isTraditionalHttp());
		}

	}

	public void setImgUrl(boolean imgUrl) {
		this.imgUrl = imgUrl;
	}

	public void setBasePath(boolean basePath) {
		this.basePath = basePath;
	}

	/**
	 * 新版通过浏览器端实现此功能
	 */
	private boolean cacheHisUri = false;
	/**
	 * 新版通过浏览器端实现此功能
	 * @param cacheHisUri
	 */
	@Deprecated
	public void setCacheHisUri(boolean cacheHisUri) {
		this.cacheHisUri = cacheHisUri;
	}
	
	public CommonProperties getCommonProperties() {
		return commonProperties;
	}
	
	public void setCommonProperties(CommonProperties commonProperties) {
		this.commonProperties = commonProperties;
	}
}
