package com.easycodebox.common.web.springmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.net.HttpUtils;

/**
 * @author WangXiaoJin
 *
 */
public class DefaultControllerHandler implements Controller {

	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] paths = HttpUtils.getParticularPaths(request);
		String view = Symbol.EMPTY;
		if(paths.length == 0)
			view = Symbol.SLASH;
		else if(paths.length == 3)
			view = paths[0] + Symbol.SLASH + paths[1] + Symbol.BOTTOM_LINE + paths[2];
		else {
			for(int i = 0; i < paths.length; i++) {
				view += i < paths.length - 1 ? paths[i] + Symbol.SLASH : paths[i];
			}
		}
		return new ModelAndView(view);
	}


}
