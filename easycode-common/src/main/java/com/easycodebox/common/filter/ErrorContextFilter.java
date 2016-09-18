package com.easycodebox.common.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.error.ErrorContext;
import com.easycodebox.common.jackson.Jacksons;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.net.HttpUtils;
import com.easycodebox.common.web.callback.Callbacks;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * @author WangXiaoJin
 * 
 */
public class ErrorContextFilter implements Filter {
	
	private static final Logger LOG = LoggerFactory.getLogger(ErrorContextFilter.class);

	private final String errorKey = "CODE_MSG";
	
	private String errorPage;
	private boolean storeException = false;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.errorPage = filterConfig.getInitParameter("errorPage");
		String store = filterConfig.getInitParameter("storeException");
		if (StringUtils.isNotBlank(store)) {
			storeException = Boolean.parseBoolean(store);
		}
	}
	
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		try{
			chain.doFilter(req, res);
		}catch(Throwable ex) {
			HttpServletRequest request = (HttpServletRequest)req; 
			HttpServletResponse response = (HttpServletResponse)res;
			
			ErrorContext ec = ex instanceof ErrorContext ? (ErrorContext)ex
					: ex.getCause() instanceof ErrorContext ? (ErrorContext)ex.getCause() : null;
					
			if(ec != null) {
				ec.log(LOG, ec.getMessage(), ec);
			}else {
				//如果此异常不是ValidateError则打印ERROR
				LOG.error(ex.getMessage(), ex);
			}
			
			CodeMsg error = null;
			if(ec != null) {
				error = ec.getError();
				error = error == null ? CodeMsg.FAIL : error;
				if(StringUtils.isBlank(error.getCode())) 
					error.code(CodeMsg.Code.FAIL_CODE);
				//因为前段JS需要首要显示服务器端返回的错误信息，如果此处设值，则错误信息始终显示Error.FAIL_MSG_INFO， 而不会显示JS定义的信息
				/*if(isBlank(error.getMsg()))
					error.msg(Error.FAIL_MSG_INFO);*/
			}else {
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
				} else if (StringUtils.isNotBlank(errorPage)) {
					response.setContentType("text/html;charset=UTF-8");
					String codeMsgStr = Jacksons.NON_NULL.toJson(error);
					HttpUtils.addCookie(errorKey, codeMsgStr, response);
					request.getRequestDispatcher(errorPage).forward(request, response);
				}
			}
		} finally {
			ErrorContext.instance().reset();
		}
	}
	
}
