package com.easycodebox.auth.model.entity.sys;

import com.easycodebox.auth.model.enums.ModuleType;
import com.easycodebox.auth.model.util.mybatis.GeneratedValue;
import com.easycodebox.auth.model.util.mybatis.*;
import com.easycodebox.common.enums.entity.LogLevel;
import com.easycodebox.jdbc.entity.AbstractCreateEntity;

import javax.persistence.*;

/**
 * 日志 - 记录系统日志
 * @author WangXiaoJin
 *
 */
@Entity
@Table(name="sys_log")
public class Log extends AbstractCreateEntity {

	/**
	 * 主键
	 */
	@Id
	@GeneratedValue(GeneratorEnum.LOG_ID)
	private Long id;
	
	/**
	 * 标题
	 */
	private String title;
	
	/**
	 * 方法 - 执行的方法
	 */
	private String method;
	
	/**
	 * 请求地址
	 */
	private String url;
	
	/**
	 * 请求参数
	 */
	private String params;
	
	/**
	 * 模块类型
	 */
	private ModuleType moduleType;
	
	/**
	 * 日志级别
	 */
	private LogLevel logLevel;
	
	/**
	 * 返回数据
	 */
	private String result;
	
	/**
	 * 客户端IP
	 */
	private String clientIp;
	
	/**
	 * 错误信息
	 */
	private String errorMsg;
	

	public Log(){
	
	}

	public Log(Long id){
		this.id = id;
	}
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getParams() {
		return params;
	}
	
	public void setParams(String params) {
		this.params = params;
	}
	
	public ModuleType getModuleType() {
		return moduleType;
	}
	
	public void setModuleType(ModuleType moduleType) {
		this.moduleType = moduleType;
	}
	
	public LogLevel getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}

	public String getResult() {
		return result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public String getClientIp() {
		return clientIp;
	}
	
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
	
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	

}

