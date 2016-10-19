package com.easycodebox.auth.core.ws.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easycodebox.auth.core.pojo.sys.Project;
import com.easycodebox.auth.core.pojo.user.Operation;
import com.easycodebox.auth.core.pojo.user.Role;
import com.easycodebox.auth.core.pojo.user.User;
import com.easycodebox.auth.core.service.sys.ProjectService;
import com.easycodebox.auth.core.service.user.GroupService;
import com.easycodebox.auth.core.service.user.OperationService;
import com.easycodebox.auth.core.service.user.RoleProjectService;
import com.easycodebox.auth.core.service.user.RoleService;
import com.easycodebox.auth.core.service.user.UserService;
import com.easycodebox.auth.core.util.CodeMsgExt;
import com.easycodebox.auth.core.ws.UserWsService;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.enums.entity.status.CloseStatus;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.error.ErrorContext;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.login.ws.bo.OperationWsBo;
import com.easycodebox.login.ws.bo.UserExtWsBo;
import com.easycodebox.login.ws.bo.UserWsBo;

/**
 * 因userWsService已被ws-client.xml中的配置占用，所以改@Service值
 *
 */
@Service("UserWsServer")
public class UserWsServiceImpl implements UserWsService {

	private static final Logger LOG = LoggerFactory.getLogger(UserWsServiceImpl.class);
	
	@Resource
	private UserService userService;
	@Resource
	private GroupService groupService;
	@Resource
	private RoleService roleService;
	@Resource
	private OperationService operationService;
	@Resource
	private RoleProjectService roleProjectService;
	@Resource
	private ProjectService projectService;
	
	@Override
	public UserWsBo loadById(String id) throws ErrorContext {
		User user = userService.load(id);
		UserWsBo bo = new UserWsBo();
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
		return bo;
	}
	
	@Override
	@Deprecated
	public UserWsBo load(String id, String username, String nickname)
			throws ErrorContext {
		return loadById(id);
	}

	@Override
	public DataPage<UserWsBo> page(Integer groupId, String userNo,
			String username, String nickname, String realname,
			CloseStatus status, String email, String mobile, String[] ids,
			Integer pageNo, Integer pageSize)throws ErrorContext {
		return userService.page(groupId, userNo, username, nickname, 
				realname, status, email, mobile, ids, pageNo, pageSize);
	}

	@Override
	public int updatePwd(String id, String oldPwd, String newPwd)
			throws ErrorContext {
		String tmpPwd = userService.getPwd(id);
		Assert.notNull(tmpPwd, CodeMsg.FAIL.msg("没有此用户"));
		Assert.isTrue(tmpPwd.equals(oldPwd), CodeMsg.FAIL.msg("密码错误"));
		return userService.updatePwd(newPwd, id);
	}

	@Override
	public int updateStatus(String[] ids, CloseStatus status)
			throws ErrorContext {
		return userService.openClose(ids, status);
	}

	@Override
	public int update(UserWsBo userWsBo) throws ErrorContext {
		User user = new User();
		user.setId(userWsBo.getId());
		user.setEmail(userWsBo.getEmail());
		user.setGender(userWsBo.getGender());
		user.setGroupId(userWsBo.getGroupId());
		user.setIsSuperAdmin(userWsBo.getIsSuperAdmin());
		user.setLoginFail(userWsBo.getLoginFail());
		user.setMobile(userWsBo.getMobile());
		user.setNickname(userWsBo.getNickname());
		user.setPassword(userWsBo.getPassword());
		user.setPic(userWsBo.getPic());
		user.setRealname(userWsBo.getRealname());
		user.setSort(userWsBo.getSort());
		user.setStatus(userWsBo.getStatus());
		user.setUsername(userWsBo.getUsername());
		user.setUserNo(userWsBo.getUserNo());
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
	public String add(UserWsBo user, String roleName) throws ErrorContext {
		User pojo = new User();
		pojo.setId(user.getId());
		pojo.setGroupId(user.getGroupId());
		pojo.setUserNo(user.getUserNo());
		pojo.setUsername(user.getUsername());
		pojo.setNickname(user.getNickname());
		pojo.setPassword(user.getPassword());
		pojo.setRealname(user.getRealname());
		pojo.setStatus(user.getStatus());
		pojo.setIsSuperAdmin(user.getIsSuperAdmin());
		pojo.setPic(user.getPic());
		pojo.setSort(user.getSort());
		pojo.setGender(user.getGender());
		pojo.setEmail(user.getEmail());
		pojo.setMobile(user.getMobile());
		pojo.setLoginFail(user.getLoginFail());
		pojo.setGroupName(user.getGroupName());
		
		pojo = userService.add(pojo);
		
		if(StringUtils.isNotBlank(roleName)) {
			List<Role> role = roleService.list(CloseStatus.OPEN, roleName);
			if(role.size() > 0) {
				if(role.size() > 1) {
					LOG.error("There are multiple role name {0}", roleName);
				}
				Integer roleId = role.get(0).getId();
				roleService.installRolesOfUser(pojo.getId(), new Integer[]{roleId});
			}
		}
		return pojo.getId();
	}

	@Override
	public int remove(String[] ids) throws ErrorContext {
		return userService.remove(ids);
	}

	
	@Override
	public UserExtWsBo loginSucBack(String userId, String projectNo,
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
		List<Operation> allOs = operationService.listAllOpsOfUser(user.getId(), pro.getId(), null);
		List<String> strOps = new ArrayList<>(allOs.size());
		for(Operation o : allOs) {
			if (o.getUrl() != null) {
				strOps.add(o.getUrl() + (o.getIsOwn() == YesNo.YES ? Symbol.EMPTY : ":0"));
			}
		}
		
		List<Operation> treeOs = operationService.listOperationsOfUser(user.getId(), pro.getId(), YesNo.YES);
		List<OperationWsBo> menus = new ArrayList<OperationWsBo>(treeOs.size());
		for(Operation o : treeOs) {
			OperationWsBo m = new OperationWsBo();
			m.setId(o.getId());
			m.setName(o.getName());
			m.setIcon(o.getIcon());
			m.setParentId(o.getParentId());
			m.setUrl(o.getUrl());
			menus.add(m);
		}
		
		UserExtWsBo bo = new UserExtWsBo();
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
		bo.setOperations(StringUtils.join(strOps, Symbol.COMMA));
		bo.setMenus(menus);
		return bo;
	}

	@Override
	@Deprecated
	public UserExtWsBo loginSucNew(String userId, String projectNo)
			throws ErrorContext {
		return loginSucBack(userId, projectNo, true);
	}

	@Override
	@Deprecated
	public UserExtWsBo loginSuc(String username, String projectNo) throws ErrorContext {
		User user = userService.loadByUsername(username);
		return this.loginSucNew(user.getId(), projectNo);
	}

	
}
