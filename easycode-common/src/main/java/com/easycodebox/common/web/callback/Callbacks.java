package com.easycodebox.common.web.callback;

import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.net.Https;

import javax.servlet.http.HttpServletResponse;

/**
 * @author WangXiaoJin
 *
 */
public class Callbacks {
	
	//private static final Logger log = LoggerFactory.getLogger(Callbacks.class);
	
	/**
	 * 获取回调对象，此回调对象为参数错误
	 * @return
	 */
	public static CallbackData paramError(CallbackData callback) {
		return callback.setAction(CallbackAction.PARAM_ERROR);
	}
	
	/**
	 * 获取回调对象，此回调对象为参数错误
	 * @return
	 */
	public static CallbackData paramError(CodeMsg error) {
		return CallbackData.instance(error).setAction(CallbackAction.PARAM_ERROR);
	}
	
	/**
	 * 获取回调对象，此回调对象为参数错误
	 * @return
	 */
	public static CallbackData paramError(String msg) {
		return CallbackData.instance(CallbackAction.PARAM_ERROR, CodeMsg.Code.FAIL_CODE, msg);
	}
	
	/**
	 * 跳转到指定的url
	 * @return
	 */
	public static CallbackData forward(CallbackData callback) {
		return callback.setAction(CallbackAction.FORWARD);
	}
	
	/**
	 * 获取回调对象，跳转到指定的url，且code=suc
	 * @return
	 */
	public static CallbackData forward(String url) {
		return CallbackData.instance(CallbackAction.FORWARD
				, CodeMsg.SUC, url);
	}
	
	/**
	 * 获取回调对象，跳转到指定的url，且code=suc
	 * @return
	 */
	public static CallbackData forward(String url, Object data) {
		return CallbackData.instance(CallbackAction.FORWARD
				, CodeMsg.SUC.data(data), url);
	}
	
	/**
	 * 获取回调对象，跳转到相对应的list，且code=suc
	 * @return
	 */
	public static CallbackData forward() {
		return CallbackData.instance(CallbackAction.FORWARD, CodeMsg.SUC);
	}
	
	/**
	 * 页面不执行任何动作，如果code=suc 弹出form表单定义好的suc信息，
	 * 如果code=fail 弹出form表单定义好的fail信息。
	 * 如果form表单中没有对应的信息则不弹出信息
	 * @return
	 */
	public static CallbackData none(String code) {
		return none(code, null, null);
	}
	
	/**
	 * 页面不执行任何动作，如果code=suc 弹出form表单定义好的suc信息，
	 * 如果code=fail 弹出form表单定义好的fail信息。
	 * 如果form表单中没有对应的信息则不弹出信息
	 * @return
	 */
	public static CallbackData none(CallbackData callback) {
		return callback.setAction(CallbackAction.NONE);
	}
	
	/**
	 * 页面不执行任何动作，弹出信息为msg。
	 * 若果msg == null ，则逻辑和none(String code)一样
	 * @return
	 */
	public static CallbackData none(CodeMsg error) {
		return CallbackData.instance(error).setAction(CallbackAction.NONE);
	}
	
	/**
	 * 页面不执行任何动作，弹出信息为msg。
	 * 若果msg == null ，则逻辑和none(String code)一样
	 * @return
	 */
	public static CallbackData none(String code, String msg) {
		return none(code, msg, null);
	}
	
	/**
	 * 页面不执行任何动作，弹出信息为msg。
	 * 若果msg == null ，则逻辑和none(String code)一样
	 * @return
	 */
	public static CallbackData none(String code, String msg, Object data) {
		return CallbackData.instance(CallbackAction.NONE, 
				CodeMsg.NONE.codeMsg(code, msg).data(data));
	}
	
	/**
	 * 刷新当前页面
	 * @return
	 */
	public static CallbackData flushCur(CallbackData callback) {
		return callback.setAction(CallbackAction.FLUSH_CUR);
	}
	
	/**
	 * 刷新当前页面
	 * @return
	 */
	public static CallbackData flushCur(String code) {
		return CallbackData.instance(CallbackAction.FLUSH_CUR, code, null);
	}
	
	/**
	 * 刷新当前页面
	 * @return
	 */
	public static CallbackData flushCur(CodeMsg error) {
		return CallbackData.instance(error).setAction(CallbackAction.FLUSH_CUR);
	}
	
	/**
	 * 刷新当前页面
	 * @return
	 */
	public static CallbackData flushCur(String code, String msg) {
		return CallbackData.instance(CallbackAction.FLUSH_CUR, code, msg);
	}
	
	/**
	 * 刷新上级页面, code=suc
	 * @return
	 */
	public static CallbackData flushHis() {
		return CallbackData.instance(CallbackAction.FLUSH_HIS, CodeMsg.SUC);
	}
	
