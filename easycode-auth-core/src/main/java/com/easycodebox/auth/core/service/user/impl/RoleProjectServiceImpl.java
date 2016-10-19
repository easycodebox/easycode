package com.easycodebox.auth.core.service.user.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easycodebox.auth.core.enums.ModuleType;
import com.easycodebox.auth.core.pojo.sys.Project;
import com.easycodebox.auth.core.pojo.user.RoleProject;
import com.easycodebox.auth.core.service.sys.ProjectService;
import com.easycodebox.auth.core.service.user.RoleProjectService;
import com.easycodebox.auth.core.util.R;
import com.easycodebox.auth.core.util.aop.log.Log;
import com.easycodebox.common.enums.entity.OpenClose;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.jdbc.support.AbstractService;

/**
 * @author WangXiaoJin
 *
 */
@Service("roleProjectService")
public class RoleProjectServiceImpl extends AbstractService<RoleProject> implements RoleProjectService {

	@Resource
	private ProjectService projectService;
	
	@Override
	public boolean permit(Integer[] roleIds, String projectNo) {
		if(roleIds == null || roleIds.length == 0 || projectNo == null)
			return false;
		Project project = projectService.load(projectNo);
		if(project == null || project.getStatus() != OpenClose.OPEN)
			return false;
		return super.exist(sql()
				.in(R.RoleProject.roleId, roleIds)
				.eq(R.RoleProject.projectId, project.getId()));
	}

	@Override
	public List<Integer> getProjectIdsByRoleId(Integer roleId) {
		Assert.notNull(roleId, "roleId can't be null.");
		return super.list(sql()
				.column(R.RoleProject.projectId)
				.eq(R.RoleProject.roleId, roleId)
				, Integer.class);
	}

	@Override
	@Transactional
	@Log(title = "配置指定角色和项目的对应关系", moduleType = ModuleType.USER)
	public int updateRoleProjectByRoleId(Integer roleId, Integer[] projectIds) {
		this.removePhyByRoleId(roleId);
		int num = 0;
		for(Integer projectId : projectIds) {
			RoleProject rp = new RoleProject(roleId, projectId);
			super.save(rp);
			num++;
		}
		return num;
	}

	@Override
	@Log(title = "删除指定角色和项目的对应关系", moduleType = ModuleType.USER)
	public int removePhyByRoleId(Integer roleId) {
		Assert.notNull(roleId, "roleId can't be null.");
		return super.deletePhy(sql().eq(R.RoleProject.roleId, roleId));
	}

	@Override
	@Log(title = "删除角色项目", moduleType = ModuleType.USER)
	public int removePhyByProjectIds(Integer[] projectIds) {
		Assert.notNull(projectIds);
		return super.deletePhy(sql().in(R.RoleProject.projectId, projectIds));
	}
	
}
