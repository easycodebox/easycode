package com.easycodebox.auth.model.util.mybatis;

import com.easycodebox.auth.model.enums.*;
import com.easycodebox.common.enums.*;
import com.easycodebox.common.enums.entity.*;
import com.easycodebox.common.enums.entity.status.*;
import org.apache.ibatis.type.*;

import java.sql.*;

/**
 * @author WangXiaoJin
 *
 */
@MappedTypes({
	/* ---- CORE包 ----- */
	LogLevel.class,
	ModuleType.class,
	MsgType.class,
	/* ---- COMMON包 ----- */
	Gender.class,
	OpenClose.class,
	PhoneType.class,
	YesNo.class,
	DataType.class,
	RequestMethod.class,
	
	Status.class,
	UserStatus.class,
	/* ---- utils ----- */
	GeneratorEnum.class
})
public class EnumTypeHandler<T extends Enum<T> & DetailEnum<V>, V> extends BaseTypeHandler<T>{
	
	private Class<T> classType;
	
	public EnumTypeHandler(Class<T> clazz) {
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
		return (T)rs.getObject(columnIndex);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getNullableResult(CallableStatement cs,
			int columnIndex) throws SQLException {
		return (T)cs.getObject(columnIndex);
	}

}
