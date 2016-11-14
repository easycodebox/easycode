package com.easycodebox.auth.core.util;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.processor.StaticValue;

/**
 * @author WangXiaoJin
 * 
 */
public class Constants extends BaseConstants {
	
	//登录失败指定次数就锁住该用户
	@StaticValue("${login_fail}")
	public static Integer loginFail;
	//重置后的新密码
	@StaticValue("${reset_pwd}")
	public static String resetPwd;
	
	/**
	 * 混合存储缓存的KeyGenerator bean name
	 */
	public static final String METHOD_ARGS_KEY_GENERATOR = "methodArgsKeyGenerator";
	/**
	 * 批量删除缓存的KeyGenerator bean name
	 */
	public static final String MULTI_KEY_GENERATOR = "multiKeyGenerator";
	/**
	 * CacheName的缩写
	 */
	public static final class CN {
		
		private static final String PREFIX = "auth_";
		
		/**
		 * key:partnerId value:partner
		 */
		public static final String PARTNER = PREFIX + "partner";
		
		/**
		 * key:projectId value:project
		 */
		public static final String PROJECT = PREFIX + "project";
		/**
		 * key:projectNo value:project
		 */
		public static final String PROJECT_NO = PREFIX + "project_no";
		
		/**
		 * key:groupId value:group
		 */
		public static final String GROUP = PREFIX + "group";
		/**
		 * key:groupId value:roles
		 */
		public static final String GROUP_ROLE = PREFIX + "group_role";
		
		/**
		 * key:mix(混合) value:permissions
		 */
		public static final String PERMISSION = PREFIX + "permission";
		
		/**
		 * key:roleId value:role
		 */
		public static final String ROLE = PREFIX + "role";
		
		/**
		 * key:userId value:user
		 */
		public static final String USER = PREFIX + "user";
		/**
		 * key:userId value:roles
		 */
		public static final String USER_ROLE = PREFIX + "user_role";
		
	}
	
}
