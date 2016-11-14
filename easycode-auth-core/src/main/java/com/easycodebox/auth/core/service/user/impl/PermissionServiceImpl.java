package com.easycodebox.auth.core.service.user.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easycodebox.auth.core.dao.user.PermissionMapper;
import com.easycodebox.auth.core.idconverter.UserIdConverter;
import com.easycodebox.auth.core.service.sys.ProjectService;
import com.easycodebox.auth.core.service.user.PermissionService;
import com.easycodebox.auth.core.service.user.RoleProjectService;
import com.easycodebox.auth.core.service.user.RoleService;
import com.easycodebox.auth.core.service.user.UserService;
import com.easycodebox.auth.core.util.AccessTools;
import com.easycodebox.auth.core.util.CodeMsgExt;
import com.easycodebox.auth.core.util.Constants;
import com.easycodebox.auth.core.util.aop.log.Log;
import com.easycodebox.auth.model.entity.sys.Project;
import com.easycodebox.auth.model.entity.user.Permission;
import com.easycodebox.auth.model.entity.user.RolePermission;
import com.easycodebox.auth.model.enums.ModuleType;
import com.easycodebox.auth.model.util.R;
import com.easycodebox.common.enums.entity.OpenClose;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.freemarker.ConfigurationFactory;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.common.xml.XmlDataParser;
import com.easycodebox.jdbc.support.AbstractServiceImpl;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

/**
 * @author WangXiaoJin
 *
 */
@Service("permissionService")
public class PermissionServiceImpl extends AbstractServiceImpl<Permission> implements PermissionService {
	
	@Resource
	private UserIdConverter userIdConverter;
	
	@Resource
	private RoleService roleService;
	@Resource
	private UserService userService;
	@Resource
	private ProjectService projectService;
	@Resource
	private RoleProjectService roleProjectService;
	
	@Resource
	private PermissionMapper permissionMapper;
	
	@Override
	public List<Permission> list(Integer projectId, OpenClose status, YesNo isMenu) {
		return super.list(sql()
				.eq(R.Permission.projectId, projectId)
				.eq(R.Permission.status, status)
				.eq(R.Permission.isMenu, isMenu)
				.eq(R.Permission.deleted, YesNo.NO)
				.desc(R.Permission.isMenu)
				.desc(R.Permission.sort)
				.desc(R.Permission.createTime)
				);
	}

	@Override
	public Permission load(Long id, Integer projectId, String url) {
		if(id == null && (projectId == null || StringUtils.isBlank(url)))
			throw new BaseException("param id or projectId or url is null.");
		
		Permission data = permissionMapper.load(id, projectId, url);
		if (data != null) {
			data.setCreatorName(userIdConverter.id2RealOrNickname(data.getCreator()));
			data.setModifierName(userIdConverter.id2RealOrNickname(data.getModifier()));
		}
		return data;
	}

	@Override
	@Transactional
	@Log(title = "添加权限", moduleType = ModuleType.USER)
	//超级管理员具有所有的权限，所以新增权限后要更新缓存
	@CacheEvict(cacheNames=Constants.CN.PERMISSION, allEntries=true)
	public Permission add(Permission permission) {
		
		/*Assert.isFalse(this.existName(permission.getProjectId(), permission.getName(), permission.getId()),
				ErrorCode.FAIL.msg("权限名{0}已被占用", permission.getName()));*/
		
		if(StringUtils.isNotBlank(permission.getUrl()))
			Assert.isFalse(this.existUrl(permission.getProjectId(), permission.getUrl(), permission.getId()),
					CodeMsgExt.FAIL.msg("url{0}已被占用", permission.getUrl()));
		
		if(permission.getStatus() == null)
			permission.setStatus(OpenClose.OPEN);
		permission.setDeleted(YesNo.NO);
		super.save(permission);
		return permission;
	}
	
