package com.easycodebox.common.jdbc;

import java.io.Serializable;

import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;

/**
 * @author WangXiaoJin
 *
 */
public class Property implements Serializable {
	
	private static final long serialVersionUID = -8153752533287263315L;
	
	/**
	 * 表明此对象是原型,原型对象提供给R资源文件使用
	 */
	private transient boolean prototype;
	
	private String propertyName;
	private Class<?> table;
	private String tableAlias;
	private String alias;
	
	protected Property(String propertyName, Class<?> table, boolean prototype) {
		this.propertyName = propertyName;
		this.table = table;
		this.prototype = prototype;
	}
	
	/**
	 * 创建的此对象为原型对象
	 * @param propertyName
	 * @param table
	 * @return
	 */
	public static Property instance(String propertyName, Class<?> table) {
		return new Property( propertyName, table, true );
	}
	
	/**
	 * 
	 * @param propertyName
	 * @param table
	 * @param prototype prototype == true 时，调用as和ta方法时会重新创建一个相对应的Property对象
	 * @return
	 */
	public static Property instance(String propertyName, Class<?> table, boolean prototype) {
		return new Property( propertyName, table, prototype );
	}
	
	public Property as(String alias) {
		if(prototype) {
			return instance(propertyName, table, false).as(alias);
		}else {
			this.alias = alias;
			return this;
		}
	}
	
	/**
	 * 设置表别名前缀
	 * @param tableAlias
	 * @return
	 */
	public Property ta(String tableAlias) {
		if(prototype) {
			return instance(propertyName, table, false).ta(tableAlias);
		}else {
			this.tableAlias = tableAlias;
			return this;
		}
	}
	
	public String getPropertyName() {
		return propertyName;
	}

	public Class<?> getTable() {
		return table;
	}

	public String getTableAlias() {
		return tableAlias;
	}

	public String getAlias() {
		return alias;
	}

	/**
	 * 返回映射sql语句，例：s.name as shopName
	 * @return
	 */
	public String toProjectionSql(Table table) {
		Dialect dialect = Configuration.dialect;
		StringBuilder sql = new StringBuilder();
		if(StringUtils.isNotBlank(tableAlias))
			sql.append(dialect.wrapQuote(tableAlias)).append(Symbol.PERIOD);
		if(StringUtils.isNotBlank(propertyName))
			sql.append( dialect.wrapQuote( table.getColumn(propertyName).getSqlName() ) );
		else
			return Symbol.EMPTY;
		if(StringUtils.isNotBlank(alias))
			sql.append(" AS ").append(dialect.wrapQuote(alias));
		return sql.toString();
	}
	
	/**
	 * 返回作为条件的sql语句
	 * @return
	 */
	public String toConditionSql(Table table) {
		Dialect dialect = Configuration.dialect;
		StringBuilder sql = new StringBuilder();
		if(StringUtils.isNotBlank(tableAlias))
			sql.append(dialect.wrapQuote(tableAlias)).append(Symbol.PERIOD);
		if(StringUtils.isNotBlank(propertyName))
			sql.append( dialect.wrapQuote( table.getColumn(propertyName).getSqlName() ) );
		else
			return Symbol.EMPTY;
		return sql.toString();
	}

}
