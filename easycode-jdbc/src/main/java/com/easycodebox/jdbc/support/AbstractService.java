package com.easycodebox.jdbc.support;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.PropertyUtils;

import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.generator.Generators;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.lang.reflect.ClassUtils;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.jdbc.LockMode;
import com.easycodebox.jdbc.PkColumn;
import com.easycodebox.jdbc.Property;
import com.easycodebox.jdbc.entity.Entity;
import com.easycodebox.jdbc.grammar.SqlGrammar;
import com.easycodebox.jdbc.util.AnnotateUtils;
import com.easycodebox.jdbc.util.SqlUtils;

/**
 * @author WangXiaoJin
 *
 */
public abstract class AbstractService<T extends Entity> {
	
	protected final Logger LOG = LoggerFactory.getLogger(getClass());

	@Resource
	private JdbcHandler jdbcHandler;
	
	@Resource
	private JdbcProcessor jdbcProcessor;
	
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
		return StringUtils.isBlank(alias) ? jdbcProcessor.instanceSqlGrammar(entityClass)
				: jdbcProcessor.instanceSqlGrammar(entityClass, alias);
	}
	
	public int save(T entity) {
		return save(entity, entityClass);
	}
	
	/**
	 * 在这里加上entityClass参数，不是无意义的乱加。而是为了后期修改项目代码时，方便全局搜索定位用的
	 * @param entity
	 * @param entityClass
	 * @return
	 */
	public <K extends Entity> int save(K entity, Class<K> entityClass) {
		try {
			if (jdbcHandler != null)
				jdbcHandler.beforeSave(entity);
			List<PkColumn> pks = AnnotateUtils.getPrimaryKeys(entityClass);
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
					com.easycodebox.jdbc.config.Configuration.getTable(entityClass), 
					sqlIncludePk);
			return jdbcProcessor.insert(sql(entityClass), sql, null, int.class);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new BaseException("Execute save entity error.", e);
		}
	}
	
	protected long count(SqlGrammar sqlGrammar) {
		sqlGrammar.countSql(true);
		return get(sqlGrammar, long.class);
	}
	
	/**
	 * 逻辑删除
	 * @param idVal
	 * @return
	 */
	public int delete(Serializable idVal) {
		return delete(idVal, entityClass);
	}
	
	/**
	 * 逻辑删除，默认使用jdbcHandler.getDeletedValue()值作为删除状态
	 * @param idVal
	 * @param entityClass
	 * @return
	 */
	public <K extends Entity> int delete(Serializable idVal, Class<K> entityClass) {
		return delete(idVal, jdbcHandler.getDeletedValue(), entityClass);
	}
	
	/**
	 * 逻辑删除
	 * @param idVal
	 * @param deletedVal	代表删除的值
	 * @param entityClass
	 * @return
	 */
	public <K extends Entity> int delete(Serializable idVal, Object deletedVal, Class<K> entityClass) {
		List<PkColumn> pks = AnnotateUtils.getPrimaryKeys(entityClass);
		SqlGrammar sqlGrammar = sql(entityClass)
				.update(Property.instance(jdbcHandler.getDeletedFieldName(), entityClass, false), deletedVal);
		if (idVal.getClass().isArray()) {
			sqlGrammar.in(Property.instance(pks.get(0).getName(), entityClass, false), (Object[])idVal);
		}else if(idVal instanceof Collection<?>) {
			sqlGrammar.in(Property.instance(pks.get(0).getName(), entityClass, false), (Collection<?>)idVal);
		}else {
			sqlGrammar.eq(Property.instance(pks.get(0).getName(), entityClass, false), idVal);
		}
		return update(sqlGrammar);
	}
	
	/**
	 * 当条件满足status参数时物理删除数据
	 * @param idVal  可以是java中任意数据类型，因为list，set，数组都实现了Serializable接口
	 * @param status
	 * @return
	 * @throws Exception
	 */
	@SafeVarargs
	public final <V extends DetailEnum<?>> int deletePhy(Serializable idVal, V... status) {
		return this.deletePhy(idVal, entityClass, status);
	}
	
	/**
	 * 当条件满足status参数时物理删除数据
	 * @param idVal  可以是java中任意数据类型，因为list，set，数组都实现了Serializable接口
	 * @param status
	 * @return
	 * @throws Exception
	 */
	@SafeVarargs
	public final <K extends Entity, V extends DetailEnum<?>> int deletePhy(Serializable idVal, Class<K> entityClass, V... status) {
		List<PkColumn> pks = AnnotateUtils.getPrimaryKeys(entityClass);
		SqlGrammar sqlGrammar = sql(entityClass);
		if(status != null && status.length > 0) {
			if(status.length > 1)
				sqlGrammar.in(Property.instance(jdbcHandler.getStatusFieldName(), entityClass, false), status);
			else
				sqlGrammar.eq(Property.instance(jdbcHandler.getStatusFieldName(), entityClass, false), status[0]);
		}
		if (idVal.getClass().isArray()) {
			sqlGrammar.in(Property.instance(pks.get(0).getName(), entityClass, false), (Object[])idVal);
		}else if(idVal instanceof Collection<?>) {
			sqlGrammar.in(Property.instance(pks.get(0).getName(), entityClass, false), (Collection<?>)idVal);
		}else {
			sqlGrammar.eq(Property.instance(pks.get(0).getName(), entityClass, false), idVal);
		}
		return deletePhy(sqlGrammar);
	}
	
	/**
	 * 物理删除数据
	 * @param sqlGrammar
	 * @return
	 */
	protected int deletePhy(SqlGrammar sqlGrammar) {
		return jdbcProcessor.delete(sqlGrammar, null, null, int.class);
	}
	
	/**
	 * 用到此功能时，不能事务回滚
	 * @return
	 */
	public int truncate() {
		return truncate(entityClass);
	}
	
	/**
	 * 用到此功能时，不能事务回滚
	 * @return
	 */
	public <K extends Entity> int truncate(Class<K> entityClass) {
		String tableName = com.easycodebox.jdbc.config.Configuration.dialect.wrapQuote(
				com.easycodebox.jdbc.config.Configuration.getTable(entityClass).getName());
		return jdbcProcessor.delete(sql(entityClass), "TRUNCATE TABLE " + tableName , null, int.class);
	}
	
	@SafeVarargs
	public final <V extends DetailEnum<?>> boolean exist(Serializable idVal,  V... status) {
		return this.exist(idVal, entityClass, status);
	}
	
	@SafeVarargs
	public final <K extends Entity, V extends DetailEnum<?>> boolean exist(Serializable idVal, Class<K> entityClass, V... status) {
		List<PkColumn> pks = AnnotateUtils.getPrimaryKeys(entityClass);
		SqlGrammar sqlGrammar = sql(entityClass);
		int valCount = 1;
		if(status != null && status.length > 0) {
			if(status.length > 1)
				sqlGrammar.in(Property.instance(jdbcHandler.getStatusFieldName(), entityClass, false), status);
			else
				sqlGrammar.eq(Property.instance(jdbcHandler.getStatusFieldName(), entityClass, false), status[0]);
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
	public final <V extends DetailEnum<?>> T get(Serializable id, V... status) {
		return get(id, (LockMode)null, status);
	}
	
	@SafeVarargs
	public final <K extends Entity, V extends DetailEnum<?>> K get(Serializable id, Class<K> entityClass, V... status) {
		return get(id, entityClass, null, status);
	}
	
	@SafeVarargs
	public final <V extends DetailEnum<?>> T get(Serializable id, LockMode lockMode, V... status) {
		return this.get(id, entityClass, lockMode, status);
	}
	
	@SafeVarargs
	public final <K extends Entity, V extends DetailEnum<?>> K get(Serializable id, Class<K> entityClass, LockMode lockMode, V... status) {
		List<PkColumn> pks = AnnotateUtils.getPrimaryKeys(entityClass);
		SqlGrammar sqlGrammar = sql(entityClass)
				.eq(Property.instance(pks.get(0).getName(), entityClass, false), id);
		if(status != null && status.length > 0) {
			if(status.length > 1)
				sqlGrammar.in(Property.instance(jdbcHandler.getStatusFieldName(), entityClass, false), status);
			else
				sqlGrammar.eq(Property.instance(jdbcHandler.getStatusFieldName(), entityClass, false), status[0]);
		}
		return get(sqlGrammar.lockMode(lockMode), entityClass);
	}
	
	protected T get(SqlGrammar sqlGrammar) {
		return get(sqlGrammar, entityClass);
	}
	
	protected <K> K get(SqlGrammar sqlGrammar, Class<K> resultClass) {
		return jdbcProcessor.selectOne(sqlGrammar, null, null, resultClass);
	}
	
	protected List<T> list(SqlGrammar sqlGrammar) {
		return list(sqlGrammar, entityClass);
	}
	
	@SuppressWarnings("unchecked")
	protected <K> List<K> list(SqlGrammar sqlGrammar, Class<K> resultClass) {
		List<K> result = jdbcProcessor.selectList(sqlGrammar, null, null, resultClass);
		return result == null ? Collections.EMPTY_LIST : result;
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
		if (jdbcHandler != null)
			jdbcHandler.beforeUpdate(sqlGrammar);
		return jdbcProcessor.update(sqlGrammar, null, null, int.class);
	}
	
	public int update(T entity) {
		return update(entity, entityClass);
	}
	
	/**
	 * 在这里加上entityClass参数，不是无意义的乱加。而是为了后期修改项目代码时，方便全局搜索定位用的
	 * @param entity
	 * @param entityClass
	 * @return
	 */
	public <K extends Entity> int update(K entity, Class<K> entityClass) {
		try {
			if (jdbcHandler != null)
				jdbcHandler.beforeUpdate(entity);
			
			String sql = SqlUtils.getUpdateSql(entity, 
					com.easycodebox.jdbc.config.Configuration.getTable(entityClass));
			return jdbcProcessor.update(sql(entityClass), sql, null, int.class);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new BaseException("Execute update entity error.", e);
		}
	}
	
	public <V extends DetailEnum<?>> int updateStatus(Serializable idVal, V status) {
		return updateStatus(idVal, status, entityClass);
	}
	
	public <K extends Entity,V extends DetailEnum<?>> int updateStatus(Serializable idVal, 
			V status, Class<K> entityClass) {
		List<PkColumn> pks = AnnotateUtils.getPrimaryKeys(entityClass);
		SqlGrammar sqlGrammar = sql(entityClass)
				.update(Property.instance(jdbcHandler.getStatusFieldName(), entityClass, false), status);
		if (idVal.getClass().isArray()) {
			sqlGrammar.in(Property.instance(pks.get(0).getName(), entityClass, false), (Object[])idVal);
		}else if(idVal instanceof Collection<?>) {
			sqlGrammar.in(Property.instance(pks.get(0).getName(), entityClass, false), (Collection<?>)idVal);
		}else {
			sqlGrammar.eq(Property.instance(pks.get(0).getName(), entityClass, false), idVal);
		}
		return update(sqlGrammar);
	}
	
}
