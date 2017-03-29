package com.easycodebox.login.config;

import com.easycodebox.login.ws.UserWsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;

/**
 * @author WangXiaoJin
 */
@Configuration
public class WsClientConfig {
	
	@Autowired
	private LoginProperties loginProperties;
	
	@Bean
	public HessianProxyFactoryBean userWsService() {
		HessianProxyFactoryBean factoryBean = new HessianProxyFactoryBean();
		factoryBean.setServiceUrl(loginProperties.getAuthWsUrl() + "/user");
		factoryBean.setServiceInterface(UserWsService.class);
		return factoryBean;
	}
}
