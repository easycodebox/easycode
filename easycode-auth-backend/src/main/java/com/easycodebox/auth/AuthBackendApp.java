package com.easycodebox.auth;

import com.easycodebox.login.config.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

/**
 * @author WangXiaoJin
 */
@Import({ShiroConfig.class, WsClientConfig.class, WsServerConfig.class})
@SpringBootApplication
public class AuthBackendApp {
	
	public static void main(String[] args) throws Exception {
		/* ----------------- 测试配置 BEGIN ------------------- */
		//args = new String[] {
				//"--logging.level.root=DEBUG",
				//"--debug"
		//};
		/* ----------------- 测试配置 END ------------------- */
		
		boolean existConfigName = false;
		for (String arg : args) {
			if (arg.startsWith("--spring.config.name=")) {
				existConfigName = true;
				break;
			}
		}
		if (!existConfigName) {
			args = Arrays.copyOf(args, args.length + 1);
			args[args.length - 1] = "--spring.config.name=application,core-application";
		}
		SpringApplication.run(AuthBackendApp.class, args);
	}
	
}
