package com.easycodebox.common.cache.spring;

import java.io.Serializable;

/**
 * 做批量操作缓存使用
 * @author WangXiaoJin
 *
 */
@SuppressWarnings("serial")
public class MultiKey implements Serializable {

	private final Object[] params;
	
	public MultiKey(Object[] params) {
		this.params = params;
	}

	public Object[] getParams() {
		return params;
	}
	
}
