package com.easycodebox.common.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.easycodebox.common.validate.Assert;

/**
 * 设置CuratorFramework对象的namespace，返回一个新对象（NamespaceFacade），返回的对象不能调用CuratorFramework.close()方法，否则抛异常
 * @author WangXiaoJin
 *
 */
public class NamespaceCuratorFrameworkFactory implements FactoryBean<CuratorFramework>, InitializingBean {

	private CuratorFramework client;
	private String namespace;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(client);
	}

	@Override
	public CuratorFramework getObject() throws Exception {
		return client.usingNamespace(namespace);
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

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
}
