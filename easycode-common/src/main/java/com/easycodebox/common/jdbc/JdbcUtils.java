package com.easycodebox.common.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;


/**
 * @author WangXiaoJin
 * 
 */
public abstract class JdbcUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(JdbcUtils.class);

	public static Connection connect(String driverClass, String jdbcUrl, 
			String user, String password) throws InstantiationException, 
					IllegalAccessException, ClassNotFoundException, 
					SQLException {
		Class.forName(driverClass).newInstance();
		return java.sql.DriverManager.getConnection(jdbcUrl, user, password);
	}
	
	public static void closeQuietly(ResultSet rs) {
		if(rs == null) {
			return;
		}
		try {
			rs.close();
		} catch (SQLException e) {
			LOG.warn("failed to close result set", e);
		}
	}
	
	public static void closeQuietly(Statement stat) {
		if(stat == null) {
			return;
		}
		try {
			stat.close();
		} catch (SQLException e) {
			LOG.warn("failed to close statement", e);
		}
	}
	
	public static void closeQuietly(PreparedStatement pstat) {
		if(pstat == null) {
			return;
		}
		try {
			pstat.close();
		} catch (SQLException e) {
			LOG.warn("failed to close prepared statement", e);
		}
	}
	
	public static void closeQuietly(Connection con) {
		if(con == null) {
			return;
		}
		try {
			con.close();
		} catch (SQLException e) {
			LOG.warn("failed to close connection", e);
		}
	}
}
