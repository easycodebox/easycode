package com.easycodebox.common.zookeeper;

/**
 * zookeeper的数据序列化
 * @author WangXiaoJin
 * 
 */
public interface ZkSerializer {
	
	/**
	 * 序列化节点数据
	 * @param data
	 * @return
	 */
	byte[] serialize(Object data) throws ZkSerializeException;
	
	<T> boolean support(Class<T> clazz);

}
