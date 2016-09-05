package com.easycodebox.common.api;

import java.util.Map;

import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.error.CodeMsg;

/**
 * @author WangXiaoJin
 *
 */
public abstract class ReceiveApiAdapter extends ApiAdapter {
	
	@Override
	public CodeMsg send(Map<String, Object> params) throws Exception {
		throw new BaseException("不能处理发送请求");
	}

}
