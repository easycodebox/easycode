package com.easycodebox.jdbc;

import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.jdbc.config.Configuration;

import javax.persistence.JoinColumn;
import java.io.Serializable;

/**
 * @author WangXiaoJin
 *
 */
public class JoinColumnObj implements Serializable {
	
	private JoinColumnObj() {
		
	}
	
	private JoinColumnObj(JoinColumn joinColumn) {
		this.name = StringUtils.stripToNull(joinColumn.name());
		this.referencedColumnName = StringUtils.stripToNull(joinColumn.referencedColumnName());
		this.unique = joinColumn.unique();
		this.nullable = joinColumn.nullable();
		this.insertable = joinColumn.insertable();
		this.updatable = joinColumn.updatable();
		this.table = StringUtils.stripToNull(joinColumn.table());
	}
	
	public static JoinColumnObj instance() {
		return new JoinColumnObj();
	}
	
	public static JoinColumnObj transfer(JoinColumn joinColumn) {
		return new JoinColumnObj(joinColumn);
	}
	
	public static JoinColumnObj[] transfer(JoinColumn[] joinColumns) {
		JoinColumnObj[] objs = new JoinColumnObj[joinColumns.length];
		for(int i = 0; i < joinColumns.length; i++) {
			objs[i] = transfer(joinColumns[i]);
		}
		return objs;
	}
	
	/** 
     * 表的列名，既有外键的表的列名。默认为主表表名_id
     */
    private String name;
    /**
     * 外键引用的表的列名，默认为外键引用表的主键列名
     */
    private String referencedColumnName;

    private boolean unique = false;

    private boolean nullable = true;

    private boolean insertable = true;

    private boolean updatable = true;

    /**
     * (Optional) The name of the table that contains
     * the column. 如果此属性没有值，则自动使用有外键的表名
     */
    private String table;
    
    private Class<?> entity;
    
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReferencedColumnName() {
		return referencedColumnName;
	}

	public void setReferencedColumnName(String referencedColumnName) {
		this.referencedColumnName = referencedColumnName;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isInsertable() {
		return insertable;
	}

	public void setInsertable(boolean insertable) {
		this.insertable = insertable;
	}

	public boolean isUpdatable() {
		return updatable;
	}

	public void setUpdatable(boolean updatable) {
		this.updatable = updatable;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public Class<?> getEntity() {
		if(entity == null && StringUtils.isNotBlank(table)) {
			return entity = Configuration.getEntityByTableName(table);
		}
		return entity;
	}

	public void setEntity(Class<?> entity) {
		this.entity = entity;
	}

}
