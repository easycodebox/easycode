package com.easycodebox.common.web;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.net.HttpUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 新版通过浏览器端sessionStorage实现此功能
 * @author WangXiaoJin
 */
@Deprecated
public class CacheHisUris {
	
	private static final Logger log = LoggerFactory.getLogger(CacheHisUris.class);

	public static final String CACHE_HIS_URI_KEY = "cached_his_uri";
	public static final String FLUSH_CACHED_URI = "flush_cached_uri";
	public static final String BACK_CACHED_URI = "back_cached_uri";
	
	public static final String CUR_URI = "curUri";
	public static final String BACK_URI = "backUri";
	
	public static final int CACHE_NUM = 10;
	
	private static ConcurrentHashMap<String, Boolean> uris = new ConcurrentHashMap<>();
	
	/**
	 * 判断method对应的请求是否能被缓存
	 * @param method
	 * @return
	 */
	public static boolean isCacheHisMethod(Method method) {
		String key = method.toGenericString();
		boolean exist = uris.containsKey(key);
		if(!exist) {
			CacheHisUri his = method.getAnnotation(CacheHisUri.class);
			if(his != null) {
				uris.putIfAbsent(key, Boolean.TRUE);
				exist = true;
			}else {
				uris.putIfAbsent(key, Boolean.FALSE);
			}
		}else {
			exist = uris.get(key);
		}
		return exist;
	}
	
	/**
	 * 
	 * @param method
	 * @param request
	 * @return	true 该请求有@CacheHisUri注解， false 反之
	 */
	@SuppressWarnings("unchecked")
	public static boolean cacheHisUri(Method method, HttpServletRequest request) {
		boolean isCacheUri = isCacheHisMethod(method);
		try {
			HttpSession session = request.getSession();
			List<String> uris = (List<String>)session.getAttribute(CACHE_HIS_URI_KEY);
			if(uris == null) {
				uris = new ArrayList<>(CACHE_NUM);
				session.setAttribute(CACHE_HIS_URI_KEY, uris);
			}
			if(isCacheUri) {
				if(request.getQueryString() == null
						|| !(request.getQueryString().contains(FLUSH_CACHED_URI)
								|| request.getQueryString().contains(BACK_CACHED_URI))) {
					//清除超出的缓存地址
					while(uris.size() >= CACHE_NUM) {
						uris.remove(0);
					}
					String fullUri = HttpUtils.getFullRequestUri(request, 2, BaseConstants.httpParamTradition);
					//缓存uri地址
					uris.add(fullUri);
					log.debug("cache hisUri = {0}", fullUri);
				}else if(request.getQueryString().contains(BACK_CACHED_URI)) {
					uris.remove(uris.size() - 1);
					
				}
				
				String curUri = uris == null || uris.size() == 0 ? Symbol.EMPTY : uris.get(uris.size() - 1),
						backUri = uris == null || uris.size() < 2 ? Symbol.EMPTY : uris.get(uris.size() - 2);
				request.setAttribute(CUR_URI, curUri);
				request.setAttribute(BACK_URI, backUri);
				
			}else {
				String backUri = uris == null || uris.size() == 0 ? Symbol.EMPTY : uris.get(uris.size() - 1);
				request.setAttribute(BACK_URI, backUri);
			}
		} catch (Exception e) {
			log.error("CacheHisUri error.", e);
		}
		return isCacheUri;
	}
	
}
