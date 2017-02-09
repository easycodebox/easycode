package com.easycodebox.common.web.springmvc;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.error.*;
import com.easycodebox.common.jackson.Jacksons;
import com.easycodebox.common.net.Https;
import com.easycodebox.common.web.callback.Callbacks;
import com.fasterxml.jackson.core.JsonGenerator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * @author WangXiaoJin
 *
 */
public class DefaultMappingExceptionResolver extends SimpleMappingExceptionResolver {
	
	private String exceptionAttribute = DEFAULT_EXCEPTION_ATTRIBUTE;
	public static final String MSG_ATTR = "msg";
	
	@Override
	public ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, 
			Object handler, Exception ex) {
		
		CodeMsg error;
		if(ex instanceof ErrorContext) {
			error = ((ErrorContext)ex).getError();
			if(isBlank(error.getCode())) 
				error.code(CodeMsg.Code.FAIL_CODE);
			//因为前段JS需要首要显示服务器端返回的错误信息，如果此处设值，则错误信息始终显示Error.FAIL_MSG_INFO， 而不会显示JS定义的信息
			/*if(isBlank(error.getMsg()))
				error.msg(Error.FAIL_MSG_INFO);*/
		}else {
			error = CodeMsg.FAIL;
		}
		
		if(Https.isAjaxRequest(request)) {
			response.setContentType("application/json;charset=UTF-8");
			try (JsonGenerator jsonGenerator = Jacksons.NON_NULL.getFactory()
					.createGenerator(response.getWriter())) {
				Jacksons.NON_NULL.writeValue(jsonGenerator, error);
				return null;
			}catch (Exception jsonEx) {
				throw new BaseException("Could not write JSON: " + jsonEx.getMessage(), jsonEx);
			}
		}else {
			if(request.getParameter(BaseConstants.DIALOG_REQ) != null) {
				Callbacks.callback(Callbacks.none(error), null, response);
				return null;
			}
			//打印此日志是因为错误已被springMVC过滤掉，到达不了ErrorContextFilter拦截器中打印
			logger.error("Execute controller error.", ex);
			return super.doResolveException(request, response, handler, ex);
		}
	}
	
	@Override
	protected String determineViewName(Exception ex, HttpServletRequest request) {
		Object responseUrl = request.getAttribute(BaseConstants.responseUrlKey);
		if(responseUrl != null) {
			return responseUrl.toString();
		}else {
			return super.determineViewName(ex, request);
		}
	}
	
	@Override
	protected ModelAndView getModelAndView(String viewName, Exception ex) {
		ModelAndView mv = new ModelAndView(viewName);
		if (this.exceptionAttribute != null) {
			mv.addObject(this.exceptionAttribute, ex);
		}
		String errorMsg = CodeMsg.Msg.FAIL_MSG_INFO;
		if(ex instanceof ErrorContext) {
			CodeMsg error = ((ErrorContext)ex).getError();
			errorMsg = isBlank(error.getMsg()) ? errorMsg : error.getMsg();
		}
		mv.addObject(MSG_ATTR, errorMsg);
		return mv;
	}
	
}
