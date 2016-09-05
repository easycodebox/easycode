package com.easycodebox.auth.core.pojo.user;

import java.util.List;

import com.easycodebox.auth.core.util.mybatis.GeneratedValue;
import com.easycodebox.auth.core.util.mybatis.GeneratorEnum;
import com.easycodebox.common.enums.entity.status.CloseStatus;
import com.easycodebox.common.jpa.Entity;
import com.easycodebox.common.jpa.Id;
import com.easycodebox.common.jpa.JoinColumn;
import com.easycodebox.common.jpa.ManyToOne;
import com.easycodebox.common.jpa.OneToMany;
import com.easycodebox.common.jpa.Table;
import com.easycodebox.common.jpa.Transient;
import com.easycodebox.common.lang.dto.AbstractOperateEntity;

/**
 * 用户组 - 用户组
 * @author WangXiaoJin
 *
 */
@Entity
@Table(name="u_group")
public class Group extends AbstractOperateEntity {

	private static final long serialVersionUID = 5454155825314635342L;
	
	/**
	 * 主键
	 */
	@Id
	@GeneratedValue(GeneratorEnum.GROUP_ID)
	private Integer id;
	
	/**
	 * 上级组织
	 */
	private Integer parentId;
	
	/**
	 * 组名
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
	

	@ManyToOne
	@JoinColumn(name="parentId") 
	private Group parent;
	
	@OneToMany(mappedBy="parent")
	private List<Group> children;
	
	@OneToMany(mappedBy="group")
	private List<User> users;
	
	@OneToMany(mappedBy="group")
	private List<GroupRole> groupRoles;
	
	/************ 冗余字段 *******************/
	@Transient
	private String parentName;
	
	public Group(){
	
	}

	public Group(Integer id){
		this.id = id;
	}
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
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
	
	public Group getParent() {
		return parent;
	}

	public void setParent(Group parent) {
		this.parent = parent;
	}

	public List<Group> getChildren() {
		return children;
	}

	public void setChildren(List<Group> children) {
		this.children = children;
	}

	public void setUsers(List<User> users){
		this.users = users;
	}
	
	public List<User> getUsers() {
		return users;
	}
	
	public void setGroupRoles(List<GroupRole> groupRoles){
		this.groupRoles = groupRoles;
	}
	
	public List<GroupRole> getGroupRoles() {
		return groupRoles;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

}

