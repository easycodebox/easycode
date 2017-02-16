package com.easycodebox.auth.backend.controller.user;

import com.easycodebox.auth.core.service.sys.ProjectService;
import com.easycodebox.auth.core.service.user.PermissionService;
import com.easycodebox.auth.core.service.user.RoleProjectService;
import com.easycodebox.auth.core.util.CodeMsgExt;
import com.easycodebox.auth.model.entity.user.Permission;
import com.easycodebox.common.enums.entity.OpenClose;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.error.ErrorContext;
import com.easycodebox.common.file.FileInfo;
import com.easycodebox.common.idconverter.UserIdConverter;
import com.easycodebox.common.jackson.Jacksons;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.common.validate.Validators;
import com.easycodebox.common.web.BaseController;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author WangXiaoJin
 *
 */
@Controller
public class PermissionController extends BaseController {
	
	@Resource
	private UserIdConverter userIdConverter;
	@Resource
	private PermissionService permissionService;
	@Resource
	private ProjectService projectService;
	@Resource
	private RoleProjectService roleProjectService;

	/**
	 * 列表
	 */
	@ResponseBody
	public CodeMsg list(Permission permission, DataPage<Permission> dataPage) throws Exception {
		DataPage<Permission> data = permissionService.page(permission.getParentName(), 
				permission.getProjectName(), permission.getName(), 
				permission.getIsMenu(), permission.getStatus(), permission.getUrl(), 
				dataPage.getPageNo(), dataPage.getPageSize()); 
		for (Permission item : data.getData()) {
			item.setCreatorName(userIdConverter.idToRealOrNickname(item.getCreator()));
		}
		return none(data);
	}
	
	/**
	 * 详情
	 */
	@ResponseBody
	public CodeMsg load(Long id) throws Exception {
		Assert.notNull(id, CodeMsg.FAIL.msg("主键参数不能为空"));
		Permission data = permissionService.load(id, null, null);
		return isTrueNone(data != null, "没有对应的权限").data(data);
	}
	
	/**
	 * 新增
	 */
	@ResponseBody
	public CodeMsg add(Permission permission) throws Exception {
		permissionService.add(permission);
		return CodeMsg.SUC;
	}
	
	/**
	 * 修改
	 */
	@ResponseBody
	public CodeMsg update(Permission permission) throws Exception {
		int count = permissionService.update(permission);
		return isTrue(count > 0);
	}
	
	/**
	 * 逻辑删除
	 */
	@ResponseBody
	public CodeMsg remove(Long[] ids) throws Exception {
		Validators.instance(ids)
			.minLength(1, "主键参数不能传空值")
			.notEmptyInside("主键参数不能传空值");
		int count = permissionService.remove(ids);
		return isTrue(count > 0);
	}
	
	/**
	 * 物理删除
	 */
	@ResponseBody
	public CodeMsg removePhy(Long[] ids) throws Exception {
		Validators.instance(ids)
			.minLength(1, "主键参数不能传空值")
			.notEmptyInside("主键参数不能传空值");
		int count = permissionService.removePhy(ids);
		return isTrue(count > 0);
	}
	
	/**
	 * 导入权限
	 */
	@ResponseBody
	public CodeMsg imports(@RequestParam("files[]")MultipartFile[] files) throws Exception {
		//List<InputStream> streams = Resources.scan2InputStream("permissions/*.xml");
		List<FileInfo> fileInfos = new ArrayList<>(files.length);
		for (MultipartFile file : files) {
			FileInfo fileInfo = new FileInfo();
			fileInfo.setName(file.getOriginalFilename());
			if (!file.isEmpty()) {
				try {
					permissionService.importFromXml(file.getInputStream());
					fileInfo.setError("上传成功");
				} catch (ErrorContext e) {
					fileInfo.setError(e.getError().getMsg());
				} catch (Exception e) {
					fileInfo.setError("解析失败");
				}
			} else {
				fileInfo.setError("空文件");
			}
			fileInfos.add(fileInfo);
		}
		return CodeMsg.NONE.data(fileInfos);
	}
	
