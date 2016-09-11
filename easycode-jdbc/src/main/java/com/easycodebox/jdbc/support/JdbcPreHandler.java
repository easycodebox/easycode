package com.easycodebox.jdbc.support;

import com.easycodebox.jdbc.entity.Entity;
import com.easycodebox.jdbc.grammar.SqlGrammar;

/**
 * @author WangXiaoJin
 *
 */
public interface JdbcPreHandler {

	/**
	 * 在执行save（insert sql）前执行的逻辑
	 * @param entity
	 */
	void beforeSave(Entity entity);
	
	/**
	 * 在执行update（update sql）前执行的逻辑
	 * @param entity
	 */
	void beforeUpdate(Entity entity);
	
	/**
	 * 在执行update（update sql）前执行的逻辑
	 * @param entity
	 */
	void beforeUpdate(SqlGrammar sqlGrammar);
	
}
