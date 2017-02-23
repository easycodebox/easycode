package com.easycodebox.common.security;

import com.easycodebox.common.Init;
import com.easycodebox.common.config.CommonProperties;

import javax.annotation.PostConstruct;
import java.io.Serializable;

/**
 * @author WangXiaoJin
 */
public abstract class AbstractSecurityInfoHandler<S, T extends Serializable> implements SecurityInfoHandler<S, T>, Init {
	
	private CommonProperties commonProperties;
	
	@Override
	@PostConstruct
	public void init() throws Exception {
		commonProperties = commonProperties == null ? CommonProperties.instance() : commonProperties;
	}
	
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
