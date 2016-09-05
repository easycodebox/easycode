package com.easycodebox.login.util;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.jackson.Jacksons;
import com.easycodebox.common.net.HttpUtils;
import com.easycodebox.common.web.callback.Callbacks;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * @author WangXiaoJin
 *
 */
public class UrlAuthorizationFilter extends AuthorizationFilter {

	@Override
	protected boolean isAccessAllowed(ServletRequest request,
			ServletResponse response, Object mappedValue) throws Exception {
		HttpServletRequest req = (HttpServletRequest)request;
		Subject subject = getSubject(request, response);
		return !CollectionUtils.isEmpty(subject.getPrincipals()) 
					&& Permits.isPermitted(req);
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request,
			ServletResponse response) throws IOException {
		HttpServletResponse res = (HttpServletResponse)response;
		
		if(HttpUtils.isAjaxRequest((HttpServletRequest)request)) {
			response.setContentType("application/json;charset=UTF-8");
			try {
				JsonGenerator jsonGenerator = Jacksons.NON_NULL.getFactory()
						.createGenerator(response.getWriter());
				Jacksons.NON_NULL.writeValue(jsonGenerator, CodeMsg.FAIL.msg("您没有权限执行此操作"));
			} catch (Exception e) {
				throw new BaseException("Could not write JSON string.", e);
			}
		}else {
			if(request.getParameter(BaseConstants.DIALOG_REQ) != null) {
				Callbacks.callback(Callbacks.closeDialogQuiet(CodeMsg.FAIL.msg("您没有权限执行此操作")), null, res);
			}else
				super.onAccessDenied(request, response);
		}
		return false;
	}
	
}
