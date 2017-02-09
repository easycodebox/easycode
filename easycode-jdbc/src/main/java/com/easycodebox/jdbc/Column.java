package com.easycodebox.jdbc;

import java.io.Serializable;

/**
 * @author WangXiaoJin
 *
 */
public class Column implements Serializable {

	private String name;
	private String sqlName;
	private Class<?> type;
	private String sqlType;
	private boolean primaryKey;

	public Column() {
		
	}

	public Column(String columnName) {
		setName(columnName);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSqlName() {
		return sqlName;
	}

	public void setSqlName(String sqlName) {
		this.sqlName = sqlName;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String getSqlType() {
		return sqlType;
	}

	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean isPrimaryKey) {
		this.primaryKey = isPrimaryKey;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof Column && equals( (Column) object );
	}

	public boolean equals(Column column) {
		return null != column && (this == column || name.equals(column.name));
	}


}
