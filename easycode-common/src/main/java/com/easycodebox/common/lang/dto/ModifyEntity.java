package com.easycodebox.common.lang.dto;


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
