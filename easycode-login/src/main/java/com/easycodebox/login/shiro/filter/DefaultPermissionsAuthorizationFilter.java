package com.easycodebox.login.shiro.filter;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.jackson.Jacksons;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.net.Https;
import com.easycodebox.common.web.callback.Callbacks;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 如果perms没有参数则等效于perms["user/load"]，其中的"user/load"为请求uri
 *
 * @author WangXiaoJin
 */
public class DefaultPermissionsAuthorizationFilter extends PermissionsAuthorizationFilter {
	
	private String pathDivider = Symbol.SLASH;
	
	/**
	 * 标记此请求为pjax的请求参数值
	 */
	private String pjaxKey;
	/**
	 * 标记此次请求是弹出框发送的请求，controller返回callback(closeDialog(), response)格式的数据
	 */
	private String dialogReqKey = BaseConstants.DIALOG_REQ_KEY;
	
	@Override
	public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
		if (mappedValue == null) {
			String path = Https.getShortPath((HttpServletRequest) request);
			if (!Symbol.SLASH.equals(pathDivider)) {
				path = path.replace(Symbol.SLASH, pathDivider);
			}
			if (Strings.isBlank(path)) {
				path = Symbol.SLASH;
			}
			mappedValue = new String[]{path};
		}
		return super.isAccessAllowed(request, response, mappedValue);
	}
	
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		if (Https.isAjaxRequest((HttpServletRequest) request) &&
				req.getHeader(pjaxKey == null ? BaseConstants.PJAX_KEY : pjaxKey) == null) {
			response.setContentType("application/json;charset=UTF-8");
			try (JsonGenerator jsonGenerator = Jacksons.NON_NULL.getFactory()
					.createGenerator(response.getWriter())) {
				Jacksons.NON_NULL.writeValue(jsonGenerator, CodeMsg.FAIL.msg("您没有权限执行此操作"));
			} catch (Exception e) {
				throw new BaseException("Could not write JSON string.", e);
			}
		} else {
			if (request.getParameter(dialogReqKey) != null) {
				Callbacks.callback(Callbacks.closeDialogQuiet(CodeMsg.FAIL.msg("您没有权限执行此操作")), null, res);
			} else
				super.onAccessDenied(request, response);
		}
		return false;
	}
	
	public String getPathDivider() {
		return pathDivider;
	}
	
	public void setPathDivider(String pathDivider) {
		this.pathDivider = pathDivider;
	}
	
	public String getPjaxKey() {
		return pjaxKey;
	}
	
	public void setPjaxKey(String pjaxKey) {
		this.pjaxKey = pjaxKey;
	}
	
	public String getDialogReqKey() {
		return dialogReqKey;
	}
	
	public void setDialogReqKey(String dialogReqKey) {
		this.dialogReqKey = dialogReqKey;
	}
	
}
