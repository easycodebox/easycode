package com.easycodebox.common.jdbc;

/**
 * @author WangXiaoJin
 *
 */
public interface TableRule {

	/**
	 * 生成外键的列名
	 * @param referencedTableName	外键引用的表名
	 * @param referencedTablePk		外键引用表的主键名字
	 * @return
	 */
	String generateFk(String referencedTableName, String referencedTablePk);
	
}
