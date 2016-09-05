package com.easycodebox.upload.servlet;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.jackson.Jacksons;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.net.HttpUtils;
import com.easycodebox.common.validate.Assert;

/**
 * @author WangXiaoJin
 * 
 */
public abstract class BaseServlet extends HttpServlet {

	private static final long serialVersionUID = -1064465770862657698L;
	
	protected final Logger LOG = LoggerFactory.getLogger(getClass());
	
	protected void outData(CodeMsg error, String responseUrl, HttpServletResponse resp) throws IOException {
		Assert.notNull(error);
		if(StringUtils.isBlank(responseUrl))
			HttpUtils.outHtml(error, resp);
		else {
			//responseUrl 为跨域上传图片的解决方案
			String backData = "back_data=" + URLEncoder.encode(Jacksons.COMMUNICATE.writeValueAsString(error), "UTF-8");
			responseUrl = responseUrl.indexOf(Symbol.QUESTION) == -1 ? responseUrl + Symbol.QUESTION + backData : 
				responseUrl.endsWith(Symbol.AND_MARK) ? responseUrl + backData : responseUrl + Symbol.AND_MARK + backData;
			resp.sendRedirect(responseUrl);
		}
	}
	
}
