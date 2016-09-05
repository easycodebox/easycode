package com.easycodebox.common.jdbc;

import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.validate.Assert;

/**
 * @author WangXiaoJin
 *
 */
public abstract class Dialect {
	
	public char openQuote() {
		return '"';
	}
	
	public char closeQuote() {
		return '"';
	}

	/**
	 * 为name包裹引号，更具数据库的类型包括相对应的引号
	 * Oracle  name ==》 "name"
	 * MySql   name ==》 `name`
	 * @param name
	 * @return
	 */
	public String wrapQuote(String name) {
		Assert.notBlank(name);
		return openQuote() + name.trim() + closeQuote();
	}
	
	/**
	 * joinType = NONE 时，return null
	 * @param joinType
	 * @return
	 */
	public String joinSql(JoinType joinType) {
		switch (joinType) {
			case INNER_JOIN:
				return "INNER JOIN";
			case LEFT_OUTER_JOIN:
				return "LEFT OUTER JOIN";
			case RIGHT_OUTER_JOIN:
				return "RIGHT OUTER JOIN";
			case FULL_JOIN:
				return "FULL JOIN";
			case NONE:
				return null;
			default:
				throw new BaseException("unknown JoinType " + joinType);
		}
	}
	
	/**
	 * 把param参数的特殊符号转义
	 * @param param
	 * @return
	 */
	abstract String escapeString(String val);

}
