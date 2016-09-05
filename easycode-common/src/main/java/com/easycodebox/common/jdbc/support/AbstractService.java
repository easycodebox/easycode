package com.easycodebox.common.jdbc.support;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionTemplate;

import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.generator.Generators;
import com.easycodebox.common.jdbc.DefaultConfiguration;
import com.easycodebox.common.jdbc.LockMode;
import com.easycodebox.common.jdbc.PkColumn;
import com.easycodebox.common.jdbc.Property;
import com.easycodebox.common.jdbc.SqlCommandType;
import com.easycodebox.common.jdbc.SqlGrammar;
import com.easycodebox.common.jdbc.SqlUtils;
import com.easycodebox.common.jdbc.Table;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.lang.dto.Entity;
import com.easycodebox.common.lang.reflect.ClassUtils;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.security.SecurityUtils;
import com.easycodebox.common.validate.Assert;

/**
 * @author WangXiaoJin
 *
 */
public abstract class AbstractService<T extends Entity> {
	
	protected final Logger LOG = LoggerFactory.getLogger(getClass());

	@Resource
	private SqlSessionTemplate sqlSessionTemplate;
	@Resource
	private JdbcPreHandler jdbcPreHandler;
	
	private Class<T> entityClass;
	
	@SuppressWarnings("unchecked")
	public AbstractService() {
		entityClass = ClassUtils.getSuperClassGenricType(getClass());
	}
	
	/**
	 * 实例化SqlGrammar
	 * @return
	 */
	protected <K extends Entity> SqlGrammar sql() {
		return sql(entityClass, null);
	}
	
	/**
	 * 实例化SqlGrammar
	 * @return
	 */
	protected <K extends Entity> SqlGrammar sql(Class<K> entityClass) {
		return sql(entityClass, null);
	}
	
	/**
	 * 实例化SqlGrammar
	 * @return
	 */
	protected <K extends Entity> SqlGrammar sql(Class<K> entityClass, String alias) {
		return StringUtils.isBlank(alias) ? SqlGrammar.instance(entityClass)
				: SqlGrammar.instance(entityClass, alias);
	}
	
	protected int save(T entity) {
		return save(entity, entityClass);
	}
	
	/**
	 * 在这里加上entityClass参数，不是无意义的乱加。而是为了后期修改项目代码时，方便全局搜索定位用的
	 * @param entity
	 * @param entityClass
	 * @return
	 */
	protected <K extends Entity> int save(K entity, Class<K> entityClass) {
		try {
			if (jdbcPreHandler != null)
				jdbcPreHandler.beforeSave(entity);
			List<PkColumn> pks = getPrimaryKeys(entityClass);
			boolean sqlIncludePk = true;
			for(PkColumn c : pks) {
				Object data = PropertyUtils.getSimpleProperty(entity, c.getName());
				if(data == null) {
					if(c.getGeneratorType() != null) {
						Object pkVal = Generators.getGeneratorNextVal(c.getGeneratorType());
						PropertyUtils.setSimpleProperty(entity, c.getName(), pkVal);
					}else {
						sqlIncludePk = false;
						break;
					}
				}
			}
			String sql = SqlUtils.getInsertSql(entity, 
					com.easycodebox.common.jdbc.Configuration.getTable(entityClass), 
					sqlIncludePk);
			Object result = processJdbc(sql(entityClass), sql, null, 
					int.class, SqlCommandType.INSERT);
			return result == null ? 0 : (Integer)result;
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new BaseException("Execute save entity error.", e);
		}
	}
	
	protected long count(SqlGrammar sqlGrammar) {
		sqlGrammar.countSql(true);
		return get(sqlGrammar, long.class);
	}
	
	/**
	 * 
	 * @param idVal  可以是java中任意数据类型，因为list，set，数组都实现了Serializable接口
	 * @param status
	 * @return
	 * @throws Exception
	 */
	@SafeVarargs
	protected final <V extends DetailEnum<?>> int delete(Serializable idVal, V... status) {
		return this.delete(idVal, entityClass, status);
	}
	
