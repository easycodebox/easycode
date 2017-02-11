package com.easycodebox.auth.backend.controller.sys;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.easycodebox.auth.core.idconverter.UserIdConverter;
import com.easycodebox.auth.core.service.sys.LogService;
import com.easycodebox.auth.model.entity.sys.Log;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.common.validate.Validators;
import com.easycodebox.common.web.BaseController;

/**
 * @author WangXiaoJin
 *
 */
@Controller
public class LogController extends BaseController {
	
	@Resource
	private UserIdConverter userIdConverter;
	@Resource
	private LogService logService;

	/**
	 * 列表
	 */
	@ResponseBody
	public CodeMsg list(Log log, DataPage<Log> dataPage) throws Exception {
		DataPage<Log> data = logService.page(log.getTitle(), log.getUrl(), log.getParams(), 
				log.getModuleType(), log.getLogLevel(), log.getResult(), 
				log.getClientIp(), dataPage.getPageNo(), dataPage.getPageSize());
		for (Log item : data.getData()) {
			item.setCreatorName(userIdConverter.idToRealOrNickname(item.getCreator()));
		}
		return none(data);
	}
	
	/**
	 * 详情
	 */
	@ResponseBody
	public CodeMsg load(Long id) throws Exception {
		Assert.notNull(id, CodeMsg.FAIL.msg("主键参数不能为空"));
		Log data = logService.load(id);
		return isTrueNone(data != null, "没有对应的日志").data(data);
	}
	
	/**
	 * 新增
	 */
	@ResponseBody
	public CodeMsg add(Log log) throws Exception {
		logService.add(log);
		return CodeMsg.SUC;
	}
	
	/**
	 * 修改
	 */
	@ResponseBody
	public CodeMsg update(Log log) throws Exception {
		int count = logService.update(log);
		return isTrue(count > 0);
	}
	
	/**
	 * 物理删除
	 */
	@ResponseBody
	public CodeMsg removePhy(Long[] ids) throws Exception {
		Validators.instance(ids)
			.minLength(1, "主键参数不能传空值")
			.notEmptyInside("主键参数不能传空值");
		int count = logService.removePhy(ids);
		return isTrue(count > 0);
	}
	
}
