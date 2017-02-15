package com.easycodebox.common.idgenerator;

import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.enums.DetailEnums;

/**
 * @author WangXiaoJin
 */
public class DetailEnumIdGenTypeParser extends AbstractIdGenTypeParser {
	
	private Class<? extends DetailEnum<String>> detailEnumClass;
	
	public DetailEnumIdGenTypeParser(Class<? extends DetailEnum<String>> detailEnumClass) {
		this.detailEnumClass = detailEnumClass;
	}
	
	@Override
	public IdGeneratorType parsePersistentKey(String persistentKey) {
		return (IdGeneratorType)DetailEnums.parse(detailEnumClass, persistentKey);
	}
	
}
