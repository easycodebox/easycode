package com.easycodebox.auth.backend.controller.sys;

import com.easycodebox.auth.core.idconverter.UserIdConverter;
import com.easycodebox.auth.core.service.sys.GeneratorService;
import com.easycodebox.auth.model.entity.sys.Generator;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.common.validate.Validators;
import com.easycodebox.common.web.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author WangXiaoJin
 *
 */
@Controller
public class GeneratorController extends BaseController {
	
	@Resource
	private UserIdConverter userIdConverter;
	@Resource
	private GeneratorService generatorService;

	/**
	 * 列表
	 */
	@ResponseBody
	public CodeMsg list(Generator generator, DataPage<Generator> dataPage) throws Exception {
		DataPage<Generator> data = generatorService.page(generator.getId(),
				generator.getIsCycle(), dataPage.getPageNo(), dataPage.getPageSize());
		for (Generator item : data.getData()) {
			item.setCreatorName(userIdConverter.idToRealOrNickname(item.getCreator()));
		}
		return none(data);
	}
	
	/**
	 * 详情
	 */
	@ResponseBody
	public CodeMsg load(String id) throws Exception {
		Assert.notNull(id, CodeMsg.FAIL.msg("主键参数不能为空"));
		Generator data = generatorService.load(id);
		return isTrueNone(data != null, "没有对应的生成策略").data(data);
	}
	
	/**
	 * 修改
	 */
	@ResponseBody
	public CodeMsg update(Generator generator) throws Exception {
		int count = generatorService.update(generator);
		return isTrue(count > 0);
	}
	
	@ResponseBody
	public CodeMsg updateIsCycle(String id, YesNo isCycle) throws Exception {
		Validators.instance(id)
			.notNull("主键参数不能传空值");
		int count = generatorService.updateIsCycle(id, isCycle);
		return isTrueNone(count > 0);
	}
	
}
