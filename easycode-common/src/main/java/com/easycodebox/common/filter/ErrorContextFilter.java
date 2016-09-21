package com.easycodebox.common.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.enums.Enums;
import com.easycodebox.common.enums.entity.LogLevel;
import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.error.ErrorContext;
import com.easycodebox.common.jackson.Jacksons;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.lang.reflect.ClassUtils;
import com.easycodebox.common.log.slf4j.LogLevelConfig;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.net.HttpUtils;
import com.easycodebox.common.web.callback.Callbacks;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * 
 * @author WangXiaoJin
 * 
 */
public class ErrorContextFilter implements Filter {
	
	private static final Logger LOG = LoggerFactory.getLogger(ErrorContextFilter.class);

	private static final String REDIRECT_FLAG = "redirect:";
	private static final String SEPARATOR_PATTERN = "[,\n]";
	
	private final String errorKey = "CODE_MSG";
	
	/**
	 * 检查异常类型的嵌套层次数。因为某些框架会把抛出的异常封装进cause属性中。
	 */
	private int depth = 2;
	
	private String defaultPage;
	
	private int defaultStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	
	/**
	 * 格式：com.xxx.SpecificException = redirect:/errors/500.html, com.xxx.AuthException = /errors/403.html <br>
	 * 分隔符可以是 <b>,</b> 或者 <b>\n</b> 
	 */
	private Map<Class<?>, String> exceptionMappings = Collections.emptyMap();
	
	/**
	 * 格式：/errors/500.html = 500, /errors/403.html = 403 <br>
	 * key对应exceptionMappings的value（page页面）<br>
	 * value对应http的status值 <br>
	 * 分隔符可以是 <b>,</b> 或者 <b>\n</b> 
	 */
	private Map<String, Integer> statusMappings = Collections.emptyMap();
	
	/**
	 * 判断是否需要打印指定的异常信息 <br>
	 * 格式：com.xxx.SpecificException = true, com.xxx.AuthException = false <br>
	 * 分隔符可以是 <b>,</b> 或者 <b>\n</b> 
	 */
	private Map<Class<?>, Boolean> logMappings = Collections.emptyMap();
	
	/**
	 * 是否打印捕获的异常信息
	 */
	private boolean isLog = true;
	
	/**
	 * 打印捕获的异常日志的日志级别，默认为ERROR
	 */
	private LogLevelConfig logLevelConfig = new LogLevelConfig();
	
	private boolean storeException = false;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String defaultPage = filterConfig.getInitParameter("defaultPage"),
				defaultStatus = filterConfig.getInitParameter("defaultStatus"),
				exceptionMappings = filterConfig.getInitParameter("exceptionMappings"),
				statusMappings = filterConfig.getInitParameter("statusMappings"),
				logMappings = filterConfig.getInitParameter("logMappings"),
				isLog = filterConfig.getInitParameter("isLog"),
				logLevel = filterConfig.getInitParameter("logLevel"),
				store = filterConfig.getInitParameter("storeException");
		
