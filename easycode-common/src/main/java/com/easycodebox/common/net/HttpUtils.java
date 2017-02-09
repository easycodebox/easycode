package com.easycodebox.common.net;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.file.MimeTypes;
import com.easycodebox.common.file.UploadFileInfo;
import com.easycodebox.common.jackson.Jacksons;
import com.easycodebox.common.lang.*;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.validate.Assert;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.*;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author WangXiaoJin
 * 
 */
public class HttpUtils {
	
	private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);
	
	/**
	 * 获得客户端ip
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {     
	     String ip = request.getHeader("x-forwarded-for");     
	     if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {     
	         ip = request.getHeader("Proxy-Client-IP");     
	     }     
	      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {     
	         ip = request.getHeader("WL-Proxy-Client-IP");     
	      }     
	     if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {     
	          ip = request.getRemoteAddr();     
	     }     
	     return ip;     
	}
	
	/**
	 * 获取请求地址信息，包含参数
	 * 数组会转换成[1,2]形式
	 */
	@SuppressWarnings("rawtypes")
	public static String getRequestUrlAndParams(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder("REQUEST URL === ");
		sb.append(request.getRequestURL())
		.append("?");
		Enumeration keys = request.getParameterNames();
		while(keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			String[] values = request.getParameterValues(key);
			sb.append(key)
				.append(Symbol.EQ)
				.append(values == null || values.length > 1 
						? DataConvert.array2String(values, Symbol.COMMA, Symbol.L_BRACKET, Symbol.R_BRACKET, false) : values[0])
				.append("&");
		}
		return sb.toString();
	}
	
	public static Map<String, String> convertQueryParams2Map(String query) {
		Map<String, String> params = new HashMap<>();
		if(StringUtils.isBlank(query)) return params;
		int index = query.indexOf(Symbol.QUESTION);
		query = index > -1 ? query.substring(0, index) : query;
		String[] keyVals = query.split(Symbol.AND_MARK);
		for(String keyVal : keyVals) {
			String[] kv = keyVal.split(Symbol.EQ);
			params.put(kv[0], kv.length > 1 ? kv[1] : "");
		}
		return params;
	}
	
	/**
	 * 获取请求地址信息，包含参数。例：/backend/group/list.html?name=xxx
	 * 直接封装成有效的请求地址
	 */
	public static String getFullRequestUri(HttpServletRequest request, int encodeNum, 
			boolean tradition, String... excludeKeys) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getRequestURI()).append(Symbol.QUESTION);
		sb.append(getRequestParams(request, encodeNum, tradition, excludeKeys));
		String tmp = sb.toString();
		if(tmp.lastIndexOf(Symbol.QUESTION) == tmp.length() - 1)
			tmp = tmp.substring(0, tmp.length() - 1);
		return tmp;
	}
	
	/**
	 * 获取请求地址信息，包含参数。例：http://localhost:8080/backend/group/list.html?name=xxx
	 * 直接封装成有效的请求地址
	 */
	public static String getFullRequestUrl(HttpServletRequest request, int encodeNum, 
			boolean tradition, String... excludeKeys) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getRequestURL()).append(Symbol.QUESTION);
		sb.append(getRequestParams(request, encodeNum, tradition, excludeKeys));
		String tmp = sb.toString();
		if(tmp.lastIndexOf(Symbol.QUESTION) == tmp.length() - 1)
			tmp = tmp.substring(0, tmp.length() - 1);
		return tmp;
	}
	
	/**
	 * 获取请求特有的path<br>
	 * http://www.xx.com ==> ""<br>
	 * http://www.xx.com/a/b.html ==> /a/b.html<br>
	 * http://localhost:8080/test/a/b.html ==> /a/b.html<br>
	 * @param request
	 * @return
	 */
	public static String getParticularPath(HttpServletRequest request) {
		String uri = request.getRequestURI(),
			contextPath = request.getContextPath();
		if(StringUtils.isBlank(uri))
			return StringUtils.EMPTY;
		if(StringUtils.EMPTY.equals(contextPath)) {
			return uri;
		}else {
			return uri.replaceFirst(contextPath, StringUtils.EMPTY);
		}
	}
	
	/**
	 * 获取简短的请求路径 <br>
	 * http://www.xx.com ==> "" <br>
	 * http://www.xx.com/a/b.html ==> /a/b <br>
	 * http://localhost:8080/test/a/b.html ==> /a/b <br>
	 * @param request
	 * @return
	 */
	public static String getShortPath(HttpServletRequest request) {
		String path = getParticularPath(request);
		int index = path.lastIndexOf(Symbol.PERIOD);
		return index == -1 ? path : path.substring(0, index);
	}
	
	/**
	 * 获取请求的根路径
	 * http://www.xx.com ==> http://www.xx.com
	 * http://www.xx.com/a/b.html ==> http://www.xx.com
	 * http://localhost:8080/test/a/b.html ==> http://localhost:8080/test
	 * @param request
	 * @return
	 */
	public static String getBasePath(HttpServletRequest request) {
		String url = request.getRequestURL().toString(),
			uri = request.getRequestURI(),
			contextPath = request.getContextPath();

		if(StringUtils.isNotEmpty(uri)) {
			int index = url.lastIndexOf(uri);
			url = url.substring(0, index);
		}
		
		if(!"".equals(contextPath))
			url = url + contextPath;
		
		return url.endsWith(Symbol.SLASH) ? url.substring(0, url.length() - 1) : url;
	}
	
	/**
	 * http://www.xxx.com/shop/list.do ==> [shop, list]
	 * http://www.xxx.com/js/util.js ==> [js, util]
	 * @param request
	 * @return
	 */
	public static String[] getParticularPaths(HttpServletRequest request) {
		String path = getParticularPath(request);
		path = path.charAt(0) == '/' ? path.substring(1) : path;
		String[] paths = path.split(Symbol.SLASH);
		
		path = paths[paths.length - 1];
		int index = path.lastIndexOf(Symbol.PERIOD);
		if(index > -1)
			paths[paths.length - 1] = path.substring(0, index);
		return paths;
	}
	
	/**
	 * 获取请求的参数 例：name=wangxj&shop=wxj
	 * @param request
	 * @param encodeNum	encode value的次数，某些场景下需要经过两次encode才能正确GET形式传中文
	 * @param tradition	是否已传统格式传数组。传统格式：name=wang&name=zhang，非传统格式：name[]=wang&name[]=zhang
	 * @param excludeKeys
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("rawtypes")
	public static String getRequestParams(HttpServletRequest request, int encodeNum, 
			boolean tradition, String... excludeKeys) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		Enumeration keys = request.getParameterNames();
		
		loopKey:
		while(keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			if(excludeKeys != null && excludeKeys.length > 0) {
				for(String excludeKey : excludeKeys) {
					if(excludeKey != null && excludeKey.equals(key))
						continue loopKey;
				}
			}
			String[] values = request.getParameterValues(key);
			if(values == null) continue;
			for (String val : values) {
				int count = encodeNum;
				while (count-- > 0) {
					val = URLEncoder.encode(val, "UTF-8");
				}
				sb.append(key).append(values.length > 1 && !tradition ? "[]" : Symbol.EMPTY)
						.append(Symbol.EQ)
						.append(val)
						.append(Symbol.AND_MARK);
			}
		}
		if(sb.length() > 0 && sb.charAt(sb.length() - 1) == '&')
			sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	/**
	 * 把参数转换成url中的键值对
	 * @param params
	 * @param jsonKeys 指定哪些key的值用json解析
	 * @param excludeKeys 排除的key
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	public static String assembleParams(Map<?, ?> params, Collection<?> jsonKeys, String... excludeKeys) throws IOException  {
		StringBuilder sb = new StringBuilder();
		if(params != null && params.size() > 0) {
			loopKey:
			for(Map.Entry<?,?> entry : params.entrySet()) {
				if(entry.getKey() == null
						|| entry.getValue() == null) 
					continue;
				String key = entry.getKey().toString();
				if(excludeKeys != null && excludeKeys.length > 0) {
					for(String excludeKey : excludeKeys) {
						if(excludeKey != null && excludeKey.equals(key))
							continue loopKey;
					}
				}
				Object value = entry.getValue();
				boolean isJsonKey = false;
				if(jsonKeys != null && jsonKeys.size() > 0) {
					for(Object jsonKey : jsonKeys) {
						if(jsonKey != null && key.equals(jsonKey.toString())) {
							isJsonKey = true;
							break;
						}
					}
				}
				if(isJsonKey) {
					value = Jacksons.NON_NULL.toJson(value);
				}else {
					if(value.getClass().isArray()) {
						Object[] vals = (Object[])value;
	        			for(Object val : vals) {
	        				if(val == null) continue;
	        				sb.append(key).append(BaseConstants.httpParamTradition ? "" : "[]")
	        				.append(Symbol.EQ)
	        				.append(URLEncoder.encode(val.toString(), "UTF-8"))
	        				.append("&");
	        			}
	        			continue loopKey;
					}else if(value instanceof Collection) {
						for(Object val : (Collection<?>)value) {
							if(val == null) continue;
							sb.append(key).append(BaseConstants.httpParamTradition ? "" : "[]")
	        				.append(Symbol.EQ)
	        				.append(URLEncoder.encode(val.toString(), "UTF-8"))
	        				.append("&");
	        			}
						continue loopKey;
					}
				}
				sb.append(key)
				.append(Symbol.EQ)
				.append(URLEncoder.encode(value.toString(), "UTF-8"))
				.append("&");
			}
			if(sb.length() > 0 && sb.charAt(sb.length() - 1) == '&')
				sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
	
	/**
	 * 给指定的url增加参数字符窜，根据url的“？”字符返回相对应的格式字符窜
	 * @param url
	 * @param params
	 * @return
	 */
	public static String addParams2Url(String url, String params) {
		Assert.notBlank(url, "url can not be blank.");
		if(StringUtils.isBlank(params))
			return url;
		else {
			int index = url.indexOf(Symbol.QUESTION);
			if(index == url.length() - 1)
				return url + params;
			else if(index > -1)
				return url + Symbol.AND_MARK + params;
			else
				return url + Symbol.QUESTION + params;
		}
	}
	
	/**
	 * 判断是否为ajax请求
	 * @param request
	 * @return
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) { 
		    String header = request.getHeader("X-Requested-With");
		return header != null && "XMLHttpRequest".equals(header);
	}
	
	/**
	 * 判断是否为ajax请求
	 * @param request
	 * @return
	 */
	public static boolean isResponseJson(HttpServletRequest request) { 
		String accept = request.getHeader("Accept");
		return accept != null && accept.trim().startsWith("application/json");
	}
	
	/**
	 * 判断是否为ajax请求
	 * @param request
	 * @return
	 */
	public static boolean isResponseHtml(HttpServletRequest request) { 
		String accept = request.getHeader("Accept");
		return accept != null && accept.trim().startsWith("text/html");
	}
	
	/**
	 * 不存储cookie，浏览器关闭则删除cookie
	 * @param name
	 * @param value
	 * @param response
	 */
	public static void addCookie(String name, String value,
			HttpServletResponse response) {
		addCookie(name, value, null, response);
	}
	
	/**
	 * 
	 * @param name
	 * @param value
	 * @param maxAge	单位/秒
	 * @param response
	 */
	public static void addCookie(String name, String value, Integer maxAge,
			HttpServletResponse response) {
		if(!StringUtils.isEmpty(value)){
			try {
				//URLEncoder.encode会把空格换成'+'，所以需要转义下，不然前端JS decodeURIComponent会显示'+'号
				//或者直接可以使用Spring提供的工具类UriUtils.encode来进行处理，这个工具类不会把空格替换成'+'号
				value = URLEncoder.encode(value, "UTF-8").replace(Symbol.PLUS, "%20");
			} catch (UnsupportedEncodingException e) {
				log.error("HttpUtil class addCookie method error.",e);
			}
		}
		Cookie cookie = new Cookie(name, value);
		if(maxAge != null) 
			cookie.setMaxAge(maxAge);
		cookie.setPath(Symbol.SLASH);
		response.addCookie(cookie);
	}
	
	public static Cookie getCookie(String cookieName, 
			HttpServletRequest request) {
		if(request == null) return null;
		return WebUtils.getCookie(request, cookieName);
	}
	
	public static String getCookieVal(String cookieName, 
			HttpServletRequest request) {
		if(request == null) return null;
		Cookie c = WebUtils.getCookie(request, cookieName);
		if(c != null)
			return c.getValue() == null ? null : c.getValue().trim();
		return null;
	}
	
	public static <T> T getCookieVal(String cookieName, 
			HttpServletRequest request, Class<T> clazz) {
		if(request == null) return null;
		Cookie c = WebUtils.getCookie(request, cookieName);
		if(c != null && c.getValue() != null)
			return DataConvert.convertType(c.getValue().trim(), clazz);
		return null;
	}
	
	public static void removeCookie(String cookieName, 
			HttpServletResponse response) {
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setMaxAge(0);
		cookie.setPath(Symbol.SLASH);
		response.addCookie(cookie);
	}
	
	/**
	 * 把字符输出到客户端
	 * 
	 */
	public static void outString(String str, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.write(str);
		} catch (IOException e) {
			log.error("The method outString in HttpUtil:" + e.getMessage());
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
	
	/**
	 * 把对象输出到客户端
	 * 
	 */
	public static void outObject(Object obj, HttpServletResponse response) {
		try (JsonGenerator jsonGenerator = Jacksons.COMMUNICATE.getFactory()
				.createGenerator(response.getWriter())) {
			Jacksons.COMMUNICATE.writeValue(jsonGenerator, obj);
		} catch (Exception e) {
			log.error("The method outString in HttpUtil:" + e.getMessage());
		}
	}

	/**
	 * 以文本的格式输出
	 * 
	 */
	public static void outPlainString(String str, HttpServletResponse response) {
		response.setContentType("text/plain");
		outString(str, response);
	}
	
	/**
	 * 以文本的格式输出
	 * 
	 */
	public static void outPlainString(Object obj, HttpServletResponse response) {
		response.setContentType("text/plain");
		outObject(obj, response);
	}
	
	/**
	 * 以js格式输出
	 * 
	 */
	public static void outJs(String str, HttpServletResponse response) {
		response.setContentType("text/javascript;charset=UTF-8");
		outString(str, response);
	}
	
	/**
	 * 以js格式输出
	 * 
	 */
	public static void outJs(Object obj, HttpServletResponse response) {
		response.setContentType("text/javascript;charset=UTF-8");
		outObject(obj, response);
	}

	/**
	 * 以XML格式输出
	 * 
	 */
	public static void outXml(String str, HttpServletResponse response) {
		response.setContentType("application/xml;charset=UTF-8");
		outString(str, response);
	}
	
	/**
	 * 以XML格式输出
	 * 
	 */
	public static void outXml(Object obj, HttpServletResponse response) {
		response.setContentType("application/xml;charset=UTF-8");
		outObject(obj, response);
	}

	/**
	 * 以HTML格式输出
	 * 
	 */
	public static void outHtml(String str, HttpServletResponse response) {
		response.setContentType("text/html;charset=UTF-8");
		outString(str, response);
	}
	
	/**
	 * 以HTML格式输出JS,给str 头尾自动加上&lt;script type="text/javascript"&gt; &lt;/script&gt;
	 * 
	 */
	public static void outHtmlJs(String str, HttpServletResponse response) {
		response.setContentType("text/html;charset=UTF-8");
		StringBuilder sb = new StringBuilder()
			.append("<script type=\"text/javascript\">")
			.append(str)
			.append("</script>");
		outString(sb.toString(), response);
	}
	
	/**
	 * 以HTML格式输出
	 * 
	 */
	public static void outHtml(Object obj, HttpServletResponse response) {
		response.setContentType("text/html;charset=UTF-8");
		outObject(obj, response);
	}

	/**
	 * 以Json格式输出
	 * 
	 */
	public static void outJson(String str, HttpServletResponse response) {
		response.setContentType("application/json;charset=UTF-8");
		outString(str, response);
	}
	
	/**
	 * 以Json格式输出
	 * 
	 */
	public static void outJson(Object obj, HttpServletResponse response) {
		response.setContentType("application/json;charset=UTF-8");
		outObject(obj, response);
	}
	
	/**
	 * 下载文件
	 * @param srcFile	下载的源文件
	 * @param filename	下载到客户端的文件名
	 * @param response
	 * @throws IOException
	 */
	public static void download(File srcFile, String filename, HttpServletResponse response) throws IOException {
		try (FileInputStream is = new FileInputStream(srcFile)) {
			download(is, filename, response);
		}
	}
	
	/**
	 * 下载文件
	 * @param inputStream 输入流，此流需要自己关闭，此方法不会帮你关闭
	 * @param filename 下载到客户端的文件名
	 * @throws IOException 
	 * 
	 */
	public static void download(InputStream inputStream, String filename, HttpServletResponse response) throws IOException {
		Assert.notBlank(filename);
		String fn = filename;
		try {
			fn = new String(fn.getBytes("UTF-8"),"ISO-8859-1");
		} catch (UnsupportedEncodingException ignored) {
			
		}
		response.setContentType(MimeTypes.getMimeTypeByExt(FilenameUtils.getExtension(filename)));
		response.setHeader("Content-Disposition", "attachment;fileName=" + fn);
		try (OutputStream outputStream = response.getOutputStream()) {
			IOUtils.copy(inputStream, outputStream);
		}
	}

	public static class Request {
		
		/**
	     * HTTP 同步 Get请求 获取内容 ,charset 使用UTF-8
	     * @param url  请求的url地址
	     * @return    页面内容
		 * @throws ClientProtocolException 
		 * @throws IOException 
		 * @throws ExecutionException 
		 * @throws InterruptedException 
	     */
		public static String get(String url) throws IOException, InterruptedException, ExecutionException {
			return get(url, false);
		}
		
		/**
		 * HTTP Get 获取内容 ,charset 使用UTF-8
		 * @param url  请求的url地址
		 * @return    页面内容
		 * @throws ClientProtocolException 
		 * @throws IOException 
		 * @throws ExecutionException 
		 * @throws InterruptedException 
		 */
		public static String get(String url, boolean asyn) throws IOException, InterruptedException, ExecutionException {
			return get(url, asyn, null);
		}
		
		/**
	     * HTTP Get 获取内容 ,charset 使用UTF-8
	     * @param url  请求的url地址
	     * @param params 请求的参数
	     * @return    页面内容
		 * @throws ClientProtocolException 
		 * @throws IOException 
		 * @throws ExecutionException 
		 * @throws InterruptedException 
	     */
		public static String get(String url, boolean asyn, Map<String, ?> params) throws IOException, InterruptedException, ExecutionException {
			return get(url, asyn, params, null);
		}
		
		/**
	     * HTTP Get 获取内容 ,charset 使用UTF-8
	     * @param url  请求的url地址
	     * @param params 请求的参数
	     * @param jsonKeys 指定哪些key的值用json解析
	     * @return    页面内容
		 * @throws ClientProtocolException 
		 * @throws IOException 
		 * @throws ExecutionException 
		 * @throws InterruptedException 
	     */
		public static String get(String url, boolean asyn, Map<String, ?> params, Collection<Object> jsonKeys) 
				throws IOException, InterruptedException, ExecutionException {
			return get(url, asyn, params, jsonKeys, "UTF-8");
		}
		
		public static String get(final String url, boolean asyn,
				final Map<String, ?> params, final Collection<Object> jsonKeys,
				final String charset) throws IOException, InterruptedException, ExecutionException {
			if(asyn) {
				final ExecutorService e = Executors.newSingleThreadExecutor();
				Future<String> f = e.submit(new Callable<String>() {

					@Override
					public String call() throws Exception {
						try {
							return get(url, params, jsonKeys, charset);
						} catch (Exception e) {
							if(log.isErrorEnabled()) {
								log.error("get " + url + " request error.", e);
							}
							throw e;
						}
					}

				});
				e.shutdown();

				return f.get();
			}else
				return get(url, params, jsonKeys, charset);
			
		}

		/**
	     * HTTP Get 获取内容
	     * @param url  请求的url地址 
	     * @param params 请求的参数
	     * @param jsonKeys 指定哪些key的值用json解析
	     * @param charset    编码格式
	     * @return    页面内容
		 * @throws ClientProtocolException 
		 * @throws IOException 
	     */
	    public static String get(String url, Map<String, ?> params, Collection<Object> jsonKeys, String charset) 
	    		throws IOException {
	        if(StringUtils.isBlank(url)) return null;
	        if(params != null && !params.isEmpty()) {
	        	String paramStr = assembleParams(params, jsonKeys);
                url = addParams2Url(url, paramStr);
            }
	        HttpGet httpGet = new HttpGet(url);
	        return execute(httpGet, charset);
	    }
	    
	    /**
	     * HTTP同步 post请求 获取内容 ,charset 使用UTF-8
	     * @param url  请求的url地址
	     * @return    页面内容
		 * @throws ClientProtocolException 
		 * @throws IOException 
	     * @throws ExecutionException 
	     * @throws InterruptedException 
	     */
		public static String post(String url) throws IOException, InterruptedException, ExecutionException {
			return post(url, false);
		}
		
		/**
		 * HTTP post 获取内容 ,charset 使用UTF-8
		 * @param url  请求的url地址
		 * @return    页面内容
		 * @throws ClientProtocolException 
		 * @throws IOException 
		 * @throws ExecutionException 
		 * @throws InterruptedException 
		 */
		public static String post(String url, boolean asyn) throws IOException, InterruptedException, ExecutionException {
			return post(url, asyn, null);
		}
		
		/**
	     * HTTP post 获取内容 ,charset 使用UTF-8
	     * @param url  请求的url地址
	     * @param params 请求的参数
	     * @return    页面内容
		 * @throws ClientProtocolException 
		 * @throws IOException 
		 * @throws ExecutionException 
		 * @throws InterruptedException 
	     */
		public static String post(String url, boolean asyn, Map<String, ?> params) 
				throws IOException, InterruptedException, ExecutionException {
			return post(url, asyn, params, null);
		}
		
		/**
	     * HTTP post 获取内容 ,charset 使用UTF-8
	     * @param url  请求的url地址
	     * @param params 请求的参数
	     * @param jsonKeys 指定哪些key的值用json解析
	     * @return    页面内容
		 * @throws IOException
		 * @throws ExecutionException 
		 * @throws InterruptedException 
	     */
		public static String post(String url, boolean asyn, Map<String, ?> params, Collection<Object> jsonKeys) 
				throws IOException, InterruptedException, ExecutionException {
			return post(url, asyn, params, jsonKeys, "UTF-8");
		}
	    
		public static String post(final String url, boolean asyn, final Map<String, ?> params, final Collection<?> jsonKeys, final String charset) 
	    		throws IOException, InterruptedException, ExecutionException {
	    	if(asyn) {
	    		final ExecutorService e = Executors.newSingleThreadExecutor();
	    		Future<String> f = e.submit(new Callable<String>(){
	    			
	    			@Override
	    			public String call() throws Exception {
	    				try {
	    					return post(url, params, jsonKeys, charset);
						} catch (Exception e) {
							if(log.isErrorEnabled()) {
								log.error("post " + url + " request error.", e);
							}
							throw e;
						}
	    			}
	    			
	    		});
	    		e.shutdown();
	    		
	    		return f.get();
	    	}else
	    		return post(url, params, jsonKeys, charset);
	    }
		
	    /**
	     * HTTP Post 获取内容
	     * @param url  请求的url地址
	     * @param params 请求的参数
	     * @param charset    编码格式
	     * @return    页面内容
	     * @throws IOException 
	     */
	    public static String post(String url, Map<String, ?> params, Collection<?> jsonKeys, String charset) 
	    		throws IOException {
	        if(StringUtils.isBlank(url)) return null;
            List<NameValuePair> pairs = null;
            if(params != null && !params.isEmpty()) {
                pairs = new ArrayList<>();
                for(Map.Entry<String, ?> entry : params.entrySet()) {
                	String key = entry.getKey();
                	boolean isJsonKey = false;
                	if(jsonKeys != null && jsonKeys.size() > 0) {
                		for(Object jsonKey : jsonKeys) {
                			if(jsonKey != null && jsonKey.toString().equals(key)) {
                				isJsonKey = true;
                				break;
                			}
                		}
                	}
                    Object value = isJsonKey ? Jacksons.NON_NULL.toJson(entry.getValue()) 
                    		: entry.getValue();
                    if(value != null) {
                    	if(value.getClass().isArray()) {
    						Object[] vals = (Object[])value;
    	        			for(Object val : vals) {
    	        				if(val == null) continue;
    	        				pairs.add(new BasicNameValuePair(key, val.toString()));
    	        			}
    					}else if(value instanceof Collection) {
    						for(Object val : (Collection<?>)value) {
    							if(val == null) continue;
    							pairs.add(new BasicNameValuePair(key, val.toString()));
    	        			}
    					}else
    						pairs.add(new BasicNameValuePair(key, value.toString()));
                    }
                }
            }
            HttpPost httpPost = new HttpPost(url);
        	httpPost.setEntity(new UrlEncodedFormEntity(pairs, charset));
            return execute(httpPost, charset);
	    }
	    
	    /**
	     * 跨域上传需要再测试调通
	     * @param url
	     * @param files
	     * @param params
	     * @return
	     * @throws IOException
	     */
	    public static String multipart(String url, List<UploadFileInfo> files, Map<String, ?> params) 
	    		throws IOException {
	    	return multipart(url, files, params, "UTF-8");
	    }
	    /**
	     * 跨域上传需要再测试调通
	     * @param url
	     * @param files
	     * @param params
	     * @return
	     * @throws IOException
	     */
	    public static String multipart(String url, List<UploadFileInfo> files, Map<String, ?> params, String charset) 
	    		throws IOException {
	    	MultipartEntityBuilder builder = MultipartEntityBuilder.create()
	    				.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
	    				.setCharset(Charset.forName(charset));
	    	if(params != null && params.size() > 0) {
	    		Iterator<String> keys = params.keySet().iterator();
	    		while(keys.hasNext()) {
	    			String key = keys.next();
	    			Object value = params.get(key);
	    			if(value != null) {
                    	if(value.getClass().isArray()) {
    						Object[] vals = (Object[])value;
    	        			for(Object val : vals) {
    	        				if(val == null) continue;
    	        				builder.addTextBody(key, val.toString());
    	        			}
    					}else if(value instanceof Collection) {
    						for(Object val : (Collection<?>)value) {
    							if(val == null) continue;
    							builder.addTextBody(key, val.toString());
    	        			}
    					}else
    						builder.addTextBody(key, value.toString());
                    }
	    				
	    		}
	    	}
	    	
	    	if(files != null && files.size() > 0) {
	    		for(UploadFileInfo file : files) {
	    			Assert.notNull(file.getParamKey(), "paramKey can not be null.");
	    			Assert.isTrue(file.getFile() != null || file.getInputStream() != null, "file data is null.");
	    			String filename = file.getName() == null 
	    					? file.getFile() == null ? System.currentTimeMillis() + ".tmp" : file.getName() : file.getName();
	    			if(file.getInputStream() != null)
	    				builder.addBinaryBody(file.getParamKey(), file.getInputStream(), 
	    						ContentType.DEFAULT_BINARY, filename);
	    	    	else
	    	    		builder.addPart(file.getParamKey(), new FileBody(file.getFile(), ContentType.DEFAULT_BINARY, filename));
	    		}
	    	}
	    	HttpPost httpPost = new HttpPost(url);
        	httpPost.setEntity(builder.build());
            return execute(httpPost, charset);
	    }
	    
	    private static String execute(HttpUriRequest request, final String charset) throws ClientProtocolException, IOException {
	    	CloseableHttpClient httpClient = null;
            try {
            	httpClient = HttpClients.createDefault();
	            // Create a custom response handler
	            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

	                public String handleResponse(
	                        final HttpResponse response) throws ClientProtocolException, IOException {
	                    int status = response.getStatusLine().getStatusCode();
	                    if (status >= 200 && status < 300) {
	                        HttpEntity entity = response.getEntity();
	                        return entity != null ? EntityUtils.toString(entity, charset) : null;
	                    } else {
	                        throw new ClientProtocolException("Unexpected response status: " + status);
	                    }
	                }

	            };
	            return httpClient.execute(request, responseHandler);
	        } finally {
	        	if(httpClient != null)
	        		httpClient.close();
	        }
	    }
	    
	}
	
}
