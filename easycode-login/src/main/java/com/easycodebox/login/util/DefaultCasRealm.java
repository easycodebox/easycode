package com.easycodebox.login.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.cas.CasAuthenticationException;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.cas.CasToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Value;

import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.login.ws.UserWsService;
import com.easycodebox.login.ws.bo.UserExtWsBo;

public class DefaultCasRealm extends CasRealm implements Serializable {

	private static final long serialVersionUID = 2888923134019023168L;

	private static final Logger LOG = LoggerFactory.getLogger(DefaultCasRealm.class);
	
	public static final String PRINCIPAL_USER_ID_KEY = "id";
	
	@Resource
	private UserWsService userWsService;
	
	@Value("${project}")
	private String projectNo;
	@Value("${valid_project_auth:true}")
	private boolean validProjectAuth;
	
	public DefaultCasRealm() {
		setName("defaultCasRealm");
	}

	/**
	 * 重写认证 Authenticates a user and retrieves its information.
	 * 
	 * @param token the authentication token
	 * @throws AuthenticationException  if there is an error during authentication.
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {

		CasToken casToken = (CasToken) token;
		if (token == null) {
			return null;
		}
		String ticket = (String) casToken.getCredentials();
		if (StringUtils.isBlank(ticket)) {
			return null;
		}
		TicketValidator ticketValidator = ensureTicketValidator();
		try {
			
			// contact CAS server to validate service ticket
			Assertion casAssertion = ticketValidator.validate(ticket,
					getCasService());
			// get principal, user id and attributes
			AttributePrincipal casPrincipal = casAssertion.getPrincipal();
			Map<String, Object> attributes = casPrincipal.getAttributes();
			String username = casPrincipal.getName(),
					userId = (String)attributes.get(PRINCIPAL_USER_ID_KEY);
			LOG.debug("Validate ticket : {} in CAS server : {} to retrieve user : {}",
					new Object[] { ticket, getCasServerUrlPrefix(), username });
			
			// refresh authentication token (user id + remember me)
			casToken.setUserId(username);
			String rememberMeAttributeName = getRememberMeAttributeName();
			String rememberMeStringValue = (String) attributes.get(rememberMeAttributeName);
			boolean isRemembered = rememberMeStringValue != null
					&& Boolean.parseBoolean(rememberMeStringValue);
			if (isRemembered) {
				casToken.setRememberMe(true);
			}
			// create simple authentication info
			List<Object> principals = CollectionUtils.asList(username, attributes);
			PrincipalCollection principalCollection = new SimplePrincipalCollection(
					principals, getName());
			
			UserExtWsBo user = userWsService.loginSucBack(userId, projectNo, validProjectAuth);
			ShiroSecurityUtils.setSubject(user);
			
			return new SimpleAuthenticationInfo(principalCollection, ticket);
		} catch (Exception e) {
			LOG.warn("Get authentication error!", e);
			throw new CasAuthenticationException("Get authentication error!", e);
		}
	}
	
}
