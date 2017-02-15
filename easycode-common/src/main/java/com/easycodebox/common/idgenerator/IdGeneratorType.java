package com.easycodebox.common.idgenerator;

import java.io.Serializable;


/**
 * @author WangXiaoJin
 * 
 */
public interface IdGeneratorType extends Serializable {
	
	/**
	 * 获取原生的IdGenerator
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	AbstractIdGenerator getRawIdGenerator();
	
	/**
	 * 获取当前使用的IdGenerator
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	AbstractIdGenerator getIdGenerator();
	
	@SuppressWarnings("rawtypes")
	void setIdGenerator(AbstractIdGenerator idGenerator);

}
