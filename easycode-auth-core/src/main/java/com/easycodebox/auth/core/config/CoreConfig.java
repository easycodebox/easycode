package com.easycodebox.auth.core.config;

import com.easycodebox.auth.core.idconverter.DefaultUserIdConverter;
import com.easycodebox.auth.core.service.user.UserService;
import com.easycodebox.auth.core.util.aop.log.LogAspect;
import com.easycodebox.auth.model.enums.IdGeneratorEnum;
import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.enums.EnumClassFactory;
import com.easycodebox.common.freemarker.ConfigurationPostProcessor;
import com.easycodebox.common.idconverter.*;
import com.easycodebox.common.idgenerator.DetailEnumIdGenTypeParser;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.spring.ApplicationContextFactory;
import com.easycodebox.jdbc.config.ConfigEntityBean;
import com.easycodebox.jdbc.mybatis.*;
import com.easycodebox.jdbc.mybatis.spring.DefaultSqlSessionFactoryBean;
import com.easycodebox.jdbc.mybatis.type.DetailEnumTypeHandler;
import com.easycodebox.jdbc.support.DefaultJdbcHandler;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * core包Spring配置
 * @author WangXiaoJin
 */
@Configuration
@ComponentScan(basePackages = "com.easycodebox.idgenerator.service")
@SuppressWarnings("Duplicates")
public class CoreConfig {
	
	/**
	 * 枚举类包路径
	 */
	private String[] enumPackages = {
			"com.easycodebox.auth.model.enums",
			"com.easycodebox.common.enums.entity"
	};
	/**
	 * 实体类包路径
	 */
	private String[] entityPackages = {
			"com.easycodebox.auth.model.entity",
			"com.easycodebox.idgenerator.entity"
	};
	
	/**
	 * 增加自定义日期格式化工厂
	 * @return
	 */
	@Bean
	public static ConfigurationPostProcessor freemarkerCfgPostProcessor() {
		return new ConfigurationPostProcessor();
	}
	
	/**
	 * 配置日志
	 */
	@Bean
	public LogAspect logAspect() {
		return new LogAspect();
	}
	
	@Bean
	public DetailEnumIdGenTypeParser idGenTypeParser() {
		return new DetailEnumIdGenTypeParser(IdGeneratorEnum.class);
	}
	
	/**
	 * 配置枚举类型工厂
	 */
	@Bean
	public EnumClassFactory enumClassFactory() {
		EnumClassFactory factory = new EnumClassFactory();
		factory.setPackagesToScan(enumPackages);
		return factory;
	}
	
	@Bean
	public ConfigEntityBean annotatedConfig() {
		ConfigEntityBean bean = new ConfigEntityBean();
		bean.setPackagesToScan(entityPackages);
		return bean;
	}
	
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.druid")
	public DataSource dataSource(DataSourceProperties properties) throws SQLException {
		return properties.initializeDataSourceBuilder().build();
	}
	
	/* ----------------------   配置MyBatis   ----------------------------------*/
	/**
	 * autoMappingBehavior=FULL : 查询结果支持嵌套属性赋值,默认不支持（PARTIAL）
	 */
	@Bean
	public DefaultConfiguration ibatisConfiguration() {
		DefaultConfiguration configuration = new DefaultConfiguration();
		configuration.setLazyLoadingEnabled(false);
		configuration.setCacheEnabled(false);
		configuration.setAutoMappingBehavior(AutoMappingBehavior.FULL);
		return configuration;
	}
	
	@Bean
	public DynamicTypeHandlerRegister detailEnumRegister() {
		DynamicTypeHandlerRegister register = new DynamicTypeHandlerRegister(DetailEnum.class, DetailEnumTypeHandler.class);
		register.setPackages(enumPackages);
		return register;
	}
	
	@Bean
	public DefaultSqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws SQLException {
		DefaultSqlSessionFactoryBean factoryBean = new DefaultSqlSessionFactoryBean();
		factoryBean.setDataSource(dataSource);
		factoryBean.setConfiguration(ibatisConfiguration());
		factoryBean.setTypeAliasesPackage(Strings.join(entityPackages, Symbol.COMMA));
		factoryBean.setMapperLocations(new Resource[]{new ClassPathResource("CommonMapper.xml")});
		factoryBean.setDynamicTypeHandlerRegister(detailEnumRegister());
		return factoryBean;
	}
	
	/**
	 * 在insert、update时修改自动设置操作人、操作时间
	 */
	@Bean
	public DefaultJdbcHandler jdbcHandler() {
		return new DefaultJdbcHandler();
	}
	
	/**
	 * jdbc处理器，实际处理sql的类
	 */
	@Bean
	public MybatisJdbcProcessor jdbcProcessor(SqlSessionTemplate sqlSessionTemplate) {
		MybatisJdbcProcessor processor = new MybatisJdbcProcessor();
		processor.setSqlSessionTemplate(sqlSessionTemplate);
		return processor;
	}
	
	@Bean
	public ApplicationContextFactory applicationContextFactory() {
		return new ApplicationContextFactory();
	}
	
	/* =================================== 【ID转换器 - START】  ============================================== */
	@Bean
	public DefaultUserIdConverter userIdConverter(UserService userService) {
		DefaultUserIdConverter converter = new DefaultUserIdConverter(userService);
		converter.setJdbcHandler(jdbcHandler());
		return converter;
	}
	
	@Bean
	public IdConverterRegistry idConverterRegistry(UserIdConverter userIdConverter) {
		IdConverterRegistry registry = new IdConverterRegistry();
		registry.setDefaultModule("user");
		Map<String, IdConverter> map = new HashMap<>();
		map.put("user", userIdConverter);
		registry.setConverterMap(map);
		return registry;
	}
	/* =================================== 【ID转换器 - END 】  ============================================== */
	
}
