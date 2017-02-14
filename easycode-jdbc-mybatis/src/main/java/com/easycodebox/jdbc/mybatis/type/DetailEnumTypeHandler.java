package com.easycodebox.jdbc.mybatis.type;

import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.enums.DetailEnums;
import org.apache.ibatis.type.*;

import java.sql.*;

/**
 *
 * @author WangXiaoJin
 */
public class DetailEnumTypeHandler<T extends Enum<T> & DetailEnum<V>, V> extends BaseTypeHandler<T> {
	
	private Class<T> classType;
	
	public DetailEnumTypeHandler(Class<T> clazz) {
		this.classType = clazz;
	}
	
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i,
	                                T parameter, JdbcType jdbcType) throws SQLException {
		ps.setObject(i, parameter.getValue());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		return DetailEnums.parse(classType, rs.getObject(columnName));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		return (T) rs.getObject(columnIndex);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T getNullableResult(CallableStatement cs,
	                           int columnIndex) throws SQLException {
		return (T) cs.getObject(columnIndex);
	}
	
}
