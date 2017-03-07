package com.easycodebox.auth.core.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.easycodebox.auth.core.idconverter.DefaultUserIdConverter;
import com.easycodebox.auth.core.service.user.UserService;
import com.easycodebox.auth.core.util.aop.log.LogAspect;
import com.easycodebox.auth.model.enums.IdGeneratorEnum;
import com.easycodebox.common.CommonProperties;
import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.enums.EnumClassFactory;
import com.easycodebox.common.error.CodeMsg.Code;
import com.easycodebox.common.freemarker.FreemarkerProperties;
import com.easycodebox.common.idconverter.*;
import com.easycodebox.common.idgenerator.DetailEnumIdGenTypeParser;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.spring.ApplicationContextFactory;
import com.easycodebox.common.spring.StringToEnumConverterFactory;
import com.easycodebox.jdbc.config.ConfigEntityBean;
import com.easycodebox.jdbc.mybatis.*;
import com.easycodebox.jdbc.mybatis.spring.DefaultSqlSessionFactoryBean;
import com.easycodebox.jdbc.mybatis.spring.DefaultSqlSessionTemplate;
import com.easycodebox.jdbc.mybatis.type.DetailEnumTypeHandler;
import com.easycodebox.jdbc.support.DefaultJdbcHandler;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.datetime.DateTimeFormatAnnotationFormatterFactory;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.sql.SQLException;
import java.util.*;

/**
 * core包Spring配置
 * @author WangXiaoJin
 */
