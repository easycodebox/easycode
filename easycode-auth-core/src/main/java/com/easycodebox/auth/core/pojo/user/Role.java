package com.easycodebox.auth.core.pojo.user;

import java.util.List;

import com.easycodebox.auth.core.util.mybatis.GeneratedValue;
import com.easycodebox.auth.core.util.mybatis.GeneratorEnum;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.enums.entity.status.CloseStatus;
import com.easycodebox.common.jpa.Entity;
import com.easycodebox.common.jpa.Id;
import com.easycodebox.common.jpa.OneToMany;
import com.easycodebox.common.jpa.Table;
import com.easycodebox.common.jpa.Transient;
import com.easycodebox.common.lang.dto.AbstractOperateEntity;

/**
 * 角色 - 角色
 * @author WangXiaoJin
 *
 */
@Entity
@Table(name="u_role")
public class Role extends AbstractOperateEntity {

	private static final long serialVersionUID = 5454155825314635342L;
	
	/**
	 * 主键
	 */
	@Id
	@GeneratedValue(GeneratorEnum.ROLE_ID)
	private Integer id;
	
	/**
	 * 角色名
	 */
	private String name;
	
	/**
	 * 排序值
	 */
	private Integer sort;
	
	/**
	 * 状态
	 */
	private CloseStatus status;
	
	/**
	 * 描述
	 */
	private String description;
	
	/**
	 * 备注
	 */
	private String remark;
	

	@OneToMany(mappedBy="role")
	private List<RoleOperation> roleOperations;
	
	@OneToMany(mappedBy="role")
	private List<UserRole> userRoles;
	
	@OneToMany(mappedBy="role")
	private List<GroupRole> groupRoles;
	
	/************ 冗余字段 *******************/
	/**
	 * 标记拥有该角色
	 */
	@Transient
	private YesNo isOwn;
	/**
	 * 标记该角色是否属于组
	 */
	@Transient
	private YesNo isGroupOwn;
	
	public Role(){
	
	}

	public Role(Integer id){
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
	
	public Integer getSort() {
		return sort;
	}
	
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	public CloseStatus getStatus() {
		return status;
	}
	
	public void setStatus(CloseStatus status) {
		this.status = status;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getRemark() {
		return remark;
	}
	
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public void setRoleOperations(List<RoleOperation> roleOperations){
		this.roleOperations = roleOperations;
	}
	
	public List<RoleOperation> getRoleOperations() {
		return roleOperations;
	}
	public void setUserRoles(List<UserRole> userRoles){
		this.userRoles = userRoles;
	}
	
	public List<UserRole> getUserRoles() {
		return userRoles;
	}
	public void setGroupRoles(List<GroupRole> groupRoles){
		this.groupRoles = groupRoles;
	}
	
	public List<GroupRole> getGroupRoles() {
		return groupRoles;
	}

	public YesNo getIsOwn() {
		return isOwn;
	}

	public void setIsOwn(YesNo isOwn) {
		this.isOwn = isOwn;
	}

	public YesNo getIsGroupOwn() {
		return isGroupOwn;
	}

	public void setIsGroupOwn(YesNo isGroupOwn) {
		this.isGroupOwn = isGroupOwn;
	}
	
}

