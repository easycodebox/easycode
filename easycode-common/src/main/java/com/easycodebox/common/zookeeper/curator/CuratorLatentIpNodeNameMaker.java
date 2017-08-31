package com.easycodebox.common.zookeeper.curator;

import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.net.InetAddresses;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.common.zookeeper.ZkNodeNameMaker;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * zookeeper的节点名制造器，节点名根据IP来定位，但是IP值作为数据存储在节点中。
 * 所以需要遍历前缀节点下的所有子节点，然后匹配子节点中的IP数据，匹配成功则跳出遍历并返回此节点。
 * @author WangXiaoJin
 * 
 */
public class CuratorLatentIpNodeNameMaker implements ZkNodeNameMaker {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private CuratorFramework client;
	
	/**
	 * 前缀节点名
	 */
	private String prefixNodeName;
	/**
	 * 当存储IP的节点匹配不了当前IP时，使用此默认节点
	 */
	private String defaultIpDataNode;
	/**
	 * 子节点名
	 */
	private String child;
	/**
	 * IP前缀符
	 */
	private String ipPrefix = StringUtils.EMPTY;
	/**
	 * IP后缀符
	 */
	private String ipPostfix = StringUtils.EMPTY;
	
	@Override
	public String make() {
		Assert.notNull(client, "'client' can't be null.");
		Assert.notBlank(child, "'child' can't be blank.");
		prefixNodeName = StringUtils.isBlank(prefixNodeName) ? Symbol.SLASH : prefixNodeName;
		String name = null;
		try {
			Stat stat = client.checkExists().forPath(prefixNodeName);
			if (stat != null) {
				List<String> chs = client.getChildren().forPath(prefixNodeName);
				for (String c : chs) {
					c = ZKPaths.makePath(prefixNodeName, c);
					byte[] ips = client.getData().forPath(c);
					if(ips != null) {
						String ip = InetAddresses.getLocalIp();
						if ((new String(ips) + Symbol.COMMA).contains(ipPrefix + ip + ipPostfix + Symbol.COMMA)) {
							name = c;
							break;
						}
					}
				}
				if (name == null && StringUtils.isNotBlank(defaultIpDataNode)) {
					String defaultNode = ZKPaths.makePath(prefixNodeName, defaultIpDataNode);
					Stat st = client.checkExists().forPath(defaultNode);
					if (st != null) 
						name = defaultNode;
				}
			}
		} catch (Exception e) {
			log.error("Make zookeeper node name error.", e);
		}
		return name == null ? null : name + Symbol.SLASH + child;
	}

	public CuratorFramework getClient() {
		return client;
	}

	public void setClient(CuratorFramework client) {
		this.client = client;
	}

	public String getPrefixNodeName() {
		return prefixNodeName;
	}

	public void setPrefixNodeName(String prefixNodeName) {
		this.prefixNodeName = prefixNodeName;
	}

	public String getIpPrefix() {
		return ipPrefix;
	}

	public void setIpPrefix(String ipPrefix) {
		this.ipPrefix = ipPrefix;
	}

	public String getIpPostfix() {
		return ipPostfix;
	}

	public void setIpPostfix(String ipPostfix) {
		this.ipPostfix = ipPostfix;
	}

	public String getChild() {
		return child;
	}

	public void setChild(String child) {
		this.child = child;
	}

	public String getDefaultIpDataNode() {
		return defaultIpDataNode;
	}

	public void setDefaultIpDataNode(String defaultIpDataNode) {
		this.defaultIpDataNode = defaultIpDataNode;
	}
	
}
