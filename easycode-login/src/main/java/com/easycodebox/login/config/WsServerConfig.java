package com.easycodebox.login.config;

import com.easycodebox.login.ws.UserWsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianServiceExporter;

/**
 * @author WangXiaoJin
 */
@Configuration
public class WsServerConfig {
	
	@Bean("/ws/user")
	public HessianServiceExporter hessianServiceExporter(UserWsService userWsServer) {
		HessianServiceExporter exporter = new HessianServiceExporter();
		exporter.setService(userWsServer);
		exporter.setServiceInterface(UserWsService.class);
		return exporter;
	}
}
