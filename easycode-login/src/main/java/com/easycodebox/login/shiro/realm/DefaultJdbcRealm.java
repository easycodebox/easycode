package com.easycodebox.login.shiro.realm;

import javax.sql.DataSource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cas.CasToken;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.easycodebox.common.enums.entity.LogLevel;
import com.easycodebox.common.error.ErrorContext;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

/**
 * 
 * @author WangXiaoJin
 *
 */
public class DefaultJdbcRealm extends JdbcRealm {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private JdbcTemplate jdbcTemplate;
	private String authFailMsg;
	private RowMapper<?> rowMapper;
	
	public DefaultJdbcRealm() {
		super();
		setAuthenticationTokenClass(CasToken.class);
	}
	
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

		CasToken casToken = (CasToken) token;
		if (casToken == null) {
			return null;
		}
		String userId = (String)casToken.getPrincipal();
        try {
        	
        	Object user = null;
        	if (rowMapper != null) {
        		user = getJdbcTemplate().queryForObject(authenticationQuery, rowMapper, userId);
			} else {
				user = getJdbcTemplate().queryForMap(authenticationQuery, userId);
			}
        	
            PrincipalCollection principalCollection = new SimplePrincipalCollection(user, getName());
            return new SimpleAuthenticationInfo(principalCollection, (String)casToken.getCredentials());
            
        } catch (IncorrectResultSizeDataAccessException e) {
        	
        	if (e.getActualSize() == 0) {
        		throw ErrorContext.instance(authFailMsg).logLevel(LogLevel.WARN);
            } else {
            	final String msg = "Multiple records found for user [" + userId + "]";
            	log.error(msg, e);
                throw new AuthenticationException(msg, e);
            }
        } catch (DataAccessException e) {
        	
        	final String msg = "There was a SQL error while authenticating user [" + userId + "]";
        	log.error(msg, e);
        	throw new AuthenticationException(msg, e);
        }
    }
	
	/**
	 * 权限/角色信息统一由DefaultCasRealm处理
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		return null;
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		super.setDataSource(dataSource);
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public String getAuthFailMsg() {
		return authFailMsg;
	}

	public void setAuthFailMsg(String authFailMsg) {
		this.authFailMsg = authFailMsg;
	}

	public RowMapper<?> getRowMapper() {
		return rowMapper;
	}

	public void setRowMapper(RowMapper<?> rowMapper) {
		this.rowMapper = rowMapper;
	}

}
