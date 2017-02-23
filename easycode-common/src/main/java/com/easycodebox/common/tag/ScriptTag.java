package com.easycodebox.common.tag;

import com.easycodebox.common.config.CommonProperties;
import com.easycodebox.common.enums.DetailEnums;
import com.easycodebox.common.enums.entity.ProjectEnv;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.Symbol;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author WangXiaoJin
 *
 */
public class ScriptTag extends AbstractHtmlTag {
	
	private String type;
	private String src;
	/**
	 * src中多个文件地址的分隔符，默认为<b>,</b>
	 */
	private String separator;
	/**
	 * src中js参数的边界符号，默认为<b>??</b>
	 */
	private String boundary;
	private String charset;
	private String async;
	private String defer;
	
	/**
	 * 该标签的运行环境
	 */
	private ProjectEnv env;
	/**
	 * 是否把该标签的url添加.min后缀（转换成压缩后的url）<br>
	 * DEV 环境会忽略此属性
	 */
	private Boolean min;
	
	@Override
	protected void init() {
		type = "text/javascript";
		separator = Symbol.COMMA;
		boundary = "??";
		super.init();
	}
	
	@Override
	public int doStartTag() throws JspException {
		CommonProperties props = (CommonProperties) pageContext.findAttribute(CommonProperties.DEFAULT_NAME);
		props = props == null ? CommonProperties.instance() : props;
		env = env == null ? props.getProjectEnv() : env;
		min = min == null ? props.isTransMinJsCss() : min;
		
		StringBuilder sb = new StringBuilder("<script ");
		if(type != null)
			sb.append("type=\"").append(type).append("\" ");
		if(src != null) {
			src = src.replaceAll("\\s*", "");
			if(env != ProjectEnv.DEV && min) {
				//自动转换成压缩后的min.js
				src = src.replaceAll("(?<!\\.min)\\.js(?=" + Pattern.quote(separator) + "|\\?|$)", ".min.js");
			}
			sb.append("src=\"").append(env == ProjectEnv.DEV ? "{0}" : src).append("\" ");
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
			if(env == ProjectEnv.DEV) {
				String tag = sb.toString();
				String[] srcFrags = src.split(Pattern.quote(boundary));
				if(srcFrags.length == 1) {
					write.append(Strings.format(tag, src));
				}else {
					String[] files = srcFrags[1].split(Pattern.quote(separator));
					for(int i = 0; i < files.length; i++) {
						write.append(Strings.format(tag, srcFrags[0] + files[i]));
						if(i < files.length - 1)
							write.append("</script>");
					}
				}
				
			}else {
				write.append(sb.toString());
			}
		} catch (IOException e) {
			log.error("IOException.", e);
		}
		return EVAL_BODY_INCLUDE;
	}
	
	@Override
	public int doEndTag() throws JspException {
		try {
			pageContext.getOut().append("</script>");
		} catch (IOException e) {
			log.error("IOException.", e);
		}
		return super.doEndTag();
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public void setBoundary(String boundary) {
		this.boundary = boundary;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setAsync(String async) {
		this.async = async;
	}

	public void setDefer(String defer) {
		this.defer = defer;
	}

	public void setEnv(String env) {
		if (Strings.isNotBlank(env)) {
			this.env = DetailEnums.deserialize(ProjectEnv.class, env, false);
		}
	}

	public void setMin(String min) {
		if (Strings.isNotBlank(min)) {
			this.min = Boolean.parseBoolean(min);
		}
	}

}