	/**
	 * 刷新上级页面
	 * @return
	 */
	public static CallbackData flushHis(CallbackData callback) {
		return callback.setAction(CallbackAction.FLUSH_HIS);
	}
	
	/**
	 * 刷新上级页面
	 * @return
	 */
	public static CallbackData flushHis(String code) {
		return CallbackData.instance(CallbackAction.FLUSH_HIS, code, null);
	}
	
	/**
	 * 刷新上级页面
	 * @return
	 */
	public static CallbackData flushHis(CodeMsg error) {
		return CallbackData.instance(error).setAction(CallbackAction.FLUSH_HIS);
	}
	
	/**
	 * 刷新上级页面
	 * @return
	 */
	public static CallbackData flushHis(String code, String msg) {
		return CallbackData.instance(CallbackAction.FLUSH_HIS, code, msg);
	}
	
	/**
	 * 关闭对话框后不刷新当前页面，只用于dialog的表单页面, code=suc
	 * @return
	 */
	public static CallbackData closeDialogQuiet() {
		return CallbackData.instance(CallbackAction.CLOSE_DIALOG_QUIET, CodeMsg.SUC);
	}
	
	/**
	 * 关闭对话框后不刷新当前页面，只用于dialog的表单页面
	 * @return
	 */
	public static CallbackData closeDialogQuiet(CallbackData callback) {
		return callback.setAction(CallbackAction.CLOSE_DIALOG_QUIET);
	}
	
	/**
	 * 关闭对话框后不刷新当前页面，只用于dialog的表单页面
	 * @return
	 */
	public static CallbackData closeDialogQuiet(String code) {
		return CallbackData.instance(CallbackAction.CLOSE_DIALOG_QUIET, code, null);
	}
	
	/**
	 * 关闭对话框后不刷新当前页面，只用于dialog的表单页面
	 * @return
	 */
	public static CallbackData closeDialogQuiet(CodeMsg error) {
		return CallbackData.instance(error).setAction(CallbackAction.CLOSE_DIALOG_QUIET);
	}
	
	/**
	 * 关闭对话框后不刷新当前页面，只用于dialog的表单页面
	 * @return
	 */
	public static CallbackData closeDialogQuiet(String code, String msg) {
		return CallbackData.instance(CallbackAction.CLOSE_DIALOG_QUIET, code, msg);
	}
	
	/**
	 * 关闭对话框后刷新当前的页面，只用于dialog的表单页面, code=suc
	 * @return
	 */
	public static CallbackData closeDialog() {
		return CallbackData.instance(CallbackAction.CLOSE_DIALOG, CodeMsg.SUC);
	}
	
	/**
	 * 关闭对话框后刷新当前的页面，只用于dialog的表单页面
	 * @return
	 */
	public static CallbackData closeDialog(CallbackData callback) {
		return callback.setAction(CallbackAction.CLOSE_DIALOG);
	}
	
	/**
	 * 关闭对话框后刷新当前的页面，只用于dialog的表单页面
	 * @return
	 */
	public static CallbackData closeDialog(String code) {
		return CallbackData.instance(CallbackAction.CLOSE_DIALOG, code, null);
	}
	
	/**
	 * 关闭对话框后刷新当前的页面，只用于dialog的表单页面
	 * @return
	 */
	public static CallbackData closeDialog(CodeMsg error) {
		return CallbackData.instance(error).setAction(CallbackAction.CLOSE_DIALOG);
	}
	
	/**
	 * 关闭对话框后刷新当前的页面，只用于dialog的表单页面
	 * @return
	 */
	public static CallbackData closeDialog(String code, String msg) {
		return CallbackData.instance(CallbackAction.CLOSE_DIALOG, code, msg);
	}
	
	
	/**
	 * 以HTML格式输出,页面只包含js脚本
	 * 
	 */
	public static void callback(CallbackData obj, HttpServletResponse response) {
		callback(obj, null, response);
	}
	
	/**
	 * 以HTML格式输出,页面只包含js脚本
	 * 
	 */
	public static void callback(CallbackData obj, String callbackFun, HttpServletResponse response) {
		StringBuilder sb = new StringBuilder();
		sb.append("<script type=\"text/javascript\">")
			.append("window.parent.utils.submit.callback")
			.append(Symbol.L_PARENTHESIS)
			.append("window.name, ")
			.append(obj)
			.append(Strings.isNotBlank(callbackFun) ? ",'" + callbackFun + "'" : "")
			.append(Symbol.R_PARENTHESIS)
			.append(";</script>");
		Https.outHtml(sb.toString(), response);
	}

}
