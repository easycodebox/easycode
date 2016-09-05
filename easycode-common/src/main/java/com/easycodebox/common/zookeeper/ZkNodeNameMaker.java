package com.easycodebox.common.zookeeper;

/**
 * zookeeper的节点名制造器
 * @author WangXiaoJin
 * 
 */
public interface ZkNodeNameMaker {
	
	/**
	 * 制造节点名
	 * @return return null 表明生成node name失败
	 */
	String make();

}