@Configuration
@Import(CoreCacheConfig.class)
@EnableAspectJAutoProxy
@ComponentScan(
		basePackages = {
				"com.easycodebox.auth.core",
				"com.easycodebox.idgenerator.service"
		}
)
@PropertySource(ignoreResourceNotFound = true, value = {
		"classpath:core.properties",
		"classpath:jdbc.properties",
		"classpath:redis.properties",
		"classpath:url.properties",
		"classpath:mail.properties",
		"classpath:login.properties"
})
@EnableTransactionManagement
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
	 * MyBatis Mapper文件base packages
	 */
	private static final String[] mapperPackages = {
			"com.easycodebox.auth.core.dao"
	};
	
	@Autowired
	private CoreProperties coreProperties;
	
	@Autowired
	private Environment environment;
	
	/**
	 * 因{@link PropertySourcesPlaceholderConfigurer}实现了{@link BeanFactoryPostProcessor}接口且类上有{@link Configuration}，
	 * 所以方法必须是{@code static}
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholder() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
		configurer.setIgnoreResourceNotFound(true);
		configurer.setTrimValues(true);
		configurer.setProperties(customProperties());
		return configurer;
	}
	
	/**
	 * 把环境变量拷贝到map中，供其他类使用。通过{@link PropertySource}加载的属性文件，最终生成Properties类，
	 * 而此类是线程安全的，性能会有一定的损耗，应在只读的场景下转成非线程安全的Map
	 */
	@Bean
	@SuppressWarnings("unchecked")
	public Map properties() {
		Map props = new HashMap();
		if (environment instanceof ConfigurableEnvironment) {
			ConfigurableEnvironment configEnv = (ConfigurableEnvironment) environment;
			for (org.springframework.core.env.PropertySource<?> source : configEnv.getPropertySources()) {
				if (source instanceof EnumerablePropertySource) {
					EnumerablePropertySource eps = (EnumerablePropertySource) source;
					for (String key : eps.getPropertyNames()) {
						props.put(key, eps.getProperty(key));
					}
				}
			}
		}
		//增加自定义的属性资源
		Properties custom = customProperties();
		for (Object key : custom.keySet()) {
			props.put(key, custom.get(key));
		}
		return props;
	}
	
	/**
	 * 返回自定义的属性资源
	 * @return
	 */
	private static Properties customProperties() {
		Properties props = new Properties();
		props.setProperty("code.suc", Code.SUC_CODE);
		props.setProperty("code.fail", Code.FAIL_CODE);
		props.setProperty("code.no.login", Code.NO_LOGIN_CODE);
		return props;
	}
	
	/**
	 * 配置项刷入Bean中
	 */
	@Bean
	public CommonProperties commonProperties() {
		return new CommonProperties();
	}
	
	/**
	 * 配置项刷入Bean中
	 */
	@Bean
	public FreemarkerProperties freemarkerProperties() {
		return new FreemarkerProperties();
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
	
	/**
	 * 数据源配置 <p/>
	 * initialSize/minIdle/maxActive ==> 配置初始化大小、最小、最大 <p/>
	 * maxWait ==> 配置获取连接等待超时的时间 <p/>
	 * timeBetweenEvictionRunsMillis ==> 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 <p/>
	 * minEvictableIdleTimeMillis ==> 配置一个连接在池中最小生存的时间，单位是毫秒 <p/>
	 * poolPreparedStatements/maxPoolPreparedStatementPerConnectionSize ==> 打开PSCache，并且指定每个连接上PSCache的大小 <p/>
	 * filters ==> 配置监控统计拦截的filters <p/>
	 * connectionProperties ==> config.decrypt用于解密数据库密码 <p/>
	 */
	@Bean(initMethod = "init", destroyMethod = "close")
	public DruidDataSource dataSource() throws SQLException {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(coreProperties.getJdbcUrl());
		dataSource.setUsername(coreProperties.getJdbcUsername());
		dataSource.setPassword(coreProperties.getJdbcPassword());
		dataSource.setInitialSize(coreProperties.getJdbcInitialSize());
		dataSource.setMinIdle(coreProperties.getJdbcMinIdle());
		dataSource.setMaxActive(coreProperties.getJdbcMaxActive());
		dataSource.setMaxWait(coreProperties.getJdbcMaxWait());
		dataSource.setTimeBetweenEvictionRunsMillis(coreProperties.getJdbcTBERunsMillis());
		dataSource.setMinEvictableIdleTimeMillis(coreProperties.getJdbcMinEvictIdleTime());
		dataSource.setValidationQuery(coreProperties.getJdbcValidationQuery());
		dataSource.setTestWhileIdle(coreProperties.getJdbcTestWhileIdle());
		dataSource.setTestOnBorrow(coreProperties.getJdbcTestOnBorrow());
		dataSource.setTestOnReturn(coreProperties.getJdbcTestOnReturn());
		dataSource.setPoolPreparedStatements(coreProperties.getJdbcPreparedStatements());
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(coreProperties.getJdbcMaxPPSPCSize());
		dataSource.setFilters(coreProperties.getJdbcPoolFilters());
		dataSource.setConnectionProperties("config.decrypt=true");
		return dataSource;
	}
	
	@Bean
	public DataSourceTransactionManager txManager() throws SQLException {
		DataSourceTransactionManager manager = new DataSourceTransactionManager();
		manager.setDataSource(dataSource());
		return manager;
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
	public DefaultSqlSessionFactoryBean sqlSessionFactory() throws SQLException {
		DefaultSqlSessionFactoryBean factoryBean = new DefaultSqlSessionFactoryBean();
		factoryBean.setDataSource(dataSource());
		factoryBean.setConfiguration(ibatisConfiguration());
		factoryBean.setTypeAliasesPackage(Strings.join(entityPackages, Symbol.COMMA));
		factoryBean.setMapperLocations(new Resource[]{new ClassPathResource("CommonMapper.xml")});
		factoryBean.setDynamicTypeHandlerRegister(detailEnumRegister());
		return factoryBean;
	}
	
	@Bean
	public DefaultSqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws SQLException {
		return new DefaultSqlSessionTemplate(sqlSessionFactory);
	}
	
	/**
	 * 因{@link MapperScannerConfigurer}实现了{@link BeanFactoryPostProcessor}接口且类上有{@link Configuration}，
	 * 所以方法必须是{@code static}
	 */
	@Bean
	public static MapperScannerConfigurer mapperScannerConfigurer() {
		MapperScannerConfigurer configurer = new MapperScannerConfigurer();
		configurer.setBasePackage(Strings.join(mapperPackages, Symbol.COMMA));
		return configurer;
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
	
	@Bean
	public FormattingConversionServiceFactoryBean conversionService() {
		FormattingConversionServiceFactoryBean factoryBean = new FormattingConversionServiceFactoryBean();
		factoryBean.setConverters(Collections.singleton(new StringToEnumConverterFactory()));
		Set<Object> formatters = new HashSet<>();
		//设置Date类型默认Formatter,全局有效。pattern属性：格式化的默认格式
		DateFormatter dateFormatter = new DateFormatter();
		dateFormatter.setPattern("yyyy-MM-dd HH:mm:ss");
		formatters.add(dateFormatter);
		/*
		默认情况下FormattingConversionServiceFactoryBean已经注册了DateTimeFormatAnnotationFormatterFactory，
		由registerDefaultFormatters属性控制，此属性默认为true。
		这里再次注册DateTimeFormatAnnotationFormatterFactory的原因如下：
		Spring可以对相同类型转换提供多个Converter，以List形式保存，后添加的Converter会被插入到最前面。而注册
		默认Formatters的行为在上面DateFormatter之前，所以日期类型转换时，只会用上面的DateFormatter，无论你
		有没有在属性上配置@DateTimeFormat注解，Spring都不会使用DateTimeFormatAnnotationFormatterFactory。
		因此这里再次注册了DateTimeFormatAnnotationFormatterFactory，让它第一个检查，如果有@DateTimeFormat则
		用它，没有则使用上面的DateFormatter。这是最简单的方法，不需要写代码就可以实现需求了。
		*/
		formatters.add(new DateTimeFormatAnnotationFormatterFactory());
		factoryBean.setFormatters(formatters);
		return factoryBean;
	}
}
