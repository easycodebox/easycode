package com.easycodebox.upload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author WangXiaoJin
 */
@SpringBootApplication
public class UploadApp {
	
	public static void main(String[] args) throws Exception {
		/* ----------------- 测试配置 BEGIN ------------------- */
		/*args = new String[] {
				//"--logging.level.root=DEBUG",
				//"--env=prod",
				"--debug"
		};*/
		/* ----------------- 测试配置 END ------------------- */
		SpringApplication.run(UploadApp.class, args);
	}
	
}
