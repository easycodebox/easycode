package com.easycodebox.common.rmi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMISocketFactory;
import java.util.List;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.validate.Assert;

/**
 * @author WangXiaoJin
 * 
 */
public class RemoteFactory {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	//注册时的端口
	public static final int REGISTRY_PORT = 1099;
	//通信的端口
	public static final int SEND_MSG_PORT = 1100;
	
	private List<Remote> remotes;
	
	public RemoteFactory() {
		 /**
		  * 1、RMISocketFactory.setSocketFactory(new FixPortRMISocketFactory());
		  * 2、LocateRegistry.createRegistry(REGISTRY_PORT);
		  * 3、IHello hello = new HelloImpl();
		  * 注意这三行的顺序，必须最后实例化Rmi接口
		  */
		try {
			RMISocketFactory.setSocketFactory(new FixPortRMISocketFactory());
			LocateRegistry.createRegistry(REGISTRY_PORT);
		} catch (RemoteException e) {
			log.error("Registry RMI error.", e);
		} catch (IOException e) {
			log.error("Setting rmi socket factory error.", e);
		}
	}
	
	public void createRemote() {
		Assert.notBlank(BaseConstants.rmiIp, "rmiIp param can not be blank.");
		if(remotes == null || remotes.size() == 0)
			return;
		
		try {
			for(int i = 0; i < remotes.size(); i++) {
				Naming.bind("rmi://" + BaseConstants.rmiIp 
						+ ":" + REGISTRY_PORT + Symbol.SLASH + remotes.get(i).getClass().getInterfaces()[0].getSimpleName()
						,remotes.get(i));
			}
		} catch (RemoteException e) {
			log.error("RemoteFactory  createRemote error.", e);
		} catch (MalformedURLException e) {
			log.error("RemoteFactory  createRemote error.", e);
		} catch (AlreadyBoundException e) {
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
