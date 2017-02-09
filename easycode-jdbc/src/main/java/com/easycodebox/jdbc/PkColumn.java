package com.easycodebox.jdbc;

import com.easycodebox.common.generator.GeneratorType;

/**
 * @author WangXiaoJin
 *
 */
public class PkColumn extends Column {

	private GeneratorType generatorType;
	
	public PkColumn() {
		
	}
	
	public PkColumn(String columnName) {
		super(columnName);
	}

	public GeneratorType getGeneratorType() {
		return generatorType;
	}

	public void setGeneratorType(GeneratorType generatorType) {
		this.generatorType = generatorType;
	}

}
