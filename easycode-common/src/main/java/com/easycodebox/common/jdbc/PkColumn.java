package com.easycodebox.common.jdbc;

import com.easycodebox.common.generator.GeneratorType;


/**
 * @author WangXiaoJin
 *
 */
public class PkColumn extends Column {

	private static final long serialVersionUID = 5935229290400313223L;
	
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
