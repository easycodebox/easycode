package com.easycodebox.auth.core.service.sys;

import com.easycodebox.auth.model.entity.sys.Generator;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.idgenerator.IdGenerateProcess;
import com.easycodebox.common.idgenerator.IdGeneratorType;
import com.easycodebox.common.lang.dto.DataPage;

import java.util.List;

/**
 * @author WangXiaoJin
 *
 */
public interface GeneratorService extends IdGenerateProcess {
	
	/**
	 * 生成策略列表
	 * @return
	 */
	List<Generator> list();
	
	/**
	 * 生成策略详情
	 * @param id
	 * @return
	 */
	Generator load(String id);
	
	/**
	 * 修改生成策略
	 * @param generator
	 * @return
	 */
	int update(Generator generator);
	
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
	DataPage<Generator> page(String id, YesNo isCycle, int pageNo, int pageSize);
	
	/**
	 * 如果指定GeneratorType没有初始化Generator则初始化
	 * 增长生成器值并返回增长后的值
	 * @return
	 * @throws Exception
	 */
	Generator incrementAndGet(IdGeneratorType idGeneratorType);
	
}
