package com.easycodebox.auth.core.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * @author WangXiaoJin
 */
@TestConfiguration
public class CoreTestConfig {
	
	/**
	 * 当easycode-jdbc-mybatis支持HSQL时再开启此功能，
	 */
	//@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.HSQL)
				.addScript("classpath:easycode-auth-schema.sql")
				.addScript("classpath:easycode-auth-data.sql")
				.build();
	}
	
}
