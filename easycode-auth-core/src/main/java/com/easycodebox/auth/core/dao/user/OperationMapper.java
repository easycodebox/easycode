package com.easycodebox.auth.core.dao.user;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.easycodebox.auth.core.pojo.user.Operation;
import com.easycodebox.common.enums.entity.OpenClose;
import com.easycodebox.common.enums.entity.YesNo;

/**
 * @author WangXiaoJin
 *
 */
public interface OperationMapper {

	Operation load(@Param("id")Long id, 
			@Param("projectId")Integer projectId, @Param("url")String url);
	
	List<Operation> page(
			@Param("parentName")String parentName, @Param("projectName")String projectName, 
			@Param("operationName")String operationName, @Param("isMenu")YesNo isMenu, 
			@Param("status")OpenClose status, @Param("url")String url,
			@Param("pageNo")int pageNo, @Param("pageSize")int pageSize);

	long pageTotalCount(
			@Param("parentName")String parentName, @Param("projectName")String projectName, 
			@Param("operationName")String operationName, @Param("isMenu")YesNo isMenu, 
			@Param("status")OpenClose status, @Param("url")String url);
	
	List<Operation> listOperationsOfRoles(@Param("roleIds")Integer[] roleIds, 
			@Param("projectId")Integer projectId, 
			@Param("isMenu")YesNo isMenu);
	
}
