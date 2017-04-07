package com.easycodebox.common;

import com.easycodebox.common.enums.entity.ProjectEnv;

/**
 * @author WangXiaoJin
 */
public class CommonProperties {
	
	private static volatile CommonProperties instance;
	
	public static CommonProperties instance() {
		if (instance == null) {
			synchronized (CommonProperties.class) {
				if (instance == null) {
					instance = new CommonProperties();
				}
			}
		}
		return instance;
	}
	
	private CommonProperties() {
		
	}
	
	/**
	 * 项目所处环境
	 */
	private ProjectEnv projectEnv = ProjectEnv.DEV;
	
	/**
	 * 使用script/style标签时，是否自动显示压缩后的文件：
	 * test.js  --> test.min.js
	 * test.css --> test.min.css
	 */
	private boolean transMinJsCss = true;
	
	/**
	 * 当前项目的根路径：http://www.xxx.com
	 */
	private String basePath = "/";
	
	/**
	 * 存放临时文件的文件名
	 */
	private String tmpFilename = "tmp";
	
	/**
	 * 图片根地址
	 * 注意：子类实现此配置
	 */
	private String imgUrl;
	
	/**
	 * 默认图片
	 */
	private String imgDefault = "default/default.jpg";
	
	/**
	 * http请求参数以传统格式传送：
	 * true	== url.do?name=wang&name=zhang
	 * false == url.do?name[]=wang&name[]=zhang
	 */
	private boolean traditionalHttp = true;
	
	/**
	 * pjax请求的header key值
	 */
	private String pjaxKey = "X-PJAX";
	
	/**
	 * response url参数key值
	 */
	private String responseUrlKey = "responseUrl";
	
	/**
	 * 用户信息的key值
	 */
	private String userKey = "user_info";
	
	/**
	 * 项目功能菜单KEY值
	 */
	private String projectMenuKey = "project_menu";
	
	/**
	 * 权限KEY值
	 */
	private String permissionKey = "permission";
	
	/**
	 * 标记此次请求是弹出框发送的请求，controller返回callback(closeDialog(), response)格式的数据
	 */
	private String dialogReqKey = "DIALOG_REQ";
	
	public ProjectEnv getProjectEnv() {
		return projectEnv;
	}
	
	public void setProjectEnv(ProjectEnv projectEnv) {
		this.projectEnv = projectEnv;
	}
	
	public boolean isTransMinJsCss() {
		return transMinJsCss;
	}
	
	public void setTransMinJsCss(boolean transMinJsCss) {
		this.transMinJsCss = transMinJsCss;
	}
	
	public String getBasePath() {
		return basePath;
	}
	
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	public String getTmpFilename() {
		return tmpFilename;
	}
	
	public void setTmpFilename(String tmpFilename) {
		this.tmpFilename = tmpFilename;
	}
	
	public String getImgUrl() {
		return imgUrl;
	}
	
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	public String getImgDefault() {
		return imgDefault;
	}
	
	public void setImgDefault(String imgDefault) {
		this.imgDefault = imgDefault;
	}
	
	public boolean isTraditionalHttp() {
		return traditionalHttp;
	}
	
	public void setTraditionalHttp(boolean traditionalHttp) {
		this.traditionalHttp = traditionalHttp;
	}
	
	public String getPjaxKey() {
		return pjaxKey;
	}
	
	public void setPjaxKey(String pjaxKey) {
		this.pjaxKey = pjaxKey;
	}
	
	public String getResponseUrlKey() {
		return responseUrlKey;
	}
	
	public void setResponseUrlKey(String responseUrlKey) {
		this.responseUrlKey = responseUrlKey;
	}
	
	public String getUserKey() {
		return userKey;
	}
	
	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}
	
	public String getProjectMenuKey() {
		return projectMenuKey;
	}
	
	public void setProjectMenuKey(String projectMenuKey) {
		this.projectMenuKey = projectMenuKey;
	}
	
	public String getPermissionKey() {
		return permissionKey;
	}
	
	public void setPermissionKey(String permissionKey) {
		this.permissionKey = permissionKey;
	}
	
	public String getDialogReqKey() {
		return dialogReqKey;
	}
	
	public void setDialogReqKey(String dialogReqKey) {
		this.dialogReqKey = dialogReqKey;
	}
}
