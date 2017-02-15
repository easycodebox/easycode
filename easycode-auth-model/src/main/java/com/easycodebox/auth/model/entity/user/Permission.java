package com.easycodebox.auth.model.entity.user;

import com.easycodebox.auth.model.entity.sys.Project;
import com.easycodebox.auth.model.enums.GeneratorEnum;
import com.easycodebox.common.generator.GeneratedValue;
import com.easycodebox.common.enums.entity.*;
import com.easycodebox.jdbc.entity.AbstractOperateEntity;

import javax.persistence.*;
import java.util.List;

/**
 * 权限 - 权限
 * @author WangXiaoJin
 *
 */
@Entity
@Table(name="u_permission")
public class Permission extends AbstractOperateEntity {

	/**
	 * 主键
	 */
	@Id
	@GeneratedValue(type = GeneratorEnum.class, key = "PERMISSION_ID")
	private Long id;
	
	/**
	 * 上级权限
	 */
	private Long parentId;
	
	/**
	 * 项目ID
	 */
	private Integer projectId;
	
	/**
	 * 权限名
	 */
	private String name;
	
	/**
	 * 状态
	 */
	private OpenClose status;
	
	/**
	 * 是否删除
	 */
	private YesNo deleted;
	
	/**
	 * 菜单 - 是否为菜单按钮
	 */
	private YesNo isMenu;
	
	/**
	 * 地址
	 */
	private String url;
	
	/**
	 * 排序值
	 */
	private Integer sort;
	
	/**
	 * 图标
	 */
	private String icon;
	
	/**
	 * 描述
	 */
	private String description;
	
	/**
	 * 备注
	 */
	private String remark;
	
	@ManyToOne
	@JoinColumn(name="parentId") 
	private Permission parent;
	
	@ManyToOne
	@JoinColumn(name="projectId") 
	private Project project;
	
	@OneToMany(mappedBy="parent")
	private List<Permission> children;

	@OneToMany(mappedBy="permission")
	private List<RolePermission> rolePermissions;
	
	/************ 冗余字段 *******************/
	/**
	 * 标记拥有该角色
	 */
	@Transient
	private YesNo isOwn;
	
	/**
	 * 父级权限名
	 */
	@Transient
	private String parentName;
	
	/**
	 * 项目名
	 */
	@Transient
	private String projectName;
	
	public Permission(){
	
	}
	
	public Permission(Long id, Long parentId, String name, 
			Integer projectId, YesNo isMenu, String url, String description,
			String icon){
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.projectId = projectId;
		this.isMenu = isMenu;
		this.url = url;
		this.description = description;
		this.icon = icon;
		this.sort = 0;
	}

	public Permission(Long id){
		this.id = id;
	}
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Integer getProjectId() {
		return projectId;
	}
	
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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

	public YesNo getIsMenu() {
		return isMenu;
	}
	
	public void setIsMenu(YesNo isMenu) {
		this.isMenu = isMenu;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Integer getSort() {
		return sort;
	}
	
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
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
	
	public Permission getParent() {
		return parent;
	}

	public void setParent(Permission parent) {
		this.parent = parent;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<Permission> getChildren() {
		return children;
	}

	public void setChildren(List<Permission> children) {
		this.children = children;
	}

	public void setRolePermissions(List<RolePermission> rolePermissions){
		this.rolePermissions = rolePermissions;
	}
	
	public List<RolePermission> getRolePermissions() {
		return rolePermissions;
	}

	public YesNo getIsOwn() {
		return isOwn;
	}

	public void setIsOwn(YesNo isOwn) {
		this.isOwn = isOwn;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}

