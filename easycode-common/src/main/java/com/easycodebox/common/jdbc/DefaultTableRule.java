package com.easycodebox.common.jdbc;

import org.apache.commons.lang.WordUtils;

/**
 * @author WangXiaoJin
 *
 */
public class DefaultTableRule implements TableRule {

	@Override
	public String generateFk(String referencedTableName,
			String referencedTablePk) {
		referencedTableName = referencedTableName.replaceFirst("[a-zA-Z0-9]_", "") + "_" + referencedTablePk;
		return WordUtils.capitalize(referencedTableName, new char[]{'_'}).replaceAll("_", "");
	}
	
}
