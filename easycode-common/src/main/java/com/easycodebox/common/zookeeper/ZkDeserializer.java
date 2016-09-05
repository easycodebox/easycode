package com.easycodebox.common.zookeeper;

/**
 * zookeeper的数据反序列化
 * @author WangXiaoJin
 * 
 */
public interface ZkDeserializer<T> {

	/**
	 * 反序列化节点数据
	 * @param data
	 * @return
	 */
	T deserialize(byte[] data) throws ZkDeserializeException;
	
}
