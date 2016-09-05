package com.easycodebox.auth.core.service.sys;

import java.util.List;

import com.easycodebox.auth.core.pojo.sys.Project;
import com.easycodebox.common.enums.entity.status.CloseStatus;
import com.easycodebox.common.lang.dto.DataPage;

/**
 * @author WangXiaoJin
 *
 */
public interface ProjectService {

	/**
	 * 授权项目列表
	 * @return
	 */
	List<Project> list(String name, CloseStatus status);
	
	/**
	 * 授权项目详情
	 * @param id
	 * @return
	 */
	Project load(Integer id);
	
	/**
	 * 授权项目详情
	 * @param projectNo
	 * @return
	 */
	Project load(String projectNo);
	
	/**
	 * 新增授权项目
	 * @param project
	 * @return	应该实现返回数据能获取到主键
	 */
	Project add(Project project);
	
	/**
	 * 修改授权项目
	 * @param project
	 * @return
	 */
	int update(Project project);
	
	/**
	 * 逻辑删除授权项目
	 * @param ids
	 * @return
	 */
	int remove(Integer[] ids);
	
	/**
	 * 物理删除授权项目
	 * @param ids
	 * @return
	 */
	int removePhy(Integer[] ids);
	
	/**
	 * 授权项目分页
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	DataPage<Project> page(String name, String projectNo, CloseStatus status, int pageNo, int pageSize);
	
	/**
	 * 开启、关闭授权项目
	 * @param ids
	 * @param status
	 * @return
	 */
	int openClose(Integer[] ids, CloseStatus status);
	
	boolean existName(String name, Integer excludeId);
	
	boolean existProjectNo(String projectNo, Integer excludeId);
	
}
