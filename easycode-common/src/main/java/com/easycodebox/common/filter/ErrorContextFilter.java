package com.easycodebox.common.filter;

import com.easycodebox.common.CommonProperties;
import com.easycodebox.common.enums.DetailEnums;
import com.easycodebox.common.enums.entity.LogLevel;
import com.easycodebox.common.error.*;
import com.easycodebox.common.jackson.Jacksons;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.lang.reflect.Classes;
import com.easycodebox.common.log.slf4j.*;
import com.easycodebox.common.net.Https;
import com.easycodebox.common.web.callback.Callbacks;
import com.fasterxml.jackson.core.JsonGenerator;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 注：如果你的应用部署于Nginx后面，请确保Nginx不会拦截对应的status，不然永远只显示Nginx的错误页面。<br>
 * 如Nginx典型配置：
 * 	<pre>error_page   500 502 503 504  /50x.html;</pre>
 * 如果你Nginx有这样的配置，则500/502/503/504状态码只会显示Nginx自己的错误错误页面。
 * @author WangXiaoJin
 * 
 */
public class ErrorContextFilter implements Filter {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final String REDIRECT_FLAG = "redirect:";
	private static final String SEPARATOR_PATTERN = "[,\n]";
	
	private final String errorKey = "CODE_MSG";
	private final String storeExceptionKey = "EXCEPTION_INFO";
	
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
	
	private ExceptionHandler exceptionHandler;
	
	private CommonProperties commonProperties;

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
		
