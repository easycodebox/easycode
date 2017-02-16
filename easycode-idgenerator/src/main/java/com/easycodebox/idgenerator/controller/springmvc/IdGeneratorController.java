package com.easycodebox.idgenerator.controller.springmvc;

import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.idconverter.UserIdConverter;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.common.validate.Validators;
import com.easycodebox.common.web.BaseController;
import com.easycodebox.idgenerator.entity.IdGenerator;
import com.easycodebox.idgenerator.service.IdGeneratorService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author WangXiaoJin
 *
 */
@Controller
public class IdGeneratorController extends BaseController {
	
	@Resource
	private UserIdConverter userIdConverter;
	@Resource
	private IdGeneratorService idGeneratorService;
	
	@GetMapping("/idGenerator")
	public String index() throws Exception {
		return "sys/idGenerator";
	}
	
	/**
	 * 列表
	 */
	@ResponseBody
	public CodeMsg list(IdGenerator idGenerator, DataPage<IdGenerator> dataPage) throws Exception {
		DataPage<IdGenerator> data = idGeneratorService.page(idGenerator.getId(),
				idGenerator.getIsCycle(), dataPage.getPageNo(), dataPage.getPageSize());
		for (IdGenerator item : data.getData()) {
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
		IdGenerator data = idGeneratorService.load(id);
		return isTrueNone(data != null, "没有对应的生成策略").data(data);
	}
	
	/**
	 * 修改
	 */
	@ResponseBody
	public CodeMsg update(IdGenerator idGenerator) throws Exception {
		int count = idGeneratorService.update(idGenerator);
		return isTrue(count > 0);
	}
	
	@ResponseBody
	public CodeMsg updateIsCycle(String id, YesNo isCycle) throws Exception {
		Validators.instance(id)
			.notNull("主键参数不能传空值");
		int count = idGeneratorService.updateIsCycle(id, isCycle);
		return isTrueNone(count > 0);
	}
	
}
