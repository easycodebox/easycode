package com.easycodebox.cas.cache;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.util.http.HttpClient;
import org.jasig.cas.util.http.HttpMessage;

/**
 * @author WangXiaoJin
 *
 */
public class UrlClearUserCache implements ClearUserCache {

	private HttpClient httpClient;
	/**
	 * http://xxx.com/clearuser/{userId}
	 */
	private String url;
	/**
	 * 是否作为异步请求处理
	 */
	private boolean async = true;
	private String userIdPattern = "{userId}";
	
	@Override
	public boolean clear(String userId) throws MalformedURLException {
		String newUrl = url.replace(userIdPattern, userId);
		return httpClient.sendMessageToEndPoint(new HttpMessage(new URL(newUrl), StringUtils.EMPTY, async));
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isAsync() {
		return async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public String getUserIdPattern() {
		return userIdPattern;
	}

	public void setUserIdPattern(String userIdPattern) {
		this.userIdPattern = userIdPattern;
	}

}
