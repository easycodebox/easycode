package com.easycodebox.common.tag;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.taglibs.standard.tag.common.core.ParamParent;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.net.HttpUtils;
import com.easycodebox.common.validate.Assert;

/**
 * 缓存当前的URL到sessionStorage
 * @author WangXiaoJin
 *
 */
public class CacheUrl extends TagExt implements ParamParent {
	
	private static final long serialVersionUID = 1L;
	
	private Boolean condition;
	private String content;
	private Map<String, String> extraParams = new LinkedHashMap<String, String>(4);
	private String[] excludeParams;
	
	@Override
	protected void init() {
		condition = null;
		content = "<script type=\"text/javascript\">%n" + 
					"try {%n" +
					"	if(utils && utils.cacheUrl) {%n" +
					"		utils.cacheUrl(\"%1$s\");%n" +
					"	}else {%n" +
					"		throw Error(\"There is no utils.cacheUrl method.\");%n" +
					"	}%n" +
					"}catch(e) {%n" +
					"	(function() {%n" +
					"		var original = null;%n" +
					"		if(\"[object Function]\" === Object.prototype.toString.call(window.utilsReady)) {%n" +
					"			original = utilsReady;%n" +
					"		}%n" +
					"		window.utilsReady = function() {%n" +
					"			if(original) {%n" +
					"				original();%n" +
					"			}%n" +
					"			utils.cacheUrl(\"%1$s\");%n" +
					"		};%n" +
					"	})();%n" +
					"}%n" +
					"</script>";
		extraParams = null;
		excludeParams = null;
		super.init();
	}
	
	@Override
	public int doStartTag() throws JspException {
		if(condition != null && !condition) {
			return SKIP_BODY;
		}
		extraParams = new LinkedHashMap<String, String>();
		return super.doStartTag();
	}

	@Override
	public int doEndTag() throws JspException {
		Assert.notNull(content);
		StringBuilder params = new StringBuilder();
		if(extraParams.size() > 0) {
			Set<String> keys = extraParams.keySet();
			for (String key : keys) {
				params.append(Symbol.AND_MARK).append(key).append(Symbol.EQ)
					.append(extraParams.get(key));
			}
		}
		
		try {
			//添加当前请求的参数
			HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
			String url = HttpUtils.getFullRequestUrl(request, 2, BaseConstants.httpParamTradition, excludeParams);
			url = HttpUtils.addParams2Url(url, params.toString());
			pageContext.getOut().append(String.format(content, url));
		} catch (IOException e) {
			log.error("TextCut Tag processing error.", e);
			this.release();
			return SKIP_BODY;
		}
		
		return super.doEndTag();
	}
	
	@Override
	public void addParameter(String name, String value) {
		extraParams.put(name, value);
	}

	public Boolean getCondition() {
		return condition;
	}

	public void setCondition(Boolean condition) {
		this.condition = condition;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setExcludeParams(String params) {
		if(StringUtils.isNotBlank(params)) {
			this.excludeParams = params.trim().split("\\s*" + Symbol.COMMA + "\\s*");
		}
	}
	
}
