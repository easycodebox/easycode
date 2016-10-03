package com.easycodebox.common.security;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

/**
 * @author WangXiaoJin
 * 
 */
public class SessionPool {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionPool.class);
	
	private static ConcurrentMap<String, HttpSession> dataMap = new ConcurrentHashMap<String, HttpSession>() ;
	
	public static void addSession(HttpSession session){
		if(session==null) {
			LOGGER.info("session is null,session:"+session);
			return;
		}
		dataMap.put(session.getId(), session);
		LOGGER.info("insert session into SessionPool.sessionId:{0}\tsession:{1}",session.getId(),session);
	}
	
	public static void remove(String sessionId){
		if(StringUtils.isBlank(sessionId)) return;
		HttpSession session = dataMap.get(sessionId);
		if (null != session) {
			dataMap.remove(sessionId);
			LOGGER.info("remove session from SessionPool.sessionId:{0}",session.getId());
		}
	}
	
	public static HttpSession getSession(String sessionId){
		if(StringUtils.isBlank(sessionId)) return null;
		return dataMap.get(sessionId);
	}
		
}
