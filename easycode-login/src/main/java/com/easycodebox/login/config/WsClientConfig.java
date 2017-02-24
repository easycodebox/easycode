package com.easycodebox.login.config;

import com.easycodebox.login.ws.UserWsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;

/**
 * @author WangXiaoJin
 */
@Configuration
public class WsClientConfig {
	
	@Value("${auth.ws.url}")
	private String authWsUrl;
	
	@Bean
	public HessianProxyFactoryBean userWsService() {
		HessianProxyFactoryBean factoryBean = new HessianProxyFactoryBean();
		factoryBean.setServiceUrl(authWsUrl + "/user");
		factoryBean.setServiceInterface(UserWsService.class);
		return factoryBean;
	}
}
