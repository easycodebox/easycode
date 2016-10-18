package com.easycodebox.auth.backend.controller.user;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.easycodebox.auth.core.idconverter.UserIdConverter;
import com.easycodebox.auth.core.pojo.user.Group;
import com.easycodebox.auth.core.pojo.user.User;
import com.easycodebox.auth.core.service.user.GroupService;
import com.easycodebox.auth.core.service.user.UserService;
import com.easycodebox.auth.core.util.CodeMsgExt;
import com.easycodebox.common.enums.entity.status.CloseStatus;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.security.SecurityUtils;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.common.validate.Validators;
import com.easycodebox.common.web.BaseController;

/**
 * @author WangXiaoJin
 *
 */
@Controller
public class UserController extends BaseController {
	
	@Resource
	private UserService userService;
	@Resource
	private GroupService groupService;
	@Resource
	private UserIdConverter userIdConverter;

	/**
	 * 列表
	 */
	@ResponseBody
	public CodeMsg list(User user, DataPage<User> dataPage) throws Exception {
		DataPage<User> data = userService.page(user.getGroupName(), user.getUserNo(),
				user.getUsername(), user.getNickname(), user.getRealname(),
				user.getStatus(), user.getEmail(),
				user.getMobile(), dataPage.getPageNo(), dataPage.getPageSize());
		for (User item : data.getData()) {
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
		User user = userService.load(id);
		if (user != null) {
			user.setCreatorName(userIdConverter.id2RealOrNickname(user.getCreator()));
			user.setModifierName(userIdConverter.id2RealOrNickname(user.getModifier()));
		}
		if(user != null && user.getGroupId() != null) {
			Group group = groupService.load(user.getGroupId());
			if(group != null)
				user.setGroupName(group.getName());
		}
		return isTrueNone(user != null, "没有对应的用户").data(user);
	}
	
	@ResponseBody
	@RequestMapping("/clearuser/{id}")
	public boolean clear(@PathVariable String id) throws Exception {
		Assert.notBlank(id, CodeMsg.FAIL.msg("主键参数不能为空"));
		return userService.clearCache(id);
	}
	
	/**
	 * 新增
	 */
	@ResponseBody
	public CodeMsg add(User user) throws Exception {
		user.setPassword(DigestUtils.md5Hex(user.getPassword()));
		userService.add(user);
		return CodeMsg.SUC;
	}
	
	/**
	 * 修改
	 */
	@ResponseBody
	public CodeMsg update(User user) throws Exception {
		int count = userService.update(user);
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
		int count = userService.remove(ids);
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
		int count = userService.removePhy(ids);
		return isTrue(count > 0);
	}
	
	@ResponseBody
	public CodeMsg openClose(String[] ids, CloseStatus status) throws Exception {
		Validators.instance(ids)
			.minLength(1, "主键参数不能传空值")
			.notEmptyInside("主键参数不能传空值");
		int count = userService.openClose(ids, status);
		return isTrueNone(count > 0);
	}
	
	@ResponseBody
	@RequestMapping("/permit/user/existUsername")
	public CodeMsg existUsername(String username, String excludeId) throws Exception {
		Assert.notBlank(username, CodeMsgExt.PARAM_BLANK.fillArgs("用户名"));
		boolean exist = userService.existUsername(username, excludeId);
		return none(exist);
	}
	
	@ResponseBody
	@RequestMapping("/permit/user/existNickname")
	public CodeMsg existNickname(String nickname, String excludeId) throws Exception {
		Assert.notBlank(nickname, CodeMsgExt.PARAM_BLANK.fillArgs("昵称"));
		boolean exist = userService.existNickname(nickname, excludeId);
		return none(exist);
	}
	
	/**
	 * 跳转修改密码页面
	 */
	public void updatePwd() throws Exception {
		
	}
	
	/**
	 * 执行修改密码
	 */
	@ResponseBody
	public CodeMsg updatingPwd(String oldPwd, String pwd) throws Exception {
		String tmpPwd = userService.getPwd(SecurityUtils.getUserId());
		oldPwd = DigestUtils.md5Hex(oldPwd);
		
		if(tmpPwd == null) {
			return CodeMsg.FAIL.msg("不能修改此用户的密码");
		}else if(!tmpPwd.equals(oldPwd))
			return CodeMsg.FAIL.msg("原密码输入错误");
		else {
			int num = userService.updatePwd(DigestUtils.md5Hex(pwd), SecurityUtils.getUserId());
			return isTrue(num > 0);
		}
	}
	
}
