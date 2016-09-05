package com.easycodebox.common.zookeeper.curator;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.data.Stat;

import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;

/**
 * 
 * @author WangXiaoJin
 * 
 */
public class ZooKeeperTest {

	private CuratorFramework client;
	
	public ZooKeeperTest(CuratorFramework client) {
		this.client = client;
	}
	
	/**
	 * 上传配置文件到ZooKeeper上
	 * @param prefixNodeName	节点前缀名
	 * @param env	在哪个环境下才上传数据
	 * @param cfgFiles	上传数据额配置文件
	 */
	public <T extends Enum<T> & IpArrays> void addCfgFile2LatentIoNode(String prefixNodeName, T env, String[] cfgFiles) {
		Enum<T>[] envs = env.getDeclaringClass().getEnumConstants();
		try {
			for (Enum<T> envTmp : envs) {
				String node = ZKPaths.makePath(prefixNodeName, envTmp.name());
				Stat stat = client.checkExists().forPath(node);
				byte[] data = StringUtils.join(((IpArrays)envTmp).getIps(), Symbol.COMMA).getBytes();
				if (stat == null) {
					client.create().forPath(node, data);
				} else {
					client.setData().forPath(node, data);
				}
				if (env == envTmp) {
					for (String file : cfgFiles) {
						String fileNode = node + Symbol.SLASH + FilenameUtils.getName(file);
						stat = client.checkExists().forPath(fileNode);
						try (InputStream input = ZooKeeperTest.class.getResourceAsStream(file)) {
							if (stat == null) {
								client.create().forPath(fileNode, IOUtils.toByteArray(input));
							} else {
								client.setData().forPath(fileNode, IOUtils.toByteArray(input));
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static interface IpArrays {
		
		String[] getIps();
		
	}
	
	static enum Envs implements IpArrays, DetailEnum<String> {
		
		DEV("DEV", "开发环境") {
			@Override
			public String[] getIps() {
				//如下IP在项目启动时会自动获取ZooKeeper配置项
				return new String[] {
						"192.168.1.101",
						"192.168.1.102",
						"192.168.1.103",
						"192.168.1.104"
				};
			}
		},
		TEST("TEST", "测试环境") {
			@Override
			public String[] getIps() {
				return new String[] {
						
				};
			}
		},
		PRE("PRE", "预发环境") {
			@Override
			public String[] getIps() {
				return new String[] {
						
				};
			}
		},
		PROD("PROD", "生产环境") {
			@Override
			public String[] getIps() {
				return new String[] {
						
				};
			}
		};
		
		private final String value;
		private final String desc;
		
		private Envs(String value, String desc) {
			this.value = value;
			this.desc = desc;
		}
		
		@Override
		public String getValue() {
			return this.value;
		}

		@Override
		public String getDesc() {
			return this.desc;
		}
		
		@Override
		public String getClassName() {
			return this.name();
		}

	}
	
	public static void main(String[] args) {
		
		String connectString = "192.168.1.101:2181,192.168.1.102:2181,192.168.1.103:2181/conf/easycode/auth";
		CuratorFramework client = null;
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		try {
			client = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
			client.start();
			ZooKeeperTest test = new ZooKeeperTest(client);
			
			test.addCfgFile2LatentIoNode("", Envs.DEV, new String[] {
					"zk-test.properties"
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseableUtils.closeQuietly(client);
		}
	}
	
}
