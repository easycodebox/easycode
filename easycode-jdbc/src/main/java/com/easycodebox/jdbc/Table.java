package com.easycodebox.jdbc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WangXiaoJin
 *
 */
public class Table implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Class<?> entityType;
	/**
	 * 表名
	 */
	private String name;
	private String schema;
	private String catalog;
	/**
	 * 包含主键
	 */
	private Map<String, Column> columns = new HashMap<String, Column>(4);
	/**
	 * 关联字段
	 */
	private Map<String, AssociatedColumn> associatedColumns = new HashMap<String, AssociatedColumn>(4);
	private List<PkColumn> primaryKeys = new ArrayList<PkColumn>(4);
	

	public Table() { }

	public Table(String name) {
		setName( name );
	}
	
	public void addColumn(Column column) {
		columns.put(column.getName(), column);
	}
	
	public Column getColumn(String propertyName) {
		return columns.get(propertyName);
	}
	
	public void addAssociatedColumn(String key, AssociatedColumn associatedColumn) {
		associatedColumns.put(key, associatedColumn);
	}
	
	public void addPrimaryKey(PkColumn column) {
		primaryKeys.add(column);
	}

	public Class<?> getEntityType() {
		return entityType;
	}

	public void setEntityType(Class<?> entityType) {
		this.entityType = entityType;
	}

	/**
	 * 获取表名
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public Map<String, Column> getColumns() {
		return columns;
	}

	public void setColumns(Map<String, Column> columns) {
		this.columns = columns;
	}
	
	public Map<String, AssociatedColumn> getAssociatedColumns() {
		return associatedColumns;
	}

	public void setAssociatedColumns(Map<String, AssociatedColumn> associatedColumns) {
		this.associatedColumns = associatedColumns;
	}

	public List<PkColumn> getPrimaryKeys() {
		return primaryKeys;
	}

	public void setPrimaryKeys(List<PkColumn> primaryKeys) {
		this.primaryKeys = primaryKeys;
	}
	
}
