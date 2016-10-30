package com.easycodebox.jdbc.mybatis;

import org.apache.ibatis.mapping.MappedStatement;

import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.lang.reflect.FieldUtils;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

/**
 * @author WangXiaoJin
 * 
 */
public class DefaultConfiguration extends
		org.apache.ibatis.session.Configuration {
	
	private static final Logger log = LoggerFactory.getLogger(DefaultConfiguration.class);
	
	private static final ThreadLocal<MappedStatement> CUR_MAPPED_STATEMENT = new ThreadLocal<MappedStatement>();
	
	/**
	 * 处理SqlSource，用DelegateSqlSource 代理SqlSource
	 */
	private boolean delegateSqlSource = true;

	public static MappedStatement getCurMappedStatement() {
		return CUR_MAPPED_STATEMENT.get();
	}
	
	public static void setCurMappedStatement(MappedStatement ms) {
		CUR_MAPPED_STATEMENT.set(ms);
	}
	
	public static void resetMappedStatement() {
		CUR_MAPPED_STATEMENT.set(null);
	}
	
	public static void removeMappedStatement() {
		CUR_MAPPED_STATEMENT.remove();
	}

	@Override
	public MappedStatement getMappedStatement(String id,
			boolean validateIncompleteStatements) {
		if (validateIncompleteStatements) {
			buildAllStatements();
		}
		MappedStatement ms = null;
		try{
			ms = mappedStatements.get(id);
		}catch(Exception e) {
			ms = getCurMappedStatement();
			if(ms == null)
				throw new BaseException("没有对应的映射sql", e);
		}
		return ms;
	}
	
	@Override
	public void addMappedStatement(MappedStatement ms) {
		if(delegateSqlSource) {
			try {
				FieldUtils.writeField(ms, "sqlSource", new DelegateSqlSource(ms.getSqlSource()), true);
			} catch (Exception e) {
				log.error("set field value error.", e);
			}
		}
	    mappedStatements.put(ms.getId(), ms);
	}

	public boolean isDelegateSqlSource() {
		return delegateSqlSource;
	}

	public void setDelegateSqlSource(boolean delegateSqlSource) {
		this.delegateSqlSource = delegateSqlSource;
	}
	
}