	/**
	 * 
	 * @param idVal  可以是java中任意数据类型，因为list，set，数组都实现了Serializable接口
	 * @param status
	 * @return
	 * @throws Exception
	 */
	@SafeVarargs
	protected final <K extends Entity, V extends DetailEnum<?>> int delete(Serializable idVal, Class<K> entityClass, V... status) {
		List<PkColumn> pks = getPrimaryKeys(entityClass);
		SqlGrammar sqlGrammar = sql(entityClass);
		if(status != null && status.length > 0) {
			if(status.length > 1)
				sqlGrammar.in(Property.instance("status", entityClass, false), status);
			else
				sqlGrammar.eq(Property.instance("status", entityClass, false), status[0]);
		}
		if (idVal.getClass().isArray()) {
			sqlGrammar.in(Property.instance(pks.get(0).getName(), entityClass, false), (Object[])idVal);
		}else if(idVal instanceof Collection<?>) {
			sqlGrammar.in(Property.instance(pks.get(0).getName(), entityClass, false), (Collection<?>)idVal);
		}else {
			sqlGrammar.eq(Property.instance(pks.get(0).getName(), entityClass, false), idVal);
		}
		return delete(sqlGrammar);
	}
	
	protected int delete(SqlGrammar sqlGrammar) {
		Object result = processJdbc(sqlGrammar, null, null, 
				int.class, SqlCommandType.DELETE);
		return result == null ? 0 : (Integer)result;
	}
	
	/**
	 * 用到此功能时，不能事务回滚
	 * @return
	 */
	protected int truncate() {
		return truncate(entityClass);
	}
	
	/**
	 * 用到此功能时，不能事务回滚
	 * @return
	 */
	protected <K extends Entity> int truncate(Class<K> entityClass) {
		String tableName = com.easycodebox.common.jdbc.Configuration.dialect.wrapQuote(
				com.easycodebox.common.jdbc.Configuration.getTable(entityClass).getName());
		Object result = processJdbc(null, "TRUNCATE TABLE " + tableName , null, 
				int.class, SqlCommandType.DELETE);
		return result == null ? 0 : (Integer)result;
	}
	
	@SafeVarargs
	protected final <V extends DetailEnum<?>> boolean exist(Serializable idVal,  V... status) {
		return this.exist(idVal, entityClass, status);
	}
	
	@SafeVarargs
	protected final <K extends Entity, V extends DetailEnum<?>> boolean exist(Serializable idVal, Class<K> entityClass, V... status) {
		List<PkColumn> pks = getPrimaryKeys(entityClass);
		SqlGrammar sqlGrammar = sql(entityClass);
		int valCount = 1;
		if(status != null && status.length > 0) {
			if(status.length > 1)
				sqlGrammar.in(Property.instance("status", entityClass, false), status);
			else
				sqlGrammar.eq(Property.instance("status", entityClass, false), status[0]);
		}
		if (idVal.getClass().isArray()) {
			sqlGrammar.in(Property.instance(pks.get(0).getName(), entityClass, false), (Object[])idVal);
			valCount = ((Object[])idVal).length;
		}else if(idVal instanceof Collection<?>) {
			sqlGrammar.in(Property.instance(pks.get(0).getName(), entityClass, false), (Collection<?>)idVal);
			valCount = ((Collection<?>)idVal).size();
		}else {
			sqlGrammar.eq(Property.instance(pks.get(0).getName(), entityClass, false), idVal);
		}
		if(count(sqlGrammar) < valCount)
			return false;
		else
			return true;
	}
	
	protected boolean exist(SqlGrammar sqlGrammar) {
		if(count(sqlGrammar) > 0)
			return true;
		else
			return false;
	}
	
	@SafeVarargs
	protected final <V extends DetailEnum<?>> T get(Serializable id, V... status) {
		return get(id, (LockMode)null, status);
	}
	
	@SafeVarargs
	protected final <K extends Entity, V extends DetailEnum<?>> K get(Serializable id, Class<K> entityClass, V... status) {
		return get(id, entityClass, null, status);
	}
	
