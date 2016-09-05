package com.easycodebox.common.command;

/**
 * @author WangXiaoJin
 * 
 */
public class ClosureCompiler {
	
	private String compilerPath = "E:\\soft-source\\closure-compiler\\compiler.jar";
	private String outPath = "E:\\develop\\workspace\\backend\\WebRoot\\js\\global.min.js";
	private String[] files = {
		"E:\\develop\\workspace\\backend\\WebRoot\\js\\global.js"
	};

	public static void main(String[] args) {
		ClosureCompiler c = new ClosureCompiler();
		StringBuilder sb = new StringBuilder();
		sb.append("java -jar ").append(c.compilerPath).append(" --summary_detail_level 3 ");
		for(int i = 0; i < c.files.length; i++) {
			sb.append(" --js ").append(c.files[i]);
		}
		sb.append(" --js_output_file ").append(c.outPath);
		try {
			RuntimeUtils.exec(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
