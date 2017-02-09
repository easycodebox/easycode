package com.easycodebox.auth.model.entity.user;

import com.easycodebox.auth.model.util.mybatis.GeneratedValue;
import com.easycodebox.auth.model.util.mybatis.*;
import com.easycodebox.common.enums.entity.*;
import com.easycodebox.jdbc.entity.AbstractOperateEntity;

import javax.persistence.*;
import java.util.List;

/**
 * 用户组 - 用户组
 * @author WangXiaoJin
 *
 */
@Entity
@Table(name="u_group")
public class Group extends AbstractOperateEntity {

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
	private OpenClose status;
	
	/**
	 * 是否删除
	 */
	private YesNo deleted;

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

