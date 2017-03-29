package com.easycodebox.auth.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 在此类中添加本项目私有配置
 * @author WangXiaoJin
 */
@ConfigurationProperties(prefix = "core")
public class CoreProperties {
	
	/**
	 * 重置后的新密码
	 */
	private String resetPwd;
	/**
	 * 是否可以修改super admin信息
	 */
	private boolean modifySuperAdmin;
	
	public String getResetPwd() {
		return resetPwd;
	}
	
	public void setResetPwd(String resetPwd) {
		this.resetPwd = resetPwd;
	}
	
	public boolean isModifySuperAdmin() {
		return modifySuperAdmin;
	}
	
	public void setModifySuperAdmin(boolean modifySuperAdmin) {
		this.modifySuperAdmin = modifySuperAdmin;
	}
}