	@Override
	@Log(title = "修改权限", moduleType = ModuleType.USER)
	@CacheEvict(cacheNames=Constants.CN.PERMISSION, allEntries=true)
	public int update(Permission permission) {
		
		/*Assert.isFalse(this.existName(permission.getProjectId(), permission.getName(), permission.getId()),
				ErrorCode.FAIL.msg("权限名{0}已被占用", permission.getName()));*/
		
		if(StringUtils.isNotBlank(permission.getUrl()))
			Assert.isFalse(this.existUrl(permission.getProjectId(), permission.getUrl(), permission.getId()),
					CodeMsgExt.FAIL.msg("url{0}已被占用", permission.getUrl()));
		
		if(permission.getStatus() != null) {
			log.info("The update method can not update status property.");
		}
		
		return super.update(sql()
				.updateNeed(R.Permission.projectId, permission.getProjectId())
				.updateNeed(R.Permission.parentId, permission.getParentId())
				.updateNeed(R.Permission.name, permission.getName())
				//.update(R.Permission.status, permission.getStatus())
				.updateNeed(R.Permission.isMenu, permission.getIsMenu())
				.updateNeed(R.Permission.url, permission.getUrl())
				.updateNeed(R.Permission.sort, permission.getSort())
				.updateNeed(R.Permission.icon, permission.getIcon())
				.updateNeed(R.Permission.description, permission.getDescription())
				.updateNeed(R.Permission.remark, permission.getRemark())
				.eqAst(R.Permission.id, permission.getId())
				);
	}

	@Override
	@Log(title = "逻辑删除权限", moduleType = ModuleType.USER)
	@CacheEvict(cacheNames=Constants.CN.PERMISSION, allEntries=true)
	public int remove(Long[] ids) {
		return super.delete(ids);
	}
	
	@Override
	@Log(title = "物理删除权限", moduleType = ModuleType.USER)
	@CacheEvict(cacheNames=Constants.CN.PERMISSION, allEntries=true)
	public int removePhy(Long[] ids) {
		return super.deletePhy(ids);
	}
	
	@Override
	@Log(title = "开启关闭权限", moduleType = ModuleType.USER)
	@CacheEvict(cacheNames=Constants.CN.PERMISSION, allEntries=true)
	public int openClose(Long[] ids, OpenClose status) {
		return super.updateStatus(ids, status);
	}

	@Override
	public DataPage<Permission> page(String parentName, String projectName, 
			String permissionName, YesNo isMenu, OpenClose status, 
			String url, int pageNo, int pageSize) {
		
		parentName = StringUtils.trimToNull(parentName);
		projectName = StringUtils.trimToNull(projectName);
		permissionName = StringUtils.trimToNull(permissionName);
		url = StringUtils.trimToNull(url);
		
		List<Permission> os = permissionMapper.page(parentName, projectName, 
				permissionName, isMenu, status, url, pageNo, pageSize);
		long totalCount = permissionMapper.pageTotalCount(parentName, projectName, permissionName, isMenu, status, url);
		return new DataPage<Permission>(pageNo, pageSize, totalCount, os);
	}
	
	@Override
	public List<Long> listPermissionIds(String userId, Integer projectId) {
		YesNo isSuperAdmin = userId == null ? YesNo.YES : userService.isSuperAdmin(userId);
		if(isSuperAdmin == YesNo.YES) 
			return super.list(sql()
					.column(R.Permission.id)
					.eq(R.Permission.projectId, projectId)
					.eq(R.Permission.deleted, YesNo.NO)
					.eq(R.Permission.status, OpenClose.OPEN)
					.desc(R.Permission.sort)
					.desc(R.Permission.createTime)
					, Long.class);
		else {
			Integer[] roleIds = roleService.listOpenedRoleIdsByUserId(userId);
			return super.list(sql()
					.join(R.Permission.rolePermissions, "ro")
					.distinct(R.Permission.id)
					.eq(R.Permission.projectId, projectId)
					.eq(R.Permission.deleted, YesNo.NO)
					.eq(R.Permission.status, OpenClose.OPEN)
					.in(R.RolePermission.roleId, roleIds)
					.desc(R.Permission.sort)
					.desc(R.Permission.createTime)
					, Long.class);
		}
	}
	
