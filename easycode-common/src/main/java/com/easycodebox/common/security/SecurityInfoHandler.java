package com.easycodebox.common.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * 存储/获取Security信息
 * @author WangXiaoJin
 * @param <S>	storage仓库类型
 * @param <T>	securityInfo类型
 */
public interface SecurityInfoHandler<S, T extends Serializable> {
	
	/**
	 * 返回securityInfo存储于storage中的key
	 * @return
	 */
	String getKey();
	
	/**
	 * 根据storage创建一个新的SecurityContext实例
	 * @param storage
	 * @return
	 */
	SecurityContext<T> newSecurityContext(S storage, HttpServletRequest request, HttpServletResponse response);
	
	/**
	 * 获取Security数据
	 * @param storage 存放SecurityInfo的仓库
	 * @return
	 */
	T getSecurityInfo(S storage);
	
	/**
	 * 存储securityInfo至storage
	 * @param storage
	 * @param securityInfo
	 */
	void storeSecurityInfo(S storage, T securityInfo);
	
	/**
	 * 摧毁securityInfo
	 * @param storage
	 */
	void destroySecurityInfo(S storage);
	
}
