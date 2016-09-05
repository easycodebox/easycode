package com.easycodebox.common.jdbc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.validate.Assert;

/**
 * @author WangXiaoJin
 *
 */
public class Join implements Serializable {

	private static final long serialVersionUID = -4201707842197198405L;
	
	private Class<?> table;
	private String tableAlias;
	private JoinType joinType;
	private List<Property[]> joinColumns = new ArrayList<Property[]>(4);
	
	private Join(Class<?> table, String tableAlias, JoinType joinType) {
		this.table = table;
		this.tableAlias = tableAlias;
		this.joinType = joinType;
	}
	
	public static Join instance(Class<?> table, String tableAlias, JoinType joinType) {
		return new Join(table, tableAlias, joinType);
	}
	
	/**
	 * 默认关联类型是JoinType.INNER_JOIN
	 * @param table
	 * @param tableAlias
	 * @return
	 */
	public static Join instance(Class<?> table, String tableAlias) {
		return new Join(table, tableAlias, JoinType.INNER_JOIN);
	}
	
	/**
	 * 设置join sql语句中  on部分。当前table的主键和table关联表的referencedColumn列之间有关联关系
	 * 如果是联合主键关联，则首先查找当前table有没有referencedColumn列明，有则用它，没有就会报异常
	 * @param referencedColumn	当前table关联表的referencedColumn列
	 * @return
	 */
	public Join on(Property referencedColumn) {
		joinColumns.add(new Property[]{null, referencedColumn});
		return this;
	}
	
	/**
	 * 设置join sql语句中  on部分
	 * @param joinColumn	当前Join类中的属性。joinColumn可以为null，为null时则joinColumn就是当前table的主键
	 * @param referencedColumn	当前table关联表的referencedColumn列
	 * @return
	 */
	public Join on(Property joinColumn, Property referencedColumn) {
		joinColumns.add(new Property[]{joinColumn, referencedColumn});
		return this;
	}
	
	public String toSql(Table selfTable, Table referencedTable, String referencedTableAlias) {
		Dialect dialect = Configuration.dialect;
		String selfAlias = dialect.wrapQuote(tableAlias);
		StringBuilder sql = new StringBuilder();
		sql.append(dialect.joinSql(joinType)).append(Symbol.SPACE)
			.append( dialect.wrapQuote(selfTable.getName()) )
			.append(" AS ").append( selfAlias ).append(Symbol.SPACE);
		if(joinColumns.size() > 0) {
			sql.append("ON ");
			referencedTableAlias = dialect.wrapQuote(referencedTableAlias);
			for(int i = 0; i < joinColumns.size(); i++) {
				Property[] columns = joinColumns.get(i);
				Assert.length(columns, 2);
				
				Column selfColumn = null,
						referencedColumn = null;
				if(columns[0] == null) {
					//当columns[0] == null 时需要自动获取当前table的主键，并赋值给selfColumn
					List<PkColumn> pkColumns = selfTable.getPrimaryKeys();
					Assert.notEmpty(pkColumns, "Table {0} has no pk.", table.getName());
					if(pkColumns.size() == 1) {
						//如果当前table只有一个主键则此主键赋值给selfColumn
						selfColumn = pkColumns.get(0);
					} else if(pkColumns.size() > 0) {
						//当table有多个主键时，判断哪个主键的名字和columns[1].getPropertyName()相等，则此主键赋值给selfColumn
						for(PkColumn c : pkColumns) {
							if(c.getName().equals(columns[1].getPropertyName())) 
								selfColumn = c;
						}
					}
					referencedColumn = referencedTable.getColumn(columns[1].getPropertyName());
				}else {
					if(columns[0].getTable() == selfTable.getEntityType()) {
						selfColumn = selfTable.getColumn(columns[0].getPropertyName());
					}else if(columns[1].getTable() == selfTable.getEntityType()) {
						selfColumn = selfTable.getColumn(columns[1].getPropertyName());
					}
					if(columns[0].getTable() == referencedTable.getEntityType()) {
						referencedColumn = referencedTable.getColumn(columns[0].getPropertyName());
					}else if(columns[1].getTable() == referencedTable.getEntityType()) {
						referencedColumn = referencedTable.getColumn(columns[1].getPropertyName());
					}
				}
				if(selfColumn == null || referencedColumn == null)
					throw new BaseException("join params are not corresponding.({0}, {1}, {2}, {3}).", 
							columns[0].getTable(), columns[0].getPropertyName(), 
							columns[1].getTable(), columns[1].getPropertyName());
				
				sql.append(referencedTableAlias).append(Symbol.PERIOD)
					.append(dialect.wrapQuote(referencedColumn.getSqlName()))
					.append(" = ")
					.append(selfAlias).append(Symbol.PERIOD)
					.append(dialect.wrapQuote(selfColumn.getSqlName()))
					.append( i == joinColumns.size() - 1 ? Symbol.SPACE : " AND ");
			}
		}
		
		return sql.toString();
	}

	/**
	 * 关联表的entity class
	 * @return
	 */
	public Class<?> getTable() {
		return table;
	}

	public String getTableAlias() {
		return tableAlias;
	}

	public JoinType getJoinType() {
		return joinType;
	}

}
