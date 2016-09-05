package com.easycodebox.common.web.springmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.lang.reflect.ClassUtils;
import com.easycodebox.common.net.HttpUtils;

/**
 * 主要为了解决controller方法不返回view时自动生成view。重写此HandlerAdapter的原因是RequestToViewNameTranslator
 * 无法满足需求。 <br>
 * 注意：如果此类配置后无效，请增加@Order注释或者实现Order接口，让此类的优先级大于其他HandlerAdapter
 * @author WangXiaoJin
 *
 */
public class DefaultRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {

	private boolean autoView = false;
	
	protected ModelAndView invokeHandlerMethod(HttpServletRequest request,
			HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {

		ModelAndView view = super.invokeHandlerMethod(request, response, handlerMethod);
		if(view != null && !view.hasView() && autoView) {
			String pkg = ClassUtils.getLastPkg(handlerMethod.getBeanType());
			String[] frags = HttpUtils.getParticularPaths(request);
			String viewName = null;
			if(frags.length == 1) {
				viewName = pkg + Symbol.SLASH + frags[0];
			}
			else if(frags.length == 2) {
				viewName = pkg + Symbol.SLASH + frags[0] + Symbol.BOTTOM_LINE + frags[1];
			}
			else
				viewName = HttpUtils.getShortPath(request);
			view.setViewName(viewName);
		}
		return view;
	}

	public boolean isAutoView() {
		return autoView;
	}

	public void setAutoView(boolean autoView) {
		this.autoView = autoView;
	}
	
}
