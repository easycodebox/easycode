package com.easycodebox.jdbc.grammar;

import static com.easycodebox.common.validate.Assert.isFalse;
import static com.easycodebox.common.validate.Assert.notBlank;
import static com.easycodebox.common.validate.Assert.notNull;
import static com.easycodebox.jdbc.util.SqlUtils.format;
import static com.easycodebox.jdbc.util.SqlUtils.formatRawVal;
import static com.easycodebox.jdbc.util.SqlUtils.joinByDot;
import static com.easycodebox.jdbc.util.SqlUtils.value2Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.jdbc.AssociatedColumn;
import com.easycodebox.jdbc.Join;
import com.easycodebox.jdbc.JoinColumnObj;
import com.easycodebox.jdbc.JoinType;
import com.easycodebox.jdbc.LockMode;
import com.easycodebox.jdbc.ManyToMany;
import com.easycodebox.jdbc.OneToMany;
import com.easycodebox.jdbc.Property;
import com.easycodebox.jdbc.SqlCommandType;
import com.easycodebox.jdbc.Table;
import com.easycodebox.jdbc.config.Configuration;
import com.easycodebox.jdbc.dialect.Dialect;

/**
 * @author WangXiaoJin
 * 
 */
public class SqlGrammar implements Cloneable {
	
	protected static final String MASTER_ALIAS = "_this";
	private Dialect dialect = Configuration.dialect;
	
	/**
	 * 主表
	 */
	private Class<?> entity;

	private StringBuilder updateSql = new StringBuilder();
	private StringBuilder projection = new StringBuilder();
	private StringBuilder condition = new StringBuilder();
	private StringBuilder group = new StringBuilder();
	private StringBuilder having = new StringBuilder();
	private StringBuilder order = new StringBuilder();
	private Integer limitStart;
	private Integer limitSize; 	//获取的条数
	private Integer partIndex;//分流式分页的当前分流索引值
	private Integer partSize;//分流式分页一次分流几条数据
	private LockMode lockMode = LockMode.NONE;
	//创建sql时是不是count语句
	private boolean isCountSql = false;
	
	/**
	 * 关联表
	 */
	private List<Join> joins;
	/**
	 * 表的别名
	 */
	private Map<Class<?>, String> tableAliases = new HashMap<Class<?>, String>(4);
	private Map<Class<?>, Table> tables = new HashMap<Class<?>, Table>(4);
	
	//like方法时LIKE_L 只有左边加"%",LIKE_R只有右边加,LIKE_A左右都加
	public static final short LIKE_A = 0;
	public static final short LIKE_L = 1;
	public static final short LIKE_R = 2;
	
	/*************** jdbc控制参数 *************************/
	private String databaseId;
	private String keyProperty;
	private String keyColumn;
	private String resultSets;
	private Integer fetchSize;
	private String lang;
	private boolean resultOrdered = false;
	
	protected SqlGrammar(Class<?> entity, String alias) {
		this.entity = entity;
		tableAliases.put(entity, alias);
		Table table = Configuration.getTable(entity);
		notNull(table);
		tables.put(entity, table);
	}
	
	public static SqlGrammar instance(Class<?> entity) {
		return instance(entity, MASTER_ALIAS);
	}
	
	public static SqlGrammar instance(Class<?> entity, String alias) {
		return new SqlGrammar(entity, alias);
	}
	
	/**
	 * 如果用到关联查询时，此方法必须放在上面，instance方法下面紧接着就是此方法。
	 * @param join
	 * @return
	 */
	public SqlGrammar join(Join join) {
		if(join != null) {
			Table table = Configuration.getTable(join.getTable());
			notNull(table);
			tables.put(join.getTable(), table);
			tableAliases.put(join.getTable(), join.getTableAlias());
			joins = joins == null ? new ArrayList<Join>(4) : joins;
			joins.add(join);
		}
		return this;
	}
	
