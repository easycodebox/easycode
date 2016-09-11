package com.easycodebox.jdbc.entity;


import java.util.Date;

/**
 * @author WangXiaoJin
 * 
 */
public interface ModifyEntity extends Entity {
	
	String getModifier();

	void setModifier(String modifier);

	Date getModifyTime();

	void setModifyTime(Date modifyTime);
	
}
