package com.easycodebox.auth.core;

import com.easycodebox.auth.core.config.CoreConfig;
import com.easycodebox.auth.core.config.CoreTestConfig;
import com.easycodebox.auth.core.util.Constants;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author WangXiaoJin
 */
@RunWith(SpringRunner.class)
@Transactional  //回滚修改数据库的数据
@ContextConfiguration(classes = {
		CoreConfig.class,
		CoreTestConfig.class
})
@ActiveProfiles(Constants.INTEGRATION_TEST_KEY)
public abstract class AbstractTest {
	
}
