package com.easycodebox.common.lang.dto;


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
