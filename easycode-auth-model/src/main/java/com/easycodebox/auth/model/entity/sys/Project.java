package com.easycodebox.auth.model.entity.sys;

import com.easycodebox.auth.model.enums.GeneratorEnum;
import com.easycodebox.auth.model.util.GeneratedValue;
import com.easycodebox.common.enums.entity.*;
import com.easycodebox.jdbc.entity.AbstractOperateEntity;

import javax.persistence.*;

/**
 * 项目 - 受权限管理的项目
 * @author WangXiaoJin
 *
 */
@Entity
@Table(name="sys_project")
public class Project extends AbstractOperateEntity {

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
	private OpenClose status;
	
	/**
	 * 是否删除
	 */
	private YesNo deleted;
	
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

	public Project(Integer id) {
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
	
	public OpenClose getStatus() {
		return status;
	}

	public void setStatus(OpenClose status) {
		this.status = status;
	}

	public YesNo getDeleted() {
		return deleted;
	}

	public void setDeleted(YesNo deleted) {
		this.deleted = deleted;
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

