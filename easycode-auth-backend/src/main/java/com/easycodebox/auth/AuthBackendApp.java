package com.easycodebox.auth;

import com.easycodebox.auth.core.config.PropertyConfig;
import com.easycodebox.login.config.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.util.Map;

/**
 * @author WangXiaoJin
 */
@Import({ShiroConfig.class, WsClientConfig.class, WsServerConfig.class})
@SpringBootApplication
public class AuthBackendApp {
	
	public static void main(String[] args) throws Exception {
		/* ----------------- 测试配置 BEGIN ------------------- */
		/*args = new String[] {
				//"--logging.level.root=DEBUG",
				//"--env=prod",
				"--debug"
		};*/
		/* ----------------- 测试配置 END ------------------- */
		Map<String, Object> props = PropertyConfig.defaultProperties();
		props.put("spring.config.name", "application,core-application");
		SpringApplication application = new SpringApplication(AuthBackendApp.class);
		application.setDefaultProperties(props);
		application.run(args);
	}
	
}
