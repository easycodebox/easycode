package com.easycodebox.common.web.callback;

import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.jackson.Jacksons;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

/**
 * @author WangXiaoJin
 *
 */
public class CallbackData extends CodeMsg {
	
	private transient final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * 回调函数执行的动作类型
	 * 依据CallbackAction类的action
	 */
	private String action;
	
	/**
	 * 回调函数跳转的地址
	 */
	private String url;
	
	public static CallbackData instance() {
		return instance(null, null, null, null);
	}
	
	public static CallbackData instance(String code) {
		return instance(null, code, null, null);
	}
	
	public static CallbackData instance(String code, String msg) {
		return instance(null, code, msg, null);
	}
	
	public static CallbackData instance(String action, String code, 
			String msg) {
		return instance(action, code, msg, null);
	}
	
	public static CallbackData instance(String action, String code, 
			String msg, String url) {
		return new CallbackData(code, msg).setAction(action).setUrl(url);
	}
	
	public static CallbackData instance(CodeMsg codeMsg) {
		return instance(null, codeMsg, null);
	}
	
	public static CallbackData instance(String action, CodeMsg codeMsg) {
		return instance(action, codeMsg, null);
	}
	
	public static CallbackData instance(String action, CodeMsg codeMsg, String url) {
		return new CallbackData(
				codeMsg == null ? null : codeMsg.getCode(), 
				codeMsg == null ? null : codeMsg.getMsg(), 
				codeMsg == null ? null : codeMsg.getData()
			).setAction(action).setUrl(url);
	}
	
	private CallbackData(String code, String msg) {
		super(code, msg);
	}
	
	private CallbackData(String code, String msg, Object data) {
		super(code, msg, data);
	}
	
	/**
	 * 判断参数code是否和当前code像相等
	 * @param code 
	 * @return
	 */
	public boolean eqcode(String code) {
		return (code == null && this.getCode() == null)
				|| (code != null && code.equals(this.getCode()));
	}
	
	@Override
	public String toString() {
		String msg = getMsg();
		msg = Strings.isBlank(msg) ? null : Strings.string2unicode(msg);
		StringBuilder sb = new StringBuilder()
			.append("{")
			.append("\"action\"").append(":").append(Strings.valueToString(getAction())).append(",")
			.append("\"code\"").append(":").append(Strings.valueToString(getCode())).append(",")
			.append("\"msg\"").append(":").append(Strings.valueToString(msg)).append(",")
			.append("\"url\"").append(":").append(Strings.valueToString(getUrl()));
		try {
			sb.append(",").append("\"data\"").append(":")
			.append(Jacksons.COMMUNICATE.toJson(getData()));
		} catch (Exception e) {
			log.error("解析JSON错误", e);
		}
		sb.append("}");
		return sb.toString();
	}

	public String getUrl() {
		return url;
	}

	public CallbackData setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getAction() {
		return action;
	}

	public CallbackData setAction(String action) {
		this.action = action;
		return this;
	}
	
}
