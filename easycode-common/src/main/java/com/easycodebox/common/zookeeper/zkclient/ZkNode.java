package com.easycodebox.common.zookeeper.zkclient;

import com.easycodebox.common.validate.Assert;
import com.easycodebox.common.zookeeper.*;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * zookeeper相关操作
 * @author WangXiaoJin
 * 
 */
public class ZkNode<T> implements InitializingBean {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * 打印load和store的数据
	 */
	private boolean debug = false;
	private ZooKeeper client;
	private boolean watchBool;
	private Watcher watchObj;
	private Stat stat;
	private String nodeName;
	private ZkNodeNameMaker maker;
	private ZkDeserializer<T> deserializer;
	private ZkSerializer serializer;
	private int version = -1;

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(client, "'client' can't be null.");
		if (nodeName == null && maker == null) 
			throw new IllegalArgumentException("'nodeName' and 'maker' at least one has value.");
	}
	
	/**
	 * 获取指定节点数据
	 * @return
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 * @throws ZkDeserializeException 
	 */
	public Object load() throws KeeperException, InterruptedException, ZkDeserializeException {
		String name = StringUtils.isBlank(nodeName) ? maker.make() : nodeName;
		if (name == null) return null;
		byte[] data;
		if (watchObj == null) {
			data = client.getData(name, watchBool, stat);
		}else {
			data = client.getData(name, watchObj, stat);
		}
		if(debug) {
			log.info("ZooKeeper get data. path: {} --- data: {}", name, data);
		}
		if(deserializer != null) {
			return deserializer.deserialize(data);
		}
		return data;
	}
	
	/**
	 * 存储数据
	 * @return
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 * @throws ZkSerializeException 
	 */
	public void store(Object data) throws KeeperException, InterruptedException, ZkSerializeException {
		String name = StringUtils.isBlank(nodeName) ? maker.make() : nodeName;
		if (name == null) return;
		byte[] bytes = null;
		if (data != null && serializer != null && serializer.support(data.getClass())) {
			bytes = serializer.serialize(data);
		}else if(data != null) {
			bytes = data.toString().getBytes();
		}
		client.setData(name, bytes, version);
		if(debug) {
			log.info("ZooKeeper set data. path: {} --- data: {}", name, bytes);
		}
	}
	
	public ZooKeeper getClient() {
		return client;
	}

	public void setClient(ZooKeeper client) {
		this.client = client;
	}

	public boolean isWatchBool() {
		return watchBool;
	}

	public void setWatchBool(boolean watchBool) {
		this.watchBool = watchBool;
	}

	public Watcher getWatchObj() {
		return watchObj;
	}

	public void setWatchObj(Watcher watchObj) {
		this.watchObj = watchObj;
	}

	public Stat getStat() {
		return stat;
	}

	public void setStat(Stat stat) {
		this.stat = stat;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public ZkNodeNameMaker getMaker() {
		return maker;
	}

	public void setMaker(ZkNodeNameMaker maker) {
		this.maker = maker;
	}

	public ZkDeserializer<T> getDeserializer() {
		return deserializer;
	}

	public void setDeserializer(ZkDeserializer<T> deserializer) {
		this.deserializer = deserializer;
	}

	public ZkSerializer getSerializer() {
		return serializer;
	}

	public void setSerializer(ZkSerializer serializer) {
		this.serializer = serializer;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
}
