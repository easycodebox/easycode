package com.easycodebox.auth.model.entity.user;

import javax.persistence.*;

import com.easycodebox.jdbc.entity.AbstractCreateEntity;

/**
 * 用户角色 - 用户与角色的对应关系
 * @author WangXiaoJin
 *
 */
@Entity
@Table(name="u_user_role")
public class UserRole extends AbstractCreateEntity {

	private static final long serialVersionUID = 5454155825314635342L;
	
	/**
	 * 用户ID
	 */
	@Id
	private String userId;
	
	/**
	 * 角色ID
	 */
	@Id
	private Integer roleId;
	

	@ManyToOne
	@JoinColumn(name="roleId") 
	private Role role;
	
	@ManyToOne
	@JoinColumn(name="userId") 
	private User user;
	
	public UserRole(){
	
	}

	public UserRole(String userId, Integer roleId){
		this.userId = userId;
		this.roleId = roleId;
	}
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public Integer getRoleId() {
		return roleId;
	}
	
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	
	public void setRole(Role role){
		this.role = role;
	}
	
	public Role getRole() {
		return role;
	}
	public void setUser(User user){
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}

}

