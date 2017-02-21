package com.easycodebox.common.rmi;

import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.*;

/**
 * 请使用Spring提供的RMI工具类
 * <p>参考类：{@link org.springframework.remoting.rmi.RmiServiceExporter RmiServiceExporter}、
 * {@link org.springframework.remoting.rmi.RmiProxyFactoryBean RmiProxyFactoryBean}
 *
 * @author WangXiaoJin
 */
@Deprecated
public class LookupRemote {
	
	private static final Logger log = LoggerFactory.getLogger(LookupRemote.class);

	@SuppressWarnings("unchecked")
	public static <T> T lookup(Class<T> result, String rmiIp, Integer port) {
		try {
			return (T)Naming.lookup("rmi://" + rmiIp + ":" + port + Symbol.SLASH + result.getSimpleName());
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			log.error("LookupRemote for {0} error.", e, result.getSimpleName());
		}
		return null;
	}
	
}
