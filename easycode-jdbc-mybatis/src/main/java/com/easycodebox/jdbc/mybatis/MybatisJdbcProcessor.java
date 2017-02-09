package com.easycodebox.jdbc.mybatis;

import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.jdbc.SqlCommandType;
import com.easycodebox.jdbc.grammar.SqlGrammar;
import com.easycodebox.jdbc.support.JdbcProcessor;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author WangXiaoJin
 *
 */
public class MybatisJdbcProcessor implements JdbcProcessor {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private SqlSessionTemplate sqlSessionTemplate;
	
	private final String STATEMENT_ID = "Mybatis.dynamic.sql";
	
	@Override
	public <T> MybatisSqlGrammar instanceSqlGrammar(Class<T> entity) {
		return MybatisSqlGrammar.instance(entity);
	}

	@Override
	public <T> MybatisSqlGrammar instanceSqlGrammar(Class<T> entity, String alias) {
		return MybatisSqlGrammar.instance(entity, alias);
	}

	@Override
	public int insert(SqlGrammar sqlGrammar, String sql, Object parameter, Class<?> resultType) {
		int result = 0;
		boolean inited = initMappedStatement(sqlGrammar, sql, parameter == null ? null : parameter.getClass(), 
				resultType, SqlCommandType.INSERT);
		if(inited) {
			result = sqlSessionTemplate.insert(STATEMENT_ID, parameter);
			destroyMappedStatement();
		}
		return result;
	}

	@Override
	public <T> T selectOne(SqlGrammar sqlGrammar, String sql, Object parameter, Class<?> resultType) {
		T result = null;
		boolean inited = initMappedStatement(sqlGrammar, sql, parameter == null ? null : parameter.getClass(), 
				resultType, SqlCommandType.SELECT);
		if(inited) {
			result = sqlSessionTemplate.selectOne(STATEMENT_ID, parameter);
			destroyMappedStatement();
		}
		return result;
	}

	@Override
	public <T> List<T> selectList(SqlGrammar sqlGrammar, String sql, Object parameter, Class<?> resultType) {
		List<T> result = null;
		boolean inited = initMappedStatement(sqlGrammar, sql, parameter == null ? null : parameter.getClass(), 
				resultType, SqlCommandType.SELECT);
		if(inited) {
			result = sqlSessionTemplate.selectList(STATEMENT_ID, parameter);
			destroyMappedStatement();
		}
		return result;
	}

	@Override
	public int update(SqlGrammar sqlGrammar, String sql, Object parameter, Class<?> resultType) {
		int result = 0;
		boolean inited = initMappedStatement(sqlGrammar, sql, parameter == null ? null : parameter.getClass(), 
				resultType, SqlCommandType.UPDATE);
		if(inited) {
			result = sqlSessionTemplate.update(STATEMENT_ID, parameter);
			destroyMappedStatement();
		}
		return result;
	}

	@Override
	public int delete(SqlGrammar sqlGrammar, String sql, Object parameter, Class<?> resultType) {
		int result = 0;
		boolean inited = initMappedStatement(sqlGrammar, sql, parameter == null ? null : parameter.getClass(), 
				resultType, SqlCommandType.DELETE);
		if(inited) {
			result = sqlSessionTemplate.delete(STATEMENT_ID, parameter);
			destroyMappedStatement();
		}
		return result;
	}

	/**
	 * 
	 * @param sqlGrammar
	 * @param sql			sql参数可以不传，不传将会调用sqlGrammar生成sql语句
	 * @param parameterType
	 * @param resultType
	 * @param sqlCommandType
	 */
	private boolean initMappedStatement(SqlGrammar sqlGrammar, String sql, Class<?> parameterType, 
			Class<?> resultType, SqlCommandType sqlCommandType) {
		Assert.notNull(sqlGrammar);
		if(StringUtils.isBlank(sql))
			sql = sqlGrammar.buildSql(sqlCommandType);
		log.debug("SqlGrammar : {}", sql);
		if(StringUtils.isBlank(sql))
			return false;
		Configuration configuration = sqlSessionTemplate.getConfiguration();
		SqlSource sqlSource = new StaticSqlSource(configuration, sql);
		MappedStatement.Builder statementBuilder = new MappedStatement.Builder(
				configuration, STATEMENT_ID, sqlSource, convertSqlCommandType(sqlCommandType));
		statementBuilder.fetchSize(sqlGrammar.getFetchSize());
		statementBuilder.statementType(((MybatisSqlGrammar)sqlGrammar).getStatementType());
		statementBuilder.keyGenerator(((MybatisSqlGrammar)sqlGrammar).getKeyGenerator());
		statementBuilder.keyProperty(sqlGrammar.getKeyProperty());
		statementBuilder.keyColumn(sqlGrammar.getKeyColumn());
		statementBuilder.databaseId(sqlGrammar.getDatabaseId());
		statementBuilder.lang(getLanguageDriver(sqlGrammar.getLang()));
		statementBuilder.resultOrdered(sqlGrammar.isResultOrdered());
		statementBuilder.resulSets(sqlGrammar.getResultSets());
		statementBuilder.timeout(configuration.getDefaultStatementTimeout());

		setStatementParameterMap(null, parameterType, statementBuilder);
		setStatementResultMap(null, resultType, null, statementBuilder);
		DefaultConfiguration.setCurMappedStatement(statementBuilder.build());
		return true;
	}
	
