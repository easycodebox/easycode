package com.easycodebox.auth.core.service.user.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easycodebox.auth.core.dao.user.RoleMapper;
import com.easycodebox.auth.core.enums.ModuleType;
import com.easycodebox.auth.core.idconverter.UserIdConverter;
import com.easycodebox.auth.core.pojo.user.GroupRole;
import com.easycodebox.auth.core.pojo.user.Role;
import com.easycodebox.auth.core.pojo.user.RoleOperation;
import com.easycodebox.auth.core.pojo.user.UserRole;
import com.easycodebox.auth.core.service.user.RoleService;
import com.easycodebox.auth.core.util.CodeMsgExt;
import com.easycodebox.auth.core.util.Constants;
import com.easycodebox.auth.core.util.R;
import com.easycodebox.auth.core.util.aop.log.Log;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.enums.entity.status.CloseStatus;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.jdbc.support.AbstractService;

/**
 * @author WangXiaoJin
 *
 */
@Service("roleService")
public class RoleServiceImpl extends AbstractService<Role> implements RoleService {

	@Resource
	private UserIdConverter userIdConverter;
	
	@Resource
	private RoleMapper roleMapper;
	
	@Override
	public List<Role> list(CloseStatus status, String eqName) {
		return super.list(sql()
				.eq(R.Role.status, status)
				.eq(R.Role.name, eqName)
				.ne(R.Role.status, CloseStatus.DELETE)
				.desc(R.Role.sort)
				.desc(R.Role.createTime)
				);
	}

	@Override
	@Cacheable(cacheNames=Constants.CN.ROLE)
	public Role load(Integer id) {
		Role data = super.get(id, CloseStatus.OPEN, CloseStatus.CLOSE);
		if (data != null) {
			data.setCreatorName(userIdConverter.id2RealOrNickname(data.getCreator()));
			data.setModifierName(userIdConverter.id2RealOrNickname(data.getModifier()));
		}
		return data;
	}

	@Override
	@Transactional
	@Log(title = "添加角色", moduleType = ModuleType.USER)
	public Role add(Role role) {
		
		Assert.isFalse(this.existName(role.getName(), role.getId()),
				CodeMsgExt.FAIL.msg("角色名{0}已被占用", role.getName()));
		if(role.getStatus() == null)
			role.setStatus(CloseStatus.OPEN);
		super.save(role);
		return role;
	}
	
	@Override
	@Log(title = "修改角色", moduleType = ModuleType.USER)
	@CacheEvict(cacheNames=Constants.CN.ROLE, key="#role.id")
	public int update(Role role) {
		
		Assert.isFalse(this.existName(role.getName(), role.getId()),
				CodeMsgExt.FAIL.msg("角色名{0}已被占用", role.getName()));
		
		if(role.getStatus() != null) {
			LOG.info("The update method can not update status property.");
		}
		
		return super.update(sql()
				.updateNeed(R.Role.name, role.getName())
				.updateNeed(R.Role.sort, role.getSort())
				//.update(R.Role.status, role.getStatus())
				.updateNeed(R.Role.description, role.getDescription())
				.updateNeed(R.Role.remark, role.getRemark())
				.eqAst(R.Role.id, role.getId())
				);
	}

	@Override
	@Transactional
	@Log(title = "逻辑删除角色", moduleType = ModuleType.USER)
	@Caching(evict={
			@CacheEvict(cacheNames=Constants.CN.ROLE, keyGenerator=Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames=Constants.CN.USER_ROLE, allEntries=true),
			@CacheEvict(cacheNames=Constants.CN.GROUP_ROLE, allEntries=true),
			@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	})
	public int remove(Integer[] ids) {
		super.delete(sql(GroupRole.class).in(R.GroupRole.roleId, ids));
		super.delete(sql(UserRole.class).in(R.UserRole.roleId, ids));
		super.delete(sql(RoleOperation.class).in(R.RoleOperation.roleId, ids));
		return super.updateStatus(ids, CloseStatus.DELETE);
	}
	
	@Override
	@Transactional
	@Log(title = "物理删除角色", moduleType = ModuleType.USER)
	@Caching(evict={
			@CacheEvict(cacheNames=Constants.CN.ROLE, keyGenerator=Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames=Constants.CN.USER_ROLE, allEntries=true),
			@CacheEvict(cacheNames=Constants.CN.GROUP_ROLE, allEntries=true),
			@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	})
	public int removePhy(Integer[] ids) {
		super.delete(sql(GroupRole.class).in(R.GroupRole.roleId, ids));
		super.delete(sql(UserRole.class).in(R.UserRole.roleId, ids));
		super.delete(sql(RoleOperation.class).in(R.RoleOperation.roleId, ids));
		return super.delete(ids);
	}
	
