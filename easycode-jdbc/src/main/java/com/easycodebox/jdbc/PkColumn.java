package com.easycodebox.jdbc;

import com.easycodebox.common.idgenerator.IdGeneratorType;

/**
 * @author WangXiaoJin
 *
 */
public class PkColumn extends Column {

	private IdGeneratorType idGeneratorType;
	
	public PkColumn() {
		
	}
	
	public PkColumn(String columnName) {
		super(columnName);
	}

	public IdGeneratorType getIdGeneratorType() {
		return idGeneratorType;
	}

	public void setIdGeneratorType(IdGeneratorType idGeneratorType) {
		this.idGeneratorType = idGeneratorType;
	}

}
