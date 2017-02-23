package com.easycodebox.auth.core.config;

import com.easycodebox.common.NamedSupport;
import com.easycodebox.common.CommonProperties;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author WangXiaoJin
 */
public class CoreProperties extends NamedSupport {
	
	public static final String DEFAULT_NAME = CoreProperties.class.getName();
	
	private static CommonProperties INSTANCE;
	
	public static CommonProperties instance() {
		return INSTANCE == null ? (INSTANCE = new CommonProperties()) : INSTANCE;
	}
	
	public CoreProperties() {
		this(DEFAULT_NAME);
	}
	
	public CoreProperties(String name) {
		super(name);
	}
	
	/**
	 * 重置后的新密码
	 */
	@Value("${reset_pwd}")
	private String resetPwd;
	/**
	 * 是否可以修改super admin信息
	 */
	@Value("${modify_super_admin}")
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