	@SafeVarargs
	protected final <V extends DetailEnum<?>> T get(Serializable id, LockMode lockMode, V... status) {
		return this.get(id, entityClass, lockMode, status);
	}
	
	@SafeVarargs
	protected final <K extends Entity, V extends DetailEnum<?>> K get(Serializable id, Class<K> entityClass, LockMode lockMode, V... status) {
		List<PkColumn> pks = getPrimaryKeys(entityClass);
		SqlGrammar sqlGrammar = SqlGrammar
				.instance(entityClass)
				.eq(Property.instance(pks.get(0).getName(), entityClass, false), id);
		if(status != null && status.length > 0) {
			if(status.length > 1)
				sqlGrammar.in(Property.instance("status", entityClass, false), status);
			else
				sqlGrammar.eq(Property.instance("status", entityClass, false), status[0]);
		}
		return get(sqlGrammar.lockMode(lockMode), entityClass);
	}
	
	protected T get(SqlGrammar sqlGrammar) {
		return get(sqlGrammar, entityClass);
	}
	
	@SuppressWarnings("unchecked")
	protected <K> K get(SqlGrammar sqlGrammar, Class<K> resultClass) {
		return (K)processJdbc(sqlGrammar, null, null, 
				resultClass, SqlCommandType.SELECT_ONE);
	}
	
	protected List<T> list(SqlGrammar sqlGrammar) {
		return list(sqlGrammar, entityClass);
	}
	
	@SuppressWarnings("unchecked")
	protected <K> List<K> list(SqlGrammar sqlGrammar, Class<K> resultClass) {
		Object result = processJdbc(sqlGrammar, null, null, 
				resultClass, SqlCommandType.SELECT_LIST);
		return result == null ? Collections.EMPTY_LIST : (List<K>)result;
	}
	
	protected DataPage<T> page(SqlGrammar sqlGrammar) {
		return page(sqlGrammar, entityClass);
	}
	
	protected <K> DataPage<K> page(SqlGrammar sg, Class<K> resultClass) {
		//Assert.notNull(sqlGrammar.getPageNo(), "PageNo cant not be null.");
		//Assert.notNull(sqlGrammar.getPageSize(), "PageSize cant not be null.");
		List<K> data = list(sg, resultClass);
		long count = sg.getPageNo() != null && sg.getPageSize() != null ? count(sg) : data.size();
		return new DataPage<K>(sg.getPageNo(), sg.getPageSize(), 
				sg.getPartIndex(), sg.getPartSize(), 
				count, data);
	}
	
	protected int update(SqlGrammar sqlGrammar) {
		if (jdbcPreHandler != null)
			jdbcPreHandler.beforeUpdate(sqlGrammar);
		Object result = processJdbc(sqlGrammar, null, null, 
				int.class, SqlCommandType.UPDATE);
		return result == null ? 0 : (Integer)result;
	}
	
	protected int update(T entity) {
		return update(entity, entityClass);
	}
	
	/**
	 * 在这里加上entityClass参数，不是无意义的乱加。而是为了后期修改项目代码时，方便全局搜索定位用的
	 * @param entity
	 * @param entityClass
	 * @return
	 */
	protected <K extends Entity> int update(K entity, Class<K> entityClass) {
		try {
			if (jdbcPreHandler != null)
				jdbcPreHandler.beforeUpdate(entity);
			
			String sql = SqlUtils.getUpdateSql(entity, 
					com.easycodebox.common.jdbc.Configuration.getTable(entityClass));
			Object result = processJdbc(sql(entityClass), sql, null, 
					int.class, SqlCommandType.UPDATE);
			return result == null ? 0 : (Integer)result;
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new BaseException("Execute update entity error.", e);
		}
	}
	
	protected <V extends DetailEnum<?>> int updateStatus(Serializable idVal, V status) {
		return updateStatus(idVal, status, entityClass);
	}
	
