package com.easycodebox.login.ws.bo;

import java.util.List;

import com.easycodebox.jdbc.entity.AbstractEntity;

/**
 * 权限 - 权限
 * @author WangXiaoJin
 *
 */
public class OperationWsBo extends AbstractEntity {

	private static final long serialVersionUID = 5454155825314635342L;
	
	/**
	 * 主键
	 */
	private Long id;
	
	/**
	 * 上级权限
	 */
	private Long parentId;
	
	/**
	 * 权限名
	 */
	private String name;
	
	/**
	 * 地址
	 */
	private String url;
	
	/**
	 * 图标
	 */
	private String icon;
	
	private List<OperationWsBo> children;

	public OperationWsBo() {
	
	}
	
	public OperationWsBo(Long id) {
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

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public List<OperationWsBo> getChildren() {
		return children;
	}

	public void setChildren(List<OperationWsBo> children) {
		this.children = children;
	}

}

