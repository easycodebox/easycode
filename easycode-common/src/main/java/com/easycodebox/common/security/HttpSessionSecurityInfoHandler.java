package com.easycodebox.common.security;

import com.easycodebox.common.lang.dto.UserInfo;
import com.easycodebox.common.net.Https;
import com.easycodebox.common.validate.Assert;

import javax.servlet.http.*;

/**
 * SecurityInfo存储于HttpSession中，且SecurityInfo的类型为{@link com.easycodebox.common.lang.dto.UserInfo}
 * @author WangXiaoJin
 */
public class HttpSessionSecurityInfoHandler extends AbstractSecurityInfoHandler<HttpSession, UserInfo> {
	
	/**
	 *  storage、request两个参数至少一个有值
	 */
	@Override
	public SecurityContext<UserInfo> newSecurityContext(HttpSession storage, HttpServletRequest request, HttpServletResponse response) {
		if (storage == null && request != null) {
			storage = request.getSession();
		}
		Assert.notNull(storage);
		SecurityContext<UserInfo> context = new SecurityContext<>();
		UserInfo user = getSecurityInfo(storage);
		context.setSessionId(storage.getId());
		if(user != null) {
			context.setSecurity(user);
		}
		if (request != null) {
			context.setIp(Https.getIpAddr(request));
			context.setUserAgent(request.getHeader("User-Agent"));
			context.setRequest(request);
		}
		if (response != null) {
			context.setResponse(response);
		}
		return context;
	}
	
	@Override
	public UserInfo getSecurityInfo(HttpSession storage) {
		return storage == null ? null : (UserInfo)storage.getAttribute(getKey());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void storeSecurityInfo(HttpSession storage, UserInfo securityInfo) {
		Assert.notNull(storage);
		storage.setAttribute(getKey(), securityInfo);
		SecurityContext<UserInfo> sc = (SecurityContext<UserInfo>)SecurityContexts.getCurSecurityContext();
		if(sc == null) {
			SecurityContext<UserInfo> tmp = new SecurityContext<>();
			tmp.setSecurity(securityInfo);
			tmp.setSessionId(storage.getId());
			SecurityContexts.setCurSecurityContext(tmp);
		} else {
			sc.setSecurity(securityInfo);
			sc.setSessionId(storage.getId());
		}
	}
	
	@Override
	public void destroySecurityInfo(HttpSession storage) {
		if (storage == null) return ;
		storage.removeAttribute(getKey());
		SecurityContexts.setCurSecurityContext(null);
	}
	
}
