package com.easycodebox.common.jdbc;

/**
 * @author WangXiaoJin
 *
 */
public enum SqlCommandType {
	
	UNKNOWN {
		@Override
		public org.apache.ibatis.mapping.SqlCommandType getIbatisType() {
			return org.apache.ibatis.mapping.SqlCommandType.UNKNOWN;
		}
	}, INSERT {
		@Override
		public org.apache.ibatis.mapping.SqlCommandType getIbatisType() {
			return org.apache.ibatis.mapping.SqlCommandType.INSERT;
		}
	}, UPDATE {
		@Override
		public org.apache.ibatis.mapping.SqlCommandType getIbatisType() {
			return org.apache.ibatis.mapping.SqlCommandType.UPDATE;
		}
	}, DELETE {
		@Override
		public org.apache.ibatis.mapping.SqlCommandType getIbatisType() {
			return org.apache.ibatis.mapping.SqlCommandType.DELETE;
		}
	}, SELECT_ONE {
		@Override
		public org.apache.ibatis.mapping.SqlCommandType getIbatisType() {
			return org.apache.ibatis.mapping.SqlCommandType.SELECT;
		}
	}, SELECT_LIST {
		@Override
		public org.apache.ibatis.mapping.SqlCommandType getIbatisType() {
			return org.apache.ibatis.mapping.SqlCommandType.SELECT;
		}
	};
	
	public abstract org.apache.ibatis.mapping.SqlCommandType getIbatisType();

}