	protected <K extends Entity,V extends DetailEnum<?>> int updateStatus(Serializable idVal, 
			V status, Class<K> entityClass) {
		List<PkColumn> pks = getPrimaryKeys(entityClass);
		SqlGrammar sqlGrammar = sql(entityClass)
				.update(Property.instance("status", entityClass, false), status);
		if (idVal.getClass().isArray()) {
			sqlGrammar.in(Property.instance(pks.get(0).getName(), entityClass, false), (Object[])idVal);
		}else if(idVal instanceof Collection<?>) {
			sqlGrammar.in(Property.instance(pks.get(0).getName(), entityClass, false), (Collection<?>)idVal);
		}else {
			sqlGrammar.eq(Property.instance(pks.get(0).getName(), entityClass, false), idVal);
		}
		return update(sqlGrammar);
	}
	
	private final String STATEMENT_ID = "Mybatis.dynamic.sql";

	private List<PkColumn> getPrimaryKeys(Class<?> entityClass) {
		Table table = com.easycodebox.common.jdbc.Configuration.getTable(entityClass);
		List<PkColumn> pks = table.getPrimaryKeys();
		Assert.notEmpty(pks, "{0} class has no @id", entityClass.getName());
		return pks;
	}
	
	private Object processJdbc(SqlGrammar sqlGrammar, String sql,
			Object parameter, Class<?> resultType, SqlCommandType sqlCommandType) {
		boolean inited = initMappedStatement(sqlGrammar, sql, parameter == null ? null : parameter.getClass(), 
				resultType, sqlCommandType);
		if(!inited)
			return null;
		Object result = null;
		switch (sqlCommandType) {
			case INSERT:
				result = sqlSessionTemplate.insert(STATEMENT_ID, parameter);
				break;
			case SELECT_ONE:
				result = sqlSessionTemplate.selectOne(STATEMENT_ID, parameter);
				break;
			case SELECT_LIST:
				result = sqlSessionTemplate.selectList(STATEMENT_ID, parameter);
				break;
			case UPDATE:
				result = sqlSessionTemplate.update(STATEMENT_ID, parameter);
				break;
			case DELETE:
				result = sqlSessionTemplate.delete(STATEMENT_ID, parameter);
				break;
			default:
				break;
		}
		destroyMappedStatement();
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
	private boolean initMappedStatement(SqlGrammar sqlGrammar, String sql,
			Class<?> parameterType, Class<?> resultType, SqlCommandType sqlCommandType) {
		if(StringUtils.isBlank(sql))
			sql = sqlGrammar.buildSql(sqlCommandType.getIbatisType());
		sqlGrammar = sqlGrammar == null ? sql() : sqlGrammar;
		LOG.info("SQL({0}): {1}", SecurityUtils.getSessionId() , sql);
		if(StringUtils.isBlank(sql))
			return false;
		Configuration configuration = sqlSessionTemplate.getConfiguration();
		SqlSource sqlSource = new StaticSqlSource(configuration, sql);
		MappedStatement.Builder statementBuilder = new MappedStatement.Builder(
				configuration, STATEMENT_ID, sqlSource, sqlCommandType.getIbatisType());
		statementBuilder.fetchSize(sqlGrammar.getFetchSize());
		statementBuilder.statementType(sqlGrammar.getStatementType().getIbatisType());
		statementBuilder.keyGenerator(sqlGrammar.getKeyGenerator());
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
			List<ParameterMapping> parameterMappings = new ArrayList<ParameterMapping>();
			ParameterMap.Builder inlineParameterMapBuilder = new ParameterMap.Builder(
					sqlSessionTemplate.getConfiguration(), statementBuilder.id()
							+ "-Inline", parameterTypeClass, parameterMappings);
			statementBuilder.parameterMap(inlineParameterMapBuilder.build());
		}
	}

	private void setStatementResultMap(String resultMap, Class<?> resultType,
			ResultSetType resultSetType,
			MappedStatement.Builder statementBuilder) {

		List<ResultMap> resultMaps = new ArrayList<ResultMap>();
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
	
}
