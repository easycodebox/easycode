package com.easycodebox.common.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.enums.entity.ProjectEnv;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;

/**
 * @author WangXiaoJin
 *
 */
public class ScriptTag extends AbstractHtmlTag {
	
	private static final long serialVersionUID = -1586374485223953984L;

	private String type;
	private String src;
	private String charset;
	private String async;
	private String defer;
	
	@Override
	protected void init() {
		type = "text/javascript";
		super.init();
	}
	
	@Override
	public int doStartTag() throws JspException {
		
		StringBuilder sb = new StringBuilder("<script ");
		if(type != null)
			sb.append("type=\"").append(type).append("\" ");
		if(src != null) {
			src = src.replaceAll("\\s*", "");
			if(BaseConstants.projectEnv != ProjectEnv.DEV && BaseConstants.transMinJsCss) {
				//自动转换成压缩后的min.js
				src = src.replaceAll("(?<!\\.min)\\.js(?=,|\\?|$)", ".min.js");
			}
			sb.append("src=\"").append(BaseConstants.projectEnv == ProjectEnv.DEV ? "{0}" : src).append("\" ");
		}
		if(charset != null)
			sb.append("charset=\"").append(charset).append("\" ");
		if(async != null)
			sb.append("async=\"").append(async).append("\" ");
		if(defer != null)
			sb.append("defer=\"").append(defer).append("\" ");
		sb.append(super.generateHtml()).append(">");
		
		try {
			JspWriter write = pageContext.getOut();
			if(BaseConstants.projectEnv == ProjectEnv.DEV) {
				String tag = sb.toString();
				String[] srcFrags = src.split("\\?\\?");
				if(srcFrags.length == 1) {
					write.append(StringUtils.format(tag, src));
				}else {
					String[] files = srcFrags[1].split(Symbol.COMMA);
					for(int i = 0; i < files.length; i++) {
						write.append(StringUtils.format(tag, srcFrags[0] + files[i]));
						if(i < files.length - 1)
							write.append("</script>");
					}
				}
				
			}else {
				write.append(sb.toString());
			}
		} catch (IOException e) {
			LOG.error("IOException.", e);
		}
		return EVAL_BODY_INCLUDE;
	}
	
	@Override
	public int doEndTag() throws JspException {
		try {
			pageContext.getOut().append("</script>");
		} catch (IOException e) {
			LOG.error("IOException.", e);
		}
		return super.doEndTag();
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setAsync(String async) {
		if(StringUtils.isNotBlank(async)) {
			async = async.toLowerCase();
			if("async".equals(async) || "true".equals(async))
				this.async = "async";
		}
	}

	public void setDefer(String defer) {
		if(StringUtils.isNotBlank(defer)) {
			defer = defer.toLowerCase();
			if("defer".equals(defer) || "true".equals(defer))
				this.defer = "defer";
		}
	}
	
}
