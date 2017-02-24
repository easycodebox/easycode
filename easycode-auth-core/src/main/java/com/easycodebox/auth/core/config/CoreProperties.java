package com.easycodebox.auth.core.config;

import com.easycodebox.common.CommonProperties;
import com.easycodebox.common.NamedSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author WangXiaoJin
 */
@Component("coreProperties")
public class CoreProperties extends NamedSupport {
	
	public static final String DEFAULT_NAME = CoreProperties.class.getName();
	
	private static CommonProperties INSTANCE;
	
	public static CommonProperties instance() {
		return INSTANCE == null ? (INSTANCE = new CommonProperties()) : INSTANCE;
	}
	
	public CoreProperties() {
		this(DEFAULT_NAME);
	}
	
	public CoreProperties(String name) {
		super(name);
	}
	
	/**
	 * 重置后的新密码
	 */
	@Value("${reset_pwd}")
	private String resetPwd;
	/**
	 * 是否可以修改super admin信息
	 */
	@Value("${modify_super_admin}")
	private boolean modifySuperAdmin;
	
	@Value("${jdbc.url}")
	private String jdbcUrl;
	
	@Value("${jdbc.username}")
	private String jdbcUsername;
	
	@Value("${jdbc.password}")
	private String jdbcPassword;
	
	@Value("${jdbc.pool.initialSize}")
	private Integer jdbcInitialSize;
	
	@Value("${jdbc.pool.minIdle}")
	private Integer jdbcMinIdle;
	
	@Value("${jdbc.pool.maxActive}")
	private Integer jdbcMaxActive;
	
	@Value("${jdbc.pool.maxWait}")
	private Long jdbcMaxWait;
	
	@Value("${jdbc.pool.timeBetweenEvictionRunsMillis}")
	private Long jdbcTBERunsMillis;
	
	@Value("${jdbc.pool.minEvictableIdleTimeMillis}")
	private Long jdbcMinEvictIdleTime;
	
	@Value("${jdbc.pool.validationQuery}")
	private String jdbcValidationQuery;
	
	@Value("${jdbc.pool.testWhileIdle}")
	private Boolean jdbcTestWhileIdle;
	
	@Value("${jdbc.pool.testOnBorrow}")
	private Boolean jdbcTestOnBorrow;
	
	@Value("${jdbc.pool.testOnReturn}")
	private Boolean jdbcTestOnReturn;
	
	@Value("${jdbc.pool.poolPreparedStatements}")
	private Boolean jdbcPreparedStatements;
	
	@Value("${jdbc.pool.maxPoolPreparedStatementPerConnectionSize}")
	private Integer jdbcMaxPPSPCSize;
	
	@Value("${jdbc.pool.filters}")
	private String jdbcPoolFilters;
	
	public String getResetPwd() {
		return resetPwd;
	}
	
	public void setResetPwd(String resetPwd) {
		this.resetPwd = resetPwd;
	}
	
	public boolean isModifySuperAdmin() {
		return modifySuperAdmin;
	}
	
	public void setModifySuperAdmin(boolean modifySuperAdmin) {
		this.modifySuperAdmin = modifySuperAdmin;
	}
	
	public String getJdbcUrl() {
		return jdbcUrl;
	}
	
	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}
	
	public String getJdbcUsername() {
		return jdbcUsername;
	}
	
	public void setJdbcUsername(String jdbcUsername) {
		this.jdbcUsername = jdbcUsername;
	}
	
	public String getJdbcPassword() {
		return jdbcPassword;
	}
	
	public void setJdbcPassword(String jdbcPassword) {
		this.jdbcPassword = jdbcPassword;
	}
	
	public Integer getJdbcInitialSize() {
		return jdbcInitialSize;
	}
	
	public void setJdbcInitialSize(Integer jdbcInitialSize) {
		this.jdbcInitialSize = jdbcInitialSize;
	}
	
	public Integer getJdbcMinIdle() {
		return jdbcMinIdle;
	}
	
	public void setJdbcMinIdle(Integer jdbcMinIdle) {
		this.jdbcMinIdle = jdbcMinIdle;
	}
	
	public Integer getJdbcMaxActive() {
		return jdbcMaxActive;
	}
	
	public void setJdbcMaxActive(Integer jdbcMaxActive) {
		this.jdbcMaxActive = jdbcMaxActive;
	}
	
	public Long getJdbcMaxWait() {
		return jdbcMaxWait;
	}
	
	public void setJdbcMaxWait(Long jdbcMaxWait) {
		this.jdbcMaxWait = jdbcMaxWait;
	}
	
	public Long getJdbcTBERunsMillis() {
		return jdbcTBERunsMillis;
	}
	
	public void setJdbcTBERunsMillis(Long jdbcTBERunsMillis) {
		this.jdbcTBERunsMillis = jdbcTBERunsMillis;
	}
	
	public Long getJdbcMinEvictIdleTime() {
		return jdbcMinEvictIdleTime;
	}
	
	public void setJdbcMinEvictIdleTime(Long jdbcMinEvictIdleTime) {
		this.jdbcMinEvictIdleTime = jdbcMinEvictIdleTime;
	}
	
	public String getJdbcValidationQuery() {
		return jdbcValidationQuery;
	}
	
	public void setJdbcValidationQuery(String jdbcValidationQuery) {
		this.jdbcValidationQuery = jdbcValidationQuery;
	}
	
	public Boolean getJdbcTestWhileIdle() {
		return jdbcTestWhileIdle;
	}
	
	public void setJdbcTestWhileIdle(Boolean jdbcTestWhileIdle) {
		this.jdbcTestWhileIdle = jdbcTestWhileIdle;
	}
	
	public Boolean getJdbcTestOnBorrow() {
		return jdbcTestOnBorrow;
	}
	
	public void setJdbcTestOnBorrow(Boolean jdbcTestOnBorrow) {
		this.jdbcTestOnBorrow = jdbcTestOnBorrow;
	}
	
	public Boolean getJdbcTestOnReturn() {
		return jdbcTestOnReturn;
	}
	
	public void setJdbcTestOnReturn(Boolean jdbcTestOnReturn) {
		this.jdbcTestOnReturn = jdbcTestOnReturn;
	}
	
	public Boolean getJdbcPreparedStatements() {
		return jdbcPreparedStatements;
	}
	
	public void setJdbcPreparedStatements(Boolean jdbcPreparedStatements) {
		this.jdbcPreparedStatements = jdbcPreparedStatements;
	}
	
	public Integer getJdbcMaxPPSPCSize() {
		return jdbcMaxPPSPCSize;
	}
	
	public void setJdbcMaxPPSPCSize(Integer jdbcMaxPPSPCSize) {
		this.jdbcMaxPPSPCSize = jdbcMaxPPSPCSize;
	}
	
	public String getJdbcPoolFilters() {
		return jdbcPoolFilters;
	}
	
	public void setJdbcPoolFilters(String jdbcPoolFilters) {
		this.jdbcPoolFilters = jdbcPoolFilters;
	}
	
}