	@Override
	@Log(title = "开启关闭角色", moduleType = ModuleType.USER)
	@Caching(evict={
			@CacheEvict(cacheNames=Constants.CN.ROLE, keyGenerator=Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames=Constants.CN.USER_ROLE, allEntries=true),
			@CacheEvict(cacheNames=Constants.CN.GROUP_ROLE, allEntries=true),
			@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	})
	public int openClose(Integer[] ids, CloseStatus status) {
		return super.updateStatus(ids, status);
	}
	
	@Override
	public DataPage<Role> page(String name, CloseStatus status, int pageNo, int pageSize) {
		return super.page(sql()
				.likeTrim(R.Role.name, name)
				.eq(R.Role.status, status)
				.ne(R.Role.status, CloseStatus.DELETE)
				.desc(R.Role.sort)
				.desc(R.Role.createTime)
				.limit(pageNo, pageSize)
				);
	}
	
	@Override
	@Cacheable(cacheNames=Constants.CN.USER_ROLE)
	public List<Role> listOpenedByUserId(String userId) {
		return roleMapper.listOpenedByUserId(userId);
	}
	
	@Override
	public Integer[] listOpenedRoleIdsByUserId(String userId) {
		return roleMapper.listOpenedRoleIdsByUserId(userId);
	}
	
	@Override
	@Cacheable(cacheNames=Constants.CN.GROUP_ROLE)
	public List<Role> listOpenedByGroupId(int groupId) {
		return roleMapper.listOpenedByGroupId(groupId);
	}

	@Override
	public List<Role> listAllByGroupId(int groupId) {
		List<Role> rs = this.list(CloseStatus.OPEN, null);
		List<Role> owns = this.listOpenedByGroupId(groupId);
		for(Role o : owns) {
			for(Role r : rs) {
				if(r.getId().equals(o.getId())) {
					r.setIsOwn(YesNo.YES);
					break;
				}
			}
		}
		return rs;
	}

	@Override
	public List<Role> listAllByUserId(String userId) {
		Assert.notBlank(userId, CodeMsgExt.PARAM_BLANK, "用户ID");
		List<Role> rs = this.list(CloseStatus.OPEN, null);
		List<Role> owns = this.listOpenedByUserId(userId);
		for(Role o : owns) {
			for(Role r : rs) {
				if(r.getId().equals(o.getId())) {
					r.setIsOwn(YesNo.YES);
					break;
				}
			}
		}
		return rs;
	}

	@Override
	@Transactional
	@Log(title = "配置组织的角色", moduleType = ModuleType.USER)
	@Caching(evict={
			@CacheEvict(cacheNames=Constants.CN.GROUP_ROLE, key="#groupId"),
			//用户拥有角色的逻辑也与group有关
			@CacheEvict(cacheNames=Constants.CN.USER_ROLE, allEntries=true),
			@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	})
	public void installRolesOfGroup(int groupId, Integer[] roleIds) {
		super.delete(sql(GroupRole.class)
				.eq(R.GroupRole.groupId, groupId)
				);
		if(roleIds != null && roleIds.length > 0) {
			for(Integer roleId : roleIds) {
				GroupRole gr = new GroupRole();
				gr.setGroupId(groupId);
				gr.setRoleId(roleId);
				super.save(gr, GroupRole.class);
			}
		}
	}

	@Override
	@Transactional
	@Log(title = "配置用户的角色", moduleType = ModuleType.USER)
	@Caching(evict={
			@CacheEvict(cacheNames=Constants.CN.USER_ROLE, key="#userId"),
			@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	})
	public void installRolesOfUser(String userId, Integer[] roleIds) {
		Assert.notBlank(userId, CodeMsgExt.PARAM_BLANK, "用户ID");
		super.delete(sql(UserRole.class)
				.eq(R.UserRole.userId, userId)
				);
		if(roleIds != null && roleIds.length > 0) {
			for(Integer roleId : roleIds) {
				UserRole gr = new UserRole();
				gr.setUserId(userId);
				gr.setRoleId(roleId);
				super.save(gr, UserRole.class);
			}
		}
	}
	
	@Override
	public boolean existName(String name, Integer excludeId) {
		return this.exist(sql()
				.eqAst(R.Role.name, name)
				.ne(R.Role.status, CloseStatus.DELETE)
				.ne(R.Role.id, excludeId)
				);
	}
	
}