	@Override
	public Map<Long, Long> listOperateCode(String userId, Integer projectId) {
		List<Long> ors = this.listPermissionIds(userId, projectId);
		/* 将操作集合转化为用户操作码 */
		return AccessTools.convertPermissionNosToPermissionCode(ors);
	}

	@Override
	@Transactional
	@SuppressWarnings("unchecked")
	@Log(title = "导入权限", moduleType = ModuleType.USER)
	@CacheEvict(cacheNames=Constants.CN.PERMISSION, allEntries=true)
	public synchronized void importFromXml(InputStream streams) throws Exception {
		//此处不用truncate，因truncate不能被事务回滚，而且此接口改成操作单个项目的权限了
		//super.truncate();
		
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(streams);
		} finally {
			IOUtils.closeQuietly(streams);
		}
		Element root = document.getRootElement();
		String project = XmlDataParser.getXmlAttributeVal(root, "project");
		Assert.notBlank(project, CodeMsg.FAIL.msg("XML文件中缺少project属性"));
		Project pro = projectService.load(project);
		Assert.notNull(pro, CodeMsg.FAIL.msg("没有项目编号为({})的授权项目", project));
		//删除原有权限
		super.deletePhy(sql().eq(R.Permission.projectId, pro.getId()));
		
		long cal0 = (pro.getNum())*100000000L;
		//列出以及菜单
		List<Element> menu1s =  root.elements("menu1");
		for(int i = 0; i < menu1s.size(); i++) {
			Element menu1 = menu1s.get(i);
			long cal1 = cal0 + (i + 1)*1000000L;
			String menu1Id = XmlDataParser.getXmlAttributeVal(menu1, "id"),
					menu1Url = XmlDataParser.getXmlAttributeVal(menu1, "url"),
					menu1Name = XmlDataParser.getXmlAttributeVal(menu1, "name"),
					menu1Icon = XmlDataParser.getXmlAttributeVal(menu1, "icon"),
					menu1Status = XmlDataParser.getXmlAttributeVal(menu1, "status"),
					menu1Sort = XmlDataParser.getXmlAttributeVal(menu1, "sort"),
					menu1Description = XmlDataParser.getXmlAttributeVal(menu1, "description", menu1Name);
			Permission o1 = new Permission(StringUtils.isNotBlank(menu1Id) ? Long.parseLong(menu1Id) : cal1 , 
					null, menu1Name, pro.getId(), YesNo.YES, menu1Url, menu1Description, menu1Icon);
			if(StringUtils.isNotBlank(menu1Status)) {
				OpenClose st = Enum.valueOf(OpenClose.class, menu1Status);
				o1.setStatus(st);
			}
			if(StringUtils.isNotBlank(menu1Sort)) {
				o1.setSort(Integer.parseInt(menu1Sort));
			}
			this.add(o1);
			//列出二级菜单
			List<Element> menu2s =  menu1.elements("menu2");
			for(int j = 0; j < menu2s.size(); j++) {
				Element menu2 = menu2s.get(j);
				long cal2 = cal1 + (j + 1)*10000L;
				String menu2Id = XmlDataParser.getXmlAttributeVal(menu2, "id"),
						menu2Url = XmlDataParser.getXmlAttributeVal(menu2, "url"),
						menu2Name = XmlDataParser.getXmlAttributeVal(menu2, "name"),
						menu2Icon = XmlDataParser.getXmlAttributeVal(menu2, "icon"),
						menu2Status = XmlDataParser.getXmlAttributeVal(menu2, "status"),
						menu2Sort = XmlDataParser.getXmlAttributeVal(menu2, "sort"),
						menu2Description = XmlDataParser.getXmlAttributeVal(menu2, "description", menu2Name);
				Permission o2 = new Permission(StringUtils.isNotBlank(menu2Id) ? Long.parseLong(menu2Id) : cal2 , 
						o1.getId(), menu2Name,
						pro.getId(), YesNo.YES, menu2Url, menu2Description, menu2Icon);
				if(StringUtils.isNotBlank(menu2Status)) {
					OpenClose st = Enum.valueOf(OpenClose.class, menu2Status);
					o2.setStatus(st);
				}
				if(StringUtils.isNotBlank(menu2Sort)) {
					o2.setSort(Integer.parseInt(menu2Sort));
				}
				this.add(o2);
				//列出三级菜单
				List<Element> menu3s =  menu2.elements("menu3");
				for(int k = 0; k < menu3s.size(); k++) {
					Element menu3 = menu3s.get(k);
					long cal3 = cal2 + (k + 1)*100L;
					String menu3Id = XmlDataParser.getXmlAttributeVal(menu3, "id"),
							menu3Url = XmlDataParser.getXmlAttributeVal(menu3, "url"),
							menu3Name = XmlDataParser.getXmlAttributeVal(menu3, "name"),
							menu3Icon = XmlDataParser.getXmlAttributeVal(menu3, "icon"),
							menu3Status = XmlDataParser.getXmlAttributeVal(menu3, "status"),
							menu3Sort = XmlDataParser.getXmlAttributeVal(menu3, "sort"),
							menu3Description = XmlDataParser.getXmlAttributeVal(menu3, "description", menu3Name);
					Permission o3 = new Permission(StringUtils.isNotBlank(menu3Id) ? Long.parseLong(menu3Id) : cal3 , 
							o2.getId(), menu3Name, 
							pro.getId(), YesNo.YES, menu3Url, menu3Description, menu3Icon);
					if(StringUtils.isNotBlank(menu3Status)) {
						OpenClose st = Enum.valueOf(OpenClose.class, menu3Status);
						o3.setStatus(st);
					}
					if(StringUtils.isNotBlank(menu3Sort)) {
						o3.setSort(Integer.parseInt(menu3Sort));
					}
					this.add(o3);
					analysisPermissionTag(o3.getId(), pro.getId(), cal3 + 50L, 1, menu3);
				}
				analysisPermissionTag(o2.getId(), pro.getId(), cal2 + 5000L, 100, menu2);
			}
			analysisPermissionTag(o1.getId(), pro.getId(), cal1 + 500000L, 10000, menu1);
		}
		analysisPermissionTag(null, pro.getId(), cal0 + 50000000L, 1000000, root);
	}
	/**
	 * 解析permission标签
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private void analysisPermissionTag(Long menuId, Integer projectId, long begin, int multiple, Element menu) throws Exception {
		//列出三级菜单
		List<Element> permissions =  menu.selectNodes("permissions/permission");
		for(int x = 0; x < permissions.size(); x++) {
			Element permission = permissions.get(x);
			String id = XmlDataParser.getXmlAttributeVal(permission, "id"),
					url = XmlDataParser.getXmlAttributeVal(permission, "url"),
					name = XmlDataParser.getXmlAttributeVal(permission, "name"),
					status = XmlDataParser.getXmlAttributeVal(permission, "status"),
					sort = XmlDataParser.getXmlAttributeVal(permission, "sort"),
					description = XmlDataParser.getXmlAttributeVal(permission, "description", name);
			Permission o = new Permission(StringUtils.isNotBlank(id) ? Long.parseLong(id) : begin + x*multiple , 
					menuId, name, projectId, YesNo.NO, url, description, null);
			if(StringUtils.isNotBlank(status)) {
				OpenClose st = Enum.valueOf(OpenClose.class, status);
				o.setStatus(st);
			}
			if(StringUtils.isNotBlank(sort)) {
				o.setSort(Integer.parseInt(sort));
			}
			this.add(o);
		}
	}
	
	@Override
	@Log(title = "导出权限", moduleType = ModuleType.USER)
	public void exportToXml(String ftlRes, Integer projectId, Writer writer) 
			throws TemplateException, TemplateNotFoundException, MalformedTemplateNameException, 
			ParseException, IOException {
		List<Permission> os = treePermissions(null, this.list(projectId, null, null), null);
		Project project = projectService.load(projectId);
		Configuration cfg = ConfigurationFactory.instance();
		//设置包装器，并将对象包装为数据模型
		Template tpl = cfg.getTemplate(ftlRes);
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("project", project.getProjectNo());
		root.put("os", os);
		tpl.process(root, writer);
	}

	@Override
	@Cacheable(cacheNames=Constants.CN.PERMISSION, keyGenerator=Constants.METHOD_ARGS_KEY_GENERATOR)
	public List<Permission> listPermissionsOfUser(String userId, Integer projectId, YesNo isMenu) {
		Assert.notNull(userId, "userId can not be null.");
		YesNo isSuperAdmin = userService.isSuperAdmin(userId);
		if(isSuperAdmin == YesNo.YES)
			return this.list(projectId, OpenClose.OPEN, isMenu);
		else {
			Integer[] roleIds = roleService.listOpenedRoleIdsByUserId(userId);
			return this.listPermissionsOfRoles(roleIds, projectId, isMenu);
		}
	}

	@Override
	public List<Permission> listTreePermissionsOfUser(String userId, Integer projectId, YesNo isMenu) {
		List<Permission> os = this.listPermissionsOfUser(userId, projectId, isMenu);
		return treePermissions(null, os, null);
	}
	
	@Override
	public List<Permission> listTreePermissionsByRoleId(Integer roleId, YesNo isMenu) {
		List<Permission> os = null;
		if(roleId == null)
			os =  this.list(null, OpenClose.OPEN, isMenu);
		else
			os = this.listPermissionsOfRole(roleId, isMenu);
		return treePermissions(null, os, null);
	}

	@Override
	public List<Permission> listAllTreePermissionsByRoleId(Integer roleId, YesNo isMenu) {
		List<Permission> allOs = this.list(null, OpenClose.OPEN, isMenu);
		List<Permission> userOs = this.listPermissionsOfRole(roleId, isMenu);
		return treePermissions(null, allOs, userOs);
	}
	
	@Override
	@Cacheable(cacheNames=Constants.CN.PERMISSION, keyGenerator=Constants.METHOD_ARGS_KEY_GENERATOR)
	public List<Permission> listAllGroupByProject(Integer roleId) {
		List<Permission> newOs = new ArrayList<Permission>();
		List<Permission> os = roleId == null ? treePermissions(null, this.list(null, null, null), null)
				: this.listAllTreePermissionsByRoleId(roleId, null);
		Map<Integer, List<Permission>> mapping = new HashMap<>();
		for(Permission o : os) {
			if(!mapping.containsKey(o.getProjectId())) {
				mapping.put(o.getProjectId(), new ArrayList<Permission>());
			}
			mapping.get(o.getProjectId()).add(o);
		}
		
		List<Integer> ownProIds = null;
		if(roleId != null) {
			ownProIds = roleProjectService.getProjectIdsByRoleId(roleId);
		}
		
		List<Project> pros = projectService.list(null, null);
		
		for(Project p : pros) {
			Permission o = new Permission();
			o.setName(p.getName());
			o.setIsMenu(YesNo.YES);
			o.setChildren(mapping.get(p.getId()));
			o.setProject(p);
			
			if(ownProIds != null) {
				for(Integer proId : ownProIds) {
					if(proId.equals(p.getId())) {
						o.setIsOwn(YesNo.YES);
						break;
					}
				}
			}
			
			newOs.add(o);
		}
		
		return newOs;
	}

	/**
	 * 为权限列表组装成树形结构
	 * @param parentId
	 * @param all	所有的权限
	 * @param owns	拥有的权限
	 * @return
	 */
	private List<Permission> treePermissions(Long parentId, List<Permission> all, List<Permission> owns) {
		List<Permission> cur = new LinkedList<Permission>();
		for(Permission o : all) {
			if(parentId == null ? o.getParentId() == null : parentId.equals(o.getParentId())) {
				o.setChildren(treePermissions(o.getId(), all, owns));
				if(owns != null) {
					YesNo isOwn = YesNo.NO;
					for(Permission own : owns) {
						if(o.getId() != null && o.getId().equals(own.getId())) {
							isOwn = YesNo.YES;
							break;
						}
					}
					o.setIsOwn(isOwn);
				}
				cur.add(o);
			}
		}
		return cur;
	}

	@Override
	public List<Permission> listAllTreePermissionsByUserId(String userId, Integer projectId, YesNo isMenu) {
		Assert.notBlank(userId, CodeMsgExt.PARAM_BLANK.fillArgs("用户ID"));
		List<Permission> allOs = this.list(projectId, OpenClose.OPEN, isMenu);
		List<Permission> userOs = this.listPermissionsOfUser(userId, projectId, isMenu);
		return treePermissions(null, allOs, userOs);
	}
	
	@Override
	@Log(title = "配置指定角色权限", moduleType = ModuleType.USER)
	@CacheEvict(cacheNames=Constants.CN.PERMISSION, allEntries=true)
	public void addPermissionsOfRole(int roleId, Long[] permissionIds) {
		super.deletePhy(sql(RolePermission.class).eq(R.RolePermission.roleId, roleId));
		for(int i = 0; i < permissionIds.length; i++) {
			RolePermission ro = new RolePermission();
			ro.setRoleId(roleId);
			ro.setPermissionId(permissionIds[i]);
			super.save(ro, RolePermission.class);
		}
	}

	@Override
	public List<Permission> listPermissionsOfRole(int roleId, YesNo isMenu) {
		return this.listPermissionsOfRoles(new Integer[]{roleId}, null, isMenu);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Permission> listPermissionsOfRoles(Integer[] roleIds, Integer projectId, YesNo isMenu) {
		if(roleIds == null || roleIds.length == 0)
			return Collections.EMPTY_LIST;
		return permissionMapper.listPermissionsOfRoles(roleIds, projectId, isMenu);
	}
	
	@Override
	public List<Permission> listAllOpsOfRoles(Integer[] roleIds,
			Integer projectId, YesNo isMenu) {
		List<Permission> all = this.list(projectId, OpenClose.OPEN, isMenu);
		List<Permission> owns = this.listPermissionsOfRoles(roleIds, projectId, isMenu);
		return analyzeOps(all, owns);
	}

	@Override
	@Cacheable(cacheNames=Constants.CN.PERMISSION, keyGenerator=Constants.METHOD_ARGS_KEY_GENERATOR)
	public List<Permission> listAllOpsOfUser(String userId, Integer projectId,
			YesNo isMenu) {
		List<Permission> all = this.list(projectId, OpenClose.OPEN, isMenu);
		List<Permission> owns = this.listPermissionsOfUser(userId, projectId, isMenu);
		return analyzeOps(all, owns);
	}
	
	private List<Permission> analyzeOps(List<Permission> all, List<Permission> owns) {
		if(all != null && all.size() > 0) {
			for(Permission o : all) {
				for(Permission t : owns) {
					if(o.getId().equals(t.getId())) {
						o.setIsOwn(YesNo.YES);
						break;
					}
				}
			}
		}
		return all;
	}

	@Override
	@Log(title = "修改权限是否为菜单项", moduleType = ModuleType.USER)
	@CacheEvict(cacheNames=Constants.CN.PERMISSION, allEntries=true)
	public int changeIsMenu(Long id, YesNo isMenu) {
		return super.update(sql()
				.updateAst(R.Permission.isMenu, isMenu)
				.eqAst(R.Permission.id, id)
				);
	}

	@Override
	public boolean existName(Integer projectId, String name, Long excludeId) {
		return this.exist(sql()
				.eqAst(R.Permission.name, name)
				.eq(R.Permission.projectId, projectId)
				.eq(R.Permission.deleted, YesNo.NO)
				.ne(R.Permission.id, excludeId)
				);
	}

	@Override
	public boolean existUrl(Integer projectId, String url, Long excludeId) {
		return this.exist(sql()
				.eqAst(R.Permission.url, url)
				.eq(R.Permission.projectId, projectId)
				.eq(R.Permission.deleted, YesNo.NO)
				.ne(R.Permission.id, excludeId)
				);
	}
	
}
