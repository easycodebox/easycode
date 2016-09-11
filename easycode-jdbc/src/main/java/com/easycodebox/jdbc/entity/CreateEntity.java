package com.easycodebox.jdbc.entity;


import java.util.Date;

/**
 * @author WangXiaoJin
 * 
 */
public interface CreateEntity extends Entity {
	
	String getCreator();

	void setCreator(String creator);

	Date getCreateTime();

	void setCreateTime(Date createTime);
	
}
