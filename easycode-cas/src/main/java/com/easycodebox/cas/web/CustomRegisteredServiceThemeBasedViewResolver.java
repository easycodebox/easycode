package com.easycodebox.cas.web;

import org.jasig.cas.authentication.principal.WebApplicationService;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.services.web.RegisteredServiceThemeBasedViewResolver;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * 此类主要用来更换主题的具体html页面
 * 重写了RegisteredServiceThemeBasedViewResolver功能，因为其不能提供根据request请求参数（例：http://www.xxx.com?themeFrame=yyy）来返回对应的主题，
 * 导致开发时配置比较繁琐。但建议生产环境中废弃此功能，在cas的配置文件中配置，因为通过请求来更换主题，很有可能会为以后的安全性埋下隐患,如果确定现在及将来主题中不会出现隐私性
 * 数据的话，那你可以放心使用。
 * 注：代码中修改的部分会标注的，未标注的都是官方代码。个人觉得services的配置（即org.jasig.cas.services.RegexRegisteredService）应该提供两种参数，
 * 一个是theme，另一个是themeFrame。theme代表的传统意义的主题，只是简单的适配js、css等。themeFrame代表页面结构即jsp、html页面都更换了。不然后的话，如果我配置
 * 了RegisteredServiceThemeBasedViewResolver后，每个主题都需要提供一套页面，不管你是theme还是themeFrame，尽然被一刀切了。。。。
 * @author WangXiaoJin
 *
 */
public class CustomRegisteredServiceThemeBasedViewResolver extends InternalResourceViewResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomRegisteredServiceThemeBasedViewResolver.class);
    private static final String DEFAULT_PATH_PREFIX = "/WEB-INF/view/jsp";
    /**
     * add by WangXiaoJin
     */
    private static final String DEFAULT_THEME_FRAME_KEY = "themeFrame";

    /** The ServiceRegistry to look up the service. */
    private final ServicesManager servicesManager;

    private final String defaultThemeId;

    private String pathPrefix = DEFAULT_PATH_PREFIX;
    
    /**
     * add by WangXiaoJin
     */
    private String themeFrameKey = DEFAULT_THEME_FRAME_KEY;
    
    /**
     * 是否启用请求参数作为依据
     * add by WangXiaoJin
     */
    private Boolean useRequestParam = Boolean.TRUE;
    
    /**
     * The {@link RegisteredServiceThemeBasedViewResolver} constructor.
     * @param defaultThemeId the theme to apply if the service doesn't specific one or a service is not provided
     * @param servicesManager the serviceManager implementation
     * @see #setCache(boolean)
     */
    public CustomRegisteredServiceThemeBasedViewResolver(final String defaultThemeId, final ServicesManager servicesManager) {
        super();
        super.setCache(false);

        this.defaultThemeId = defaultThemeId;
        this.servicesManager = servicesManager;
    }

    /**
     * Uses the viewName and the theme associated with the service.
     * being requested and returns the appropriate view.
     * @param viewName the name of the view to be resolved
     * @return a theme-based UrlBasedView
     * @throws Exception an exception
     */
    @Override
    protected AbstractUrlBasedView buildView(final String viewName) throws Exception {
        final RequestContext requestContext = RequestContextHolder.getRequestContext();
        
        /**
         * add by WangXiaoJin
         */
        String themeId = useRequestParam ? requestContext.getRequestParameters().get(themeFrameKey) : null;
        if(StringUtils.isEmpty(themeId)) {
        	
        	final WebApplicationService service = WebUtils.getService(requestContext);
        	final RegisteredService registeredService = this.servicesManager.findServiceBy(service);
        	
        	themeId = service != null && registeredService != null
        			&& registeredService.getAccessStrategy().isServiceAccessAllowed()
        			&& StringUtils.hasText(registeredService.getTheme()) ? registeredService.getTheme() : defaultThemeId;
        			
        }else {
        	themeId = themeId.toLowerCase();
        }

        final String themePrefix = String.format("%s/%s/ui/", pathPrefix, themeId);

        //Build up the view like the base classes do, but we need to forcefully set the prefix for each request.
        //From UrlBasedViewResolver.buildView
        final InternalResourceView view = (InternalResourceView) BeanUtils.instantiateClass(getViewClass());
        view.setUrl(themePrefix + viewName + getSuffix());
        final String contentType = getContentType();
        if (contentType != null) {
            view.setContentType(contentType);
        }
        view.setRequestContextAttribute(getRequestContextAttribute());
        view.setAttributesMap(getAttributesMap());

        //From InternalResourceViewResolver.buildView
        view.setAlwaysInclude(false);
        view.setExposeContextBeansAsAttributes(false);
        view.setPreventDispatchLoop(true);

        LOGGER.debug("View resolved: {}", view.getUrl());

        return view;
    }

    /**
     * setCache is not supported in the {@link RegisteredServiceThemeBasedViewResolver} because each
     * request must be independently evaluated.
     * @param cache a value indicating whether the view should cache results.
     */
    @Override
    public void setCache(final boolean cache) {
        LOGGER.warn("The {} does not support caching. Turned off caching forcefully.", this.getClass().getSimpleName());
        super.setCache(false);
    }

    /**
     * Sets path prefix. This is the location where
     * views are expected to be found. The default
     * prefix is {@link #DEFAULT_PATH_PREFIX}.
     *
     * @param pathPrefix the path prefix
     */
    public void setPathPrefix(final String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

	public String getThemeFrameKey() {
		return themeFrameKey;
	}

	public void setThemeFrameKey(String themeFrameKey) {
		this.themeFrameKey = themeFrameKey;
	}

	public Boolean getUseRequestParam() {
		return useRequestParam;
	}

	public void setUseRequestParam(Boolean useRequestParam) {
		this.useRequestParam = useRequestParam;
	}

}
