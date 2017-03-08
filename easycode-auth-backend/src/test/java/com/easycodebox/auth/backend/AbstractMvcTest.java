package com.easycodebox.auth.backend;

import com.easycodebox.auth.backend.config.SpringMvcConfig;
import com.easycodebox.auth.core.AbstractTest;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author WangXiaoJin
 */
@ContextHierarchy(
		@ContextConfiguration(classes = {
				SpringMvcConfig.class
		})
)
@WebAppConfiguration
public abstract class AbstractMvcTest extends AbstractTest {
	
	@Autowired
	protected WebApplicationContext wac;
	
	protected MockMvc mockMvc;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}
	
}
