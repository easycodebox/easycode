package com.easycodebox.common.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.lang.RegularUtils;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.validate.Assert;

/**
 * @author WangXiaoJin
 *
 */
public class ImgUrl extends AbstractHtmlTag {
	
	private static final long serialVersionUID = -1586374485223953984L;

	private String root;
	private String url;
	private String mode;
	private String rule;
	private Boolean lazy;
	private Boolean imgTag;
	
	private String defaultImg = "imgs/util/blank.gif";
	
	@Override
	protected void init() {
		root = BaseConstants.imgUrl;
		mode = "ADD";
		lazy = false;
		imgTag = true;
		url = rule = null;
		super.init();
	}
	
	@Override
	public int doStartTag() throws JspException {
		
		Assert.notBlank(rule, "rule can't not be null.");
		
		url = StringUtils.isBlank(url) ? BaseConstants.Imgs.defaultImg : url;
		StringBuilder sb = new StringBuilder(root)
			.append(Symbol.SLASH);
		if(mode.equalsIgnoreCase("ADD"))
			sb.append(RegularUtils.addImgUrlRule(url, rule.split(Symbol.COMMA)));
		else if(mode.equalsIgnoreCase("REMOVE"))
			sb.append(RegularUtils.removeImgUrlRule(url, rule.split(Symbol.COMMA)));
		try {
			pageContext.getOut().append(processContent(sb.toString()));
		} catch (IOException e) {
			LOG.error("IOException.", e);
		}
		return EVAL_BODY_INCLUDE;
	}
	
	private String processContent(String imgUrl) {
		if(imgTag) {
			StringBuilder sb = new StringBuilder()
				.append("<img " + super.generateHtml())
				.append(lazy ? " lazy='" + imgUrl + "' src='" 
				+ defaultImg + "' " : " src='" + imgUrl + "' " )
				.append(" />");
			imgUrl = sb.toString();
		}
		return imgUrl;
	}

	public void setRoot(String root) {
		this.root = obtainVal(root, String.class);
	}

	public void setUrl(String url) {
		this.url = obtainVal(url, String.class);
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setRule(String rule) {
		this.rule = obtainVal(rule, String.class);
	}

	public void setLazy(Object lazy) {
		this.lazy = obtainVal(lazy, Boolean.class);
	}

	public void setImgTag(Object imgTag) {
		this.imgTag = obtainVal(imgTag, Boolean.class);
	}
	
}
