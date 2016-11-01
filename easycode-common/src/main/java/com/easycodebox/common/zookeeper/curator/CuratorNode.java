package com.easycodebox.common.zookeeper.curator;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.InitializingBean;

import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.common.zookeeper.ZkDeserializer;
import com.easycodebox.common.zookeeper.ZkNodeNameMaker;
import com.easycodebox.common.zookeeper.ZkSerializer;

/**
 * zookeeper相关操作
 * @author WangXiaoJin
 * 
 */
public class CuratorNode<T> implements InitializingBean {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * 打印load和store的数据
	 */
	private boolean debug = false;
	private CuratorFramework client;
	private boolean watchBool;
	private CuratorWatcher watchObj;
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
	 * @throws Exception 
	 */
	public Object load() throws Exception {
		String name = StringUtils.isBlank(nodeName) ? maker.make() : nodeName;
		if (name == null) return null;
		byte[] data = null;
		GetDataBuilder builder = client.getData();
		if (watchObj != null) {
			builder.usingWatcher(watchObj);
		}else if (watchBool) {
			builder.watched();
		}
		data = builder.storingStatIn(stat).forPath(name);
		if(debug) {
			log.info("ZooKeeper get data. path: {0} --- data: {1}", name, data == null ? null : new String(data));
		}
		if(deserializer != null) {
			return deserializer.deserialize(data);
		}
		return data;
	}
	
	/**
	 * 存储数据
	 * @return
	 * @throws Exception 
	 */
	public void store(Object data) throws Exception {
		String name = StringUtils.isBlank(nodeName) ? maker.make() : nodeName;
		if (name == null) return;
		byte[] bytes = null;
		if (data != null && serializer != null && serializer.support(data.getClass())) {
			bytes = serializer.serialize(data);
		}else if(data != null) {
			bytes = data.toString().getBytes();
		}
		client.setData().withVersion(version).forPath(name, bytes);
		if(debug) {
			log.info("ZooKeeper set data. path: {0} --- data: {1}", name, bytes == null ? null : new String(bytes));
		}
	}
	
	public CuratorFramework getClient() {
		return client;
	}

	public void setClient(CuratorFramework client) {
		this.client = client;
	}

	public boolean isWatchBool() {
		return watchBool;
	}

	public void setWatchBool(boolean watchBool) {
		this.watchBool = watchBool;
	}

	public CuratorWatcher getWatchObj() {
		return watchObj;
	}

	public void setWatchObj(CuratorWatcher watchObj) {
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
