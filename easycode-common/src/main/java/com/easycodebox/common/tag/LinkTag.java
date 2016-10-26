package com.easycodebox.common.tag;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.enums.DetailEnums;
import com.easycodebox.common.enums.entity.ProjectEnv;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;

/**
 * @author WangXiaoJin
 *
 */
public class LinkTag extends AbstractHtmlTag {
	
	private static final long serialVersionUID = -1586374485223953984L;

	private String rel;
	private String type;
	private String href;
	/**
	 * src中多个文件地址的分隔符，默认为<b>,</b>
	 */
	private String separator;
	/**
	 * src中js参数的边界符号，默认为<b>??</b>
	 */
	private String boundary;
	private String media;
	private String sizes;
	
	/**
	 * 该标签的运行环境
	 */
	private ProjectEnv env;
	/**
	 * 是否把该标签的url添加.min后缀（转换成压缩后的url）<br>
	 * DEV 环境会忽略此属性
	 */
	private boolean min;
	
	@Override
	protected void init() {
		rel = "stylesheet";
		type = "text/css";
		separator = Symbol.COMMA;
		boundary = "??";
		env = BaseConstants.projectEnv;
		min = BaseConstants.transMinJsCss;
		super.init();
	}
	
	@Override
	public int doStartTag() throws JspException {
		
		StringBuilder sb = new StringBuilder("<link ");
		if(rel != null)
			sb.append("rel=\"").append(rel).append("\" ");
		if(type != null)
			sb.append("type=\"").append(type).append("\" ");
		if(href != null) {
			href = href.replaceAll("\\s*", "");
			if(env != ProjectEnv.DEV && min) {
				//自动转换成压缩后的min.css
				href = href.replaceAll("(?<!\\.min)\\.css(?=" + Pattern.quote(separator) + "|\\?|$)", ".min.css");
			}
			sb.append("href=\"").append(env == ProjectEnv.DEV ? "{0}" : href).append("\" ");
		}
		if(media != null)
			sb.append("media=\"").append(media).append("\" ");
		if(sizes != null)
			sb.append("sizes=\"").append(sizes).append("\" ");
		sb.append(super.generateHtml()).append("/>");
		
		try {
			JspWriter write = pageContext.getOut();
			if(env == ProjectEnv.DEV) {
				String tag = sb.toString();
				String[] srcFrags = href.split(Pattern.quote(boundary));
				if(srcFrags.length == 1) {
					write.append(StringUtils.format(tag, href));
				}else {
					String[] files = srcFrags[1].split(Pattern.quote(separator));
					for(String file : files) {
						write.append(StringUtils.format(tag, srcFrags[0] + file));
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
	
	public void setRel(String rel) {
		this.rel = rel;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public void setBoundary(String boundary) {
		this.boundary = boundary;
	}
	
	public void setMedia(String media) {
		this.media = media;
	}

	public void setSizes(String sizes) {
		this.sizes = sizes;
	}

	public void setEnv(String env) {
		if (StringUtils.isNotBlank(env)) {
			this.env = DetailEnums.deserialize(ProjectEnv.class, env, false);
		}
	}

	public void setMin(String min) {
		if (StringUtils.isNotBlank(min)) {
			this.min = Boolean.parseBoolean(min);
		}
	}

}
