package com.easycodebox.common.rmi;

import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.validate.Assert;

import java.io.IOException;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMISocketFactory;
import java.util.List;

/**
 * 请使用Spring提供的RMI工具类
 * <p>参考类：{@link org.springframework.remoting.rmi.RmiServiceExporter RmiServiceExporter}、
 * {@link org.springframework.remoting.rmi.RmiProxyFactoryBean RmiProxyFactoryBean}
 *
 * @author WangXiaoJin
 */
@Deprecated
public class RemoteFactory {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	//通信端口
	public static final int SEND_MSG_PORT = 1100;
	
	private String host;
	
	private Integer port;
	
	private List<Remote> remotes;
	
	public RemoteFactory(String host, Integer port) {
		Assert.notNull(host);
		Assert.notNull(port);
		this.host = host;
		this.port = port;
		
		 /*
		    1、RMISocketFactory.setSocketFactory(new FixPortRMISocketFactory());
		    2、LocateRegistry.createRegistry(REGISTRY_PORT);
		    3、IHello hello = new HelloImpl();
		    注意这三行的顺序，必须最后实例化Rmi接口
		  */
		try {
			RMISocketFactory.setSocketFactory(new FixPortRMISocketFactory());
			LocateRegistry.createRegistry(port);
		} catch (RemoteException e) {
			log.error("Registry RMI error.", e);
		} catch (IOException e) {
			log.error("Setting rmi socket factory error.", e);
		}
	}
	
	public void createRemote() {
		if(remotes == null || remotes.size() == 0)
			return;
		try {
			for (Remote remote : remotes) {
				Naming.bind("rmi://" + host + ":" + port + Symbol.SLASH + remote.getClass().getInterfaces()[0].getSimpleName()
						, remote);
			}
		} catch (RemoteException | MalformedURLException | AlreadyBoundException e) {
			log.error("RemoteFactory  createRemote error.", e);
		}
	}

	public void setRemotes(List<Remote> remotes) {
		this.remotes = remotes;
	}
	
	static class FixPortRMISocketFactory extends RMISocketFactory {

		@Override
		public Socket createSocket(String host, int port) throws IOException {
			return new Socket(host, port);
		}

		@Override
		public ServerSocket createServerSocket(int port) throws IOException {
			port = port == 0 ? SEND_MSG_PORT : port;
			return new ServerSocket(port);
		}
		
	} 
	

}
