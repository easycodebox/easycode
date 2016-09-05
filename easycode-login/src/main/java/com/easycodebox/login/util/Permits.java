package com.easycodebox.login.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.session.Session;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.net.HttpUtils;

/**
 * @author WangXiaoJin
 *
 */
public class Permits {
	
	/**
	 * 存储用户权限
	 * @param allOs
	 */
	public static void cacheOperations(Session session, Map<String, Boolean> allOs) {
		session.setAttribute(BaseConstants.OPERATION_KEY, allOs);
	}
	
	public static boolean isPermitted(HttpServletRequest request) {
		String path = HttpUtils.getParticularPath(request);
		return isPermitted(path, request);
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isPermitted(String path, HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		if(StringUtils.isNotBlank(path)) {
			int start = path.charAt(0) == '/' ? 1 : 0, 
					end = path.lastIndexOf(Symbol.PERIOD);
			path = path.substring(start, end == -1 ? path.length() : end);
		}
		Map<String, Boolean> ops = (Map<String, Boolean>)session.getAttribute(BaseConstants.OPERATION_KEY);
		return ops.get(path) == null || ops.get(path);
	}
	
}
