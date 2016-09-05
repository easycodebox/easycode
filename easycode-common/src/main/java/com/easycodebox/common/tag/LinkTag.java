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
public class LinkTag extends AbstractHtmlTag {
	
	private static final long serialVersionUID = -1586374485223953984L;

	private String rel;
	private String type;
	private String href;
	private String media;
	private String sizes;
	
	@Override
	protected void init() {
		rel = "stylesheet";
		type = "text/css";
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
			if(BaseConstants.projectEnv != ProjectEnv.DEV && BaseConstants.transMinJsCss) {
				//自动转换成压缩后的min.css
				href = href.replaceAll("(?<!\\.min)\\.css(?=,|\\?|$)", ".min.css");
			}
			sb.append("href=\"").append(BaseConstants.projectEnv == ProjectEnv.DEV ? "{0}" : href).append("\" ");
		}
		if(media != null)
			sb.append("media=\"").append(media).append("\" ");
		if(sizes != null)
			sb.append("sizes=\"").append(sizes).append("\" ");
		sb.append(super.generateHtml()).append("/>");
		
		try {
			JspWriter write = pageContext.getOut();
			if(BaseConstants.projectEnv == ProjectEnv.DEV) {
				String tag = sb.toString();
				String[] srcFrags = href.split("\\?\\?");
				if(srcFrags.length == 1) {
					write.append(StringUtils.format(tag, href));
				}else {
					String[] files = srcFrags[1].split(Symbol.COMMA);
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

	public void setMedia(String media) {
		this.media = media;
	}

	public void setSizes(String sizes) {
		this.sizes = sizes;
	}

}
