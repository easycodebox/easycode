package com.easycodebox.common.tag;

import javax.servlet.jsp.JspException;

/**
 * @author WangXiaoJin
 * 
 */
public class ElseEnum extends TagExt {
	
	@Override
	protected void init() {
		super.init();
	}
	
	@Override
	public int doStartTag() throws JspException {
		Boolean ifResult = (Boolean) pageContext.getAttribute(IfEnum.ANSWER);
        pageContext.removeAttribute(IfEnum.ANSWER);

        return ifResult == null || ifResult ? SKIP_BODY : EVAL_BODY_INCLUDE;
	}
	
}
