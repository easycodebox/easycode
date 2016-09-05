package com.easycodebox.common.generator;

import java.io.Serializable;


/**
 * @author WangXiaoJin
 * 
 */
public interface GeneratorType extends Serializable {
	
	/**
	 * 获取原生的Generator
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	AbstractGenerator getRawGenerator();
	
	/**
	 * 获取当前使用的Generator
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	AbstractGenerator getGenerator();
	
	@SuppressWarnings("rawtypes")
	void setGenerator(AbstractGenerator generator);

}
