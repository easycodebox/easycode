package com.easycodebox.auth.backend.controller.user;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.easycodebox.auth.core.idconverter.UserIdConverter;
import com.easycodebox.auth.core.pojo.user.Role;
import com.easycodebox.auth.core.service.user.RoleService;
import com.easycodebox.auth.core.service.user.UserService;
import com.easycodebox.auth.core.util.CodeMsgExt;
import com.easycodebox.common.enums.entity.OpenClose;
import com.easycodebox.common.enums.entity.YesNo;
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
public class RoleController extends BaseController {
	
	@Resource
	private UserIdConverter userIdConverter;
	@Resource
	private RoleService roleService;
	@Resource
	private UserService userService;

	/**
	 * 列表
	 */
	@ResponseBody
	public CodeMsg list(Role role, DataPage<Role> dataPage) throws Exception {
		DataPage<Role> data = roleService.page(role.getName(), role.getStatus(), 
				dataPage.getPageNo(), dataPage.getPageSize());
		for (Role item : data.getData()) {
			item.setCreatorName(userIdConverter.id2RealOrNickname(item.getCreator()));
		}
		return none(data);
	}
	
	/**
	 * 详情
	 */
	@ResponseBody
	public CodeMsg load(Integer id) throws Exception {
		Assert.notNull(id, CodeMsg.FAIL.msg("主键参数不能为空"));
		Role data = roleService.load(id);
		return isTrueNone(data != null, "没有对应的角色").data(data);
	}
	
	/**
	 * 新增
	 */
	@ResponseBody
	public CodeMsg add(Role role) throws Exception {
		roleService.add(role);
		return CodeMsg.SUC;
	}
	
	/**
	 * 修改
	 */
	@ResponseBody
	public CodeMsg update(Role role) throws Exception {
		int count = roleService.update(role);
		return isTrue(count > 0);
	}
	
	/**
	 * 逻辑删除
	 */
	@ResponseBody
	public CodeMsg remove(Integer[] ids) throws Exception {
		Validators.instance(ids)
			.minLength(1, "主键参数不能传空值")
			.notEmptyInside("主键参数不能传空值");
		int count = roleService.remove(ids);
		return isTrue(count > 0);
	}
	
	/**
	 * 物理删除
	 */
	@ResponseBody
	public CodeMsg removePhy(Integer[] ids) throws Exception {
		Validators.instance(ids)
			.minLength(1, "主键参数不能传空值")
			.notEmptyInside("主键参数不能传空值");
		int count = roleService.removePhy(ids);
		return isTrue(count > 0);
	}
	
	@ResponseBody
	public CodeMsg openClose(Integer[] ids, OpenClose status) throws Exception {
		Validators.instance(ids)
			.minLength(1, "主键参数不能传空值")
			.notEmptyInside("主键参数不能传空值");
		int count = roleService.openClose(ids, status);
		return isTrueNone(count > 0);
	}
	
	@ResponseBody
	public CodeMsg existName(String name, Integer excludeId) throws Exception {
		Assert.notBlank(name, CodeMsgExt.PARAM_BLANK.fillArgs("角色名"));
		boolean exist = roleService.existName(name, excludeId);
		return none(exist);
	}
	
	@ResponseBody
	public CodeMsg listByUserId(String userId) throws Exception {
		List<Role> all = roleService.listAllByUserId(userId);
		Integer groupId = userService.getGroupId(userId);
		if(groupId != null) {
			List<Role> gs = roleService.listOpenedByGroupId(groupId);
			for(Role g : gs) {
				for(Role a : all) {
					if(g.getId().equals(a.getId()))
						a.setIsGroupOwn(YesNo.YES);
				}
			}
		}
		return none(all);
	}
	
	@ResponseBody
	public CodeMsg cfgByUserId(String userId, Integer[] roleIds) throws Exception {
		roleService.installRolesOfUser(userId, roleIds);
		return CodeMsgExt.SUC;
	}
	
	@ResponseBody
	public CodeMsg listByGroupId(int groupId) throws Exception {
		return none(roleService.listAllByGroupId(groupId));
	}
	
	@ResponseBody
	public CodeMsg cfgByGroupId(int groupId, Integer[] roleIds) throws Exception {
		roleService.installRolesOfGroup(groupId, roleIds);
		return CodeMsgExt.SUC;
	}
	
}