	private org.apache.ibatis.mapping.SqlCommandType convertSqlCommandType(SqlCommandType type) {
		switch (type) {
		case INSERT:
			return org.apache.ibatis.mapping.SqlCommandType.INSERT;
		case UPDATE:
			return org.apache.ibatis.mapping.SqlCommandType.UPDATE;
		case DELETE:
			return org.apache.ibatis.mapping.SqlCommandType.DELETE;
		case SELECT:
			return org.apache.ibatis.mapping.SqlCommandType.SELECT;
		default:
			return org.apache.ibatis.mapping.SqlCommandType.UNKNOWN;
		}
	}
	
	private void destroyMappedStatement() {
		DefaultConfiguration.resetMappedStatement();
	}

	private void setStatementParameterMap(String parameterMap,
			Class<?> parameterTypeClass,
			MappedStatement.Builder statementBuilder) {

		if (parameterMap != null) {
			try {
				statementBuilder.parameterMap(sqlSessionTemplate.getConfiguration()
						.getParameterMap(parameterMap));
			} catch (IllegalArgumentException e) {
				throw new BaseException("Could not find parameter map " + parameterMap, e);
			}
		} else if (parameterTypeClass != null) {
			List<ParameterMapping> parameterMappings = new ArrayList<>();
			ParameterMap.Builder inlineParameterMapBuilder = new ParameterMap.Builder(
					sqlSessionTemplate.getConfiguration(), statementBuilder.id()
							+ "-Inline", parameterTypeClass, parameterMappings);
			statementBuilder.parameterMap(inlineParameterMapBuilder.build());
		}
	}

	private void setStatementResultMap(String resultMap, Class<?> resultType,
			ResultSetType resultSetType,
			MappedStatement.Builder statementBuilder) {

		List<ResultMap> resultMaps = new ArrayList<>();
		if (resultMap != null) {
			String[] resultMapNames = resultMap.split(",");
			for (String resultMapName : resultMapNames) {
				try {
					resultMaps.add(sqlSessionTemplate.getConfiguration()
							.getResultMap(resultMapName.trim()));
				} catch (IllegalArgumentException e) {
					throw new BaseException("Could not find result map " + resultMapName, e);
				}
			}
		} else if (resultType != null) {
			ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(
					sqlSessionTemplate.getConfiguration(), statementBuilder.id()
							+ "-Inline", resultType,
					new ArrayList<ResultMapping>(), null);
			resultMaps.add(inlineResultMapBuilder.build());
		}
		statementBuilder.resultMaps(resultMaps);

		statementBuilder.resultSetType(resultSetType);
	}
	
	private LanguageDriver getLanguageDriver(String lang) {
		Class<?> langClass;
		Configuration configuration = sqlSessionTemplate.getConfiguration();
		if (lang == null) {
			langClass = configuration.getLanguageRegistry()
					.getDefaultDriverClass();
		} else {
			langClass = configuration.getTypeAliasRegistry().resolveAlias(lang);
			configuration.getLanguageRegistry().register(langClass);
		}
		if (langClass == null) {
			langClass = configuration.getLanguageRegistry()
					.getDefaultDriverClass();
		}
		return configuration.getLanguageRegistry().getDriver(langClass);
	}

	public SqlSessionTemplate getSqlSessionTemplate() {
		return sqlSessionTemplate;
	}

	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}
	
}
