package com.easycodebox.auth.core;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author WangXiaoJin
 */
@RunWith(SpringRunner.class)
@Transactional  //回滚修改数据库的数据
@SpringBootTest(
		classes = CoreTestApplication.class,
		webEnvironment = WebEnvironment.NONE,
		properties = {
				//"logging.level.root=DEBUG",
				//"logging.level.org.apache.tomcat=INFO",
				//"debug=true",
				"spring.config.name=application,core-application"
		}
)
public abstract class AbstractTest {
	
}
