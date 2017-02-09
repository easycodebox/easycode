package com.easycodebox.common.security;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.lang.dto.UserInfo;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.net.HttpUtils;
import com.easycodebox.common.validate.Assert;

import javax.servlet.http.*;
import java.io.Serializable;

/**
 * @author WangXiaoJin
 * 
 */
public class SecurityUtils {

	private static final Logger log = LoggerFactory.getLogger(SecurityUtils.class);
	
	/**
	 * SecurityInfo存储于HttpSession中，且SecurityInfo的类型为{@link com.easycodebox.common.lang.dto.UserInfo}
	 */
	public static final SecurityInfoHandler<HttpSession, UserInfo> SESSION_SECURITY_INFO_HANDLER = new SecurityInfoHandler<HttpSession, UserInfo>() {

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
				context.setIp(HttpUtils.getIpAddr(request));
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
			return storage == null ? null : (UserInfo)storage.getAttribute(BaseConstants.USER_KEY);
		}

		@Override
		@SuppressWarnings("unchecked")
		public void storeSecurityInfo(HttpSession storage, UserInfo securityInfo) {
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
		public void destroySecurityInfo(HttpSession storage) {
			if (storage == null) return ;
			storage.removeAttribute(BaseConstants.USER_KEY);
			SecurityContexts.setCurSecurityContext(null);
		}

	};
	
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
	
	/**
	 * 存储/获取Security信息
	 * @author WangXiaoJin
	 * @param <S>	storage仓库类型
	 * @param <T>	securityInfo类型
	 */
	public interface SecurityInfoHandler<S, T extends Serializable> {
		
		/**
		 * 根据storage创建一个新的SecurityContext实例
		 * @param storage
		 * @return
		 */
		SecurityContext<T> newSecurityContext(S storage, HttpServletRequest request, HttpServletResponse response);
		
		/**
		 * 获取Security数据
		 * @param storage 存放SecurityInfo的仓库
		 * @return
		 */
		T getSecurityInfo(S storage);
		
		/**
		 * 存储securityInfo至storage
		 * @param storage
		 * @param securityInfo
		 */
		void storeSecurityInfo(S storage, T securityInfo);
		
		/**
		 * 摧毁securityInfo
		 * @param storage
		 */
		void destroySecurityInfo(S storage);
		
	}
	
}
