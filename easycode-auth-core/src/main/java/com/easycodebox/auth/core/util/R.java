package com.easycodebox.auth.core.util;

import static com.easycodebox.jdbc.Property.instance;

import com.easycodebox.jdbc.Property;
import com.easycodebox.jdbc.entity.Entity;

/**
 * 	如果是Entity类，则需要加上private static final Class<? extends Entity> entity = com.easycodebox.core.pojo.xxx.xxx.class;
 * 且创建的Property属性需要增加。如：public static final Property id = instance("id", entity)。
 * 	如果只是普通的BO对象，则：public static final String id = "id";
 * @author WangXiaoJin
 *
 */
public class R {

	public static class Generator {
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.core.pojo.sys.Generator.class;
		public static final Property 
			generatorType = instance("generatorType", entity),
			initialVal = instance("initialVal", entity),
			currentVal = instance("currentVal", entity),
			maxVal = instance("maxVal", entity),
			fetchSize = instance("fetchSize", entity),
			increment = instance("increment", entity),
			isCycle = instance("isCycle", entity),
			creator = instance("creator", entity),
			createTime = instance("createTime", entity),
			modifier = instance("modifier", entity),
			modifyTime = instance("modifyTime", entity),
			creatorName = instance("creatorName", entity),
			modifierName = instance("modifierName", entity);
	
	}
	
	public static class Log {
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.core.pojo.sys.Log.class;
		public static final Property 
			id = instance("id", entity),
			title = instance("title", entity),
			method = instance("method", entity),
			url = instance("url", entity),
			params = instance("params", entity),
			moduleType = instance("moduleType", entity),
			logLevel = instance("logLevel", entity),
			result = instance("result", entity),
			clientIp = instance("clientIp", entity),
			errorMsg = instance("errorMsg", entity),
			creator = instance("creator", entity),
			createTime = instance("createTime", entity),
			creatorName = instance("creatorName", entity);
	
	}
	
	public static class Partner {
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.core.pojo.sys.Partner.class;
		public static final Property 
			id = instance("id", entity),
			name = instance("name", entity),
			partnerKey = instance("partnerKey", entity),
			website = instance("website", entity),
			status = instance("status", entity),
			sort = instance("sort", entity),
			contract = instance("contract", entity),
			remark = instance("remark", entity),
			creator = instance("creator", entity),
			createTime = instance("createTime", entity),
			modifier = instance("modifier", entity),
			modifyTime = instance("modifyTime", entity),
			creatorName = instance("creatorName", entity),
			modifierName = instance("modifierName", entity);
	
	}
	
	public static class Project {
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.core.pojo.sys.Project.class;
		public static final Property 
			id = instance("id", entity),
			name = instance("name", entity),
			projectNo = instance("projectNo", entity),
			status = instance("status", entity),
			sort = instance("sort", entity),
			num = instance("num", entity),
			remark = instance("remark", entity),
			creator = instance("creator", entity),
			createTime = instance("createTime", entity),
			modifier = instance("modifier", entity),
			modifyTime = instance("modifyTime", entity),
			creatorName = instance("creatorName", entity),
			modifierName = instance("modifierName", entity);
	
	}
	
	public static class Group {
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.core.pojo.user.Group.class;
		public static final Property 
			id = instance("id", entity),
			parentId = instance("parentId", entity),
			name = instance("name", entity),
			sort = instance("sort", entity),
			status = instance("status", entity),
			parent = instance("parent", entity),
			children = instance("children", entity),
			users = instance("users", entity),
			groupRoles = instance("groupRoles", entity),
			parentName = instance("parentName", entity),
			creator = instance("creator", entity),
			createTime = instance("createTime", entity),
			modifier = instance("modifier", entity),
			modifyTime = instance("modifyTime", entity),
			creatorName = instance("creatorName", entity),
			modifierName = instance("modifierName", entity);
	
	}
	
	public static class GroupRole {
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.core.pojo.user.GroupRole.class;
		public static final Property 
			roleId = instance("roleId", entity),
			groupId = instance("groupId", entity),
			group = instance("group", entity),
			role = instance("role", entity),
			creator = instance("creator", entity),
			createTime = instance("createTime", entity),
			creatorName = instance("creatorName", entity);
	
	}
	
