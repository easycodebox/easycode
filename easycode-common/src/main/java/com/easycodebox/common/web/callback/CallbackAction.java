package com.easycodebox.common.web.callback;

/**
 * @author WangXiaoJin
 *
 */
public class CallbackAction {
	
	/**
	 * 跳转到指定地址
	 */
	public static final String FORWARD = "forward";
	
	/**
	 * 刷新当前页面
	 */
	public static final String FLUSH_CUR = "flush_cur";
	
	/**
	 * 刷新历史页面
	 */
	public static final String FLUSH_HIS = "flush_his";

	/**
	 * 传的请求参数有错误
	 */
	public static final String PARAM_ERROR = "param_error";
	
	/**
	 * 不执行任何动作
	 */
	public static final String NONE = "none";
	
	
	
	/* --------------- 只用于dialog的表单页面 ------------------- */
	/**
	 * 关闭对话框后不刷新当前页面，只用于dialog的表单页面
	 */
	public static final String CLOSE_DIALOG_QUIET = "close_dialog_quiet";
	
	/**
	 * 关闭对话框后刷新当前的页面，只用于dialog的表单页面
	 */
	public static final String CLOSE_DIALOG = "close_dialog";
	
}
