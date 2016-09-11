package com.easycodebox.jdbc.mybatis.spring;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.dao.support.PersistenceExceptionTranslator;

/**
 * @author WangXiaoJin
 *
 */
public class DefaultSqlSessionTemplate extends SqlSessionTemplate {

	public DefaultSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	public DefaultSqlSessionTemplate(SqlSessionFactory sqlSessionFactory,
			ExecutorType executorType,
			PersistenceExceptionTranslator exceptionTranslator) {
		super(sqlSessionFactory, executorType, exceptionTranslator);
	}

	public DefaultSqlSessionTemplate(SqlSessionFactory sqlSessionFactory,
			ExecutorType executorType) {
		super(sqlSessionFactory, executorType);
	}

	@Override
	public void close() {
		
	}
	
}
