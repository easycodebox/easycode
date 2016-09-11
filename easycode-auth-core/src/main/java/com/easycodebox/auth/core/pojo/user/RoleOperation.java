package com.easycodebox.auth.core.pojo.user;

import javax.persistence.*;

import com.easycodebox.jdbc.entity.AbstractCreateEntity;

/**
 * 角色权限 - 角色与权限的对应关系
 * @author WangXiaoJin
 *
 */
@Entity
@Table(name="u_role_operation")
public class RoleOperation extends AbstractCreateEntity {

	private static final long serialVersionUID = 5454155825314635342L;
	
	/**
	 * 角色ID
	 */
	@Id
	private Integer roleId;
	
	/**
	 * 权限ID
	 */
	@Id
	private Long operationId;
	

	@ManyToOne
	@JoinColumn(name="operationId") 
	private Operation operation;
	
	@ManyToOne
	@JoinColumn(name="roleId") 
	private Role role;
	
	public RoleOperation(){
	
	}

	public RoleOperation(Integer roleId, Long operationId){
		this.roleId = roleId;
		this.operationId = operationId;
	}
	public Integer getRoleId() {
		return roleId;
	}
	
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	
	public Long getOperationId() {
		return operationId;
	}
	
	public void setOperationId(Long operationId) {
		this.operationId = operationId;
	}
	
	public void setOperation(Operation operation){
		this.operation = operation;
	}
	
	public Operation getOperation() {
		return operation;
	}
	public void setRole(Role role){
		this.role = role;
	}
	
	public Role getRole() {
		return role;
	}

}

