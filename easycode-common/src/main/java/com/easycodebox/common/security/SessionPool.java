package com.easycodebox.common.security;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author WangXiaoJin
 */
public class SessionPool {
	
	private static final Logger log = LoggerFactory.getLogger(SessionPool.class);
	
	private static ConcurrentMap<String, HttpSession> dataMap = new ConcurrentHashMap<>();
	
	public static void addSession(HttpSession session) {
		if (session == null) {
			log.info("session is null.");
			return;
		}
		dataMap.put(session.getId(), session);
		log.info("insert session into SessionPool.sessionId:{}\tsession:{}", session.getId(), session);
	}
	
	public static void remove(String sessionId) {
		if (StringUtils.isBlank(sessionId)) return;
		HttpSession session = dataMap.get(sessionId);
		if (null != session) {
			dataMap.remove(sessionId);
			log.info("remove session from SessionPool.sessionId:{}", session.getId());
		}
	}
	
	public static HttpSession getSession(String sessionId) {
		if (StringUtils.isBlank(sessionId)) return null;
		return dataMap.get(sessionId);
	}
	
}
