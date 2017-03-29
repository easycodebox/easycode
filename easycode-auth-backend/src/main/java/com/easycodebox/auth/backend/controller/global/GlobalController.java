package com.easycodebox.auth.backend.controller.global;

import com.easycodebox.common.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author WangXiaoJin
 *
 */
@Controller
@SuppressWarnings("Duplicates")
public class GlobalController extends BaseController {
	
	@Autowired
	private ViewResolver viewResolver;
	
	@Autowired
	private LocaleResolver localeResolver;
	
	@Autowired
	private Map properties;
	
	/**
	 * index
	 */
	@RequestMapping("/")
	public String index() throws Exception {
		return "welcome";
	}
	
	@RequestMapping("/decorator")
	public void decorator(HttpServletRequest request, HttpServletResponse response) throws Exception {
		AbstractTemplateView view = (AbstractTemplateView)viewResolver.resolveViewName("decorator", 
				localeResolver.resolveLocale(request));
		//因为此请求是Sitemesh forward进来的，如果不做下面两个Override配置的话会重复设置Model，而Spring MVC碰到重复参数名会抛异常
		//详细逻辑请阅读 {@link AbstractTemplateView} 的 renderMergedOutputModel方法
		
		//启用spring.freemarker.expose-request-attributes=true时则需要启用下面的代码
		//view.setAllowRequestOverride(true);
		
		//启用spring.freemarker.expose-session-attributes=true时则需要启用下面的代码
		//view.setAllowSessionOverride(true);
		
		view.setExposeSpringMacroHelpers(false);
		view.render(null, request, response);
	}
	
	/**
	 * 生成JS的配置文件
	 */
	@GetMapping("/js/config")
	public String configJs(HttpServletResponse response, Model model) throws Exception {
		CacheControl cacheControl = CacheControl.maxAge(30, TimeUnit.MINUTES).cachePublic();
		response.addHeader("Cache-Control", cacheControl.getHeaderValue());
		response.addHeader("Content-Type", "application/javascript;charset=UTF-8");
		model.addAllAttributes(properties);
		return "config-js";
	}
	
}
