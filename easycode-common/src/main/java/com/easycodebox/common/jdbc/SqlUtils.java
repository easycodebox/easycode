package com.easycodebox.common.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.lang.DateUtils;
import com.easycodebox.common.lang.Symbol;

/**
 * @author WangXiaoJin
 * 
 */
public class SqlUtils {
	
	@SuppressWarnings("rawtypes")
	public static String value2Sql(Object o){
		String str = null;
		if(o == null)
			str = "null";
		else if(o instanceof String || o instanceof StringBuffer || 
				o instanceof StringBuilder) 
			str = Configuration.dialect.escapeString(o.toString());
		else if(o instanceof Date)
			str = "'" + DateUtils.DATE_FMT.format((Date)o) + "'";
		else if(o instanceof Calendar)
			str = "'" + DateUtils.DATE_FMT.format(((Calendar)o).getTime()) + "'";
		else if(o instanceof Object[]) {
			str = " ( ";
			Object[] tmp = (Object[])o;
			for(Object ot: tmp) 
				str += value2Sql(ot) + ",";
			str = str.substring(0, str.length()-1);
			str += " ) ";
		}else if(o instanceof Collection) {
			str = value2Sql(((Collection)o).toArray());
		}else if(o instanceof DetailEnum){
			str = value2Sql(((DetailEnum)o).getValue());
		}else
			str = o.toString();
		return str;
	}
	
	public static String format(String propertyName, Object value, String op) {
		return Symbol.SPACE + propertyName + Symbol.SPACE + op + Symbol.SPACE + value2Sql(value) + Symbol.SPACE;
	}
	
	/**
	 * value 不转化为sql变量形式，使用原生值
	 * @param propertyName
	 * @param value
	 * @param op
	 * @return
	 */
	public static String formatRawVal(String propertyName, Object value, String op) {
		return Symbol.SPACE + propertyName + Symbol.SPACE + op + Symbol.SPACE + (value == null ? "" : value) + Symbol.SPACE;
	}
	
	/**
	 * 组装sql语句的名字。例："s","shop"  ==> "s.shop"
	 * @param names
	 * @return
	 */
	public static String joinByDot(String... names) {
		if(names == null || names.length == 0) return Symbol.EMPTY;
		if(names.length == 1) return names[0];
		StringBuilder name = new StringBuilder();
		for(int i = 0; i < names.length; i++) {
			if(names[i] != null)
				name.append(names[i]).append( i == names.length - 1 ? Symbol.EMPTY : Symbol.PERIOD);
		}
		return name.toString();
	}
	
	/**
	 * 组装sql语句的名字。例："s","shop"  ==> "s.shop"
	 * @param names
	 * @return
	 */
	public static String joinByDot(Property... properties) {
		if(properties == null 
				|| properties.length == 0
				|| (properties.length == 1 && properties[0] == null)) return Symbol.EMPTY;
		if(properties.length == 1) return properties[0].getPropertyName();
		StringBuilder name = new StringBuilder();
		for(int i = 0; i < properties.length; i++) {
			if(properties[i] != null)
				name.append(properties[i].getPropertyName())
					.append( i == properties.length - 1 ? Symbol.EMPTY : Symbol.PERIOD);
		}
		return name.toString();
	}
	
	public static String getInsertSql(Object entity,Table table, boolean includePk) 
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		StringBuilder sql = new StringBuilder(),
					columnsStr = new StringBuilder(Symbol.L_PARENTHESIS),
					valuesStr = new StringBuilder(Symbol.L_PARENTHESIS);
		sql.append(" INSERT INTO ").append(Configuration.dialect
				.wrapQuote(table.getName()) + Symbol.SPACE);
		
		Collection<Column> columns = table.getColumns().values();
		for(Iterator<Column> ite = columns.iterator();ite.hasNext();) {
			Column column = ite.next();
			if(column.isPrimaryKey() && !includePk) 
				continue;
			columnsStr.append(Symbol.SPACE + Configuration.dialect
					.wrapQuote(column.getSqlName()) + (ite.hasNext() ? Symbol.COMMA : Symbol.EMPTY));
			
			Object data = PropertyUtils.getProperty(entity, column.getName());
			valuesStr.append(Symbol.SPACE + value2Sql(data) + (ite.hasNext() ? Symbol.COMMA : Symbol.EMPTY));
		}
		
		columnsStr.append(Symbol.R_PARENTHESIS);
		valuesStr.append(Symbol.R_PARENTHESIS);
		
		return sql.append(columnsStr).append(" VALUES").append(valuesStr).toString();
	}
	
	public static String getUpdateSql(Object entity, Table table) 
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		StringBuilder sql = new StringBuilder(),
				where = new StringBuilder("WHERE ");
		List<PkColumn> pks = table.getPrimaryKeys();
		if(pks.size() == 0)
			throw new BaseException("{0} class has no primary key, can not update it.", entity.getClass());
		for(int i = 0; i < pks.size(); i++) {
			PkColumn c = pks.get(i);
			Object data = PropertyUtils.getProperty(entity, c.getName());
			if(data != null) {
				where.append(i == 0 ? Symbol.EMPTY : (Symbol.AND + Symbol.SPACE))
					.append(Configuration.dialect
						.wrapQuote(c.getSqlName()) + Symbol.SPACE)
					.append(Symbol.EQ)
					.append(value2Sql(data))
					.append(Symbol.SPACE);
			}else {
				throw new BaseException("cant not update entity, {0} class primary key {1} has not init value.", entity.getClass(), c.getName());
			}
		}
		
		sql.append("UPDATE ")
			.append(Configuration.dialect.wrapQuote(table.getName()) + Symbol.SPACE)
			.append("SET ");
		
		Collection<Column> columns = table.getColumns().values();
		for(Iterator<Column> ite = columns.iterator();ite.hasNext();) {
			Column column = ite.next();
			if(column.isPrimaryKey()) 
				continue;
			sql.append(Configuration.dialect
					.wrapQuote(column.getSqlName()) + Symbol.EQ);
			Object data = PropertyUtils.getProperty(entity, column.getName());
			sql.append(value2Sql(data) + (ite.hasNext() ? Symbol.COMMA : Symbol.EMPTY) + Symbol.SPACE);
		}
		
		return sql.append(where).toString();
	}
	

}
