package com.easycodebox.cas.jdbc;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.validation.constraints.NotNull;

import org.jasig.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.AccountDisabledException;
import org.jasig.cas.authentication.HandlerResult;
import org.jasig.cas.authentication.PreventedException;
import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.easycodebox.cas.cache.ClearUserCache;
import com.easycodebox.cas.exception.MultipleUserException;

/**
 * @author WangXiaoJin
 *
 */
public class JdbcUsernamePasswordAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {

	private static final Logger LOG = LoggerFactory.getLogger(JdbcUsernamePasswordAuthenticationHandler.class);
	
	private ClearUserCache clearUserCache;
	
    @NotNull
    private String verifySql;
    @NotNull
    private String loginFailSql;
    @NotNull
    private String resetFailSql;
    @NotNull
    private String closeUser;
    @NotNull
    private Integer maxLoginFail;

    @Override
	protected final HandlerResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential credential) 
			throws GeneralSecurityException, PreventedException {
        final String username = credential.getUsername();
        final String encryptedPassword = this.getPasswordEncoder().encode(credential.getPassword());
        try {
        	Map<String,Object> user = getJdbcTemplate().queryForMap(this.verifySql, username);
            if(user != null) {
            	String id = (String)user.get("id");
            	Integer status = (Integer)user.get("status"),
            			loginFail = (Integer)user.get("loginFail");
            	if(encryptedPassword.equals(user.get("password"))) {
            		if(status == 0) {
            			if(loginFail > 0) {
            				getJdbcTemplate().update(this.resetFailSql, username);
            				//清除user缓存
            				try {
								clearUserCache.clear(id);
							} catch (Exception e) {
								LOG.error("清除user(" + id + ")缓存失败！", e);
							}
            			}
            			Map<String, Object> attributes = new HashMap<String, Object>(1);
            			attributes.put("id", id);
            			return createHandlerResult(credential, 
            					this.principalFactory.createPrincipal(username, attributes), null);
            		}else if(status == 1) {
            			throw new AccountDisabledException("此用户已被禁用  : " + username);
            		}
            	}else {
        			getJdbcTemplate().update(loginFail + 1 < maxLoginFail ? 
        					loginFailSql : closeUser, username);
        			//清除user缓存
    				try {
						clearUserCache.clear(id);
					} catch (Exception e) {
						LOG.error("清除user(" + id + ")缓存失败！", e);
					}
            	}
            }
            throw new FailedLoginException("Password does not match value on record.");
        } catch (final IncorrectResultSizeDataAccessException e) {
        	if (e.getActualSize() == 0) {
                throw new AccountNotFoundException(username + " not found with SQL query");
            } else {
            	String msg = "Multiple records found for " + username;
            	LOG.error(msg, e);
                throw new MultipleUserException(msg);
            }
        } catch (final DataAccessException e) {
        	String msg = "SQL exception while executing query for " + username;
        	LOG.error(msg, e);
            throw new PreventedException(msg, e);
        }
    }

	public void setVerifySql(String verifySql) {
		this.verifySql = verifySql;
	}

	public void setLoginFailSql(String loginFailSql) {
		this.loginFailSql = loginFailSql;
	}

	public void setResetFailSql(String resetFailSql) {
		this.resetFailSql = resetFailSql;
	}

	public void setCloseUser(String closeUser) {
		this.closeUser = closeUser;
	}

	public void setMaxLoginFail(Integer maxLoginFail) {
		this.maxLoginFail = maxLoginFail;
	}

	public ClearUserCache getClearUserCache() {
		return clearUserCache;
	}

	public void setClearUserCache(ClearUserCache clearUserCache) {
		this.clearUserCache = clearUserCache;
	}

}
