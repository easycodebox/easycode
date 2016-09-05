package com.easycodebox.common.security;

import java.net.InetAddress;
import java.net.SocketException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.lang.dto.UserInfo;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.net.HttpUtils;
import com.easycodebox.common.net.InetAddresses;

/**
 * @author WangXiaoJin
 * 
 */
public class SecurityUtils {

	private static final Logger LOG = LoggerFactory.getLogger(SecurityUtils.class);
	
	public static boolean isSecurity() {
		return SecurityUtils.getUserId() == null ? false : true;
	}
	
	public static String getUserId(){
		UserInfo user = getUser();
		if(user == null) {
			LOG.debug("User info can't obtion.");
			return null;
		}
		return user.getUserId();
	}
	
	public static String getIp(){
		SecurityContext<UserInfo> context = getCurSecurityContext();
		if(context == null) return null;
		return context.getIp();
	}
	
	@SuppressWarnings("unchecked")
	public static SecurityContext<UserInfo> getCurSecurityContext(){
		return (SecurityContext<UserInfo>)SecurityContexts.getCurSecurityContext();
	}
	
	public static String getSessionId(){
		return getCurSecurityContext() == null ? null : getCurSecurityContext().getSessionId();
	}
	
	/**
	 * user信息同时保存到session
	 * @param session
	 * @param user
	 */
	public static void setUser(HttpSession session, UserInfo user){
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
	public static UserInfo getUser(HttpSession session){
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
	public static UserInfo getUser(){
		SecurityContext<UserInfo> context = getCurSecurityContext();
		if(context == null || context.getSecurity() == null) return null;
		return context.getSecurity();
	}
	
	public static String getOperator(){
		UserInfo user = getUser();
		if(user == null) {
			LOG.debug("User info can't obtion.");
			return null;
		}
		return user.getOperator();
	}
	
	private volatile static String serverId = null;
	/**
	 * 系统ID缺省是机器的hostName,如果存在有一台机器停止之后很长时间不启动的情况下，
	 * 想用另外一台机器代替该机器，可以临时性的,在Tomcat的启动脚本中，
	 * 加上"-Dtomcat.id=front001"这样的设定,注意：这里必须每台Tomcat的设定值都不一样，
	 * @return String
	 */
	public static String getServerId(){
		if(serverId == null) {
			LOG.info("SystemInfoUtils.getFrontId <<<DEBUG>>> is NULL !!!");
			synchronized (SecurityUtils.class) {
				if(serverId == null) {
					LOG.info("<<<DEBUG>>> SystemInfoUtils init frontId start !!!");
					
					String tomcatId = System.getProperty("tomcat.id");
					if (StringUtils.isNotBlank(tomcatId)) {
						LOG.info("<<<DEBUG>>> ==>tomcatId==" + tomcatId);
						serverId = tomcatId;
					}else{
						try {
							InetAddress addr = InetAddresses.getLocalAddress();
							serverId = addr.getHostName();
						} catch (SocketException e) {
							LOG.error("SystemInfoUtils <<<ERROR>>> InetAddresses.getLocalAddress ",e);
						}
						LOG.info("<<<DEBUG>>> ==>HostName==" + serverId);
					}
					
					if(serverId == null) {
						LOG.error("SystemInfoUtils.getFrontId <<<ERROR>>> frontId="+serverId);
					}
					LOG.info("SystemInfoUtils init  frontId end !!!");
				}
			}
		}
		return serverId;
	}
	
	/**
	 * 免登录的cookie格式为 userId-password-MD5
	 * 此方法是获取userId-password-MD5值
	 * @param userId
	 * @param password	MD5密码
	 * @return
	 */
	public static String getUserLoginCookie(String userId, String password){
		StringBuilder userInfo = new StringBuilder();
		userInfo.append(userId)
				.append("_")
				.append(password)
				.append("_")
				.append(DigestUtils.md5Hex(userInfo.toString()));
		return userInfo.toString();
	}
	
	/**
	 * 存免登录cookie到客户端
	 * @param userId
	 * @param password
	 * @return
	 */
	public static void addUserLoginCookie(String freeLoginCookieName, String userId, 
			Integer freeLoginCookieTime, String password, HttpServletResponse response){
		String cookie = SecurityUtils.getUserLoginCookie(userId, password);
		HttpUtils.addCookie(freeLoginCookieName, cookie, 
				freeLoginCookieTime, response);
	}
	
	/**
	 * 摧毁session数据和本地线程安全数据
	 * @param session
	 */
	public static void destroySecurityData(HttpSession session){
		session.removeAttribute(BaseConstants.USER_KEY);
		SecurityContexts.setCurSecurityContext(null);
	}
	
}
