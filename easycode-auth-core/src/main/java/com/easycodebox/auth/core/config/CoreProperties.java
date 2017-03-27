package com.easycodebox.auth.core.config;

/**
 * @author WangXiaoJin
 */
public class CoreProperties {
	
	private static volatile CoreProperties instance;
	
	public static CoreProperties instance() {
		if (instance == null) {
			synchronized (CoreProperties.class) {
				if (instance == null) {
					instance = new CoreProperties();
				}
			}
		}
		return instance;
	}
	
	private CoreProperties() {
		
	}
	
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
