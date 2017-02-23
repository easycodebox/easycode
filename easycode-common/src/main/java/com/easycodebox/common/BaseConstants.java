package com.easycodebox.common;

/**
 * @author WangXiaoJin
 * 
 */
public abstract class BaseConstants {
	
	/**
	 * response url参数key值
	 */
	public static final String RESPONSE_URL_KEY = "responseUrl";
	
	/**
	 * 用户信息的key值
	 */
	public static final String USER_KEY = "user_info";
	
	/**
	 * 项目功能菜单KEY值
	 */
	public static final String PROJECT_MENUS = "project_menus";
	
	/**
	 * 权限KEY值
	 */
	public static final String PERMISSION_KEY = "permission_key";
	
	/**
	 * 标记此次请求是弹出框发送的请求，controller返回callback(closeDialog(), response)格式的数据
	 */
	public static final String DIALOG_REQ_KEY = "DIALOG_REQ";
	
	/**
	 * pjax请求的header key值
	 */
	public static final String PJAX_KEY = "X-PJAX";
	
}
