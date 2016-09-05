package com.easycodebox.common.jdbc;

/**
 * @author WangXiaoJin
 *
 */
public enum StatementType {
	
	STATEMENT {
		@Override
		public org.apache.ibatis.mapping.StatementType getIbatisType() {
			return org.apache.ibatis.mapping.StatementType.STATEMENT;
		}
	}, PREPARED {
		@Override
		public org.apache.ibatis.mapping.StatementType getIbatisType() {
			return org.apache.ibatis.mapping.StatementType.PREPARED;
		}
	}, CALLABLE {
		@Override
		public org.apache.ibatis.mapping.StatementType getIbatisType() {
			return org.apache.ibatis.mapping.StatementType.CALLABLE;
		}
	};
	
	public abstract org.apache.ibatis.mapping.StatementType getIbatisType();
	
}
