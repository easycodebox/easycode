package com.easycodebox.common.idgenerator;

import com.easycodebox.common.idgenerator.IdGeneratedValue.Strategy;
import com.easycodebox.common.idgenerator.exception.IdGenerationException;
import com.easycodebox.common.lang.reflect.Methods;

/**
 * @author WangXiaoJin
 */
public abstract class AbstractIdGenTypeParser {
	
	public abstract IdGeneratorType parsePersistentKey(String persistentKey);
	
	public static IdGeneratorType parseIdGeneratedValue(IdGeneratedValue idGeneratedValue) {
		IdGeneratorType idGeneratorType = null;
		if (idGeneratedValue.strategy() == Strategy.ENUM) {
			idGeneratorType = (IdGeneratorType)Enum.valueOf((Class<? extends Enum>) idGeneratedValue.type(), idGeneratedValue.key());
		} else if (idGeneratedValue.strategy() == Strategy.STATIC_METHOD) {
			try {
				idGeneratorType = (IdGeneratorType) Methods.invokeStaticMethod(idGeneratedValue.type(), idGeneratedValue.key(), null);
			} catch (Exception e) {
				throw new IdGenerationException("Parse IdGeneratedValue Annotation error.", e);
			}
		}
		return idGeneratorType;
	}
	
}
