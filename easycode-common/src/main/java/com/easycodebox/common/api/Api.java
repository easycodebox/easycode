package com.easycodebox.common.api;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.easycodebox.common.error.CodeMsg;

/**
 * @author WangXiaoJin
 *
 */
public interface Api {

	/**
	 * 接受API请求
	 * @return
	 */
	CodeMsg receive(Map<String, Object> params, HttpServletRequest request) throws Exception;
	
	/**
	 * 发送API请求
	 * @return
	 */
	CodeMsg send(Map<String, Object> params) throws Exception;
	
}
