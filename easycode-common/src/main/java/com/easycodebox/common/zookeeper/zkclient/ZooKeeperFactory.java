package com.easycodebox.common.zookeeper.zkclient;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.validate.Assert;

/**
 * 
	    zookeeper工厂
	    <bean id="zooKeeperFactory" class="com.easycodebox.common.zookeeper.zkclient.ZooKeeperFactory"
	    	p:connectString="192.168.1.101:2181,192.168.1.102:2181,192.168.1.103:2181/conf/easycode/auth"
	    	p:sessionTimeout="6000" />
	    	
		节点名制造器
	    <bean id="zkLatentIpNodeNameMaker" class="com.easycodebox.common.zookeeper.zkclient.ZkLatentIpNodeNameMaker"
	    	p:client-ref="zooKeeperFactory" p:defaultIpDataNode="DEV" p:child="core.properties" />
	    	
	    zookeeper数据反序列化
	    <bean id="zkPropertiesDeserializer" class="com.easycodebox.common.zookeeper.ZkPropertiesDeserializer" />
	    
		 节点
	    <bean id="cfgNode" class="com.easycodebox.common.zookeeper.zkclient.ZkNode"
	    	p:debug="true" p:client-ref="zooKeeperFactory" p:maker-ref="zkLatentIpNodeNameMaker"
	    	p:deserializer-ref="zkPropertiesDeserializer" />
	    	
		获取节点数据
	    <bean id="cfgProperties" class="org.springframework.beans.factory.config.MethodInvokingFactoryBean"
	    	p:targetObject-ref="cfgNode" p:targetMethod="load" />
	    	
 * 注：建议使用curator包中的类
 * @author WangXiaoJin
 * 
 */
public class ZooKeeperFactory implements FactoryBean<ZooKeeper>, InitializingBean, DisposableBean {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	private ZooKeeper client;
	private CountDownLatch count = new CountDownLatch(1);
	
	private String connectString;
	private int sessionTimeout;
	private Watcher watcher;
	private boolean canBeReadOnly;
	
	private Long sessionId;
	private byte[] sessionPasswd;
	
	@Override
	public void destroy() throws Exception {
		if (client != null) {
			log.info("Shutdown zooKeeper.");
			client.close();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notBlank(connectString, "'connectString' cannot be blank.");
	}

	@Override
	public ZooKeeper getObject() throws Exception {
		if (client == null) {
			Watcher newWatcher = new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					log.info("ZooKeeper trigger event : " + event);
					if (event.getType() == Event.EventType.None
							&& event.getState() == KeeperState.SyncConnected) {
						count.countDown();
			        }
					if (watcher != null) {
						watcher.process(event);
					}
				}
			};
			if (sessionId != null) {
				client = new ZooKeeper(connectString, sessionTimeout, newWatcher, sessionId, sessionPasswd, canBeReadOnly);
			}else {
				client = new ZooKeeper(connectString, sessionTimeout, newWatcher, canBeReadOnly);
			}
			//因ZooKeeper创建完对象直接返回，与Server连接成功是异步操作，所以加上了CountDownLatch等连接成功后再执行逻辑
			count.await();
			log.info("Create zooKeeper instance successfully.");
		}
		return client;
	}

	@Override
	public Class<?> getObjectType() {
		return ZooKeeper.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public Watcher getWatcher() {
		return watcher;
	}

	public void setWatcher(Watcher watcher) {
		this.watcher = watcher;
	}

	public String getConnectString() {
		return connectString;
	}

	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public boolean isCanBeReadOnly() {
		return canBeReadOnly;
	}

	public void setCanBeReadOnly(boolean canBeReadOnly) {
		this.canBeReadOnly = canBeReadOnly;
	}

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	public byte[] getSessionPasswd() {
		return sessionPasswd;
	}

	public void setSessionPasswd(byte[] sessionPasswd) {
		this.sessionPasswd = sessionPasswd;
	}
	
}
