package com.easycodebox.auth.backend.controller.sys;

import com.easycodebox.auth.core.service.sys.ProjectService;
import com.easycodebox.auth.core.util.CodeMsgExt;
import com.easycodebox.auth.model.entity.sys.Project;
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

import java.util.List;

/**
 * @author WangXiaoJin
 *
 */
@Controller
public class ProjectController extends BaseController {
	
	@Autowired
	private UserIdConverter userIdConverter;
	@Autowired
	private ProjectService projectService;

	/**
	 * 列表
	 */
	@ResponseBody
	public CodeMsg list(Project project, DataPage<Project> dataPage) throws Exception {
		DataPage<Project> data = projectService.page(project.getName(), project.getProjectNo(), 
				project.getStatus(), dataPage.getPageNo(), dataPage.getPageSize());
		for (Project item : data.getData()) {
			item.setCreatorName(userIdConverter.idToRealOrNickname(item.getCreator()));
		}
		return none(data);
	}
	
	/**
	 * 列出所有项目
	 */
	@ResponseBody
	public CodeMsg all() throws Exception {
		List<Project> data = projectService.list(null, null);
		return none(data);
	}
	
	/**
	 * 详情
	 */
	@ResponseBody
	public CodeMsg load(Integer id) throws Exception {
		Assert.notNull(id, CodeMsg.FAIL.msg("主键参数不能为空"));
		Project data =  projectService.load(id);
		return isTrueNone(data != null, "没有对应的项目").data(data);
	}
	
	/**
	 * 新增
	 */
	@ResponseBody
	public CodeMsg add(Project project) throws Exception {
		projectService.add(project);
		return CodeMsg.SUC;
	}
	
	/**
	 * 修改
	 */
	@ResponseBody
	public CodeMsg update(Project project) throws Exception {
		int count = projectService.update(project);
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
		int count = projectService.remove(ids);
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
		int count = projectService.removePhy(ids);
		return isTrue(count > 0);
	}
	
	@ResponseBody
	public CodeMsg openClose(Integer[] ids, OpenClose status) throws Exception {
		Validators.instance(ids)
			.minLength(1, "主键参数不能传空值")
			.notEmptyInside("主键参数不能传空值");
		int count = projectService.openClose(ids, status);
		return isTrueNone(count > 0);
	}
	
	@ResponseBody
	public CodeMsg existName(String name, Integer excludeId) throws Exception {
		Assert.notBlank(name, CodeMsgExt.PARAM_BLANK.fillArgs("项目名"));
		boolean exist = projectService.existName(name, excludeId);
		return none(exist);
	}
	
	@ResponseBody
	public CodeMsg existProjectNo(String projectNo, Integer excludeId) throws Exception {
		Assert.notBlank(projectNo, CodeMsgExt.PARAM_BLANK.fillArgs("项目编码"));
		boolean exist = projectService.existProjectNo(projectNo, excludeId);
		return none(exist);
	}
	
}
