package com.easycodebox.cas.web;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.theme.AbstractThemeResolver;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * 扩展官方ServiceThemeResolver功能，因为官方此类不提供依据http请求来更换主题，只能通过org.springframework.web.servlet.theme.ThemeChangeInterceptor来达到
 * 类似的实现，但ThemeChangeInterceptor又不能应用于ServiceThemeResolver。虽然spring已经提供了CookieThemeResolver达到类似的功能，但不能达到完全由服务端控制主题的需求。
 * @author WangXiaoJin
 *
 */
public class CustomServiceThemeResolver extends AbstractThemeResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomServiceThemeResolver.class);
    
    /**
     * add by WangXiaoJin
     */
    private static final String DEFAULT_THEME_KEY = "theme";

    /** The ServiceRegistry to look up the service. */
    private ServicesManager servicesManager;

    private Map<Pattern, String> overrides = new HashMap<>();
    
    /**
     * add by WangXiaoJin
     */
    private String themeKey = DEFAULT_THEME_KEY;
    
    /**
     * 是否启用请求参数作为依据
     * add by WangXiaoJin
     */
    private Boolean useRequestParam = Boolean.TRUE;
    
    /**
     * add by WangXiaoJin
     */
    private String themeNamePrefix = StringUtils.EMPTY;

    @Override
    public String resolveThemeName(final HttpServletRequest request) {
        if (this.servicesManager == null) {
            return resolveThemeName(getDefaultThemeName());
        }
        // retrieve the user agent string from the request
        final String userAgent = request.getHeader("User-Agent");

        if (StringUtils.isBlank(userAgent)) {
            return resolveThemeName(getDefaultThemeName());
        }

        for (final Map.Entry<Pattern, String> entry : this.overrides.entrySet()) {
            if (entry.getKey().matcher(userAgent).matches()) {
                request.setAttribute("isMobile", "true");
                request.setAttribute("browserType", entry.getValue());
                break;
            }
        }
        
        if(StringUtils.isBlank(request.getParameter(themeKey))) {
        	final RequestContext context = RequestContextHolder.getRequestContext();
        	final Service service = WebUtils.getService(context);
        	if (service != null) {
        		final RegisteredService rService = this.servicesManager.findServiceBy(service);
        		if (rService != null && rService.getAccessStrategy().isServiceAccessAllowed()
        				&& StringUtils.isNotBlank(rService.getTheme())) {
        			String theme = resolveThemeName(rService.getTheme());
        			LOGGER.debug("Service [{}] is configured to use a custom theme [{}]", rService, theme);
        			final CasThemeResourceBundleMessageSource messageSource = new CasThemeResourceBundleMessageSource();
        			messageSource.setBasename(theme);
        			if (messageSource.doGetBundle(theme, request.getLocale()) != null) {
        				LOGGER.debug("Found custom theme [{}] for service [{}]", theme, rService);
        				return theme;
        			} else {
        				LOGGER.warn("Custom theme {} for service {} cannot be located. Falling back to default theme...",
        						theme, rService);
        			}
        		}
        	}
        }else {
        	/*** add by WangXiaoJin ***/
        	String theme = resolveThemeName(request.getParameter(themeKey).toLowerCase());
        	final CasThemeResourceBundleMessageSource messageSource = new CasThemeResourceBundleMessageSource();
			messageSource.setBasename(theme);
			if (messageSource.doGetBundle(theme, request.getLocale()) != null) {
				LOGGER.debug("Found custom theme [{}]", theme);
				return theme;
			} else {
				LOGGER.warn("Custom theme {} cannot be located. Falling back to default theme...", theme);
			}
			/*** ================== ***/
        }
        return resolveThemeName(getDefaultThemeName());
    }

    /**
     * add by WangXiaoJin
     * @param themeName
     * @return
     */
    private String resolveThemeName(String themeName) {
    	return getThemeNamePrefix() + themeName;
    }
    
    @Override
    public void setThemeName(final HttpServletRequest request, final HttpServletResponse response, final String themeName) {
        // nothing to do here
    }

    public void setServicesManager(final ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    /**
     * Sets the map of mobile browsers.  This sets a flag on the request called "isMobile" and also
     * provides the custom flag called browserType which can be mapped into the theme.
     * <p>
     * Themes that understand isMobile should provide an alternative stylesheet.
     *
     * @param mobileOverrides the list of mobile browsers.
     */
    public void setMobileBrowsers(final Map<String, String> mobileOverrides) {
        // initialize the overrides variable to an empty map
        this.overrides = new HashMap<>();

        for (final Map.Entry<String, String> entry : mobileOverrides.entrySet()) {
            this.overrides.put(Pattern.compile(entry.getKey()), entry.getValue());
        }
    }

    private static class CasThemeResourceBundleMessageSource extends ResourceBundleMessageSource {
        @Override
        protected ResourceBundle doGetBundle(final String basename, final Locale locale) {
            try {
                final ResourceBundle bundle = ResourceBundle.getBundle(basename, locale, getBundleClassLoader());
                if (bundle != null && bundle.keySet().size() > 0) {
                    return bundle;
                }
            } catch (final Exception e) {
                LOGGER.debug(e.getMessage(), e);
            }
            return null;
        }
    }

	public String getThemeKey() {
		return themeKey;
	}

	public void setThemeKey(String themeKey) {
		this.themeKey = themeKey;
	}

	public Boolean getUseRequestParam() {
		return useRequestParam;
	}

	public void setUseRequestParam(Boolean useRequestParam) {
		this.useRequestParam = useRequestParam;
	}

	public String getThemeNamePrefix() {
		return themeNamePrefix;
	}

	public void setThemeNamePrefix(String themeNamePrefix) {
		this.themeNamePrefix = themeNamePrefix;
	}
	
}
