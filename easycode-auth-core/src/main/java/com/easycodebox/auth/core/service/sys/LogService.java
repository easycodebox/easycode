package com.easycodebox.auth.core.service.sys;

import java.util.List;

import com.easycodebox.auth.model.entity.sys.Log;
import com.easycodebox.auth.model.enums.ModuleType;
import com.easycodebox.common.enums.entity.LogLevel;
import com.easycodebox.common.lang.dto.DataPage;

/**
 * @author WangXiaoJin
 *
 */
public interface LogService {

	/**
	 * 日志列表
	 * @return
	 */
	List<Log> list(String url, String params, ModuleType moduleType, 
			LogLevel logLevel, String result, String clientIp);
	
	/**
	 * 日志详情
	 * @param id
	 * @return
	 */
	Log load(Long id);
	
	/**
	 * 新增日志
	 * @param log
	 * @return	应该实现返回数据能获取到主键
	 */
	Log add(Log log);
	
	/**
	 * 修改日志
	 * @param log
	 * @return
	 */
	int update(Log log);
	
	/**
	 * 物理删除日志
	 * @param ids
	 * @return
	 */
	int removePhy(Long[] ids);
	
	/**
	 * 日志分页
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	DataPage<Log> page(String title, String url, String params, ModuleType moduleType, 
			LogLevel logLevel, String result, String clientIp, int pageNo, int pageSize);
	
}
