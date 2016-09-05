package com.easycodebox.common.net;

import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 因为HttpServletRequestWrapper类中的参数数据是只读的不能修改，所以需要ParameterRequestWrapper 来封装实现参数可编辑。
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ParameterRequestWrapper extends HttpServletRequestWrapper {
	
	private Map params;

	public ParameterRequestWrapper(HttpServletRequest request, Map params) {
		super(request);
		this.params = params;
	}

	@Override
	public Map getParameterMap() {
		return params;
	}

	@Override
	public Enumeration getParameterNames() {
		Vector l = new Vector(params.keySet());
		return l.elements();
	}

	@Override
	public String[] getParameterValues(String name) {
		Object v = params.get(name);
		if (v == null) {
			return null;
		} else if (v instanceof String[]) {
			return (String[]) v;
		} else if (v instanceof String) {
			return new String[] { (String) v };
		} else {
			return new String[] { v.toString() };
		}
	}

	@Override
	public String getParameter(String name) {
		Object v = params.get(name);
		if (v == null) {
			return null;
		} else if (v instanceof String[]) {
			String[] strArr = (String[]) v;
			if (strArr.length > 0) {
				return strArr[0];
			} else {
				return null;
			}
		} else if (v instanceof String) {
			return (String) v;
		} else {
			return v.toString();
		}
	}
	
}