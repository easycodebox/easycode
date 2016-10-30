package com.easycodebox.common.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ResponseBody;

import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.lang.DataConvert;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

/**
 * @author WangXiaoJin
 *
 */
public class BaseController {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * 自动映射Handler时默认执行的方法
	 */
	public void execute() throws Exception {
		
	}
	
	/**
	 * 列表
	 */
	public void list() throws Exception {
		
	}
	
	/**
	 * 详情
	 */
	public void load() throws Exception {
		
	}
	
	/**
	 * 新增
	 */
	public void add() throws Exception {
		
	}
	
	/**
	 * 适用于页面无刷新交互式操作
	 */
	/*public void form(HttpServletResponse response) throws Exception {
		callback(closeDialog(), response);
	}*/
	
	/**
	 * 修改
	 */
	public void update() throws Exception {
		
	}
	
	/**
	 * 逻辑删除
	 */
	@ResponseBody
	public void remove() throws Exception {
		
	}
	
	/**
	 * 物理删除
	 */
	@ResponseBody
	public void removePhy() throws Exception {
		
	}
	
	/**
	 * bool == true return SUC, 否则 return FAIL
	 * @param bool
	 * @return
	 */
	protected CodeMsg isTrue(boolean bool) {
		if(bool)
			return CodeMsg.SUC;
		else
			return CodeMsg.FAIL;
	}
	
	/**
	 * bool == true return SUC, 否则 return FAIL <br>
	 * 返回FAIL时并设置提示信息
	 * @param bool
	 * @return
	 */
	protected CodeMsg isTrue(boolean bool, String failMsg, Object... failMsgArgs) {
		if(bool)
			return CodeMsg.SUC;
		else
			return CodeMsg.FAIL.msg(failMsg, failMsgArgs);
	}
	
	/**
	 * bool == true return NONE, 否则 return FAIL
	 * @param bool
	 * @return
	 */
	protected CodeMsg isTrueNone(boolean bool) {
		if(bool)
			return CodeMsg.NONE;
		else
			return CodeMsg.FAIL;
	}
	
	/**
	 * bool == true return NONE, 否则 return FAIL <br>
	 * 返回FAIL时并设置提示信息
	 * @param bool
	 * @return
	 */
	protected CodeMsg isTrueNone(boolean bool, String failMsg, Object... failMsgArgs) {
		if(bool)
			return CodeMsg.NONE;
		else
			return CodeMsg.FAIL.msg(failMsg, failMsgArgs);
	}
	
	/**
	 * return NONE
	 * @return
	 */
	protected CodeMsg none() {
		return CodeMsg.NONE;
	}
	
	/**
	 * return NONE，并设置data
	 * @return
	 */
	protected CodeMsg none(Object data) {
		return CodeMsg.NONE.data(data);
	}
	
	/**
	 * 从request中获取对应的参数
	 * @param request
	 * @param name
	 * @param clazz
	 * @return
	 */
	protected <T> T obtainParam(HttpServletRequest request, String name, Class<T> clazz) {
		String[] vals = request.getParameterValues(name);
		if(vals == null) 
			return null;
		String union = Symbol.EMPTY;
		for(int i = 0; i < vals.length; i++)
			union += vals[i] + (i < vals.length - 1 ? Symbol.COMMA : Symbol.EMPTY);
		return DataConvert.convertType(union, clazz);
	}

}
