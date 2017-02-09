package com.easycodebox.common.lang;

import org.apache.commons.lang.builder.*;

import java.io.Serializable;

/**
 * 美化java的实体对象
 * @author WangXiaoJin
 *
 */
public abstract class BuilderEntity implements Serializable {

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