		if (StringUtils.isNotBlank(defaultPage)) {
			this.defaultPage = defaultPage.trim();
		}
		if (StringUtils.isNotBlank(defaultStatus)) {
			this.defaultStatus = Integer.parseInt(defaultStatus.trim());
		}
		if (StringUtils.isNotBlank(exceptionMappings)) {
			this.exceptionMappings = new HashMap<>(4);
			String[] frags = exceptionMappings.split(SEPARATOR_PATTERN);
			for (String frag : frags) {
				if (StringUtils.isNotBlank(frag)) {
					String[] vals = frag.trim().split(Symbol.EQ);
					if (vals.length == 2) {
						try {
							this.exceptionMappings.put(ClassUtils.getClass(vals[0].trim()), vals[1].trim());
						} catch (ClassNotFoundException e) {
							LOG.error("Class not find.", e);
						}
					}
				}
			}
		}
		if (StringUtils.isNotBlank(statusMappings)) {
			this.statusMappings = new HashMap<>(4);
			String[] frags = statusMappings.split(SEPARATOR_PATTERN);
			for (String frag : frags) {
				if (StringUtils.isNotBlank(frag)) {
					String[] vals = frag.trim().split(Symbol.EQ);
					if (vals.length == 2) {
						this.statusMappings.put(vals[0].trim(), Integer.parseInt(vals[1].trim()));
					}
				}
			}
		}
		if (StringUtils.isNotBlank(logMappings)) {
			this.logMappings = new HashMap<>(4);
			String[] frags = logMappings.split(SEPARATOR_PATTERN);
			for (String frag : frags) {
				if (StringUtils.isNotBlank(frag)) {
					String[] vals = frag.trim().split(Symbol.EQ);
					if (vals.length == 2) {
						try {
							this.logMappings.put(ClassUtils.getClass(vals[0].trim()), Boolean.parseBoolean(vals[1].trim()));
						} catch (ClassNotFoundException e) {
							LOG.error("Class not find.", e);
						}
					}
				}
			}
		}
		if (StringUtils.isNotBlank(isLog)) {
			this.isLog = Boolean.parseBoolean(isLog.trim());
		}
		if (StringUtils.isNotBlank(logLevel)) {
			LogLevel logLevelEnum = Enums.deserialize(LogLevel.class, logLevel, false);
			if (logLevelEnum != null) {
				logLevelConfig.setLogLevel(logLevelEnum);
			}
		}
		if (StringUtils.isNotBlank(store)) {
			this.storeException = Boolean.parseBoolean(store.trim());
		}
	}
	
	@Override
	public void destroy() {
		
	}
	
	@SuppressWarnings("unchecked")
	private <T> T spyException(Throwable ex, Class<T> clazz) {
		Throwable th = ex;
		for (int i = 0; i < this.depth; i++) {
			if (clazz.isAssignableFrom(th.getClass())) {
				return (T)th;
			}
			th = ex.getCause();
		}
		return null;
	}
	
	/**
	 * 根据异常获取对应的错误页面
	 * @param ex
	 * @return
	 */
	private String obtainErrorPage(Throwable ex) {
		for (Class<?> clazz : exceptionMappings.keySet()) {
			if (spyException(ex, clazz) != null) {
				return exceptionMappings.get(clazz);
			}
		}
		return this.defaultPage;
	}
	
	/**
	 * 判断是否需要打印日志
	 * @return
	 */
	public boolean judgeLog(Throwable ex) {
		for (Class<?> clazz : logMappings.keySet()) {
			if (spyException(ex, clazz) != null) {
				return logMappings.get(clazz);
			}
		}
		return this.isLog;
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(req, res);
		} catch(Throwable ex) {
			HttpServletRequest request = (HttpServletRequest)req; 
			HttpServletResponse response = (HttpServletResponse)res;
			
			if (judgeLog(ex)) {
				LogLevelException lle = spyException(ex, LogLevelException.class);
				if (lle != null) {
					//用户自定义log level打印日志
					lle.getLogLevelConfig().log(LOG, lle.getMessage(), lle);
				} else {
					logLevelConfig.log(LOG, ex.getMessage(), ex);
				}
			}
			
			ErrorContext ec = spyException(ex, ErrorContext.class);
			CodeMsg error = null;
			if(ec != null) {
				error = ec.getError();
				error = error == null ? CodeMsg.FAIL : error;
				if(StringUtils.isBlank(error.getCode())) 
					error.code(CodeMsg.Code.FAIL_CODE);
				//因为前段JS需要首要显示服务器端返回的错误信息，如果此处设值，则错误信息始终显示Error.FAIL_MSG_INFO， 而不会显示JS定义的信息
				/*if(isBlank(error.getMsg()))
					error.msg(Error.FAIL_MSG_INFO);*/
			} else {
				error = CodeMsg.FAIL;
			}
			
			//异常信息赋值给data属性
			if (storeException) {
				error = error.data(ex.getMessage());
			}
			
			//判断请求是否为AJAX请求
			if(HttpUtils.isAjaxRequest(request)) {
				response.setContentType("application/json;charset=UTF-8");
				try {
					JsonGenerator jsonGenerator = Jacksons.NON_NULL.getFactory()
							.createGenerator(response.getWriter());
					Jacksons.NON_NULL.writeValue(jsonGenerator, error);
				}catch (Exception jsonEx) {
					LOG.error("Write JSON data error!", jsonEx);
					throw new BaseException("Could not write JSON: " + jsonEx.getMessage(), jsonEx);
				}
			} else {
				if(request.getParameter(BaseConstants.DIALOG_REQ) != null) {
					
					Callbacks.callback(Callbacks.none(error), null, response);
				} else {
					//检索出异常页面
					String errorPage = obtainErrorPage(ex);
					if (StringUtils.isNotBlank(errorPage)) {
						if (errorPage.startsWith(REDIRECT_FLAG)) {
							//执行页面跳转
							response.sendRedirect(errorPage.replace(REDIRECT_FLAG, Symbol.EMPTY));
						} else {
							//相应错误页面
							response.setContentType("text/html;charset=UTF-8");
							String codeMsgStr = Jacksons.NON_NULL.toJson(error);
							HttpUtils.addCookie(errorKey, codeMsgStr, response);
							//设置status code
							Integer status = statusMappings.get(errorPage) == null ? defaultStatus : statusMappings.get(errorPage);
							response.setStatus(status);
							
							request.getRequestDispatcher(errorPage).forward(request, response);
						}
					} else {
						//没有合适的处理逻辑，抛出原有异常
						throw ex;
					}
				}
			}
		} finally {
			ErrorContext.instance().reset();
		}
	}
	
}
