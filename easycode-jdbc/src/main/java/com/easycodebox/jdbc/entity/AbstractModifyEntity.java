package com.easycodebox.jdbc.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author WangXiaoJin
 * 
 */
@MappedSuperclass
public abstract class AbstractModifyEntity extends AbstractEntity implements ModifyEntity {

	private String modifier;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifyTime;

	/********** 冗余字段  **************/
	@Transient
	private String modifierName;
	
	@Override
	public String getModifier() {
		return modifier;
	}

	@Override
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	@Override
	public Date getModifyTime() {
		return modifyTime;
	}

	@Override
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getModifierName() {
		return modifierName;
	}

	public void setModifierName(String modifierName) {
		this.modifierName = modifierName;
	}
	
}
