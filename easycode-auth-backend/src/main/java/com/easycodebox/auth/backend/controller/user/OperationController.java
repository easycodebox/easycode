package com.easycodebox.auth.backend.controller.user;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.easycodebox.auth.core.pojo.user.Operation;
import com.easycodebox.auth.core.service.sys.ProjectService;
import com.easycodebox.auth.core.service.user.OperationService;
import com.easycodebox.auth.core.service.user.RoleProjectService;
import com.easycodebox.auth.core.util.CodeMsgExt;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.enums.entity.status.CloseStatus;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.file.Resources;
import com.easycodebox.common.jackson.Jacksons;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.common.validate.Validators;
import com.easycodebox.common.web.BaseController;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author WangXiaoJin
 *
 */
@Controller
public class OperationController extends BaseController {
	
	@Resource
	private OperationService operationService;
	@Resource
	private ProjectService projectService;
	@Resource
	private RoleProjectService roleProjectService;

	/**
	 * 列表
	 */
	public DataPage<Operation> list(Operation operation, DataPage<Operation> dataPage) throws Exception {
		
		return operationService.page(operation.getParentName(), 
				operation.getProjectName(), operation.getName(), 
				operation.getIsMenu(), operation.getStatus(), operation.getUrl(), 
				dataPage.getPageNo(), dataPage.getPageSize()); 
	}
	
	/**
	 * 详情
	 */
	@ResponseBody
	public CodeMsg load(Long id) throws Exception {
		Assert.notNull(id, CodeMsg.FAIL.msg("主键参数不能为空"));
		Operation data = operationService.load(id, null, null);
		return isTrueNone(data != null, "没有对应的权限").data(data);
	}
	
	/**
	 * 新增
	 */
	@ResponseBody
	public CodeMsg add(Operation operation) throws Exception {
		operationService.add(operation);
		return CodeMsg.SUC;
	}
	
	/**
	 * 修改
	 */
	@ResponseBody
	public CodeMsg update(Operation operation) throws Exception {
		int count = operationService.update(operation);
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
		int count = operationService.remove(ids);
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
		int count = operationService.removePhy(ids);
		return isTrue(count > 0);
	}
	
	/**
	 * 导入权限
	 */
	@ResponseBody
	public CodeMsg imports() throws Exception {
		List<InputStream> streams = Resources.scan2InputStream("operations/*.xml");
		operationService.importFromXml(streams);
		return CodeMsgExt.SUC;
	}
	
	/**
	 * 导出权限
	 */
	@ResponseBody
	public CodeMsg exports() throws Exception {
		URL url = OperationController.class.getResource("/operations");
		File tlFile = new File(url.toURI());
		operationService.exportToXml("operations.ftl", tlFile);
		return CodeMsgExt.SUC;
	}
	
	/**
	 * 根据项目ID获取权限
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	public String listByProject(Integer projectId) throws Exception {
		List<Operation> os = operationService.list(projectId, null, null);
		Operation no = new Operation();
		no.setId(-1L);
		no.setName("--无--");
		os.add(0, no);
		Jacksons j = Jacksons.nonNull().addJsonSerializer(Operation.class, new JsonSerializer<Operation>(){

			@Override
			public void serialize(Operation value, JsonGenerator gen,
					SerializerProvider serializers) throws IOException,
					JsonProcessingException {
				gen.writeStartObject();
				gen.writeObjectField("id", value.getId());
				gen.writeObjectField("pId", value.getParentId());
				gen.writeBooleanField("isParent", value.getIsMenu() == null 
						|| value.getIsMenu() == YesNo.NO ? false : true);
				gen.writeStringField("name", value.getName());
				gen.writeEndObject();
			}
			
		});
		return j.toJson(os);
	}
	
	@ResponseBody
	public String cfgOperationByRoleId(Integer roleId) throws Exception {
		List<Operation> os = operationService.listAllGroupByProject(roleId);
		Jacksons j = Jacksons.nonNull().addJsonSerializer(Operation.class, new JsonSerializer<Operation>(){

			@Override
			public void serialize(Operation value, JsonGenerator gen,
					SerializerProvider serializers) throws IOException,
					JsonProcessingException {
				gen.writeStartObject();
				gen.writeObjectField("id", value.getId());
				if(value.getId() == null) {
					gen.writeObjectField("projectId", value.getProject().getId());
				}
				gen.writeObjectField("children", value.getChildren());
				gen.writeStringField("name", value.getName());
				gen.writeBooleanField("isParent", value.getIsMenu() == null 
						|| value.getIsMenu() == YesNo.NO ? false : true);
				gen.writeObjectField("checked", value.getIsOwn() != null && value.getIsOwn() == YesNo.YES 
						? true : false);
				gen.writeEndObject();
			}
			
		});
		return j.toJson(os);
	}
	
	@ResponseBody
	public CodeMsg addOperationsOfRole(Long[] oprtds, Integer[] projectIds, Integer roleId) throws Exception {
		operationService.addOperationsOfRole(roleId, oprtds == null ? new Long[0] : oprtds);
		roleProjectService.updateRoleProjectByRoleId(roleId, projectIds);
		return CodeMsg.SUC;
	}
	
	@ResponseBody
	public CodeMsg openClose(Long[] ids, CloseStatus status) throws Exception {
		Validators.instance(ids)
			.minLength(1, "主键参数不能传空值")
			.notEmptyInside("主键参数不能传空值");
		int count = operationService.openClose(ids, status);
		return isTrueNone(count > 0);
	}
	
	@ResponseBody
	public CodeMsg changeIsMenu(Long id, YesNo isMenu) throws Exception {
		Assert.notNull(id, CodeMsgExt.PARAM_BLANK.fillArgs("主键"));
		int count = operationService.changeIsMenu(id, isMenu);
		return isTrueNone(count > 0);
	}
	
	@ResponseBody
	public CodeMsg existName(Integer projectId, String name, Long excludeId) throws Exception {
		Assert.notNull(projectId, CodeMsgExt.PARAM_BLANK.fillArgs("项目ID"));
		Assert.notBlank(name, CodeMsgExt.PARAM_BLANK.fillArgs("权限名"));
		boolean exist = operationService.existName(projectId, name, excludeId);
		return none(exist);
	}
	
	@ResponseBody
	public CodeMsg existUrl(Integer projectId, String url, Long excludeId) throws Exception {
		Assert.notNull(projectId, CodeMsgExt.PARAM_BLANK.fillArgs("项目ID"));
		Assert.notBlank(url, CodeMsgExt.PARAM_BLANK.fillArgs("url"));
		boolean exist = operationService.existUrl(projectId, url, excludeId);
		return none(exist);
	}
	
}
