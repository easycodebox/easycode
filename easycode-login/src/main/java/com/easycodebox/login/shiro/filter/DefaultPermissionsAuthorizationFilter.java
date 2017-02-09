package com.easycodebox.login.shiro.filter;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.easycodebox.common.lang.Strings;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.jackson.Jacksons;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.net.Https;
import com.easycodebox.common.web.callback.Callbacks;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * 如果perms没有参数则等效于perms["user/load"]，其中的"user/load"为请求uri
 * @author WangXiaoJin
 *
 */
public class DefaultPermissionsAuthorizationFilter extends PermissionsAuthorizationFilter {

	private String pathDivider = Symbol.SLASH;
	
	@Override
	public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
		if (mappedValue == null) {
			String path = Https.getShortPath((HttpServletRequest)request);
			if (path.startsWith(Symbol.SLASH)) {
				path = path.substring(1);
			}
			if (!Symbol.SLASH.equals(pathDivider)) {
				path = path.replace(Symbol.SLASH, pathDivider);
			}
			//如果path等于空字符窜，则意味着此请求访问项目的根目录；这里我们排除掉这种情况，因为访问项目的根目录的权限由DefaultCasRealm中控制
			if (Strings.isNotBlank(path)) {
				mappedValue = new String[]{path};
			}
		}
		return super.isAccessAllowed(request, response, mappedValue);
	}
	
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
		HttpServletResponse res = (HttpServletResponse)response;
		
		if(Https.isAjaxRequest((HttpServletRequest)request)) {
			response.setContentType("application/json;charset=UTF-8");
			try (JsonGenerator jsonGenerator = Jacksons.NON_NULL.getFactory()
					.createGenerator(response.getWriter())) {
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

	public String getPathDivider() {
		return pathDivider;
	}

	public void setPathDivider(String pathDivider) {
		this.pathDivider = pathDivider;
	}
	
}
