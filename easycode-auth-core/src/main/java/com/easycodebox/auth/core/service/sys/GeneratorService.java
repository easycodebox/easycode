package com.easycodebox.auth.core.service.sys;

import java.util.List;

import com.easycodebox.auth.core.pojo.sys.Generator;
import com.easycodebox.auth.core.util.mybatis.GeneratorEnum;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.generator.GenerateProcess;
import com.easycodebox.common.lang.dto.DataPage;

/**
 * @author WangXiaoJin
 *
 */
public interface GeneratorService extends GenerateProcess {
	
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
	Generator load(GeneratorEnum generatorType);
	
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
	int updateIsCycle(GeneratorEnum generatorType, YesNo isCycle);
	
	/**
	 * 生成策略分页
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	DataPage<Generator> page(GeneratorEnum generatorType, 
			YesNo isCycle, int pageNo, int pageSize);
	
	/**
	 * 如果指定GeneratorType没有初始化Generator则初始化
	 * 增长生成器值并返回增长后的值
	 * @return
	 * @throws Exception
	 */
	Generator incrementAndGet(GeneratorEnum generatorType);
	
	/**
	 * 批量添加GeneratorType
	 * 注：可不执行此方法，因为Generator会自行判断，如果数据库里没值则自行导入数据
	 * @return
	 */
	int batchAdd() throws Exception;

}
