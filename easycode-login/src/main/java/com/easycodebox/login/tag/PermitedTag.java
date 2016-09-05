package com.easycodebox.login.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.tag.TagExt;
import com.easycodebox.login.util.Permits;

/**
 * @author WangXiaoJin
 * 
 */
public class PermitedTag extends TagExt {
	
	private static final long serialVersionUID = 8842670845328539115L;

	private String value;
	
	@Override
	protected void init() {
		value = null;
		super.init();
	}
	
	@Override
	public int doStartTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		return StringUtils.isBlank(value) || Permits.isPermitted(value, request) 
				? EVAL_BODY_INCLUDE : SKIP_BODY;
	}

	public void setValue(String value) {
		this.value = obtainVal(value, String.class);
	}
	
}
