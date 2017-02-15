package com.easycodebox.common.idgenerator;

import java.io.Serializable;


/**
 * @author WangXiaoJin
 * 
 */
public interface IdGeneratorType extends Serializable {
	
	/**
	 * 持久化此生成器的唯一标识值
	 * @return
	 */
	String getPersistentKey();
	
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