	/**
	 * 默认使用INNER_JOIN
	 * 如果用到关联查询时，此方法必须放在上面，instance方法下面紧接着就是此方法。
	 * @param property
	 * @param tableAlias
	 * @return
	 */
	public SqlGrammar join(Property property, String tableAlias) {
		return join(property, tableAlias, JoinType.INNER_JOIN);
	}
	
	/**
	 * 如果用到关联查询时，此方法必须放在上面，instance方法下面紧接着就是此方法。
	 * @param property
	 * @param tableAlias
	 * @return
	 */
	public SqlGrammar join(Property property, String tableAlias, JoinType joinType) {
		notNull(property, "property can not be null.");
		notBlank(tableAlias, "tableAlias can not be blank.");
		notNull(joinType, "joinType can not be null.");
		Table thisTable = tables.get(entity);
		AssociatedColumn associate = thisTable.getAssociatedColumns().get(property.getPropertyName());
		notNull(associate, "Class {0} has no property named {1}", entity, property.getPropertyName());
		
		Join join = Join.instance(associate.getAssociatedClass(), tableAlias, joinType);
		JoinColumnObj[] columns = associate.getJoinColumns();
		if(associate.getMappedBy() != null) {
			Table tmpTable = Configuration.getTable(associate.getAssociatedClass());
			AssociatedColumn ac = tmpTable.getAssociatedColumns().get(associate.getMappedBy());
			columns = ac.getJoinColumns();
		}
		if(columns != null) {
			if(associate instanceof ManyToMany) {
				//执行manyToMany逻辑
				//缺少manyToMany逻辑
			}else {
				for(JoinColumnObj column : columns) {
					
					Property col =  Property.instance(column.getName(), 
							associate instanceof OneToMany || associate.getMappedBy() != null
								? associate.getAssociatedClass() : entity, false);
					Property refCol =  Property.instance(
							column.getReferencedColumnName(), 
							associate instanceof OneToMany  || associate.getMappedBy() != null
								? entity : associate.getAssociatedClass(), false);
					join.on(col, refCol);
				}
			}
		}
		join(join);
		return this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public SqlGrammar clone() {
		SqlGrammar sqlGrammar = null;
		try {
			sqlGrammar = (SqlGrammar)super.clone();
			sqlGrammar.joins = (List<Join>)(((ArrayList<Join>)joins).clone());
			sqlGrammar.tableAliases = (Map<Class<?>, String>)(((HashMap<Class<?>, String>)tableAliases).clone());
			sqlGrammar.tables = (Map<Class<?>, Table>)(((HashMap<Class<?>, Table>)tables).clone());
		} catch (CloneNotSupportedException e) {
		    //throw new InternalError();
		}
		return sqlGrammar;
	}
	
	/**
	 * @param property
	 * @param value    为null时则不添加该sql语句
	 * @return
	 */
	public SqlGrammar update(Property property, Object value){
		if(property != null && value != null) 
			updateNeed(property, value);
		return this;
	}
	
	/**
	 * ast(Assert)参数value值
	 * @param property
	 * @param value    当value == null 抛异常
	 * @return
	 */
	public SqlGrammar updateAst(Property property, Object value){
		notNull(value, "value can not be null.");
		return updateNeed(property, value);
	}
	
	/**
	 * @param property
	 * @param value		可以为null,为null时set property=null
	 * @return
	 */
	public SqlGrammar updateNeed(Property property, Object value){
		if(property != null) 
			updateSqlAppend(format(convert2SqlName(property), value, Symbol.EQ));
		return this;
	}
	
	/**
	 * value不经过任何处理直接传给生气了语句
	 * 用来设置null值，updateByRawVal("curDlvAddr", "null")
	 * updateByRawVal(R.Shop.orderNum, "#{orderNum} + 1")
	 * @param property
	 * @param value
	 * @return
	 */
	public SqlGrammar updateByRawVal(Property property, Object value){
		if(property != null && value != null) {
			if(value instanceof String) {
				value = convertPropertySql(value.toString());
			}
			updateSqlAppend(formatRawVal(convert2SqlName(property), value, Symbol.EQ));
		}
		return this;
	}
	
	/**
	 * 生成sql语句的投影列, 例如 a.* , _this.* 
	 * @return
	 */
	public SqlGrammar columnAll(Class<?> tableClass) {
		String projection = Symbol.ASTERISK;
		if(tableClass != null && joins != null && joins.size() > 0) {
			projection = joinByDot(
					dialect.wrapQuote(
							tableAliases.get(tableClass)
					),
					projection
			);
		}
		projectionAppend(projection);
		return this;
	}
	
	/**
	 * 生成sql语句的投影列
	 * @param property
	 * @return
	 */
	public SqlGrammar column(Property property) {
		return column(property, Symbol.EMPTY);
	}
	
	/**
	 * 生成sql语句的投影列
	 * @param property
	 * @return
	 */
	public SqlGrammar column(Property property, Property alias) {
		return column(property, alias == null ? Symbol.EMPTY : alias.getPropertyName());
	}
	
	/**
	 * 生成sql语句的投影列
	 * @param property
	 * @param alias		列的别名
	 * @return
	 */
	public SqlGrammar column(Property property, String alias) {
		if(property != null)  {
			if(StringUtils.isNotBlank(alias))
				projectionAppend(convert2SqlName(property) + " AS " + dialect.wrapQuote(alias));
			else
				projectionAppend(convert2SqlName(property));
		}
		return this;
	}
	
	public SqlGrammar max(Property property) {
		return max(property, null);
	}
	
	public SqlGrammar max(Property property, String alias) {
		if(property != null)  {
			if(StringUtils.isNotBlank(alias))
				projectionAppend("MAX(" + convert2SqlName(property) + ") AS " + dialect.wrapQuote(alias));
			else
				projectionAppend("MAX(" + convert2SqlName(property) + ")");
		}
		return this;
	}
	
	public SqlGrammar min(Property property) {
		return min(property, null);
	}
	
	public SqlGrammar min(Property property, String alias) {
		if(property != null)  {
			if(StringUtils.isNotBlank(alias))
				projectionAppend("MIN(" + convert2SqlName(property) + ") AS " + dialect.wrapQuote(alias));
			else
				projectionAppend("MIN(" + convert2SqlName(property) + ")");
		}
		return this;
	}
	
	public SqlGrammar distinct(Property property) {
		return distinct(property, null);
	}
	
	public SqlGrammar distinct(Property property, String alias) {
		if(property != null)  {
			if(StringUtils.isNotBlank(alias))
				projectionAppend("DISTINCT(" + convert2SqlName(property) + ") AS " + dialect.wrapQuote(alias));
			else
				projectionAppend("DISTINCT(" + convert2SqlName(property) + ")");
		}
		return this;
	}
	
	public SqlGrammar count(Property property) {
		return count(property, null);
	}
	
	public SqlGrammar count(Property property, String alias) {
		if(property != null)  {
			if(StringUtils.isNotBlank(alias))
				projectionAppend("COUNT(" + convert2SqlName(property) + ") AS " + dialect.wrapQuote(alias));
			else
				projectionAppend("COUNT(" + convert2SqlName(property) + ")");
		}
		return this;
	}
	
	public SqlGrammar sum(Property property) {
		return sum(property, null);
	}
	
	public SqlGrammar sum(Property property, String alias) {
		if(property != null)  {
			if(StringUtils.isNotBlank(alias))
				projectionAppend("SUM(" + convert2SqlName(property) + ") AS " + dialect.wrapQuote(alias));
			else
				projectionAppend("SUM(" + convert2SqlName(property) + ")");
		}
		return this;
	}
	
	public SqlGrammar countDistinct(Property property) {
		return countDistinct(property, null);
	}
	
	public SqlGrammar countDistinct(Property property, String alias) {
		if(property != null)  {
			if(StringUtils.isNotBlank(alias))
				projectionAppend("COUNT(DISTINCT " + convert2SqlName(property) + ") AS " + dialect.wrapQuote(alias));
			else
				projectionAppend("COUNT(DISTINCT " + convert2SqlName(property) + ")");
		}
		return this;
	}
	
	public SqlGrammar avg(Property property) {
		return avg(property, null);
	}
	
	public SqlGrammar avg(Property property, String alias) {
		if(property != null)  {
			if(StringUtils.isNotBlank(alias))
				projectionAppend("AVG(" + convert2SqlName(property) + ") AS " + dialect.wrapQuote(alias));
			else
				projectionAppend("AVG(" + convert2SqlName(property) + ")");
		}
		return this;
	}
	
	public SqlGrammar rowCount() {
		return rowCount(null);
	}
	
	public SqlGrammar rowCount(String alias) {
		if(StringUtils.isNotBlank(alias))
			projectionAppend("COUNT(*) AS " + dialect.wrapQuote(alias));
		else
			projectionAppend("COUNT(*)");
		return this;
	}
	
	/**
	 * ast(Assert)参数value值
	 * 判断property == value， 当value == null 抛异常
	 * @param property
	 * @param value
	 * @return
	 */
	public SqlGrammar eqAst(Property property, Object value){
		notNull(value, "value can not be null.");
		return eq(property, value);
	}
	
	/**
	 * 判断property == value
	 * @param property
	 * @param value
	 * @return
	 */
	public SqlGrammar eq(Property property, Object value){
		if(property != null && value != null)
			conditionAppend(format(convert2SqlName(property), value, Symbol.EQ));
		return this;
	}
	
	/**
	 * ast(Assert)参数value值
	 * not equal( != ) ,当value == null 抛异常
	 * @param property
	 * @param value
	 * @return
	 */
	public SqlGrammar neAst(Property property, Object value){
		notNull(value, "value can not be null.");
		return ne(property, value);
	}
	
	/**
	 * not equal( != ) 
	 * @param property
	 * @param value
	 * @return
	 */
	public SqlGrammar ne(Property property, Object value){
		if(property != null && value != null)
			conditionAppend(format(convert2SqlName(property), value, Symbol.NE));
		return this;
	}
	
	/**
	 * great than( > ) 
	 * @param property
	 * @param value
	 * @return
	 */
	public SqlGrammar gt(Property property, Object value){
		if(property != null && value != null)
			conditionAppend(format(convert2SqlName(property), value, Symbol.GT));
		return this;
	}
	
	/**
	 * great equal( >= ) 
	 * @param property
	 * @param value
	 * @return
	 */
	public SqlGrammar ge(Property property, Object value){
		if(property != null && value != null)
			conditionAppend(format(convert2SqlName(property), value, Symbol.GE));
		return this;
	}
	
	/**
	 * less than( < ) 
	 * @param property
	 * @param value
	 * @return
	 */
	public SqlGrammar lt(Property property, Object value){
		if(property != null && value != null)
			conditionAppend(format(convert2SqlName(property), value, Symbol.LT));
		return this;
	}
	
	/**
	 * less equal( <= ) 
	 * @param property
	 * @param value
	 * @return
	 */
	public SqlGrammar le(Property property, Object value){
		if(property != null && value != null)
			conditionAppend(format(convert2SqlName(property), value, Symbol.LE));
		return this;
	}
	
	public SqlGrammar between(Property property, Object lo, Object hi) {
		if(property != null && lo != null && hi != null) {
			conditionAppend(convert2SqlName(property) + Symbol.SPACE + Symbol.BETWEEN + Symbol.SPACE
					+ value2Sql(lo) + Symbol.SPACE 
					+ Symbol.AND + Symbol.SPACE + value2Sql(hi));
		}
		return this;
	}
	
	/**
	 * values = null 时不增加此条件，但能为 []（空数组）
	 * @param property
	 * @param values
	 * @return
	 */
	public SqlGrammar in(Property property, Object[] values) {
		if(property != null && values != null) {
			isFalse(values.length == 0, "数组参数values不能为空数组");
			conditionAppend(format(convert2SqlName(property), values , "IN"));
		}
		return this;
	}
	
	public SqlGrammar notIn(Property property, Object[] values) {
		if(property != null && values != null) {
			isFalse(values.length == 0, "数组参数values不能为空数组");
			conditionAppend(format(convert2SqlName(property), values , "NOT IN"));
		}
		return this;
	}
	
	@SuppressWarnings("rawtypes")
	public SqlGrammar in(Property property, Collection value) {
		if(property != null && value != null) {
			isFalse(value.size() == 0, "集合参数value不能为空集合");
			conditionAppend(format(convert2SqlName(property), value , "IN"));
		}
		return this;
	}
	
	@SuppressWarnings("rawtypes")
	public SqlGrammar notIn(Property property, Collection value) {
		if(property != null && value != null) {
			isFalse(value.size() == 0, "集合参数value不能为空集合");
			conditionAppend(format(convert2SqlName(property), value , "NOT IN"));
		}
		return this;
	}
	
	/**
	 * 如果value等于null则此条件不添加进SQL <br>
	 * 当value等于空字符窜时，效果等于查询出不为null的数据
	 * @param property
	 * @param value
	 * @return
	 */
	public SqlGrammar like(Property property, String value) {
		return this.like(property, value, LIKE_A);
	}
	
	/**
	 * 会对value执行trim操作，操作后的值如果为null或空字符窜则sql会忽略此like条件
	 * @param property
	 * @param value
	 * @return
	 */
	public SqlGrammar likeTrim(Property property, String value) {
		return this.likeTrim(property, value, LIKE_A);
	}
	
	/**
	 * 会对value执行trim操作，操作后的值如果为null或空字符窜则sql会忽略此like条件
	 * @param property
	 * @param value
	 * @return
	 */
	public SqlGrammar likeTrim(Property property, String value, short type) {
		value = StringUtils.trimToNull(value);
		return this.like(property, value, type);
	}
	
	/**
	 * 如果value等于null则此条件不添加进SQL <br>
	 * 当value等于空字符窜时，效果等于查询出不为null的数据
	 * @param property
	 * @param value
	 * @param type
	 * @return
	 */
	public SqlGrammar like(Property property, String value, short type) {
		if(property != null && value != null) {
			switch(type) {
				case LIKE_A : value = Symbol.PERCENT + value + Symbol.PERCENT; break;
				case LIKE_L : value = Symbol.PERCENT + value; break;
				case LIKE_R : value = value + Symbol.PERCENT; break;
			}
			conditionAppend(format(convert2SqlName(property), value, "LIKE"));
		}
		return this;
	}
	
	/**
	 * 获取grammar所有的条件 把 xxx = ? and yyy = ? 转换成 and(xxx = ? and yyy = ?)
	 * @return
	 */
	public SqlGrammar and(SqlGrammar grammar) {
		if(grammar != null 
				&& grammar.getCondition().length() > 0)
			conditionAppend(Symbol.L_PARENTHESIS + grammar.getCondition() + Symbol.R_PARENTHESIS);
		return this;
	}
	
	/**
	 * 如果grammar里面只有一个条件语句，不需要用此方法，
	 * 即使用了也效果和 and(xxx = ?) 一样
	 * 多个条件的效果 如下：and(xxx = ? or yyy = ?)  
	 * @return
	 */
	public SqlGrammar or(SqlGrammar grammar) {
		if(grammar != null 
				&& grammar.getCondition().length() > 0)
			conditionAppend(Symbol.L_PARENTHESIS 
					+ grammar.getCondition().toString()
						.replaceAll("(?i)(?<!between)(\\s+\\S+)\\s+and\\s+", 
								"$1 " + Symbol.OR + Symbol.SPACE)
					+ Symbol.R_PARENTHESIS);
		return this;
	}
	
	public SqlGrammar isNull(Property property) {
		if(property != null) {
			conditionAppend(formatRawVal(convert2SqlName(property), null, "IS NULL"));
		}
		return this;
	}
	
	/**
	 * 如果condition为true时，添加property is null 条件
	 * @param property
	 * @param condition
	 * @return
	 */
	public SqlGrammar isNull(Property property, boolean condition) {
		if(condition) isNull(property);
		return this;
	}
	
	public SqlGrammar isNotNull(Property property) {
		if(property != null) {
			conditionAppend(formatRawVal(convert2SqlName(property), null, "IS NOT NULL"));
		}
		return this;
	}
	
	/**
	 * 如果condition为true时，添加property is not null 条件
	 * @param property
	 * @param condition
	 * @return
	 */
	public SqlGrammar isNotNull(Property property, boolean condition) {
		if(condition) isNotNull(property);
		return this;
	}
	
	public SqlGrammar group(Property property) {
		if(property != null)  {
			groupAppend(convert2SqlName(property));
		}
		return this;
	}
	
	public SqlGrammar having(SqlGrammar grammar) {
		if(grammar != null && grammar.getCondition().length() > 0)  {
			havingAppend(grammar.getCondition().toString());
		}
		return this;
	}
	
	public SqlGrammar desc(Property property) {
		if(property != null)  {
			orderAppend(convert2SqlName(property) + " DESC");
		}
		return this;
	}
	
	public SqlGrammar asc(Property property) {
		if(property != null)  {
			orderAppend(convert2SqlName(property) + " ASC");
		}
		return this;
	}
	
	/**
	 * @param limitSize
	 * @return
	 */
	public SqlGrammar limitByIndex(Integer limitSize) {
		return limitByIndex(null, limitSize);
	}
	
	/**
	 * @param limitStart	默认值0
	 * @param limitSize
	 * @return
	 */
	public SqlGrammar limitByIndex(Integer limitStart, Integer limitSize) {
		this.limitStart = limitSize != null && limitStart == null ? new Integer(0) : limitStart;
		this.limitSize = limitSize;
		this.partIndex = null;
		this.partSize = null;
		return this;
	}
	
	/**
	 * 
	 * @param pageNo	默认为1
	 * @param pageSize
	 * @return
	 */
	public SqlGrammar limit(Integer pageNo, Integer pageSize) {
		return limit(pageNo, pageSize, null, null);
	}
	
	/**
	 * 
	 * @param pageNo	默认1
	 * @param pageSize
	 * @param partIndex	默认1
	 * @param partSize
	 * @return
	 */
	public SqlGrammar limit(Integer pageNo, Integer pageSize,
			Integer partIndex, Integer partSize) {
		pageNo = pageNo == null ? new Integer(1) : pageNo;
		partIndex = partSize != null && partIndex == null ? new Integer(1) : partIndex;
		if(pageSize != null) {
			this.limitStart = DataPage.getStartOfPage(pageNo, pageSize, 
					partIndex, partSize);
			this.limitSize = DataPage.getObtainSize(pageNo, pageSize, 
					partIndex, partSize);
			this.partIndex = partIndex;
			this.partSize = partSize;
		}
		return this;
	}
	
	public SqlGrammar lockMode(LockMode lockMode) {
		if(lockMode != null)
			this.lockMode = lockMode;
		return this;
	}
	
	/**
	 * 通过现有的SqlGrammar对象生成SQL语句
	 * @return
	 */
	public String buildSql(SqlCommandType type) {
		String wrapTableName = dialect.wrapQuote(tables.get(entity).getName()) 
				+ ( joins != null && joins.size() > 0 ? " AS " + dialect.wrapQuote(tableAliases.get(entity)) + Symbol.SPACE : Symbol.SPACE);
		switch (type) {
			case INSERT:
				break;
			case SELECT:
				if(isCountSql)
					return "SELECT COUNT(*)"
							+ " FROM " + wrapTableName
							+ getJoinSql()
							+ (condition.length() > 0 ? " WHERE " + condition : "")
							+ (group.length() > 0 ? " GROUP BY " + group : "")
							+ (having.length() > 0 ? " HAVING " + having : "");
				else
					return "SELECT " + (projection.length() > 0 ? projection : " * ")
						+ " FROM " + wrapTableName
						+ getJoinSql()
						+ (condition.length() > 0 ? " WHERE " + condition : "")
						+ (group.length() > 0 ? " GROUP BY " + group : "")
						+ (having.length() > 0 ? " HAVING " + having : "")
						+ (order.length() > 0 ? " ORDER BY " + order : "")
						+ buildLimitSql()
						+ (lockMode == LockMode.UPGRADE ? " FOR UPDATE" : lockMode == LockMode.UPGRADE_NOWAIT ? " FOR UPDATE NOWAIT" : "" );
			case UPDATE:
				if(updateSql.length() == 0) break;
				return "UPDATE " + wrapTableName
						+ (" SET " + updateSql)
						+ (condition.length() > 0 ? " WHERE " + condition : "");
			case DELETE:
				return "DELETE FROM " + wrapTableName
						+ (condition.length() > 0 ? " WHERE " + condition : "");
			default:
				break;
		}
		return null;
	}
	
	private String buildLimitSql() {
		if(limitSize != null) {
			return " LIMIT " + (limitStart == null ? "" : limitStart + ", ") + limitSize;
		}
		return "";	
	}
	
	/**
	 * update shop set num = #{num} + 1 where id = 5;
	 * @param property
	 * @return
	 */
	private String convertPropertySql(String sql) {
		if(StringUtils.isBlank(sql)) return sql;
		Pattern p = Pattern.compile("#\\{\\s*([\\w\\.\\_]+)\\s*\\}");
		Matcher matcher = p.matcher(sql);
		boolean result = matcher.find();
		if (result) {
			StringBuffer sb = new StringBuffer();
			do {
				matcher.appendReplacement(sb, convert2SqlName(entity, matcher.group(1)));
				result = matcher.find();
			} while (result);
			matcher.appendTail(sb);
			return sb.toString();
		}
		return sql;
	}
	
	/**
	 * 把javaBean属性转换成相对应的表的列名
	 * @param property
	 * @return
	 */
	protected String convert2SqlName(Property property) {
		if(property == null)
			return null;
		return convert2SqlName(property.getTable(), property.getPropertyName());
	}
	
	/**
	 * 把javaBean属性转换成相对应的表的列名
	 * @param property
	 * @return
	 */
	protected String convert2SqlName(Class<?> tableClass, String propertyName) {
		String column = dialect.wrapQuote(
							tables.get(tableClass)
							.getColumn(propertyName)
							.getSqlName()
						);
		if(joins != null && joins.size() > 0) {
			return joinByDot(
					dialect.wrapQuote(
							tableAliases.get(tableClass)
					),
					column
			);
		}
		return column;
	}
	
	private String getJoinSql() {
		if(joins != null && joins.size() > 0) {
			StringBuilder sql = new StringBuilder();
			for(Join join : joins)
				sql.append(join.toSql(tables.get(join.getTable()), tables.get(entity), tableAliases.get(entity)));
			return sql.toString();
		}
		return Symbol.EMPTY;
	}
	
	private void updateSqlAppend(String sql){
		if(updateSql.length() > 0)
			updateSql.append(Symbol.COMMA + sql);
		else
			updateSql.append(sql);
	}
	
	private void conditionAppend(String sql){
		if(condition.length() > 0)
			condition.append(Symbol.SPACE + Symbol.AND + Symbol.SPACE + sql);
		else
			condition.append(sql);
	}
	
	private void groupAppend(String sql){
		if(group.length() > 0)
			group.append(Symbol.COMMA + Symbol.SPACE + sql);
		else
			group.append(Symbol.SPACE + sql);
	}
	
	private void havingAppend(String sql){
		having.append(Symbol.SPACE + sql);
	}
	
	private void orderAppend(String sql){
		if(order.length() > 0)
			order.append(Symbol.COMMA + Symbol.SPACE + sql);
		else
			order.append(Symbol.SPACE + sql);
	}
	
	private void projectionAppend(String sql){
		if(projection.length() > 0)
			projection.append(Symbol.COMMA + sql);
		else
			projection.append(sql);
	}
	
	public Class<?> getEntity() {
		return entity;
	}

	public StringBuilder getUpdateSql() {
		return updateSql;
	}

	public void setUpdateSql(StringBuilder updateSql) {
		this.updateSql = updateSql;
	}

	public StringBuilder getProjection() {
		return projection;
	}

	public void setProjection(StringBuilder projection) {
		this.projection = projection;
	}

	public StringBuilder getCondition() {
		return condition;
	}

	public void setCondition(StringBuilder condition) {
		this.condition = condition;
	}

	public StringBuilder getGroup() {
		return group;
	}

	public void setGroup(StringBuilder group) {
		this.group = group;
	}

	public StringBuilder getHaving() {
		return having;
	}

	public void setHaving(StringBuilder having) {
		this.having = having;
	}

	public StringBuilder getOrder() {
		return order;
	}

	public void setOrder(StringBuilder order) {
		this.order = order;
	}
	
	public boolean isCountSql() {
		return isCountSql;
	}

	public void countSql(boolean isCountSql) {
		this.isCountSql = isCountSql;
	}
	
	public Integer getPageNo() {
		if(limitStart != null && limitSize != null)
			return DataPage.getPageNo(limitStart, limitSize);
		else
			return null;
	}

	public Integer getPageSize() {
		return limitSize;
	}
	
	public Integer getLimitStart() {
		return limitStart;
	}

	public Integer getLimitSize() {
		return limitSize;
	}

	public Integer getPartIndex() {
		return partIndex;
	}

	public Integer getPartSize() {
		return partSize;
	}

	/*************** jdbc控制参数 *************************/
	
	public LockMode getLockMode() {
		return lockMode;
	}
	
	public String getDatabaseId() {
		return databaseId;
	}

	public SqlGrammar databaseId(String databaseId) {
		this.databaseId = databaseId;
		return this;
	}

	public String getKeyProperty() {
		return keyProperty;
	}

	public SqlGrammar keyProperty(String keyProperty) {
		this.keyProperty = keyProperty;
		return this;
	}

	public String getKeyColumn() {
		return keyColumn;
	}

	public SqlGrammar keyColumn(String keyColumn) {
		this.keyColumn = keyColumn;
		return this;
	}

	public String getResultSets() {
		return resultSets;
	}

	public SqlGrammar resultSets(String resultSets) {
		this.resultSets = resultSets;
		return this;
	}

	public Integer getFetchSize() {
		return fetchSize;
	}

	public SqlGrammar fetchSize(Integer fetchSize) {
		this.fetchSize = fetchSize;
		return this;
	}

	public String getLang() {
		return lang;
	}

	public SqlGrammar lang(String lang) {
		this.lang = lang;
		return this;
	}

	public boolean isResultOrdered() {
		return resultOrdered;
	}

	public SqlGrammar resultOrdered(boolean resultOrdered) {
		this.resultOrdered = resultOrdered;
		return this;
	}

	public List<Join> getJoins() {
		return joins;
	}

	public void setJoins(List<Join> joins) {
		this.joins = joins;
	}
	
	
}