	public static class Operation {
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.core.pojo.user.Operation.class;
		public static final Property 
			id = instance("id", entity),
			parentId = instance("parentId", entity),
			projectId = instance("projectId", entity),
			name = instance("name", entity),
			status = instance("status", entity),
			isMenu = instance("isMenu", entity),
			url = instance("url", entity),
			sort = instance("sort", entity),
			icon = instance("icon", entity),
			description = instance("description", entity),
			remark = instance("remark", entity),
			parent = instance("parent", entity),
			project = instance("project", entity),
			children = instance("children", entity),
			roleOperations = instance("roleOperations", entity),
			isOwn = instance("isOwn", entity),
			parentName = instance("parentName", entity),
			projectName = instance("projectName", entity),
			creator = instance("creator", entity),
			createTime = instance("createTime", entity),
			modifier = instance("modifier", entity),
			modifyTime = instance("modifyTime", entity),
			creatorName = instance("creatorName", entity),
			modifierName = instance("modifierName", entity);
	
	}
	
	public static class Role {
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.core.pojo.user.Role.class;
		public static final Property 
			id = instance("id", entity),
			name = instance("name", entity),
			sort = instance("sort", entity),
			status = instance("status", entity),
			description = instance("description", entity),
			remark = instance("remark", entity),
			roleOperations = instance("roleOperations", entity),
			userRoles = instance("userRoles", entity),
			groupRoles = instance("groupRoles", entity),
			isOwn = instance("isOwn", entity),
			isGroupOwn = instance("isGroupOwn", entity),
			creator = instance("creator", entity),
			createTime = instance("createTime", entity),
			modifier = instance("modifier", entity),
			modifyTime = instance("modifyTime", entity),
			creatorName = instance("creatorName", entity),
			modifierName = instance("modifierName", entity);
	
	}
	
	public static class RoleOperation {
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.core.pojo.user.RoleOperation.class;
		public static final Property 
			roleId = instance("roleId", entity),
			operationId = instance("operationId", entity),
			operation = instance("operation", entity),
			role = instance("role", entity),
			creator = instance("creator", entity),
			createTime = instance("createTime", entity),
			creatorName = instance("creatorName", entity);
	
	}
	
	public static class RoleProject {
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.core.pojo.user.RoleProject.class;
		public static final Property 
			roleId = instance("roleId", entity),
			projectId = instance("projectId", entity),
			project = instance("project", entity),
			role = instance("role", entity),
			creator = instance("creator", entity),
			createTime = instance("createTime", entity),
			creatorName = instance("creatorName", entity);
	
	}
	
	public static class User {
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.core.pojo.user.User.class;
		public static final Property 
			id = instance("id", entity),
			groupId = instance("groupId", entity),
			userNo = instance("userNo", entity),
			username = instance("username", entity),
			nickname = instance("nickname", entity),
			password = instance("password", entity),
			realname = instance("realname", entity),
			status = instance("status", entity),
			isSuperAdmin = instance("isSuperAdmin", entity),
			pic = instance("pic", entity),
			sort = instance("sort", entity),
			gender = instance("gender", entity),
			email = instance("email", entity),
			mobile = instance("mobile", entity),
			loginFail = instance("loginFail", entity),
			group = instance("group", entity),
			userRoles = instance("userRoles", entity),
			groupName = instance("groupName", entity),
			creator = instance("creator", entity),
			createTime = instance("createTime", entity),
			modifier = instance("modifier", entity),
			modifyTime = instance("modifyTime", entity),
			creatorName = instance("creatorName", entity),
			modifierName = instance("modifierName", entity);
	
	}
	
	public static class UserRole {
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.core.pojo.user.UserRole.class;
		public static final Property 
			userId = instance("userId", entity),
			roleId = instance("roleId", entity),
			role = instance("role", entity),
			user = instance("user", entity),
			creator = instance("creator", entity),
			createTime = instance("createTime", entity),
			creatorName = instance("creatorName", entity);
	
	}
	
	
}