		if (Strings.isNotBlank(defaultPage)) {
			this.defaultPage = defaultPage.trim();
		}
		if (Strings.isNotBlank(defaultStatus)) {
			this.defaultStatus = Integer.parseInt(defaultStatus.trim());
		}
		if (Strings.isNotBlank(exceptionMappings)) {
			this.exceptionMappings = new HashMap<>(4);
			String[] frags = exceptionMappings.split(SEPARATOR_PATTERN);
			for (String frag : frags) {
				if (Strings.isNotBlank(frag)) {
					String[] vals = frag.trim().split(Symbol.EQ);
					if (vals.length == 2) {
						try {
							this.exceptionMappings.put(Classes.getClass(vals[0].trim()), vals[1].trim());
						} catch (ClassNotFoundException e) {
							log.error("Class not find.", e);
						}
					}
				}
			}
		}
		if (Strings.isNotBlank(statusMappings)) {
			this.statusMappings = new HashMap<>(4);
			String[] frags = statusMappings.split(SEPARATOR_PATTERN);
			for (String frag : frags) {
				if (Strings.isNotBlank(frag)) {
					String[] vals = frag.trim().split(Symbol.EQ);
					if (vals.length == 2) {
						this.statusMappings.put(vals[0].trim(), Integer.parseInt(vals[1].trim()));
					}
				}
			}
		}
		if (Strings.isNotBlank(logMappings)) {
			this.logMappings = new HashMap<>(4);
			String[] frags = logMappings.split(SEPARATOR_PATTERN);
			for (String frag : frags) {
				if (Strings.isNotBlank(frag)) {
					String[] vals = frag.trim().split(Symbol.EQ);
					if (vals.length == 2) {
						try {
							this.logMappings.put(Classes.getClass(vals[0].trim()), Boolean.parseBoolean(vals[1].trim()));
						} catch (ClassNotFoundException e) {
							log.error("Class not find.", e);
						}
					}
				}
			}
		}
		if (Strings.isNotBlank(isLog)) {
			this.isLog = Boolean.parseBoolean(isLog.trim());
		}
		if (Strings.isNotBlank(logLevel)) {
			LogLevel logLevelEnum = DetailEnums.deserialize(LogLevel.class, logLevel, false);
			if (logLevelEnum != null) {
				logLevelConfig.setLogLevel(logLevelEnum);
			}
		}
		if (Strings.isNotBlank(store)) {
			this.storeException = Boolean.parseBoolean(store.trim());
		}
		if (commonProperties == null) {
			commonProperties = (CommonProperties) filterConfig.getServletContext().getAttribute(CommonProperties.DEFAULT_NAME);
			commonProperties = commonProperties == null ? CommonProperties.instance() : commonProperties;
		}
	}
	
	@Override
	public void destroy() {
		
	}
	
	@SuppressWarnings("unchecked")
	private <T> T spyException(Throwable ex, Class<T> clazz) {
		Throwable th = ex;
		for (int i = 0; i < this.depth && th != null; i++) {
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
			
			if (exceptionHandler != null) {
				exceptionHandler.handle(ex);
			}
			
			if (judgeLog(ex)) {
				LogLevelException lle = spyException(ex, LogLevelException.class);
				if (lle != null) {
					//用户自定义log level打印日志
					lle.getLogLevelConfig().log(log, lle.getMessage(), lle);
				} else {
					logLevelConfig.log(log, ex.getMessage(), ex);
				}
			}
			
			ErrorContext ec = spyException(ex, ErrorContext.class);
			CodeMsg error;
			if(ec != null) {
				error = ec.getError();
				error = error == null ? CodeMsg.FAIL : error;
				if(Strings.isBlank(error.getCode()))
					error.code(CodeMsg.Code.FAIL_CODE);
				//因为前段JS需要首要显示服务器端返回的错误信息，如果此处设值，则错误信息始终显示Error.FAIL_MSG_INFO， 而不会显示JS定义的信息
				/*if(isBlank(error.getMsg()))
					error.msg(Error.FAIL_MSG_INFO);*/
			} else {
				error = CodeMsg.FAIL;
			}
			
			//异常信息赋值给data属性
			if (storeException) {
				request.setAttribute(storeExceptionKey, ex);
				//error = error.data(ex.getMessage());
			}
			
			//如果response已经关闭了，则直接返回
			if (response.isCommitted()) {
				return;
			}
			
			//判断请求是否为AJAX请求
			if(Https.isAjaxRequest(request) &&
					request.getHeader(commonProperties.getPjaxKey()) == null) {
				response.setContentType("application/json;charset=UTF-8");
				try (JsonGenerator jsonGenerator = Jacksons.NON_NULL.getFactory()
						.createGenerator(response.getWriter())) {
					Jacksons.NON_NULL.writeValue(jsonGenerator, error);
				} catch (Exception jsonEx) {
					log.error("Write JSON data error!", jsonEx);
					throw new BaseException("Could not write JSON: " + jsonEx.getMessage(), jsonEx);
				}
			} else {
				if(request.getParameter(commonProperties.getDialogReqKey()) != null) {
					
					Callbacks.callback(Callbacks.none(error), null, response);
				} else {
					//检索出异常页面
					String errorPage = obtainErrorPage(ex);
					if (Strings.isNotBlank(errorPage)) {
						if (errorPage.startsWith(REDIRECT_FLAG)) {
							//执行页面跳转
							response.sendRedirect(errorPage.replace(REDIRECT_FLAG, Symbol.EMPTY));
						} else {
							//相应错误页面
							response.setContentType("text/html;charset=UTF-8");
							String codeMsgStr = Jacksons.NON_NULL.toJson(error);
							Https.addCookie(errorKey, codeMsgStr, response);
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

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String getDefaultPage() {
		return defaultPage;
	}

	public void setDefaultPage(String defaultPage) {
		this.defaultPage = defaultPage;
	}

	public int getDefaultStatus() {
		return defaultStatus;
	}

	public void setDefaultStatus(int defaultStatus) {
		this.defaultStatus = defaultStatus;
	}

	public Map<Class<?>, String> getExceptionMappings() {
		return exceptionMappings;
	}

	public void setExceptionMappings(Map<Class<?>, String> exceptionMappings) {
		this.exceptionMappings = exceptionMappings;
	}

	public Map<String, Integer> getStatusMappings() {
		return statusMappings;
	}

	public void setStatusMappings(Map<String, Integer> statusMappings) {
		this.statusMappings = statusMappings;
	}

	public Map<Class<?>, Boolean> getLogMappings() {
		return logMappings;
	}

	public void setLogMappings(Map<Class<?>, Boolean> logMappings) {
		this.logMappings = logMappings;
	}

	public boolean isLog() {
		return isLog;
	}

	public void setLog(boolean isLog) {
		this.isLog = isLog;
	}

	public LogLevelConfig getLogLevelConfig() {
		return logLevelConfig;
	}

	public void setLogLevelConfig(LogLevelConfig logLevelConfig) {
		this.logLevelConfig = logLevelConfig;
	}

	public ExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	public boolean isStoreException() {
		return storeException;
	}

	public void setStoreException(boolean storeException) {
		this.storeException = storeException;
	}
	
	public CommonProperties getCommonProperties() {
		return commonProperties;
	}
	
	public void setCommonProperties(CommonProperties commonProperties) {
		this.commonProperties = commonProperties;
	}
}
