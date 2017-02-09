package com.easycodebox.jdbc.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author WangXiaoJin
 * 
 */
@MappedSuperclass
public abstract class AbstractCreateEntity extends AbstractEntity implements CreateEntity {
	
	private String creator;
	
	private Date createTime;
	
	/********** 冗余字段  **************/
	@Transient
	private String creatorName;

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
	
	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	
}
