package com.easycodebox.common.filter;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.IOException;
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
import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.error.ErrorContext;
import com.easycodebox.common.freemarker.ConfigurationFactory;
import com.easycodebox.common.jackson.Jacksons;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.net.HttpUtils;
import com.easycodebox.common.web.callback.Callbacks;
import com.fasterxml.jackson.core.JsonGenerator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author WangXiaoJin
 * 
 */
public class ErrorContextFilter implements Filter {
	
	private static final Logger LOG = LoggerFactory.getLogger(ErrorContextFilter.class);
	
	private String errorView;
	private Configuration configuration;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.errorView = filterConfig.getInitParameter("errorView");
		if(configuration == null) {
			try {
				configuration = ConfigurationFactory.instance(filterConfig.getServletContext());
			} catch (TemplateException e) {
				LOG.error("Get Freemarker Configuration instance error!", e);
			}
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
				if(isBlank(error.getCode())) 
					error.code(CodeMsg.Code.FAIL_CODE);
				//因为前段JS需要首要显示服务器端返回的错误信息，如果此处设值，则错误信息始终显示Error.FAIL_MSG_INFO， 而不会显示JS定义的信息
				/*if(isBlank(error.getMsg()))
					error.msg(Error.FAIL_MSG_INFO);*/
			}else {
				error = CodeMsg.FAIL;
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
				} else if(configuration == null) {
					throw ex;
				} else {
					try {
						renderErrorView(error, ex, response);
					} catch (TemplateException e) {
						LOG.error("Load error template error!", e);
						throw new BaseException("process template error.", e);
					}
				}
			}
		}finally {
			ErrorContext.instance().reset();
		}
		
	}
	
	private void renderErrorView(CodeMsg error, Throwable ex, HttpServletResponse response) 
			throws IOException, TemplateException {
		response.setContentType("text/html;charset=UTF-8");
		Template template = configuration.getTemplate(errorView);
		Map<String, Object> dataModel = new HashMap<String, Object>();
		if(error != null) {
			if(error.getCode() != null)
				dataModel.put("code", error.getCode());
			if(error.getMsg() != null)
				dataModel.put("msg", error.getMsg());
		}
		dataModel.put("exception", ex);
		template.process(dataModel, response.getWriter());
	}
	
}
