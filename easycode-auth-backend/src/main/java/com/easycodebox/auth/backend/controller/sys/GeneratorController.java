package com.easycodebox.auth.backend.controller.sys;

import com.easycodebox.auth.core.idconverter.UserIdConverter;
import com.easycodebox.auth.core.service.sys.GeneratorService;
import com.easycodebox.auth.model.entity.sys.Generator;
import com.easycodebox.auth.model.enums.GeneratorEnum;
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
		DataPage<Generator> data = generatorService.page(generator.getGeneratorType(),
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
	public CodeMsg load(GeneratorEnum generatorType) throws Exception {
		Assert.notNull(generatorType, CodeMsg.FAIL.msg("主键参数不能为空"));
		Generator data = generatorService.load(generatorType);
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
	public CodeMsg updateIsCycle(GeneratorEnum generatorType, YesNo isCycle) throws Exception {
		Validators.instance(generatorType)
			.notNull("主键参数不能传空值");
		int count = generatorService.updateIsCycle(generatorType, isCycle);
		return isTrueNone(count > 0);
	}
	
}
