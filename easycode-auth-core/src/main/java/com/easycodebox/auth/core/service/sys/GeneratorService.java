package com.easycodebox.auth.core.service.sys;

import com.easycodebox.auth.model.entity.sys.Generator;
import com.easycodebox.auth.model.enums.IdGeneratorEnum;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.idgenerator.IdGenerateProcess;
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
	 * @param generatorType
	 * @return
	 */
	Generator load(IdGeneratorEnum generatorType);
	
	/**
	 * 修改生成策略
	 * @param generator
	 * @return
	 */
	int update(Generator generator);
	
	/**
	 * 更新是否循环
	 * @param generatorType
	 * @param isCycle
	 * @return
	 */
	int updateIsCycle(IdGeneratorEnum generatorType, YesNo isCycle);
	
	/**
	 * 生成策略分页
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	DataPage<Generator> page(IdGeneratorEnum generatorType,
			YesNo isCycle, int pageNo, int pageSize);
	
	/**
	 * 如果指定GeneratorType没有初始化Generator则初始化
	 * 增长生成器值并返回增长后的值
	 * @return
	 * @throws Exception
	 */
	Generator incrementAndGet(IdGeneratorEnum generatorType);
	
	/**
	 * 批量添加GeneratorType
	 * 注：可不执行此方法，因为Generator会自行判断，如果数据库里没值则自行导入数据
	 * @return
	 */
	int batchAdd() throws Exception;

}