	/**
	 * 导出权限
	 */
	@RequestMapping("/permission/exports/{projectId}")
	public void exports(@PathVariable("projectId")Integer projectId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String filename = projectService.load(projectId).getProjectNo() + ".xml";
		try {
			filename = new String(filename.getBytes("UTF-8"),"ISO-8859-1");
		} catch (UnsupportedEncodingException ignored) {
			
		}
		response.setContentType(request.getServletContext().getMimeType(filename));
		response.setHeader("Content-Disposition", "attachment;fileName=" + filename);
		try (Writer writer = response.getWriter()) {
			permissionService.exportToXml("permissions.ftl", projectId, writer);
		}
	}
	
	/**
	 * 根据项目ID获取权限
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	public String listByProject(Integer projectId) throws Exception {
		List<Permission> os = permissionService.list(projectId, null, null);
		Permission no = new Permission();
		no.setId(-1L);
		no.setName("--无--");
		os.add(0, no);
		Jacksons j = Jacksons.nonNull().addJsonSerializer(Permission.class, new JsonSerializer<Permission>(){

			@Override
			public void serialize(Permission value, JsonGenerator gen,
					SerializerProvider serializers) throws IOException {
				gen.writeStartObject();
				gen.writeObjectField("id", value.getId());
				gen.writeObjectField("pId", value.getParentId());
				gen.writeBooleanField("isParent", !(value.getIsMenu() == null || value.getIsMenu() == YesNo.NO));
				gen.writeStringField("name", value.getName());
				gen.writeEndObject();
			}
			
		});
		return j.toJson(os);
	}
	
	@ResponseBody
	public String cfgPermissionByRoleId(Integer roleId) throws Exception {
		List<Permission> os = permissionService.listAllGroupByProject(roleId);
		Jacksons j = Jacksons.nonNull().addJsonSerializer(Permission.class, new JsonSerializer<Permission>(){

			@Override
			public void serialize(Permission value, JsonGenerator gen,
					SerializerProvider serializers) throws IOException {
				gen.writeStartObject();
				gen.writeObjectField("id", value.getId());
				if(value.getId() == null) {
					gen.writeObjectField("projectId", value.getProject().getId());
				}
				gen.writeObjectField("children", value.getChildren());
				gen.writeStringField("name", value.getName());
				gen.writeBooleanField("isParent", !(value.getIsMenu() == null || value.getIsMenu() == YesNo.NO));
				gen.writeObjectField("checked", value.getIsOwn() != null && value.getIsOwn() == YesNo.YES);
				gen.writeEndObject();
			}
			
		});
		return j.toJson(os);
	}
	
	@ResponseBody
	public CodeMsg authoriseRole(Long[] oprtds, Integer[] projectIds, Integer roleId) throws Exception {
		permissionService.authoriseRole(roleId, oprtds == null ? new Long[0] : oprtds);
		roleProjectService.updateRoleProjectByRoleId(roleId, projectIds);
		return CodeMsg.SUC;
	}
	
	@ResponseBody
	public CodeMsg openClose(Long[] ids, OpenClose status) throws Exception {
		Validators.instance(ids)
			.minLength(1, "主键参数不能传空值")
			.notEmptyInside("主键参数不能传空值");
		int count = permissionService.openClose(ids, status);
		return isTrueNone(count > 0);
	}
	
	@ResponseBody
	public CodeMsg changeIsMenu(Long id, YesNo isMenu) throws Exception {
		Assert.notNull(id, CodeMsgExt.PARAM_BLANK.fillArgs("主键"));
		int count = permissionService.changeIsMenu(id, isMenu);
		return isTrueNone(count > 0);
	}
	
	@ResponseBody
	public CodeMsg existName(Integer projectId, String name, Long excludeId) throws Exception {
		Assert.notNull(projectId, CodeMsgExt.PARAM_BLANK.fillArgs("项目ID"));
		Assert.notBlank(name, CodeMsgExt.PARAM_BLANK.fillArgs("权限名"));
		boolean exist = permissionService.existName(projectId, name, excludeId);
		return none(exist);
	}
	
	@ResponseBody
	public CodeMsg existUrl(Integer projectId, String url, Long excludeId) throws Exception {
		Assert.notNull(projectId, CodeMsgExt.PARAM_BLANK.fillArgs("项目ID"));
		Assert.notBlank(url, CodeMsgExt.PARAM_BLANK.fillArgs("url"));
		boolean exist = permissionService.existUrl(projectId, url, excludeId);
		return none(exist);
	}
	
}
