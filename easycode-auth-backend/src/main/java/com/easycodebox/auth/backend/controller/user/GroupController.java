package com.easycodebox.auth.backend.controller.user;

import com.easycodebox.auth.core.idconverter.UserIdConverter;
import com.easycodebox.auth.core.service.user.GroupService;
import com.easycodebox.auth.core.util.CodeMsgExt;
import com.easycodebox.auth.model.entity.user.Group;
import com.easycodebox.common.enums.entity.OpenClose;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.jackson.Jacksons;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.validate.*;
import com.easycodebox.common.web.BaseController;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author WangXiaoJin
 *
 */
@Controller
public class GroupController extends BaseController {
	
	@Resource
	private UserIdConverter userIdConverter;
	@Resource
	private GroupService groupService;

	/**
	 * 列表
	 */
	@ResponseBody
	public CodeMsg list(Group group, DataPage<Group> dataPage) throws Exception {
		DataPage<Group> data = groupService.page(group.getParentName(), group.getName(), 
				group.getStatus(), dataPage.getPageNo(), dataPage.getPageSize());
		for (Group item : data.getData()) {
			item.setCreatorName(userIdConverter.idToRealOrNickname(item.getCreator()));
		}
		return none(data);
	}
	
	/**
	 * 详情
	 */
	@ResponseBody
	public CodeMsg load(Integer id) throws Exception {
		Assert.notNull(id, CodeMsg.FAIL.msg("主键参数不能为空"));
		Group data = groupService.load(id);
		return isTrueNone(data != null, "没有对应的组织").data(data);
	}
	
	/**
	 * 新增
	 */
	@ResponseBody
	public CodeMsg add(Group group) throws Exception {
		groupService.add(group);
		return CodeMsg.SUC;
	}
	
	/**
	 * 修改
	 */
	@ResponseBody
	public CodeMsg update(Group group) throws Exception {
		int count = groupService.update(group);
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
		int count = groupService.remove(ids);
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
		int count = groupService.removePhy(ids);
		return isTrue(count > 0);
	}
	
	@ResponseBody
	public CodeMsg openClose(Integer[] ids, OpenClose status) throws Exception {
		Validators.instance(ids)
			.minLength(1, "主键参数不能传空值")
			.notEmptyInside("主键参数不能传空值");
		int count = groupService.openClose(ids, status);
		return isTrueNone(count > 0);
	}
	
	@ResponseBody
	public String listAll() throws Exception {
		List<Group> gs = groupService.list(null);
		Group g = new Group();
		g.setId(-1);
		g.setName("--无--");
		gs.add(0, g);
		Jacksons j = Jacksons.nonNull().addJsonSerializer(Group.class, new JsonSerializer<Group>(){

			@Override
			public void serialize(Group value, JsonGenerator gen,
					SerializerProvider serializers) throws IOException {
				gen.writeStartObject();
				gen.writeObjectField("id", value.getId());
				gen.writeObjectField("pId", value.getParentId());
				gen.writeStringField("name", value.getName());
				gen.writeEndObject();
			}
			
		});
		return j.toJson(gs);
	}
	
	@ResponseBody
	public CodeMsg existName(String name, Integer excludeId) throws Exception {
		Assert.notBlank(name, CodeMsgExt.PARAM_BLANK.fillArgs("组名"));
		boolean exist = groupService.existName(name, excludeId);
		return none(exist);
	}
	
}
