package com.easycodebox.common.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

/**
 * @author WangXiaoJin
 * 
 */
public class LookupRemote {
	
	private static final Logger LOG = LoggerFactory.getLogger(LookupRemote.class);

	@SuppressWarnings("unchecked")
	public static <T> T lookup(Class<T> result, String rmiIp) {
		try {
			return (T)Naming.lookup("rmi://" + rmiIp + ":" + RemoteFactory.REGISTRY_PORT + Symbol.SLASH + result.getSimpleName());
		} catch (MalformedURLException e) {
			LOG.error("LookupRemote for {0} error.", e, result.getSimpleName());
		} catch (RemoteException e) {
			LOG.error("LookupRemote for {0} error.", e, result.getSimpleName());
		} catch (NotBoundException e) {
			LOG.error("LookupRemote for {0} error.", e, result.getSimpleName());
		}
		return null;
	}
	
}
