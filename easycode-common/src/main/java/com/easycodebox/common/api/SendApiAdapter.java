package com.easycodebox.common.api;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.error.CodeMsg;

/**
 * @author WangXiaoJin
 *
 */
public abstract class SendApiAdapter extends ApiAdapter {
	
	@Override
	public CodeMsg receive(Map<String, Object> params, HttpServletRequest request) throws Exception {
		throw new BaseException("不能处理发送请求");
	}

}
