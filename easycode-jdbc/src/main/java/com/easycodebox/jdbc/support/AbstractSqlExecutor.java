package com.easycodebox.jdbc.support;

import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.lang.reflect.Classes;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.idgenerator.IdGenerators;
import com.easycodebox.jdbc.*;
import com.easycodebox.jdbc.entity.Entity;
import com.easycodebox.jdbc.grammar.SqlGrammar;
import com.easycodebox.jdbc.util.AnnotateUtils;
import com.easycodebox.jdbc.util.SqlUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.math.NumberUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author WangXiaoJin
 *
 */
public abstract class AbstractSqlExecutor<T extends Entity> {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Resource
	private JdbcHandler jdbcHandler;
	
	@Resource
	private JdbcProcessor jdbcProcessor;
	
	private Class<T> entityClass;
	
	@SuppressWarnings("unchecked")
	public AbstractSqlExecutor() {
		entityClass = Classes.getSuperClassGenricType(getClass());
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
		return Strings.isBlank(alias) ? jdbcProcessor.instanceSqlGrammar(entityClass)
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
					if(c.getIdGeneratorType() != null) {
						Object pkVal = IdGenerators.nextVal(c.getIdGeneratorType());
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
				.upd(Property.instance(jdbcHandler.getDeletedPropName(), entityClass, false), deletedVal);
		addIdCondition(sqlGrammar, pks.get(0).getName(), idVal, entityClass);
		return update(sqlGrammar);
	}
	
	/**
	 * 当条件满足status参数时物理删除数据
	 * @param idVal  可以是java中任意数据类型，因为list，set，数组都实现了Serializable接口
	 * @param status
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <V extends DetailEnum<?>> int deletePhy(Serializable idVal, V... status) {
		return this.deletePhy(idVal, entityClass, status);
	}
	
	/**
	 * 当条件满足status参数时物理删除数据
	 * @param idVal  可以是java中任意数据类型，因为list，set，数组都实现了Serializable接口
	 * @param status
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <K extends Entity, V extends DetailEnum<?>> int deletePhy(Serializable idVal, Class<K> entityClass, V... status) {
		List<PkColumn> pks = AnnotateUtils.getPrimaryKeys(entityClass);
		SqlGrammar sqlGrammar = sql(entityClass);
		if(status != null && status.length > 0) {
			if(status.length > 1)
				sqlGrammar.in(Property.instance(jdbcHandler.getStatusPropName(), entityClass, false), status);
			else
				sqlGrammar.eq(Property.instance(jdbcHandler.getStatusPropName(), entityClass, false), status[0]);
		}
		addIdCondition(sqlGrammar, pks.get(0).getName(), idVal, entityClass);
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
	
	@SuppressWarnings("unchecked")
	public <V extends DetailEnum<?>> boolean exist(Serializable idVal,  V... status) {
		return this.exist(idVal, entityClass, status);
	}
	
	@SuppressWarnings("unchecked")
	public <K extends Entity, V extends DetailEnum<?>> boolean exist(Serializable idVal, Class<K> entityClass, V... status) {
		List<PkColumn> pks = AnnotateUtils.getPrimaryKeys(entityClass);
		SqlGrammar sqlGrammar = sql(entityClass);
		int valCount = idVal.getClass().isArray() ? ((Object[])idVal).length : idVal instanceof Collection<?>
				? ((Collection<?>)idVal).size() : NumberUtils.INTEGER_ONE;
		if(status != null && status.length > 0) {
			if(status.length > 1)
				sqlGrammar.in(Property.instance(jdbcHandler.getStatusPropName(), entityClass, false), status);
			else
				sqlGrammar.eq(Property.instance(jdbcHandler.getStatusPropName(), entityClass, false), status[0]);
		}
		addIdCondition(sqlGrammar, pks.get(0).getName(), idVal, entityClass);
		return count(sqlGrammar) >= valCount;
	}
	
	protected boolean exist(SqlGrammar sqlGrammar) {
		return count(sqlGrammar) > 0;
	}
	
	@SuppressWarnings("unchecked")
	public <V extends DetailEnum<?>> T get(Serializable id, V... status) {
		return get(id, (LockMode)null, status);
	}
	
	@SuppressWarnings("unchecked")
	public <K extends Entity, V extends DetailEnum<?>> K get(Serializable id, Class<K> entityClass, V... status) {
		return get(id, entityClass, null, status);
	}
	
	@SuppressWarnings("unchecked")
	public <V extends DetailEnum<?>> T get(Serializable id, LockMode lockMode, V... status) {
		return this.get(id, entityClass, lockMode, status);
	}
	
	@SuppressWarnings("unchecked")
	public <K extends Entity, V extends DetailEnum<?>> K get(Serializable id, Class<K> entityClass, LockMode lockMode, V... status) {
		List<PkColumn> pks = AnnotateUtils.getPrimaryKeys(entityClass);
		SqlGrammar sqlGrammar = sql(entityClass)
				.eq(Property.instance(pks.get(0).getName(), entityClass, false), id);
		if(status != null && status.length > 0) {
			if(status.length > 1)
				sqlGrammar.in(Property.instance(jdbcHandler.getStatusPropName(), entityClass, false), status);
			else
				sqlGrammar.eq(Property.instance(jdbcHandler.getStatusPropName(), entityClass, false), status[0]);
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
		return new DataPage<>(sg.getPageNo(), sg.getPageSize(),
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
				.upd(Property.instance(jdbcHandler.getStatusPropName(), entityClass, false), status);
		addIdCondition(sqlGrammar, pks.get(0).getName(), idVal, entityClass);
		return update(sqlGrammar);
	}
	
	/**
	 * 组装ID条件
	 */
	private <K extends Entity> void addIdCondition(SqlGrammar sqlGrammar, String idPropName,
	                                               Serializable idVal, Class<K> entityClass) {
		if (idVal.getClass().isArray()) {
			sqlGrammar.in(Property.instance(idPropName, entityClass, false), (Object[])idVal);
		}else if(idVal instanceof Collection<?>) {
			sqlGrammar.in(Property.instance(idPropName, entityClass, false), (Collection<?>)idVal);
		}else {
			sqlGrammar.eq(Property.instance(idPropName, entityClass, false), idVal);
		}
	}

	public JdbcHandler getJdbcHandler() {
		return jdbcHandler;
	}

	public void setJdbcHandler(JdbcHandler jdbcHandler) {
		this.jdbcHandler = jdbcHandler;
	}

	public JdbcProcessor getJdbcProcessor() {
		return jdbcProcessor;
	}

	public void setJdbcProcessor(JdbcProcessor jdbcProcessor) {
		this.jdbcProcessor = jdbcProcessor;
	}
	
}
