package com.easycodebox.common.idgenerator;

import com.easycodebox.common.spring.BeanFactory;

/**
 * @author WangXiaoJin
 */
public class IdGenerators {
	
	//private static final Logger log = LoggerFactory.getLogger(GeneratorFactory.class);
	
	private static IdGenerateProcess idGenerateProcess;
	
	private IdGenerators() {
		super();
	}
	
	@SuppressWarnings("rawtypes")
	public static Object nextVal(IdGeneratorType idGeneratorType) {
		AbstractIdGenerator g = idGeneratorType.getIdGenerator();
		if (g == null) {
			initAndIncrement(idGeneratorType);
			g = idGeneratorType.getIdGenerator();
			return g.nextVal();
		} else {
			Object nextVal = g.nextVal();
			//nextVal == null 本批次数据已经用完，需要加载下批数据
			while (nextVal == null) {
				initAndIncrement(idGeneratorType);
				nextVal = g.nextVal();
			}
			return nextVal;
		}
	}
	
	private static void initAndIncrement(IdGeneratorType idGeneratorType) {
		idGenerateProcess = idGenerateProcess == null ?
				BeanFactory.getBean(IdGenerateProcess.class) : idGenerateProcess;
		idGenerateProcess.incrementGenerator(idGeneratorType);
	}
	
}
