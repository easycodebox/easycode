package com.easycodebox.auth.core.pojo.sys;

import javax.persistence.*;

import com.easycodebox.auth.core.util.mybatis.GeneratedValue;
import com.easycodebox.auth.core.util.mybatis.GeneratorEnum;
import com.easycodebox.common.enums.entity.status.CloseStatus;
import com.easycodebox.jdbc.entity.AbstractOperateEntity;

/**
 * 项目 - 受权限管理的项目
 * @author WangXiaoJin
 *
 */
@Entity
@Table(name="sys_project")
public class Project extends AbstractOperateEntity {

	private static final long serialVersionUID = 5454155825314635342L;
	
	/**
	 * 主键
	 */
	@Id
	@GeneratedValue(GeneratorEnum.PROJECT_ID)
	private Integer id;
	
	/**
	 * 项目名
	 */
	private String name;
	
	/**
	 * 项目编号
	 */
	private String projectNo;
	
	/**
	 * 状态
	 */
	private CloseStatus status;
	
	/**
	 * 排序值
	 */
	private Integer sort;
	
	/**
	 * 项目数值 - 从1开始依次递增
	 */
	private Integer num;
	
	/**
	 * 备注
	 */
	private String remark;
	

	public Project(){
	
	}

	public Project(Integer id){
		this.id = id;
	}
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getProjectNo() {
		return projectNo;
	}
	
	public void setProjectNo(String projectNo) {
		this.projectNo = projectNo;
	}
	
	public CloseStatus getStatus() {
		return status;
	}

	public void setStatus(CloseStatus status) {
		this.status = status;
	}

	public Integer getSort() {
		return sort;
	}
	
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getRemark() {
		return remark;
	}
	
	public void setRemark(String remark) {
		this.remark = remark;
	}
	

}

