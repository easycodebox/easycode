package com.easycodebox.common;

import com.easycodebox.common.enums.entity.ProjectEnv;
import com.easycodebox.common.processor.StaticValue;

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
	public static String DIALOG_REQ_KEY = "DIALOG_REQ";
	
	/**
	 * pjax请求的header key值
	 */
	@StaticValue("${pjax_key}")
	public static String pjaxKey = "X-PJAX";
	
	/******************** 通用的配置  *******************************/
	
	/**
	 * 项目所处环境
	 */
	@StaticValue("${project_env}")
	public static ProjectEnv projectEnv;
	/**
	 * 使用script/style标签时，是否自动显示压缩后的文件： 
	 * test.js  --> test.min.js
	 * test.css --> test.min.css
	 */
	@StaticValue("${trans_min_js_css}")
	public static boolean transMinJsCss = true;
	
	/******** code/msg  *******/
	@StaticValue("${code_key}")
	public static String codeKey = "code";
	
	@StaticValue("${msg_key}")
	public static String msgKey = "msg";
	
	@StaticValue("${data_key}")
	public static String dataKey = "data";
	
	@StaticValue("${code.suc}")
	public static String codeSuc = "0";
	
	@StaticValue("${code.fail}")
	public static String codeFail = "1";
	
	@StaticValue("${code.no.login}")
	public static String codeNoLogin = "2";
	
	/**
	 * 临时文件名
	 */
	@StaticValue("${tmp_file}")
	public static String tmpFile = "tmp";
	
	/**
	 * 当前项目的根路径：http://www.xxx.com
	 */
	@StaticValue("${base_path}")
	public static String basePath;
	
	/**
	 * 图片根地址
	 * 注意：子类实现此配置
	 */
	@StaticValue("${img_url}")
	public static String imgUrl;
	
	/**
	 * http请求参数以传统格式传送：
	 * true	== url.do?name=wang&name=zhang
	 * false == url.do?name[]=wang&name[]=zhang
	 */
	public static Boolean httpParamTradition = true;
	
	/**
	 * 默认图片路径
	 */
	public static class Imgs {
		
		/**
		 * 默认头像
		 */
		public static String face = "default/face.jpg";
		
		/**
		 * 默认图片
		 */
		public static String defaultImg = "default/default.jpg";

	}
	
}
