package com.easycodebox.common.jdbc.support;

import com.easycodebox.common.jdbc.SqlGrammar;
import com.easycodebox.common.lang.dto.Entity;

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
