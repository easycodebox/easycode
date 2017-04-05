package com.easycodebox.auth.core;

import com.easycodebox.auth.core.config.CoreConfig;
import com.easycodebox.auth.core.util.Constants;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * @author WangXiaoJin
 */
@SpringBootApplication
public class CoreTestApplication {
	
	@TestConfiguration
	public class CoreTestConfig {
		
		/**
		 * 当easycode-jdbc-mybatis支持HSQL时再开启此功能，
		 * 同时开启{@link CoreConfig#dataSource}的@Profile功能
		 */
		@Bean
		@Profile(Constants.INTEGRATION_TEST_KEY)
		public DataSource dataSource() {
			return new EmbeddedDatabaseBuilder()
					.setType(EmbeddedDatabaseType.HSQL)
					.addScript("classpath:easycode-auth-schema.sql")
					.addScript("classpath:easycode-auth-data.sql")
					.build();
		}
		
	}
	
}
