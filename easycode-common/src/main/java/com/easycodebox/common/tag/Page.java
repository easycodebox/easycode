package com.easycodebox.common.tag;

import com.easycodebox.common.CommonProperties;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.net.Https;
import org.apache.taglibs.standard.tag.common.core.ParamParent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.util.*;

/**
 * @author WangXiaoJin
 *
 */
public class Page extends AbstractHtmlTag implements ParamParent {
	
	private Integer pageNo;
	private Integer totalPage;
	private Integer showPageNum;
	private String url;
	private String cssClass;

	private String preOmit;
	private String sufOmit;

	private String firstPage;
	private String endPage;
	private String prePage;
	private String nextPage;

	private boolean antoAddParam;
	private Boolean traditionalHttp;
	
	/**
	 * 不对外暴露
	 */
	private Map<String, String> params;
	
	@Override
	protected void init() {
		id = "";
		pageNo = totalPage = 1;
		showPageNum = 5;
		cssClass = "pagination";
		preOmit = sufOmit = "<span>...</span>";
		firstPage = "第一页";
		endPage = "最后页";
		prePage = "上一页";
		nextPage = "下一页";
		antoAddParam = true;
		params = null;
		super.init();
	}
	
	@Override
	public void addParameter(String name, String value) {
		params.put(name, value);
	}

	@Override
	public int doStartTag() throws JspException {
		params = new LinkedHashMap<>();
		CommonProperties props = (CommonProperties) pageContext.findAttribute(CommonProperties.DEFAULT_NAME);
		props = props == null ? CommonProperties.instance() : props;
		traditionalHttp = traditionalHttp == null ? props.isTraditionalHttp() : traditionalHttp;
		return super.doStartTag();
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			StringBuilder sb = new StringBuilder(),
					paramStr= new StringBuilder();
			
			Set<String> keys = params.keySet();
			for (String key : keys) {
				paramStr.append(Symbol.AND_MARK).append(key).append(Symbol.EQ)
					.append(params.get(key));
			}
			
			//自动添加上次请求的参数
			if(antoAddParam) {
				HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
				String ps = Https.getRequestParams(request, 2, traditionalHttp, "pageNo");
				if(ps.length() > 0)
					paramStr.append(paramStr.length() > 0 ? Symbol.AND_MARK : Symbol.EMPTY).append(ps);
			}
			String param = paramStr.toString();
	
			if (totalPage != 0) {
				sb.append("<ol class='" + cssClass + "'>");
				//第一页
				sb.append("<li><a href='");
				sb.append(url);
				sb.append("?" + id + "pageNo=1" + param);
				sb.append("'>" + firstPage + "</a></li>");
				//上一页
				if(pageNo == 1) {
					sb.append("<li class='disabled'><a href='javascript:'>");
					sb.append(prePage);
					sb.append("</a></li>");
				}else {
					sb.append("<li><a href='");
					sb.append(url);
					sb.append("?" + id + "pageNo=" + (pageNo - 1) + param);
					sb.append("'>" + prePage + "</a></li>");
				}
				if (totalPage <= showPageNum) {
					//页码
					for (int i = 1; i <= totalPage; i++) {
						String current = "";
						if (i == pageNo) {
							current = " class='current' ";
						}
						sb.append("<li" + current + "><a href='");
						sb.append(url);
						sb.append("?" + id + "pageNo=" + i + param);
						sb.append("'>" + i + "</a></li>");
					}
				} else {
					int preHalf = showPageNum/2,	
						sufHalf = showPageNum%2 == 0 ? preHalf - 1: preHalf,
						start,end;
					if(pageNo - preHalf < 1) {
						start = 1;
						end = showPageNum;
					}else {
						if(pageNo + sufHalf > totalPage) {
							start = totalPage - showPageNum + 1;
							end = totalPage;
						}else {
							start = pageNo - preHalf;
							end = pageNo + sufHalf;
						}
					}
					
					//判断页码前需不需要增加（...）
					if(start > 1) {
						sb.append("<li>");
						sb.append(preOmit);
						sb.append("</li>");
					}
					//页码
					for (int i = start; i <= end; i++) {
						String current = "";
						if (i == pageNo) {
							current = " class='current' ";
						}
						sb.append("<li" + current + "><a href='");
						sb.append(url);
						sb.append("?" + id + "pageNo=" + i + param);
						sb.append("'>" + i + "</a></li>");
					}
					//判断页码后需不需要增加（...）
					if(end < totalPage) {
						sb.append("<li>");
						sb.append(sufOmit);
						sb.append("</li>");
					}
				}
				//下一页
				if(pageNo + 1 > totalPage) {
					sb.append("<li class='disabled'><a href='javascript:'>");
					sb.append(nextPage);
					sb.append("</a></li>");
				}else {
					sb.append("<li><a href='");
					sb.append(url);
					sb.append("?" + id + "pageNo=" + (pageNo + 1) + param);
					sb.append("'>" + nextPage + "</a></li>");
				}
				
				//最后页
				sb.append("<li><a href='");
				sb.append(url);
				sb.append("?" + id + "pageNo=" + totalPage + param);
				sb.append("'>" + endPage + "</a></li></ol>");
				
			}
			pageContext.getOut().append(sb);
		} catch (IOException ex) {
			log.error("page the data error!", ex);
			this.release();
			return SKIP_BODY;
		}
		
		return super.doEndTag();
	}
	
	public void setPageNo(Object pageNo) {
		this.pageNo = obtainVal(pageNo, Integer.class);
	}

	public void setTotalPage(Object totalPage) {
		this.totalPage = obtainVal(totalPage, Integer.class);
	}

	public void setUrl(String url) {
		this.url = obtainVal(url, String.class);
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public void setShowPageNum(Object showPageNum) {
		this.showPageNum = obtainVal(showPageNum, Integer.class);
	}

	public void setFirstPage(String firstPage) {
		this.firstPage = firstPage;
	}

	public void setEndPage(String endPage) {
		this.endPage = endPage;
	}

	public void setPrePage(String prePage) {
		this.prePage = prePage;
	}

	public void setNextPage(String nextPage) {
		this.nextPage = nextPage;
	}

	public void setPreOmit(String preOmit) {
		this.preOmit = preOmit;
	}

	public void setSufOmit(String sufOmit) {
		this.sufOmit = sufOmit;
	}

	public void setAntoAddParam(Object antoAddParam) {
		this.antoAddParam = obtainVal(antoAddParam, Boolean.class);
	}
	
	public Boolean getTraditionalHttp() {
		return traditionalHttp;
	}
	
	public void setTraditionalHttp(Boolean traditionalHttp) {
		this.traditionalHttp = traditionalHttp;
	}
}
