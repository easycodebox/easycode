package com.easycodebox.jdbc.mybatis;

import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.StatementType;

import com.easycodebox.jdbc.grammar.SqlGrammar;

/**
 * @author WangXiaoJin
 * 
 */
public class MybatisSqlGrammar extends SqlGrammar {
	
	private StatementType statementType = StatementType.PREPARED;
	private KeyGenerator keyGenerator = new NoKeyGenerator();
	
	private MybatisSqlGrammar(Class<?> entity, String alias) {
		super(entity, alias);
	}
	
	public static MybatisSqlGrammar instance(Class<?> entity) {
		return instance(entity, MASTER_ALIAS);
	}
	
	public static MybatisSqlGrammar instance(Class<?> entity, String alias) {
		return new MybatisSqlGrammar(entity, alias);
	}

	public StatementType getStatementType() {
		return statementType;
	}

	public MybatisSqlGrammar statementType(StatementType statementType) {
		this.statementType = statementType;
		return this;
	}

	public KeyGenerator getKeyGenerator() {
		return keyGenerator;
	}

	public MybatisSqlGrammar keyGenerator(KeyGenerator keyGenerator) {
		this.keyGenerator = keyGenerator;
		return this;
	}

}
