package com.easycodebox.auth.core.service.user.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easycodebox.auth.core.dao.user.GroupMapper;
import com.easycodebox.auth.core.enums.ModuleType;
import com.easycodebox.auth.core.idconverter.UserIdConverter;
import com.easycodebox.auth.core.pojo.user.Group;
import com.easycodebox.auth.core.pojo.user.GroupRole;
import com.easycodebox.auth.core.pojo.user.User;
import com.easycodebox.auth.core.service.user.GroupService;
import com.easycodebox.auth.core.util.CodeMsgExt;
import com.easycodebox.auth.core.util.Constants;
import com.easycodebox.auth.core.util.R;
import com.easycodebox.auth.core.util.aop.log.Log;
import com.easycodebox.common.enums.entity.status.CloseStatus;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.jdbc.support.AbstractService;

/**
 * @author WangXiaoJin
 *
 */
@Service("groupService")
public class GroupServiceImpl extends AbstractService<Group> implements GroupService {
	
	@Resource
	private UserIdConverter userIdConverter;
	
	@Resource
	private GroupMapper groupMapper;
	
	@Override
	public String getNameById(Integer id) {
		return super.get(sql()
				.column(R.Group.name)
				.eq(R.Group.id, id)
				, String.class);
	}

	@Override
	public List<Group> list(CloseStatus status) {
		return super.list(sql()
				.eq(R.Group.status, status)
				.ne(R.Group.status, CloseStatus.DELETE)
				.desc(R.Group.sort)
				.desc(R.Group.createTime)
				);
	}
	
	@Override
	public List<Group> listTree() {
		List<Group> orgs = this.list(CloseStatus.OPEN);
		return processGroupTree(null, orgs);
	}
	
	/**
	 * 处理组织树形结构
	 * @param parentId
	 */
	private List<Group> processGroupTree(Integer parentId, List<Group> groups) {
		List<Group> sub = new ArrayList<Group>();
		for(Group g : groups) {
			if(parentId == null ? g.getParentId() == null : parentId.equals(g.getParentId())) {
				g.setChildren(processGroupTree(g.getId(), groups));
				sub.add(g);
			}
		}
		return sub;
	}

	@Override
	@Cacheable(Constants.CN.GROUP)
	public Group load(Integer id) {
		Group data = groupMapper.load(id);
		if (data != null) {
			data.setCreatorName(userIdConverter.id2RealOrNickname(data.getCreator()));
			data.setModifierName(userIdConverter.id2RealOrNickname(data.getModifier()));
		}
		return data;
	}

	@Override
	@Transactional
	@Log(title = "添加组织", moduleType = ModuleType.USER)
	public Group add(Group group) {
		
		Assert.isFalse(this.existName(group.getName(), group.getId()),
				CodeMsgExt.FAIL.msg("组名{0}已被占用", group.getName()));
		
		if(group.getStatus() == null)
			group.setStatus(CloseStatus.OPEN);
		super.save(group);
		return group;
	}
	
	@Override
	@Log(title = "修改组织", moduleType = ModuleType.USER)
	@CacheEvict(cacheNames=Constants.CN.GROUP, key="#group.id")
	public int update(Group group) {

		Assert.isFalse(this.existName(group.getName(), group.getId()),
				CodeMsgExt.FAIL.msg("组名{0}已被占用", group.getName()));
		
		if(group.getStatus() != null) {
			LOG.info("The update method of GroupService can not update status property.");
		}
		
		return super.update(sql()
				.updateNeed(R.Group.parentId, group.getParentId())
				.updateNeed(R.Group.name, group.getName())
				.updateNeed(R.Group.sort, group.getSort())
				//.update(R.Group.status, group.getStatus())
				.eqAst(R.Group.id, group.getId())
				);
	}

	@Override
	@Transactional
	@Log(title = "逻辑删除组织", moduleType = ModuleType.USER)
	@Caching(evict={
			@CacheEvict(cacheNames=Constants.CN.GROUP, keyGenerator=Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames=Constants.CN.GROUP_ROLE, keyGenerator=Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	})
	public int remove(Integer[] ids) {
		int count = super.updateStatus(ids, CloseStatus.DELETE);
		super.delete(sql(GroupRole.class).in(R.GroupRole.roleId, ids));
		super.update(sql(User.class)
				.updateNeed(R.User.groupId, null)
				.ne(R.User.status, CloseStatus.DELETE)
				.in(R.User.groupId, ids)
				);
		return count;
	}
	
	@Override
	@Transactional
	@Log(title = "物理删除组织", moduleType = ModuleType.USER)
	@Caching(evict={
			@CacheEvict(cacheNames=Constants.CN.GROUP, keyGenerator=Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames=Constants.CN.GROUP_ROLE, keyGenerator=Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	})
	public int removePhy(Integer[] ids) {
		int count = super.delete(ids);
		super.delete(sql(GroupRole.class).in(R.GroupRole.roleId, ids));
		super.update(sql(User.class)
				.updateNeed(R.User.groupId, null)
				.ne(R.User.status, CloseStatus.DELETE)
				.in(R.User.groupId, ids)
				);
		return count;
	}

	@Override
	public DataPage<Group> page(String parentName, String groupName,
			CloseStatus status, int pageNo, int pageSize) {
		
		parentName = parentName == null || StringUtils.isBlank(parentName) ? null 
				: Symbol.PERCENT.concat(parentName.trim()).concat(Symbol.PERCENT);
		
		groupName = groupName == null || StringUtils.isBlank(groupName) ? null 
				: Symbol.PERCENT.concat(groupName.trim()).concat(Symbol.PERCENT);
		
		 List<Group> data = groupMapper.page(parentName, groupName, status, pageNo, pageSize);
		 long totalCount = groupMapper.pageTotalCount(parentName, groupName, status);
		return new DataPage<Group>(pageNo, pageSize, totalCount, data);
	}
	
	@Override
	@Log(title = "开启关闭组织", moduleType = ModuleType.USER)
	@Caching(evict={
			@CacheEvict(cacheNames=Constants.CN.GROUP, keyGenerator=Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames=Constants.CN.GROUP_ROLE, keyGenerator=Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	})
	public int openClose(Integer[] ids, CloseStatus status) {
		return super.updateStatus(ids, status);
	}

	@Override
	public boolean existName(String name, Integer excludeId) {
		return this.exist(sql()
				.eqAst(R.Group.name, name)
				.ne(R.Group.status, CloseStatus.DELETE)
				.ne(R.Group.id, excludeId)
				);
	}
	
	
	
}
