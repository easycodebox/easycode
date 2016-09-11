package com.easycodebox.auth.core.pojo.user;

import javax.persistence.*;

import com.easycodebox.jdbc.entity.AbstractCreateEntity;

/**
 * 用户组角色 - 用户组与角色对应的关系
 * @author WangXiaoJin
 *
 */
@Entity
@Table(name="u_group_role")
public class GroupRole extends AbstractCreateEntity {

	private static final long serialVersionUID = 5454155825314635342L;
	
	/**
	 * 角色ID
	 */
	@Id
	private Integer roleId;
	
	/**
	 * 用户组ID
	 */
	@Id
	private Integer groupId;
	

	@ManyToOne
	@JoinColumn(name="groupId") 
	private Group group;
	
	@ManyToOne
	@JoinColumn(name="roleId") 
	private Role role;
	
	public GroupRole(){
	
	}

	public GroupRole(Integer roleId, Integer groupId){
		this.roleId = roleId;
		this.groupId = groupId;
	}
	public Integer getRoleId() {
		return roleId;
	}
	
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	
	public Integer getGroupId() {
		return groupId;
	}
	
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	
	public void setGroup(Group group){
		this.group = group;
	}
	
	public Group getGroup() {
		return group;
	}
	public void setRole(Role role){
		this.role = role;
	}
	
	public Role getRole() {
		return role;
	}

}

