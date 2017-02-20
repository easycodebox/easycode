package com.easycodebox.auth.model.util;

import static com.easycodebox.jdbc.Property.instance;

import com.easycodebox.jdbc.Property;
import com.easycodebox.jdbc.entity.Entity;

/**
 * <b>直接运行{@link com.easycodebox.jdbc.res.GenerateBeanRes}类会自动生成R文件的。</b>
 * <p>如果是Entity类，会生成private static final Class<? extends Entity> entity = com.easycodebox.core.pojo.xxx.xxx.class
 * 和Property属性【public static final Property id = instance("id", entity)】
 * <p>如果只是普通的BO对象，则：public static final String id = "id";
 * @author WangXiaoJin
 *
 */
public class R {

	public static class Log {
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.model.entity.sys.Log.class;
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
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.model.entity.sys.Partner.class;
		public static final Property 
			id = instance("id", entity),
			name = instance("name", entity),
			partnerKey = instance("partnerKey", entity),
			website = instance("website", entity),
			status = instance("status", entity),
			deleted = instance("deleted", entity),
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
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.model.entity.sys.Project.class;
		public static final Property 
			id = instance("id", entity),
			name = instance("name", entity),
			projectNo = instance("projectNo", entity),
			status = instance("status", entity),
			deleted = instance("deleted", entity),
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
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.model.entity.user.Group.class;
		public static final Property 
			id = instance("id", entity),
			parentId = instance("parentId", entity),
			name = instance("name", entity),
			sort = instance("sort", entity),
			status = instance("status", entity),
			deleted = instance("deleted", entity),
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
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.model.entity.user.GroupRole.class;
		public static final Property 
			roleId = instance("roleId", entity),
			groupId = instance("groupId", entity),
			group = instance("group", entity),
			role = instance("role", entity),
			creator = instance("creator", entity),
			createTime = instance("createTime", entity),
			creatorName = instance("creatorName", entity);
	
	}
	
	public static class Permission {
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.model.entity.user.Permission.class;
		public static final Property 
			id = instance("id", entity),
			parentId = instance("parentId", entity),
			projectId = instance("projectId", entity),
			name = instance("name", entity),
			status = instance("status", entity),
			deleted = instance("deleted", entity),
			isMenu = instance("isMenu", entity),
			url = instance("url", entity),
			sort = instance("sort", entity),
			icon = instance("icon", entity),
			description = instance("description", entity),
			remark = instance("remark", entity),
			parent = instance("parent", entity),
			project = instance("project", entity),
			children = instance("children", entity),
			rolePermissions = instance("rolePermissions", entity),
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
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.model.entity.user.Role.class;
		public static final Property 
			id = instance("id", entity),
			name = instance("name", entity),
			sort = instance("sort", entity),
			status = instance("status", entity),
			deleted = instance("deleted", entity),
			description = instance("description", entity),
			remark = instance("remark", entity),
			rolePermissions = instance("rolePermissions", entity),
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
	
	public static class RolePermission {
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.model.entity.user.RolePermission.class;
		public static final Property 
			roleId = instance("roleId", entity),
			permissionId = instance("permissionId", entity),
			permission = instance("permission", entity),
			role = instance("role", entity),
			creator = instance("creator", entity),
			createTime = instance("createTime", entity),
			creatorName = instance("creatorName", entity);
	
	}
	
	public static class RoleProject {
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.model.entity.user.RoleProject.class;
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
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.model.entity.user.User.class;
		public static final Property 
			id = instance("id", entity),
			groupId = instance("groupId", entity),
			userNo = instance("userNo", entity),
			username = instance("username", entity),
			nickname = instance("nickname", entity),
			password = instance("password", entity),
			realname = instance("realname", entity),
			status = instance("status", entity),
			deleted = instance("deleted", entity),
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
	
		private static final Class<? extends Entity> entity = com.easycodebox.auth.model.entity.user.UserRole.class;
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
