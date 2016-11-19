package com.easycodebox.auth.core.ws.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easycodebox.auth.core.service.sys.ProjectService;
import com.easycodebox.auth.core.service.user.GroupService;
import com.easycodebox.auth.core.service.user.PermissionService;
import com.easycodebox.auth.core.service.user.RoleProjectService;
import com.easycodebox.auth.core.service.user.RoleService;
import com.easycodebox.auth.core.service.user.UserService;
import com.easycodebox.auth.core.util.CodeMsgExt;
import com.easycodebox.auth.core.ws.UserWsService;
import com.easycodebox.auth.model.bo.user.UserFullBo;
import com.easycodebox.auth.model.entity.sys.Project;
import com.easycodebox.auth.model.entity.user.Permission;
import com.easycodebox.auth.model.entity.user.Role;
import com.easycodebox.auth.model.entity.user.User;
import com.easycodebox.common.enums.entity.OpenClose;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.error.ErrorContext;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.validate.Assert;

/**
 * 因userWsService已被ws-client.xml中的配置占用，所以改@Service值
 *
 */
@Service("UserWsServer")
public class UserWsServiceImpl implements UserWsService {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Resource
	private UserService userService;
	@Resource
	private GroupService groupService;
	@Resource
	private RoleService roleService;
	@Resource
	private PermissionService permissionService;
	@Resource
	private RoleProjectService roleProjectService;
	@Resource
	private ProjectService projectService;
	
	@Override
	public User load(String id) throws ErrorContext {
		User user = userService.load(id);
		if(user.getGroupId() != null) {
			String groupName = groupService.load(user.getGroupId()).getName();
			user.setGroupName(groupName);
		}
		return user;
	}
	
	@Override
	public DataPage<User> page(Integer groupId, String userNo,
			String username, String nickname, String realname,
			OpenClose status, String email, String mobile, String[] ids,
			Integer pageNo, Integer pageSize)throws ErrorContext {
		return userService.page(groupId, userNo, username, nickname, 
				realname, status, email, mobile, ids, pageNo, pageSize);
	}

	@Override
	public int updatePwd(String id, String oldPwd, String newPwd)
			throws ErrorContext {
		User user = userService.load(id);
		Assert.notNull(user, CodeMsg.FAIL.msg("没有此用户"));
		Assert.isTrue(user.getPassword().equals(oldPwd), CodeMsg.FAIL.msg("密码错误"));
		return userService.updatePwd(newPwd, id);
	}

	@Override
	public int updateStatus(String[] ids, OpenClose status)
			throws ErrorContext {
		return userService.openClose(ids, status);
	}

	@Override
	public int update(User user) throws ErrorContext {
		return userService.update(user);
	}

	@Override
	public int updateNickname(String id, String nickname) throws ErrorContext {
		return userService.updateNickname(nickname, id);
	}

	@Override
	public int updatePortrait(String id, String portrait) throws ErrorContext {
		return userService.updatePortrait(id, portrait);
	}

	@Override
	public int updateBaseInfo(String id, String nickname, String realname,
			String email, String mobile) throws ErrorContext {
		return userService.updateBaseInfo(id, nickname, realname, email, mobile);
	}

	@Override
	public boolean existUsername(String username, String excludeId)
			throws ErrorContext {
		return userService.existUsername(username, excludeId);
	}

	@Override
	public boolean existNickname(String nickname, String excludeId)
			throws ErrorContext {
		return userService.existNickname(nickname, excludeId);
	}

	@Override
	@Transactional
	public String add(User user, String roleName) throws ErrorContext {
		user = userService.add(user);
		
		if(StringUtils.isNotBlank(roleName)) {
			List<Role> role = roleService.list(OpenClose.OPEN, roleName);
			if(role.size() > 0) {
				if(role.size() > 1) {
					log.error("There are multiple role name {0}", roleName);
				}
				Integer roleId = role.get(0).getId();
				roleService.installRolesOfUser(user.getId(), new Integer[]{roleId});
			}
		}
		return user.getId();
	}

	@Override
	public int remove(String[] ids) throws ErrorContext {
		return userService.remove(ids);
	}

	
	@Override
	public UserFullBo loginSuc(String userId, String projectNo,
			boolean validProjectAuth) throws ErrorContext {
		User user = userService.load(userId);
		Assert.notNull(user, CodeMsgExt.PARAM_ERR.fillArgs("用户名"));
		
		List<Role> roles = roleService.listOpenedByUserId(user.getId());
		String[] roleNames = new String[roles.size()];
		Integer[] roleIds = new Integer[roles.size()];
		for(int i = 0; i < roles.size(); i++) {
			Role role = roles.get(i);
			roleNames[i] = role.getName();
			roleIds[i] = role.getId();
		}
		
		if(user.getIsSuperAdmin() == YesNo.NO && validProjectAuth) {
			//判断此用户所用的角色是否有权限登录此系统
			boolean permit = roleProjectService.permit(roleIds, projectNo);
			Assert.isTrue(permit, CodeMsg.FAIL.msg("您没有权限登录此系统"));
		}
		
		Project pro = projectService.load(projectNo);
		Assert.notNull(pro, CodeMsgExt.PARAM_ERR.fillArgs("项目编号"));
		List<Permission> allOs = permissionService.listAllOpsOfUser(user.getId(), pro.getId(), null);
		List<String> strOps = new ArrayList<>(allOs.size());
		for(Permission o : allOs) {
			if (o.getUrl() != null) {
				strOps.add(o.getUrl() + (o.getIsOwn() == YesNo.YES ? Symbol.EMPTY : ":0"));
			}
		}
		
		List<Permission> menus = permissionService.listPermissionsOfUser(user.getId(), pro.getId(), YesNo.YES);
		
		UserFullBo bo = new UserFullBo();
		bo.setId(user.getId());
		bo.setGroupId(user.getGroupId());
		bo.setUserNo(user.getUserNo());
		bo.setUsername(user.getUsername());
		bo.setNickname(user.getNickname());
		bo.setPassword(user.getPassword());
		bo.setRealname(user.getRealname());
		bo.setStatus(user.getStatus());
		bo.setIsSuperAdmin(user.getIsSuperAdmin());
		bo.setPic(user.getPic());
		bo.setSort(user.getSort());
		bo.setGender(user.getGender());
		bo.setEmail(user.getEmail());
		bo.setMobile(user.getMobile());
		bo.setLoginFail(user.getLoginFail());
		if(user.getGroupId() != null) {
			String groupName = groupService.load(user.getGroupId()).getName();
			bo.setGroupName(groupName);
		}
		
		bo.setRoleIds(StringUtils.join(roleIds, Symbol.COMMA));
		bo.setRoleNames(StringUtils.join(roleNames, Symbol.COMMA));
		bo.setPermissions(StringUtils.join(strOps, Symbol.COMMA));
		bo.setMenus(menus);
		return bo;
	}
	
}
