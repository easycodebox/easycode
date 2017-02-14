package com.easycodebox.jdbc.mybatis.spring;

import com.easycodebox.jdbc.mybatis.DynamicTypeHandlerRegister;
import org.apache.commons.lang.ArrayUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;

import java.io.IOException;

/**
 * 实现功能请参考{@link DynamicTypeHandlerRegister}
 * @author WangXiaoJin
 */
public class DefaultSqlSessionFactoryBean extends SqlSessionFactoryBean {
	
	private DynamicTypeHandlerRegister[] dynamicTypeHandlerRegisters;
	
	@Override
	protected SqlSessionFactory buildSqlSessionFactory() throws IOException {
		SqlSessionFactory factory = super.buildSqlSessionFactory();
		if (ArrayUtils.isNotEmpty(dynamicTypeHandlerRegisters)) {
			for (DynamicTypeHandlerRegister register : dynamicTypeHandlerRegisters) {
				register.register(factory.getConfiguration());
			}
		}
		return factory;
	}
	
	public DynamicTypeHandlerRegister[] getDynamicTypeHandlerRegisters() {
		return dynamicTypeHandlerRegisters;
	}
	
	public void setDynamicTypeHandlerRegisters(DynamicTypeHandlerRegister[] dynamicTypeHandlerRegisters) {
		this.dynamicTypeHandlerRegisters = dynamicTypeHandlerRegisters;
	}
	
	public void setDynamicTypeHandlerRegister(DynamicTypeHandlerRegister dynamicTypeHandlerRegister) {
		this.dynamicTypeHandlerRegisters = new DynamicTypeHandlerRegister[] {dynamicTypeHandlerRegister};
	}
	
}
