package com.easycodebox.common.generator;

/**
 * @author WangXiaoJin
 * 
 */
public interface GenerateProcess {
	
	/**
	 * 如果指定GeneratorType没有初始化Generator则初始化
	 * 增长生成器值
	 * @return
	 * @throws Exception
	 */
	void incrementGenerator(GeneratorType generatorType);

}
