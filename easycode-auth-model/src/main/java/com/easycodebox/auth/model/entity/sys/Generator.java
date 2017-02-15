package com.easycodebox.auth.model.entity.sys;

import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.jdbc.entity.AbstractOperateEntity;

import javax.persistence.*;

/**
 * @author WangXiaoJin
 *
 */
@Entity
@Table(name="sys_generator")
public class Generator extends AbstractOperateEntity {

	@Id
	private String id;
	
	private String initialVal;
	
	private String currentVal;
	
	private String maxVal;
	
	private Integer fetchSize;
	
	private Integer increment;
	
	private YesNo isCycle;
	

	public Generator(){
	
	}

	public Generator(String id){
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getInitialVal() {
		return initialVal;
	}
	
	public void setInitialVal(String initialVal) {
		this.initialVal = initialVal;
	}
	
	public String getCurrentVal() {
		return currentVal;
	}
	
	public void setCurrentVal(String currentVal) {
		this.currentVal = currentVal;
	}
	
	public String getMaxVal() {
		return maxVal;
	}
	
	public void setMaxVal(String maxVal) {
		this.maxVal = maxVal;
	}
	
	public Integer getFetchSize() {
		return fetchSize;
	}
	
	public void setFetchSize(Integer fetchSize) {
		this.fetchSize = fetchSize;
	}
	
	public Integer getIncrement() {
		return increment;
	}

	public void setIncrement(Integer increment) {
		this.increment = increment;
	}

	public YesNo getIsCycle() {
		return isCycle;
	}

	public void setIsCycle(YesNo isCycle) {
		this.isCycle = isCycle;
	}
	
}

