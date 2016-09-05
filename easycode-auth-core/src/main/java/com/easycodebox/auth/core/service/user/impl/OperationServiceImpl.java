package com.easycodebox.auth.core.service.user.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
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

import com.easycodebox.auth.core.dao.user.OperationMapper;
import com.easycodebox.auth.core.enums.ModuleType;
import com.easycodebox.auth.core.idconverter.UserIdConverter;
import com.easycodebox.auth.core.pojo.sys.Project;
import com.easycodebox.auth.core.pojo.user.Operation;
import com.easycodebox.auth.core.pojo.user.RoleOperation;
import com.easycodebox.auth.core.service.sys.ProjectService;
import com.easycodebox.auth.core.service.user.OperationService;
import com.easycodebox.auth.core.service.user.RoleProjectService;
import com.easycodebox.auth.core.service.user.RoleService;
import com.easycodebox.auth.core.service.user.UserService;
import com.easycodebox.auth.core.util.AccessTools;
import com.easycodebox.auth.core.util.CodeMsgExt;
import com.easycodebox.auth.core.util.Constants;
import com.easycodebox.auth.core.util.R;
import com.easycodebox.auth.core.util.aop.log.Log;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.enums.entity.status.CloseStatus;
import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.freemarker.ConfigurationFactory;
import com.easycodebox.common.jdbc.support.AbstractService;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.common.xml.XmlDataParser;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author WangXiaoJin
 *
 */
@Service("operationService")
public class OperationServiceImpl extends AbstractService<Operation> implements OperationService {
	
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
	private OperationMapper operationMapper;
	
	@Override
	public List<Operation> list(Integer projectId, CloseStatus status, YesNo isMenu) {
		return super.list(sql()
				.eq(R.Operation.projectId, projectId)
				.eq(R.Operation.status, status)
				.eq(R.Operation.isMenu, isMenu)
				.ne(R.Operation.status, CloseStatus.DELETE)
				.desc(R.Operation.isMenu)
				.desc(R.Operation.sort)
				.desc(R.Operation.createTime)
				);
	}

