package com.easycodebox.common.web.springmvc;

import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.lang.reflect.Classes;
import com.easycodebox.common.net.Https;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 主要为了解决controller方法不返回view时自动生成view。重写此HandlerAdapter的原因是RequestToViewNameTranslator
 * 无法满足需求。 
 * <p>
 * 注意：
 * <ul>
 * 	<li>如果此类配置后无效，请增加@Order注释或者实现Order接口，让此类的优先级大于其他HandlerAdapter</li>
 * 	<li>
 * 		如果Controller方法中包含HttpServletResponse response参数，则此类不会帮你自动生成View。
 * 	<pre>
 * 	@RequestMapping("/user/list")
 * 	public void list(HttpServletResponse response) {
 * 		//...
 * 	}
 * 	</pre>
 * 		您使用了response参数，意味着响应由你自己来控制，而不是由插件自动帮你生成View。
 *	</li>
 * </ul>
 * 
 * @author WangXiaoJin
 *
 */
public class DefaultRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {

	private boolean autoView = false;
	
	protected ModelAndView invokeHandlerMethod(HttpServletRequest request,
			HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {

		ModelAndView view = super.invokeHandlerMethod(request, response, handlerMethod);
		if(view != null && !view.hasView() && autoView) {
			String pkg = Classes.getLastPkg(handlerMethod.getBeanType());
			String[] frags = Https.getParticularPaths(request);
			String viewName;
			if(frags.length == 1) {
				viewName = pkg + Symbol.SLASH + frags[0];
			}
			else if(frags.length == 2) {
				viewName = pkg + Symbol.SLASH + frags[0] + Symbol.BOTTOM_LINE + frags[1];
			}
			else
				viewName = Https.getShortPath(request);
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
