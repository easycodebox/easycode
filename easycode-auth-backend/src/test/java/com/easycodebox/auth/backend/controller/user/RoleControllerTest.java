package com.easycodebox.auth.backend.controller.user;

import com.easycodebox.auth.backend.AbstractMvcTest;
import com.easycodebox.common.error.CodeMsg.Code;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RoleControllerTest extends AbstractMvcTest {

	@Test
	public void testLoad() throws Exception {
		MockHttpServletRequestBuilder builder = get("/role/load")
				.param("id", "3859");
		this.mockMvc.perform(builder)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.data").exists());
	}
	
	@Test
	public void testRemove() throws Exception {
		MockHttpServletRequestBuilder builder = get("/role/remove")
				.param("ids", "3859");
		this.mockMvc.perform(builder)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.code").value(Code.SUC_CODE));
	}
	
}
