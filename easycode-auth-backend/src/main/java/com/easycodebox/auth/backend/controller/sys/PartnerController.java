package com.easycodebox.auth.backend.controller.sys;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.easycodebox.auth.core.idconverter.UserIdConverter;
import com.easycodebox.auth.core.pojo.sys.Partner;
import com.easycodebox.auth.core.service.sys.PartnerService;
import com.easycodebox.auth.core.util.CodeMsgExt;
import com.easycodebox.common.enums.entity.status.CloseStatus;
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
public class PartnerController extends BaseController {
	
	@Resource
	private UserIdConverter userIdConverter;
	@Resource
	private PartnerService partnerService;

	/**
	 * 列表
	 */
	@ResponseBody
	public CodeMsg list(Partner partner, DataPage<Partner> dataPage) throws Exception {
		DataPage<Partner> data = partnerService.page(partner.getName(), partner.getPartnerKey(),
				partner.getWebsite(), partner.getStatus(), 
				dataPage.getPageNo(), dataPage.getPageSize());
		for (Partner item : data.getData()) {
			item.setCreatorName(userIdConverter.id2RealOrNickname(item.getCreator()));
		}
		return none(data);
	}
	
	/**
	 * 详情
	 */
	@ResponseBody
	public CodeMsg load(String id) throws Exception {
		Assert.notBlank(id, CodeMsg.FAIL.msg("主键参数不能为空"));
		Partner data =  partnerService.load(id);
		return isTrueNone(data != null, "没有对应的合作商").data(data);
	}
	
	/**
	 * 新增
	 */
	@ResponseBody
	public CodeMsg add(Partner partner) throws Exception {
		partnerService.add(partner);
		return CodeMsg.SUC;
	}
	
	/**
	 * 修改
	 */
	@ResponseBody
	public CodeMsg update(Partner partner) throws Exception {
		int count = partnerService.update(partner);
		return isTrue(count > 0);
	}
	
	/**
	 * 逻辑删除
	 */
	@ResponseBody
	public CodeMsg remove(String[] ids) throws Exception {
		Validators.instance(ids)
			.minLength(1, "主键参数不能传空值")
			.notEmptyInside("主键参数不能传空值");
		int count = partnerService.remove(ids);
		return isTrue(count > 0);
	}
	
	/**
	 * 物理删除
	 */
	@ResponseBody
	public CodeMsg removePhy(String[] ids) throws Exception {
		Validators.instance(ids)
			.minLength(1, "主键参数不能传空值")
			.notEmptyInside("主键参数不能传空值");
		int count = partnerService.removePhy(ids);
		return isTrue(count > 0);
	}
	
	@ResponseBody
	@RequestMapping("/permit/partner/existName")
	public CodeMsg existName(String name, String excludeId) throws Exception {
		Assert.notBlank(name, CodeMsgExt.PARAM_BLANK.fillArgs("合作商名"));
		boolean exist = partnerService.existName(name, excludeId);
		return none(exist);
	}
	
	@ResponseBody
	public CodeMsg openClose(String[] ids, CloseStatus status) throws Exception {
		Validators.instance(ids)
			.minLength(1, "主键参数不能传空值")
			.notEmptyInside("主键参数不能传空值");
		int count = partnerService.openClose(ids, status);
		return isTrueNone(count > 0);
	}
	
}
