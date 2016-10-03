package com.easycodebox.common.security;

import javax.servlet.http.HttpSession;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.lang.dto.UserInfo;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

/**
 * @author WangXiaoJin
 * 
 */
public class SecurityUtils {

	private static final Logger LOG = LoggerFactory.getLogger(SecurityUtils.class);
	
	public static boolean isSecurity() {
		return SecurityUtils.getUserId() == null ? false : true;
	}
	
	public static String getUserId() {
		UserInfo user = getUser();
		if(user == null) {
			LOG.debug("User info can't obtion.");
			return null;
		}
		return user.getUserId();
	}
	
	public static String getIp() {
		SecurityContext<UserInfo> context = getCurSecurityContext();
		if(context == null) return null;
		return context.getIp();
	}
	
	@SuppressWarnings("unchecked")
	public static SecurityContext<UserInfo> getCurSecurityContext() {
		return (SecurityContext<UserInfo>)SecurityContexts.getCurSecurityContext();
	}
	
	public static String getSessionId() {
		return getCurSecurityContext() == null ? null : getCurSecurityContext().getSessionId();
	}
	
	/**
	 * user信息同时保存到session
	 * @param session
	 * @param user
	 */
	public static void setUser(HttpSession session, UserInfo user) {
		if(session == null) return;
		session.setAttribute(BaseConstants.USER_KEY, user);
		SecurityContext<UserInfo> sc = getCurSecurityContext();
		if(sc == null) {
			SecurityContext<UserInfo> tmp = new SecurityContext<UserInfo>();
			tmp.setSessionId(session.getId());
			tmp.setSecurity(user);
			SecurityContexts.setCurSecurityContext(tmp);
		}else {
			sc.setSecurity(user);
			sc.setSessionId(session.getId());
		}
	}
	
	/**
	 * 用getUser()替代此方法
	 * @param session
	 * @return
	 */
	public static UserInfo getUser(HttpSession session) {
		if(session == null) return null;
		return (UserInfo)session.getAttribute(BaseConstants.USER_KEY);
	}
	
	public static void invalidSession(HttpSession session){
		if(session == null) return;
		session.invalidate();
	}
	
	/**
	 * 获取用户首选方法
	 * @param session
	 * @return
	 */
	public static UserInfo getUser() {
		SecurityContext<UserInfo> context = getCurSecurityContext();
		if(context == null || context.getSecurity() == null) return null;
		return context.getSecurity();
	}
	
	/**
	 * 摧毁session数据和本地线程安全数据
	 * @param session
	 */
	public static void destroySecurityData(HttpSession session) {
		session.removeAttribute(BaseConstants.USER_KEY);
		SecurityContexts.setCurSecurityContext(null);
	}
	
}
