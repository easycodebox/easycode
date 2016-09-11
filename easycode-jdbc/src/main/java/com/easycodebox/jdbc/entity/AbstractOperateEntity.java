package com.easycodebox.jdbc.entity;

import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * @author WangXiaoJin
 * 
 */
@MappedSuperclass
public abstract class AbstractOperateEntity extends AbstractEntity implements OperateEntity {

	private static final long serialVersionUID = 1850564296369846490L;

	private String creator;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
	
	private String modifier;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifyTime;

	/********** 冗余字段  **************/
	@Transient
	private String creatorName;
	
	@Transient
	private String modifierName;
	
	@Override
	public String getCreator() {
		return creator;
	}

	@Override
	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Override
	public Date getCreateTime() {
		return createTime;
	}

	@Override
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

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

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getModifierName() {
		return modifierName;
	}

	public void setModifierName(String modifierName) {
		this.modifierName = modifierName;
	}
	
}
