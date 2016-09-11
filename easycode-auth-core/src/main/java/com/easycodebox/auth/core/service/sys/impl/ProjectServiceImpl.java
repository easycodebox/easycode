package com.easycodebox.auth.core.service.sys.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easycodebox.auth.core.enums.ModuleType;
import com.easycodebox.auth.core.idconverter.UserIdConverter;
import com.easycodebox.auth.core.pojo.sys.Project;
import com.easycodebox.auth.core.service.sys.ProjectService;
import com.easycodebox.auth.core.service.user.RoleProjectService;
import com.easycodebox.auth.core.util.CodeMsgExt;
import com.easycodebox.auth.core.util.Constants;
import com.easycodebox.auth.core.util.R;
import com.easycodebox.auth.core.util.aop.log.Log;
import com.easycodebox.common.enums.entity.status.CloseStatus;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.jdbc.support.AbstractService;

/**
 * @author WangXiaoJin
 *
 */
@Service("projectService")
public class ProjectServiceImpl extends AbstractService<Project> implements ProjectService {

	@Resource
	private UserIdConverter userIdConverter;
	
	@Resource
	private RoleProjectService roleProjectService;
	
	@Override
	public List<Project> list(String name, CloseStatus status) {
		return super.list(sql()
				.like(R.Project.name, name)
				.eq(R.Project.status, status)
				.ne(R.Project.status, CloseStatus.DELETE)
				.desc(R.Project.sort)
				.desc(R.Project.createTime)
				);
	}

	@Override
	@Cacheable(Constants.CN.PROJECT)
	public Project load(Integer id) {
		Project data = super.get(sql()
				.eq(R.Project.id, id)
				.ne(R.Project.status, CloseStatus.DELETE)
				);
		if (data != null) {
			data.setCreatorName(userIdConverter.id2RealOrNickname(data.getCreator()));
			data.setModifierName(userIdConverter.id2RealOrNickname(data.getModifier()));
		}
		return data;
	}
	
	@Override
	@Cacheable(Constants.CN.PROJECT_NO)
	public Project load(String projectNo) {
		return super.get(sql()
				.eq(R.Project.projectNo, projectNo)
				.ne(R.Project.status, CloseStatus.DELETE)
				);
	}

	@Override
	@Transactional
	@Log(title = "添加授权项目", moduleType = ModuleType.SYS)
	public Project add(Project project) {
		
		Assert.isFalse(this.existName(project.getName(), project.getId()),
				CodeMsgExt.FAIL.msg("项目名{0}已被占用", project.getName()));
		
		Assert.isFalse(this.existProjectNo(project.getProjectNo(), project.getId()),
				CodeMsgExt.FAIL.msg("项目编号{0}已被占用", project.getProjectNo()));
		
		Integer maxNum = super.get(sql().max(R.Project.num), Integer.class);
		project.setNum(maxNum == null ? 1 : ++maxNum);
		if(project.getStatus() == null)
			project.setStatus(CloseStatus.OPEN);
		super.save(project);
		return project;
	}
	
	@Override
	@Log(title = "修改授权项目", moduleType = ModuleType.SYS)
	@Caching(evict={
			@CacheEvict(cacheNames=Constants.CN.PROJECT, key="#project.id"),
			@CacheEvict(cacheNames=Constants.CN.PROJECT_NO, allEntries=true),
			@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	})
	public int update(Project project) {
		
		Assert.isFalse(this.existName(project.getName(), project.getId()),
				CodeMsgExt.FAIL.msg("项目名{0}已被占用", project.getName()));
		
		Assert.isFalse(this.existProjectNo(project.getProjectNo(), project.getId()),
				CodeMsgExt.FAIL.msg("项目编号{0}已被占用", project.getProjectNo()));
		
		return super.update(sql()
				.updateNeed(R.Project.name, project.getName())
				.updateNeed(R.Project.projectNo, project.getProjectNo())
				.updateNeed(R.Project.status, project.getStatus())
				.updateNeed(R.Project.sort, project.getSort())
				.updateNeed(R.Project.remark, project.getRemark())
				.eqAst(R.Project.id, project.getId())
				);
	}

	@Override
	@Transactional
	@Log(title = "逻辑删除授权项目", moduleType = ModuleType.SYS)
	@Caching(evict={
			@CacheEvict(cacheNames=Constants.CN.PROJECT, keyGenerator=Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames=Constants.CN.PROJECT_NO, allEntries=true),
			@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	})
	public int remove(Integer[] ids) {
		roleProjectService.removePhyByProjectIds(ids);
		return super.updateStatus(ids, CloseStatus.DELETE);
	}
	
	@Override
	@Transactional
	@Log(title = "物理删除授权项目", moduleType = ModuleType.SYS)
	@Caching(evict={
			@CacheEvict(cacheNames=Constants.CN.PROJECT, keyGenerator=Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames=Constants.CN.PROJECT_NO, allEntries=true),
			@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	})
	public int removePhy(Integer[] ids) {
		roleProjectService.removePhyByProjectIds(ids);
		return super.delete(ids);
	}

	@Override
	public DataPage<Project> page(String name, String projectNo, CloseStatus status, int pageNo, int pageSize) {
		return super.page(sql()
				.like(R.Project.name, name)
				.like(R.Project.projectNo, projectNo)
				.eq(R.Project.status, status)
				.ne(R.Project.status, CloseStatus.DELETE)
				.desc(R.Project.sort)
				.desc(R.Project.createTime)
				.limit(pageNo, pageSize)
				);
	}
	
	@Override
	@Log(title = "开启关闭授权项目", moduleType = ModuleType.SYS)
	@Caching(evict={
			@CacheEvict(cacheNames=Constants.CN.PROJECT, keyGenerator=Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames=Constants.CN.PROJECT_NO, allEntries=true),
			@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	})
	public int openClose(Integer[] ids, CloseStatus status) {
		return super.updateStatus(ids, status);
	}

	@Override
	public boolean existName(String name, Integer excludeId) {
		return this.exist(sql()
				.eq(R.Project.name, name)
				.ne(R.Project.status, CloseStatus.DELETE)
				.ne(R.Project.id, excludeId)
				);
	}

	@Override
	public boolean existProjectNo(String projectNo, Integer excludeId) {
		return this.exist(sql()
				.eq(R.Project.projectNo, projectNo)
				.ne(R.Project.status, CloseStatus.DELETE)
				.ne(R.Project.id, excludeId)
				);
	}
	
}
