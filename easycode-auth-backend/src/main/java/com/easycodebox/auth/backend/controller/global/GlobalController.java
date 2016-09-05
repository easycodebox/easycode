package com.easycodebox.auth.backend.controller.global;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.easycodebox.common.web.BaseController;

/**
 * @author WangXiaoJin
 *
 */
@Controller
public class GlobalController extends BaseController {
	

	/**
	 * index
	 */
	@RequestMapping("/")
	public String index() throws Exception {
		return "main";
	}
	
	@RequestMapping("/unauthorized")
	public String unauthorized() throws Exception {
		return "unauthorized";
	}
	
}
