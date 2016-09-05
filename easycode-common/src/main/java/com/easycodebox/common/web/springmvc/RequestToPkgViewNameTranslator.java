package com.easycodebox.common.web.springmvc;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.RequestToViewNameTranslator;

import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.net.HttpUtils;

/**
 * 当spring controller逻辑处理完成后，没有找到合适的view，则可以通过次类生成默认的view <br>
 * 使用方法：
 * <code>
 * &lt;bean id="viewNameTranslator" class="com.easycodebox.common.web.springmvc.RequestToPkgViewNameTranslator" />
 * </code>
 * @author WangXiaoJin
 *
 */
public class RequestToPkgViewNameTranslator implements RequestToViewNameTranslator {

	@Override
	public String getViewName(HttpServletRequest request) throws Exception {
		String[] frags = HttpUtils.getParticularPaths(request);
		if(frags.length == 1) {
			return frags[0];
		}
		else if(frags.length == 2) {
			return frags[0] + Symbol.SLASH + frags[1];
		}
		else if(frags.length == 3)
			return frags[0] + Symbol.SLASH + frags[1] + Symbol.BOTTOM_LINE + frags[2];
		else
			return HttpUtils.getShortPath(request);
	}

}