	@Override
	public Operation load(Long id, Integer projectId, String url) {
		if(id == null && (projectId == null || StringUtils.isBlank(url)))
			throw new BaseException("param id or projectId or url is null.");
		
		Operation data = operationMapper.load(id, projectId, url);
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
	@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	public Operation add(Operation operation) {
		
		/*Assert.isFalse(this.existName(operation.getProjectId(), operation.getName(), operation.getId()),
				ErrorCode.FAIL.msg("权限名{0}已被占用", operation.getName()));*/
		
		if(StringUtils.isNotBlank(operation.getUrl()))
			Assert.isFalse(this.existUrl(operation.getProjectId(), operation.getUrl(), operation.getId()),
					CodeMsgExt.FAIL.msg("url{0}已被占用", operation.getUrl()));
		
		if(operation.getStatus() == null)
			operation.setStatus(CloseStatus.OPEN);
		super.save(operation);
		return operation;
	}
	
	@Override
	@Log(title = "修改权限", moduleType = ModuleType.USER)
	@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	public int update(Operation operation) {
		
		/*Assert.isFalse(this.existName(operation.getProjectId(), operation.getName(), operation.getId()),
				ErrorCode.FAIL.msg("权限名{0}已被占用", operation.getName()));*/
		
		if(StringUtils.isNotBlank(operation.getUrl()))
			Assert.isFalse(this.existUrl(operation.getProjectId(), operation.getUrl(), operation.getId()),
					CodeMsgExt.FAIL.msg("url{0}已被占用", operation.getUrl()));
		
		if(operation.getStatus() != null) {
			LOG.info("The update method can not update status property.");
		}
		
		return super.update(sql()
				.updateNeed(R.Operation.projectId, operation.getProjectId())
				.updateNeed(R.Operation.parentId, operation.getParentId())
				.updateNeed(R.Operation.name, operation.getName())
				//.update(R.Operation.status, operation.getStatus())
				.updateNeed(R.Operation.isMenu, operation.getIsMenu())
				.updateNeed(R.Operation.url, operation.getUrl())
				.updateNeed(R.Operation.sort, operation.getSort())
				.updateNeed(R.Operation.icon, operation.getIcon())
				.updateNeed(R.Operation.description, operation.getDescription())
				.updateNeed(R.Operation.remark, operation.getRemark())
				.eqAst(R.Operation.id, operation.getId())
				);
	}

	@Override
	@Log(title = "逻辑删除权限", moduleType = ModuleType.USER)
	@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	public int remove(Long[] ids) {
		return super.updateStatus(ids, CloseStatus.DELETE);
	}
	
	@Override
	@Log(title = "物理删除权限", moduleType = ModuleType.USER)
	@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	public int removePhy(Long[] ids) {
		return super.delete(ids);
	}
	
	@Override
	@Log(title = "开启关闭权限", moduleType = ModuleType.USER)
	@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	public int openClose(Long[] ids, CloseStatus status) {
		return super.updateStatus(ids, status);
	}

	@Override
	public DataPage<Operation> page(String parentName, String projectName, 
			String operationName, YesNo isMenu, CloseStatus status, 
			String url, int pageNo, int pageSize) {
		
		parentName = parentName == null || StringUtils.isBlank(parentName) ? null 
				: Symbol.PERCENT.concat(parentName.trim()).concat(Symbol.PERCENT);
		
		projectName = projectName == null || StringUtils.isBlank(projectName) ? null 
				: Symbol.PERCENT.concat(projectName.trim()).concat(Symbol.PERCENT);
		
		operationName = operationName == null || StringUtils.isBlank(operationName) ? null 
				: Symbol.PERCENT.concat(operationName.trim()).concat(Symbol.PERCENT);
		
		url = url == null || StringUtils.isBlank(url) ? null 
				: Symbol.PERCENT.concat(url.trim()).concat(Symbol.PERCENT);
		
		List<Operation> os = operationMapper.page(parentName, projectName, 
				operationName, isMenu, status, url, pageNo, pageSize);
		long totalCount = operationMapper.pageTotalCount(parentName, projectName, operationName, isMenu, status, url);
		return new DataPage<Operation>(pageNo, pageSize, totalCount, os);
	}
	
	@Override
	public List<Long> listOperationIds(String userId, Integer projectId) {
		YesNo isSuperAdmin = userId == null ? YesNo.YES : userService.isSuperAdmin(userId);
		if(isSuperAdmin == YesNo.YES) 
			return super.list(sql()
					.column(R.Operation.id)
					.eq(R.Operation.projectId, projectId)
					.eq(R.Operation.status, CloseStatus.OPEN)
					.desc(R.Operation.sort)
					.desc(R.Operation.createTime)
					, Long.class);
		else {
			Integer[] roleIds = roleService.listOpenedRoleIdsByUserId(userId);
			return super.list(sql()
					.join(R.Operation.roleOperations, "ro")
					.distinct(R.Operation.id)
					.eq(R.Operation.projectId, projectId)
					.eq(R.Operation.status, CloseStatus.OPEN)
					.in(R.RoleOperation.roleId, roleIds)
					.desc(R.Operation.sort)
					.desc(R.Operation.createTime)
					, Long.class);
		}
	}
	
	@Override
	public Map<Long, Long> listOperateCode(String userId, Integer projectId) {
		List<Long> ors = this.listOperationIds(userId, projectId);
		/* 将操作集合转化为用户操作码 */
		return AccessTools.convertOperationNosToOperationCode(ors);
	}

	@Override
	@Transactional
	@SuppressWarnings("unchecked")
	@Log(title = "导入权限", moduleType = ModuleType.USER)
	@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	public synchronized void importFromXml(List<InputStream> streams) throws Exception {
		//此处不用truncate，因truncate不能被事务回滚
		//super.truncate();
		super.delete(sql());
		SAXReader reader = new SAXReader();
		for(int x = 0; x < streams.size(); x++) {
			Document document = null;
			try {
				document = reader.read(streams.get(x));
			} finally {
				IOUtils.closeQuietly(streams.get(x));
			}
			Element root = document.getRootElement();
			String project = XmlDataParser.getXmlAttributeVal(root, "project");
			Assert.notBlank(project, CodeMsgExt.PARAM_BLANK, "project");
			Project pro = projectService.load(project);
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
				Operation o1 = new Operation(StringUtils.isNotBlank(menu1Id) ? Long.parseLong(menu1Id) : cal1 , 
						null, menu1Name, pro.getId(), YesNo.YES, menu1Url, menu1Description, menu1Icon);
				if(StringUtils.isNotBlank(menu1Status)) {
					CloseStatus st = Enum.valueOf(CloseStatus.class, menu1Status);
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
					Operation o2 = new Operation(StringUtils.isNotBlank(menu2Id) ? Long.parseLong(menu2Id) : cal2 , 
							o1.getId(), menu2Name,
							pro.getId(), YesNo.YES, menu2Url, menu2Description, menu2Icon);
					if(StringUtils.isNotBlank(menu2Status)) {
						CloseStatus st = Enum.valueOf(CloseStatus.class, menu2Status);
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
						Operation o3 = new Operation(StringUtils.isNotBlank(menu3Id) ? Long.parseLong(menu3Id) : cal3 , 
								o2.getId(), menu3Name, 
								pro.getId(), YesNo.YES, menu3Url, menu3Description, menu3Icon);
						if(StringUtils.isNotBlank(menu3Status)) {
							CloseStatus st = Enum.valueOf(CloseStatus.class, menu3Status);
							o3.setStatus(st);
						}
						if(StringUtils.isNotBlank(menu3Sort)) {
							o3.setSort(Integer.parseInt(menu3Sort));
						}
						this.add(o3);
						analysisOperationTag(o3.getId(), pro.getId(), cal3 + 50L, 1, menu3);
					}
					analysisOperationTag(o2.getId(), pro.getId(), cal2 + 5000L, 100, menu2);
				}
				analysisOperationTag(o1.getId(), pro.getId(), cal1 + 500000L, 10000, menu1);
			}
			analysisOperationTag(null, pro.getId(), cal0 + 50000000L, 1000000, root);
		}
	}
	/**
	 * 解析operation标签
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private void analysisOperationTag(Long menuId, Integer projectId, long begin, int multiple, Element menu) throws Exception {
		//列出三级菜单
		List<Element> operations =  menu.selectNodes("operations/operation");
		for(int x = 0; x < operations.size(); x++) {
			Element operation = operations.get(x);
			String id = XmlDataParser.getXmlAttributeVal(operation, "id"),
					url = XmlDataParser.getXmlAttributeVal(operation, "url"),
					name = XmlDataParser.getXmlAttributeVal(operation, "name"),
					status = XmlDataParser.getXmlAttributeVal(operation, "status"),
					sort = XmlDataParser.getXmlAttributeVal(operation, "sort"),
					description = XmlDataParser.getXmlAttributeVal(operation, "description", name);
			Operation o = new Operation(StringUtils.isNotBlank(id) ? Long.parseLong(id) : begin + x*multiple , 
					menuId, name, projectId, YesNo.NO, url, description, null);
			if(StringUtils.isNotBlank(status)) {
				CloseStatus st = Enum.valueOf(CloseStatus.class, status);
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
	public void exportToXml(String ftlRes, File outPath) {
		List<Operation> os = this.listAllGroupByProject(null);
		if(!outPath.exists()) {
			outPath.mkdirs();
		}
		try {
			Configuration cfg = ConfigurationFactory.instance();
			//设置包装器，并将对象包装为数据模型
			Template tpl = cfg.getTemplate(ftlRes);
			for(Operation o : os) {
				Map<String, Object> root = new HashMap<String, Object>();
				root.put("project", o.getProject().getProjectNo());
				root.put("os", o.getChildren());
				Writer out = null;
				try {
					File tmp = new File(outPath, o.getProject().getProjectNo().toLowerCase() + ".xml");
					if(tmp.exists())
						tmp.delete();
					out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp),"UTF-8"));
					tpl.process(root, out);
				}finally {
					IOUtils.closeQuietly(out);
				}
			}
			LOG.info("=== ********* generate template *********** =====");
		} catch (IOException e) {
			LOG.error("generate value-object resource error.", e);
		} catch (TemplateException e) {
			LOG.error("generate value-object resource error.", e);
		}
	}

	@Override
	@Cacheable(cacheNames=Constants.CN.OPERATION, keyGenerator=Constants.METHOD_ARGS_KEY_GENERATOR)
	public List<Operation> listOperationsOfUser(String userId, Integer projectId, YesNo isMenu) {
		Assert.notNull(userId, "userId can not be null.");
		YesNo isSuperAdmin = userService.isSuperAdmin(userId);
		if(isSuperAdmin == YesNo.YES)
			return this.list(projectId, CloseStatus.OPEN, isMenu);
		else {
			Integer[] roleIds = roleService.listOpenedRoleIdsByUserId(userId);
			return this.listOperationsOfRoles(roleIds, projectId, isMenu);
		}
	}

	@Override
	public List<Operation> listTreeOperationsOfUser(String userId, Integer projectId, YesNo isMenu) {
		List<Operation> os = this.listOperationsOfUser(userId, projectId, isMenu);
		return treeOperations(null, os, null);
	}
	
	@Override
	public List<Operation> listTreeOperationsByRoleId(Integer roleId, YesNo isMenu) {
		List<Operation> os = null;
		if(roleId == null)
			os =  this.list(null, CloseStatus.OPEN, isMenu);
		else
			os = this.listOperationsOfRole(roleId, isMenu);
		return treeOperations(null, os, null);
	}

	@Override
	public List<Operation> listAllTreeOperationsByRoleId(Integer roleId, YesNo isMenu) {
		List<Operation> allOs = this.list(null, CloseStatus.OPEN, isMenu);
		List<Operation> userOs = this.listOperationsOfRole(roleId, isMenu);
		return treeOperations(null, allOs, userOs);
	}
	
	@Override
	@Cacheable(cacheNames=Constants.CN.OPERATION, keyGenerator=Constants.METHOD_ARGS_KEY_GENERATOR)
	public List<Operation> listAllGroupByProject(Integer roleId) {
		List<Operation> newOs = new ArrayList<Operation>();
		List<Operation> os = roleId == null ? treeOperations(null, this.list(null, null, null), null)
				: this.listAllTreeOperationsByRoleId(roleId, null);
		Map<Integer, List<Operation>> mapping = new HashMap<Integer, List<Operation>>();
		for(Operation o : os) {
			if(!mapping.containsKey(o.getProjectId())) {
				mapping.put(o.getProjectId(), new ArrayList<Operation>());
			}
			mapping.get(o.getProjectId()).add(o);
		}
		
		List<Integer> ownProIds = null;
		if(roleId != null) {
			ownProIds = roleProjectService.getProjectIdsByRoleId(roleId);
		}
		
		List<Project> pros = projectService.list(null, null);
		
		for(Project p : pros) {
			Operation o = new Operation();
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
	private List<Operation> treeOperations(Long parentId, List<Operation> all, List<Operation> owns) {
		List<Operation> cur = new LinkedList<Operation>();
		for(Operation o : all) {
			if(parentId == null ? o.getParentId() == null : parentId.equals(o.getParentId())) {
				o.setChildren(treeOperations(o.getId(), all, owns));
				if(owns != null) {
					YesNo isOwn = YesNo.NO;
					for(Operation own : owns) {
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
	public List<Operation> listAllTreeOperationsByUserId(String userId, Integer projectId, YesNo isMenu) {
		Assert.notBlank(userId, CodeMsgExt.PARAM_BLANK, "用户ID");
		List<Operation> allOs = this.list(projectId, CloseStatus.OPEN, isMenu);
		List<Operation> userOs = this.listOperationsOfUser(userId, projectId, isMenu);
		return treeOperations(null, allOs, userOs);
	}
	
	@Override
	@Log(title = "配置指定角色权限", moduleType = ModuleType.USER)
	@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	public void addOperationsOfRole(int roleId, Long[] operationIds) {
		super.delete(sql(RoleOperation.class).eq(R.RoleOperation.roleId, roleId));
		for(int i = 0; i < operationIds.length; i++) {
			RoleOperation ro = new RoleOperation();
			ro.setRoleId(roleId);
			ro.setOperationId(operationIds[i]);
			super.save(ro, RoleOperation.class);
		}
	}

	@Override
	public List<Operation> listOperationsOfRole(int roleId, YesNo isMenu) {
		return this.listOperationsOfRoles(new Integer[]{roleId}, null, isMenu);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Operation> listOperationsOfRoles(Integer[] roleIds, Integer projectId, YesNo isMenu) {
		if(roleIds == null || roleIds.length == 0)
			return Collections.EMPTY_LIST;
		return operationMapper.listOperationsOfRoles(roleIds, projectId, isMenu);
	}
	
	@Override
	public List<Operation> listAllOpsOfRoles(Integer[] roleIds,
			Integer projectId, YesNo isMenu) {
		List<Operation> all = this.list(projectId, CloseStatus.OPEN, isMenu);
		List<Operation> owns = this.listOperationsOfRoles(roleIds, projectId, isMenu);
		return analyzeOps(all, owns);
	}

	@Override
	@Cacheable(cacheNames=Constants.CN.OPERATION, keyGenerator=Constants.METHOD_ARGS_KEY_GENERATOR)
	public List<Operation> listAllOpsOfUser(String userId, Integer projectId,
			YesNo isMenu) {
		List<Operation> all = this.list(projectId, CloseStatus.OPEN, isMenu);
		List<Operation> owns = this.listOperationsOfUser(userId, projectId, isMenu);
		return analyzeOps(all, owns);
	}
	
	private List<Operation> analyzeOps(List<Operation> all, List<Operation> owns) {
		if(all != null && all.size() > 0) {
			for(Operation o : all) {
				for(Operation t : owns) {
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
	@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	public int changeIsMenu(Long id, YesNo isMenu) {
		return super.update(sql()
				.updateAst(R.Operation.isMenu, isMenu)
				.eqAst(R.Operation.id, id)
				);
	}

	@Override
	public boolean existName(Integer projectId, String name, Long excludeId) {
		return this.exist(sql()
				.eqAst(R.Operation.name, name)
				.eq(R.Operation.projectId, projectId)
				.ne(R.Operation.status, CloseStatus.DELETE)
				.ne(R.Operation.id, excludeId)
				);
	}

	@Override
	public boolean existUrl(Integer projectId, String url, Long excludeId) {
		return this.exist(sql()
				.eqAst(R.Operation.url, url)
				.eq(R.Operation.projectId, projectId)
				.ne(R.Operation.status, CloseStatus.DELETE)
				.ne(R.Operation.id, excludeId)
				);
	}
	
}
