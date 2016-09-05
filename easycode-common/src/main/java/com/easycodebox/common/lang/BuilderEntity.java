package com.easycodebox.common.lang;

import java.io.Serializable;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 美化java的实体对象
 * @author WangXiaoJin
 *
 */
public abstract class BuilderEntity implements Serializable {

	private static final long serialVersionUID = -2057639362380434895L;

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
