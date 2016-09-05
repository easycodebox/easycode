package com.easycodebox.auth.core.service.user;

import java.util.List;

import com.easycodebox.auth.core.pojo.user.Group;
import com.easycodebox.common.enums.entity.status.CloseStatus;
import com.easycodebox.common.lang.dto.DataPage;

/**
 * @author WangXiaoJin
 *
 */
public interface GroupService {
	
	/**
	 * 根据ID获取组织名
	 * 请使用load接口，因为load实现了缓存
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	String getNameById(Integer id);
	
	/**
	 * 组织列表
	 * @return
	 */
	List<Group> list(CloseStatus status);
	
	/**
	 * 列出组织树形结构
	 * @return
	 */
	List<Group> listTree();
	
	/**
	 * 组织详情
	 * @param id
	 * @return
	 */
	Group load(Integer id);
	
	/**
	 * 新增组织
	 * @param group
	 * @return	应该实现返回数据能获取到主键
	 */
	Group add(Group group);
	
	/**
	 * 修改组织
	 * @param group
	 * @return
	 */
	int update(Group group);
	
	/**
	 * 逻辑删除组织
	 * @param ids
	 * @return
	 */
	int remove(Integer[] ids);
	
	/**
	 * 物理删除组织
	 * @param ids
	 * @return
	 */
	int removePhy(Integer[] ids);
	
	/**
	 * 组织分页
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	DataPage<Group> page(String parentName, String groupName,
			CloseStatus status, int pageNo, int pageSize);
	
	/**
	 * 开启、关闭组织
	 * @param ids
	 * @param status
	 * @return
	 */
	int openClose(Integer[] ids, CloseStatus status);
	
	boolean existName(String name, Integer excludeId);
	
}
