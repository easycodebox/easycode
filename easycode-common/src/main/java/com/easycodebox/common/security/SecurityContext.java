package com.easycodebox.common.security;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.easycodebox.common.enums.entity.PhoneType;

/**
 * @author WangXiaoJin
 * 
 */
public class SecurityContext<T extends Serializable> implements Externalizable{
	
	private static final long serialVersionUID = 7396550641375652577L;
	
	private transient HttpServletRequest request;
	private transient HttpServletResponse response;
	
	private T security;
	private String ip;
	private String sessionId;
	private PhoneType phoneType;
	private String loginStatusId;

	
	public T getSecurity() {
		return security;
	}

	public void setSecurity(T security) {
		this.security = security;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public PhoneType getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(PhoneType phoneType) {
		this.phoneType = phoneType;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getLoginStatusId() {
		return loginStatusId;
	}

	public void setLoginStatusId(String loginStatusId) {
		this.loginStatusId = loginStatusId;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.security = (T)in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(security);
	}

}
