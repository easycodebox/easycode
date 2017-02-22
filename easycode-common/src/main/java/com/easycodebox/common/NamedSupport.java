package com.easycodebox.common;

import com.easycodebox.common.validate.Assert;

/**
 * @author WangXiaoJin
 */
public abstract class NamedSupport implements Named {
	
	private String name;
	
	public NamedSupport(String name) {
		Assert.notBlank(name, "Param 'name' can not be blank.");
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
}
