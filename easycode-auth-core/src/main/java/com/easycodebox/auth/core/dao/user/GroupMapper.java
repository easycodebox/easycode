package com.easycodebox.auth.core.dao.user;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.easycodebox.auth.core.pojo.user.Group;
import com.easycodebox.common.enums.entity.OpenClose;

/**
 * @author WangXiaoJin
 *
 */
public interface GroupMapper {

	Group load(@Param("id")Integer id);
	
	List<Group> page(
			@Param("parentName")String parentName, @Param("groupName")String groupName, 
			@Param("status")OpenClose status, 
			@Param("pageNo")int pageNo, @Param("pageSize")int pageSize);

	long pageTotalCount(
			@Param("parentName")String parentName, @Param("groupName")String groupName, 
			@Param("status")OpenClose status);

}
