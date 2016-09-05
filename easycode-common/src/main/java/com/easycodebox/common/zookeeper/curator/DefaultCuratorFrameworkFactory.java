package com.easycodebox.common.zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.validate.Assert;

/**
 * ZooKeeper client
	<bean id="curatorFramework" class="com.easycodebox.common.zookeeper.curator.DefaultCuratorFrameworkFactory"
		p:connectString="192.168.1.101:2181,192.168.1.102:2181,192.168.1.103:2181/conf/easycode/auth" />
	
	节点名制造器
	<bean id="zkLatentIpNodeNameMaker" class="com.easycodebox.common.zookeeper.curator.CuratorLatentIpNodeNameMaker"
		p:client-ref="curatorFramework" p:defaultIpDataNode="DEV" p:child="core.properties" />
		
	zookeeper数据反序列化
	<bean id="zkPropertiesDeserializer" class="com.easycodebox.common.zookeeper.ZkPropertiesDeserializer" />
	
	节点
	<bean id="cfgNode" class="com.easycodebox.common.zookeeper.curator.CuratorNode"
		p:debug="true" p:client-ref="curatorFramework" p:maker-ref="zkLatentIpNodeNameMaker"
		p:deserializer-ref="zkPropertiesDeserializer" />
		
	获取节点数据
	<bean id="cfgProperties" class="org.springframework.beans.factory.config.MethodInvokingFactoryBean"
		p:targetObject-ref="cfgNode" p:targetMethod="load" />
 * 
 * ******************************************************************************************************
 * 
 * 如果curatorFramework需要多个地方使用，且多个地方的root路径不同，则使用NamespaceCuratorFrameworkFactory
 * 创建不同的namespace对象，然后直接使用新创建的curatorFramework。例子：
 * <bean id="curatorFramework" class="com.easycodebox.common.zookeeper.curator.DefaultCuratorFrameworkFactory"
		p:connectString="192.168.1.101:2181,192.168.1.102:2181,192.168.1.103:2181" />
	<bean id="confCurator" class="com.easycodebox.common.zookeeper.curator.NamespaceCuratorFrameworkFactory"
		p:client-ref="curatorFramework"
		p:namespace="conf/easycode/auth"/>
	<bean id="leaderCurator" class="com.easycodebox.common.zookeeper.curator.NamespaceCuratorFrameworkFactory"
		p:client-ref="curatorFramework"
		p:namespace="leader"/>
 * 
 * 
 * @author WangXiaoJin
 *
 */
public class DefaultCuratorFrameworkFactory implements FactoryBean<CuratorFramework>, InitializingBean, DisposableBean {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultCuratorFrameworkFactory.class);
	
	private CuratorFramework client;
	
	private String connectString;
	private RetryPolicy retryPolicy;
	private Integer sessionTimeoutMs;
	private Integer connectionTimeoutMs;
	private byte[] defaultData;
	private boolean canBeReadOnly;
	
	@Override
	public void destroy() throws Exception {
		if (client != null) {
			LOG.info("Shutdown zooKeeper.");
			client.close();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notBlank(connectString, "'connectString' cannot be blank.");
		if (retryPolicy == null) {
			retryPolicy = new ExponentialBackoffRetry(1000, 3);
		}
	}

	@Override
	public CuratorFramework getObject() throws Exception {
		if (client == null) {
			Builder builder = CuratorFrameworkFactory.builder()
				.connectString(connectString)
				.retryPolicy(retryPolicy)
				.canBeReadOnly(canBeReadOnly)
				.defaultData(defaultData);
			if (sessionTimeoutMs != null)
				builder.sessionTimeoutMs(sessionTimeoutMs);
			if (connectionTimeoutMs != null)
				builder.connectionTimeoutMs(connectionTimeoutMs);
			client = builder.build();
			client.start();
			LOG.info("Create zooKeeper instance successfully.");
		}
		return client;
	}

	@Override
	public Class<?> getObjectType() {
		return CuratorFramework.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public CuratorFramework getClient() {
		return client;
	}

	public void setClient(CuratorFramework client) {
		this.client = client;
	}

	public String getConnectString() {
		return connectString;
	}

	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	public RetryPolicy getRetryPolicy() {
		return retryPolicy;
	}

	public void setRetryPolicy(RetryPolicy retryPolicy) {
		this.retryPolicy = retryPolicy;
	}

	public Integer getSessionTimeoutMs() {
		return sessionTimeoutMs;
	}

	public void setSessionTimeoutMs(Integer sessionTimeoutMs) {
		this.sessionTimeoutMs = sessionTimeoutMs;
	}

	public Integer getConnectionTimeoutMs() {
		return connectionTimeoutMs;
	}

	public void setConnectionTimeoutMs(Integer connectionTimeoutMs) {
		this.connectionTimeoutMs = connectionTimeoutMs;
	}

	public byte[] getDefaultData() {
		return defaultData;
	}

	public void setDefaultData(byte[] defaultData) {
		this.defaultData = defaultData;
	}

	public boolean isCanBeReadOnly() {
		return canBeReadOnly;
	}

	public void setCanBeReadOnly(boolean canBeReadOnly) {
		this.canBeReadOnly = canBeReadOnly;
	}

}
