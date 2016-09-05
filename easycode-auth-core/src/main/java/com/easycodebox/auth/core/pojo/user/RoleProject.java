package com.easycodebox.auth.core.pojo.user;

import com.easycodebox.auth.core.pojo.sys.Project;
import com.easycodebox.common.jpa.Entity;
import com.easycodebox.common.jpa.Id;
import com.easycodebox.common.jpa.JoinColumn;
import com.easycodebox.common.jpa.ManyToOne;
import com.easycodebox.common.jpa.Table;
import com.easycodebox.common.lang.dto.AbstractCreateEntity;

/**
 * 角色项目 - 角色与项目的对应关系
 * @author WangXiaoJin
 *
 */
@Entity
@Table(name="u_role_project")
public class RoleProject extends AbstractCreateEntity {

	private static final long serialVersionUID = 5454155825314635342L;
	
	/**
	 * 角色ID
	 */
	@Id
	private Integer roleId;
	
	/**
	 * 项目ID
	 */
	@Id
	private Integer projectId;
	

	@ManyToOne
	@JoinColumn(name="projectId") 
	private Project project;
	
	@ManyToOne
	@JoinColumn(name="roleId") 
	private Role role;
	
	public RoleProject(){
	
	}

	public RoleProject(Integer roleId, Integer projectId){
		this.roleId = roleId;
		this.projectId = projectId;
	}
	
	public Integer getRoleId() {
		return roleId;
	}
	
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	
	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setRole(Role role){
		this.role = role;
	}
	
	public Role getRole() {
		return role;
	}

}

