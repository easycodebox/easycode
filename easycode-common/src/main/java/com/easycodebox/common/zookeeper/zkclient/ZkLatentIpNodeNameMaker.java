package com.easycodebox.common.zookeeper.zkclient;

import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.net.InetAddresses;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.common.zookeeper.ZkNodeNameMaker;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;
import java.util.List;

/**
 * zookeeper的节点名制造器，节点名根据IP来定位，但是IP值作为数据存储在节点中。
 * 所以需要遍历前缀节点下的所有子节点，然后匹配子节点中的IP数据，匹配成功则跳出遍历并返回此节点。
 * @author WangXiaoJin
 * 
 */
public class ZkLatentIpNodeNameMaker implements ZkNodeNameMaker {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private ZooKeeper client;
	
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
			Stat stat = client.exists(prefixNodeName, false);
			if (stat != null) {
				List<String> chs = client.getChildren(prefixNodeName, false);
				for (String c : chs) {
					if(prefixNodeName.endsWith(Symbol.SLASH))
						c = prefixNodeName + c;
					else
						c = prefixNodeName + Symbol.SLASH + c;
					byte[] ips = client.getData(c, false, stat);
					if(ips != null) {
						String ip = InetAddresses.getLocalIp();
						if ((new String(ips) + Symbol.COMMA).contains(ipPrefix + ip + ipPostfix + Symbol.COMMA)) {
							name = c;
							break;
						}
					}
				}
				if (name == null && StringUtils.isNotBlank(defaultIpDataNode)) {
					String defaultNode = prefixNodeName.endsWith(Symbol.SLASH) ? prefixNodeName + defaultIpDataNode
							: prefixNodeName + Symbol.SLASH + defaultIpDataNode;
					Stat st = client.exists(defaultNode, false);
					if (st != null) 
						name = defaultNode;
				}
			}
		} catch (KeeperException | InterruptedException | SocketException e) {
			log.error("Make zookeeper node name error.", e);
		}
		return name == null ? null : name + Symbol.SLASH + child;
	}
	
	public ZooKeeper getClient() {
		return client;
	}

	public void setClient(ZooKeeper client) {
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
