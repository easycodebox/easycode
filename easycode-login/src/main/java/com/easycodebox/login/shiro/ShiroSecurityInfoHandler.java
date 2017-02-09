package com.easycodebox.login.shiro;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.lang.dto.UserInfo;
import com.easycodebox.common.net.Https;
import com.easycodebox.common.security.SecurityContext;
import com.easycodebox.common.security.SecurityContexts;
import com.easycodebox.common.security.SecurityUtils.SecurityInfoHandler;
import com.easycodebox.common.validate.Assert;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author WangXiaoJin
 *
 */
public class ShiroSecurityInfoHandler implements SecurityInfoHandler<Session, UserInfo> {

	@Override
	public SecurityContext<UserInfo> newSecurityContext(Session storage, HttpServletRequest request,
			HttpServletResponse response) {
		if (storage == null) {
			storage = SecurityUtils.getSubject().getSession();
		}
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
	public UserInfo getSecurityInfo(Session storage) {
		return storage == null ? null : (UserInfo)storage.getAttribute(BaseConstants.USER_KEY);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void storeSecurityInfo(Session storage, UserInfo securityInfo) {
		Assert.notNull(storage);
		storage.setAttribute(BaseConstants.USER_KEY, securityInfo);
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
	public void destroySecurityInfo(Session storage) {
		if (storage == null) return ;
		storage.removeAttribute(BaseConstants.USER_KEY);
		SecurityContexts.setCurSecurityContext(null);
	}

}
