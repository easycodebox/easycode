package com.easycodebox.common.zookeeper;

/**
 * zookeeper反序列化数据异常
 * @author WangXiaoJin
 * 
 */
public class ZkDeserializeException extends Exception {

	private static final long serialVersionUID = -3383769643835708013L;

	public ZkDeserializeException() {
		super();
	}

	public ZkDeserializeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ZkDeserializeException(String message, Throwable cause) {
		super(message, cause);
	}

	public ZkDeserializeException(String message) {
		super(message);
	}

	public ZkDeserializeException(Throwable cause) {
		super(cause);
	}

}
