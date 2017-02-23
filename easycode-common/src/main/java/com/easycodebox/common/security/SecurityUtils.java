package com.easycodebox.common.security;

import com.easycodebox.common.lang.dto.UserInfo;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author WangXiaoJin
 * 
 */
public class SecurityUtils {

	private static final Logger log = LoggerFactory.getLogger(SecurityUtils.class);
	
	/**
	 * SecurityInfo保存到仓库中
	 */
	public static <S, T extends Serializable> void storeSecurityInfo(SecurityInfoHandler<S, T> handler, S storage, T securityInfo) {
		handler.storeSecurityInfo(storage, securityInfo);
	}
	
	/**
	 * 获取SecurityInfo
	 */
	public static <S, T extends Serializable> T getSecurityInfo(SecurityInfoHandler<S, T> handler, S storage) {
		return handler.getSecurityInfo(storage);
	}
	
	/**
	 * 摧毁SecurityInfo
	 */
	public static <S, T extends Serializable> void destroySecurityInfo(SecurityInfoHandler<S, T> handler, S storage) {
		handler.destroySecurityInfo(storage);
	}
	
	public static boolean isSecurity() {
		return getUserId() != null;
	}
	
	public static String getUserId() {
		UserInfo user = getUser();
		if(user == null) {
			log.debug("User info can't obtion.");
			return null;
		}
		return user.getUserId();
	}
	
	public static String getIp() {
		SecurityContext<?> context = SecurityContexts.getCurSecurityContext();
		return context == null ? null : context.getIp();
	}
	
	public static Serializable getSessionId() {
		SecurityContext<?> context = SecurityContexts.getCurSecurityContext();
		return context == null ? null : context.getSessionId();
	}
	
	/**
	 * 获取用户信息
	 */
	public static UserInfo getUser() {
		SecurityContext<?> context = SecurityContexts.getCurSecurityContext();
		if (context == null || context.getSecurity() == null) return null;
		return context.getSecurity() instanceof UserInfo ? (UserInfo)context.getSecurity() : null;
	}
	
}
