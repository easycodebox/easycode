package com.easycodebox.auth.core.dao.user;

import com.easycodebox.auth.model.entity.user.Permission;
import com.easycodebox.common.enums.entity.OpenClose;
import com.easycodebox.common.enums.entity.YesNo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author WangXiaoJin
 *
 */
@Mapper
public interface PermissionMapper {

	Permission load(@Param("id")Long id, 
			@Param("projectId")Integer projectId, @Param("url")String url);
	
	List<Permission> page(
			@Param("parentName")String parentName, @Param("projectName")String projectName, 
			@Param("permissionName")String permissionName, @Param("isMenu")YesNo isMenu, 
			@Param("status")OpenClose status, @Param("url")String url,
			@Param("pageNo")int pageNo, @Param("pageSize")int pageSize);

	long pageTotalCount(
			@Param("parentName")String parentName, @Param("projectName")String projectName, 
			@Param("permissionName")String permissionName, @Param("isMenu")YesNo isMenu, 
			@Param("status")OpenClose status, @Param("url")String url);
	
	List<Permission> listPermissionsOfRoles(@Param("roleIds")Integer[] roleIds, 
			@Param("projectId")Integer projectId, 
			@Param("isMenu")YesNo isMenu);
	
}
