package com.easycodebox.common.config;

import com.easycodebox.common.NamedSupport;
import com.easycodebox.common.enums.entity.ProjectEnv;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author WangXiaoJin
 */
public class CommonProperties extends NamedSupport {
	
	public static final String DEFAULT_NAME = CommonProperties.class.getName();
	
	private static CommonProperties INSTANCE;
	
	public static CommonProperties instance() {
		return INSTANCE == null ? (INSTANCE = new CommonProperties()) : INSTANCE;
	}
	
	/**
	 * 项目所处环境
	 */
	@Value("${project_env:DEV}")
	private ProjectEnv projectEnv = ProjectEnv.DEV;
	/**
	 * 使用script/style标签时，是否自动显示压缩后的文件：
	 * test.js  --> test.min.js
	 * test.css --> test.min.css
	 */
	@Value("${trans_min_js_css:true}")
	private boolean transMinJsCss = true;
	/**
	 * 当前项目的根路径：http://www.xxx.com
	 */
	@Value("${base_path}")
	private String basePath;
	/**
	 * 图片根地址
	 * 注意：子类实现此配置
	 */
	@Value("${img_url}")
	private String imgUrl;
	/**
	 * 默认图片
	 */
	@Value("${img.default:default/default.jpg}")
	private String imgDefault = "default/default.jpg";
	
	public CommonProperties() {
		this(DEFAULT_NAME);
	}
	
	public CommonProperties(String name) {
		super(name);
	}
	
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
}
