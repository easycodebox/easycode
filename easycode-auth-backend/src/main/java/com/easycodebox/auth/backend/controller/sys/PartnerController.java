package com.easycodebox.auth.backend.controller.sys;

import com.easycodebox.auth.core.service.sys.PartnerService;
import com.easycodebox.auth.core.util.CodeMsgExt;
import com.easycodebox.auth.model.entity.sys.Partner;
import com.easycodebox.common.enums.entity.OpenClose;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.idconverter.UserIdConverter;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.common.validate.Validators;
import com.easycodebox.common.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author WangXiaoJin
 *
 */
@Controller
public class PartnerController extends BaseController {
	
	@Autowired
	private UserIdConverter userIdConverter;
	@Autowired
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
			item.setCreatorName(userIdConverter.idToRealOrNickname(item.getCreator()));
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
	public CodeMsg existName(String name, String excludeId) throws Exception {
		Assert.notBlank(name, CodeMsgExt.PARAM_BLANK.fillArgs("合作商名"));
		boolean exist = partnerService.existName(name, excludeId);
		return none(exist);
	}
	
	@ResponseBody
	public CodeMsg openClose(String[] ids, OpenClose status) throws Exception {
		Validators.instance(ids)
			.minLength(1, "主键参数不能传空值")
			.notEmptyInside("主键参数不能传空值");
		int count = partnerService.openClose(ids, status);
		return isTrueNone(count > 0);
	}
	
}
