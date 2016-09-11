package com.easycodebox.jdbc.support;

import java.util.List;

import com.easycodebox.jdbc.grammar.SqlGrammar;

/**
 * jdbc处理器，执行sql语句
 * @author WangXiaoJin
 *
 */
public interface JdbcProcessor {
	
	<T> SqlGrammar instanceSqlGrammar(Class<T> entity);
	
	<T> SqlGrammar instanceSqlGrammar(Class<T> entity, String alias);

	int insert(SqlGrammar sqlGrammar, String sql, Object parameter, Class<?> resultType);
	
	<T> T selectOne(SqlGrammar sqlGrammar, String sql, Object parameter, Class<?> resultType);
	
	<T> List<T> selectList(SqlGrammar sqlGrammar, String sql, Object parameter, Class<?> resultType);
	
	int update(SqlGrammar sqlGrammar, String sql, Object parameter, Class<?> resultType);
	
	int delete(SqlGrammar sqlGrammar, String sql, Object parameter, Class<?> resultType);
	
}
