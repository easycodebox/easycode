package com.easycodebox.common.jdbc;


/**
 * @author WangXiaoJin
 *
 */
public class ManyToMany extends AssociatedColumn {

	private static final long serialVersionUID = 6233743566544288945L;
	
	private String joinTableName;
	private String catalog;
	private String schema;
	private JoinColumnObj[] inverseJoinColumns;	
	
	public String getJoinTableName() {
		return joinTableName;
	}

	public void setJoinTableName(String joinTableName) {
		this.joinTableName = joinTableName;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public JoinColumnObj[] getInverseJoinColumns() {
		return inverseJoinColumns;
	}

	public void setInverseJoinColumns(JoinColumnObj[] inverseJoinColumns) {
		this.inverseJoinColumns = inverseJoinColumns;
	}

}
