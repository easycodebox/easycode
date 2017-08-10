package com.easycodebox.upload;

import com.easycodebox.spring.boot.Application;
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
		Application.run(UploadApp.class, args);
	}
	
}
