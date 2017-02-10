package com.easycodebox.common.generator;

import com.easycodebox.common.spring.BeanFactory;

/**
 * @author WangXiaoJin
 */
public class Generators {
	
	//private static final Logger log = LoggerFactory.getLogger(GeneratorFactory.class);
	
	private static GenerateProcess generateProcess;
	
	private Generators() {
		super();
	}
	
	@SuppressWarnings("rawtypes")
	public static Object getGeneratorNextVal(GeneratorType generatorType) {
		AbstractGenerator g = generatorType.getGenerator();
		if (g == null) {
			initAndIncrementGenerator(generatorType);
			g = generatorType.getGenerator();
			return g.nextVal();
		} else {
			Object nextVal = g.nextVal();
			//nextVal == null 本批次数据已经用完，需要加载下批数据
			while (nextVal == null) {
				initAndIncrementGenerator(generatorType);
				nextVal = g.nextVal();
			}
			return nextVal;
		}
	}
	
	private static void initAndIncrementGenerator(GeneratorType generatorType) {
		generateProcess = generateProcess == null ?
				BeanFactory.getBean(GenerateProcess.class) : generateProcess;
		generateProcess.incrementGenerator(generatorType);
	}
	
}
