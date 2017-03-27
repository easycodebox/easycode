package com.easycodebox.auth.core.dao.user;

import com.easycodebox.auth.model.entity.user.Group;
import com.easycodebox.common.enums.entity.OpenClose;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author WangXiaoJin
 *
 */
@Mapper
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
