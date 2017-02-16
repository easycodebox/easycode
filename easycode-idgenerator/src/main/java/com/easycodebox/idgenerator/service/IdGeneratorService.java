package com.easycodebox.idgenerator.service;

import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.idgenerator.IdGenerateProcess;
import com.easycodebox.common.idgenerator.IdGeneratorType;
import com.easycodebox.idgenerator.entity.IdGenerator;

import java.util.List;

/**
 * @author WangXiaoJin
 *
 */
public interface IdGeneratorService extends IdGenerateProcess {
	
	/**
	 * 生成策略列表
	 * @return
	 */
	List<IdGenerator> list();
	
	/**
	 * 生成策略详情
	 * @param id
	 * @return
	 */
	IdGenerator load(String id);
	
	/**
	 * 修改生成策略
	 * @param idGenerator
	 * @return
	 */
	int update(IdGenerator idGenerator);
	
	/**
	 * 更新是否循环
	 * @param id
	 * @param isCycle
	 * @return
	 */
	int updateIsCycle(String id, YesNo isCycle);
	
	/**
	 * 生成策略分页
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	DataPage<IdGenerator> page(String id, YesNo isCycle, int pageNo, int pageSize);
	
	/**
	 * 如果指定GeneratorType没有初始化Generator则初始化
	 * 增长生成器值并返回增长后的值
	 * @return
	 * @throws Exception
	 */
	IdGenerator incrementAndGet(IdGeneratorType idGeneratorType);
	
}
