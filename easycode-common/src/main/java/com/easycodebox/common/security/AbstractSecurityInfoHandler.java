package com.easycodebox.common.security;

import com.easycodebox.common.CommonProperties;

import java.io.Serializable;

/**
 * @author WangXiaoJin
 */
public abstract class AbstractSecurityInfoHandler<S, T extends Serializable> implements SecurityInfoHandler<S, T> {
	
	private CommonProperties commonProperties = CommonProperties.instance();
	
	@Override
	public String getKey() {
		return commonProperties.getUserKey();
	}
	
	public CommonProperties getCommonProperties() {
		return commonProperties;
	}
	
	public void setCommonProperties(CommonProperties commonProperties) {
		this.commonProperties = commonProperties;
	}
	
}
