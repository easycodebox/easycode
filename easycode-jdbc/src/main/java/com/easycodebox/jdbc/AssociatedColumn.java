package com.easycodebox.jdbc;

import com.easycodebox.common.lang.StringUtils;

import java.io.Serializable;

/**
 * @author WangXiaoJin
 *
 */
public abstract class AssociatedColumn implements Serializable {
	
	/**
	 * 属性名
	 */
	private String propertyName;
	/**
	 * 属性的类型，包括List<Shop>
	 */
	private Class<?> propertyType;
	/**
	 * 属性中相关联的entity class
	 */
	private Class<?> associatedClass;
	private JoinColumnObj[] joinColumns;
	private String mappedBy;
	
	public AssociatedColumn() {
		super();
	}
	
	public AssociatedColumn(String propertyName, Class<?> propertyType, Class<?> associatedClass) {
		this.propertyName = propertyName;
		this.propertyType = propertyType;
		this.associatedClass = associatedClass;
	}
	
	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public Class<?> getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(Class<?> propertyType) {
		this.propertyType = propertyType;
	}

	public Class<?> getAssociatedClass() {
		return associatedClass;
	}

	public void setAssociatedClass(Class<?> associatedClass) {
		this.associatedClass = associatedClass;
	}

	public JoinColumnObj[] getJoinColumns() {
		return joinColumns;
	}

	public void setJoinColumns(JoinColumnObj[] joinColumns) {
		this.joinColumns = joinColumns;
	}
	
	public String getMappedBy() {
		return mappedBy;
	}

	public void setMappedBy(String mappedBy) {
		this.mappedBy = StringUtils.stripToNull(mappedBy);
	}
}
