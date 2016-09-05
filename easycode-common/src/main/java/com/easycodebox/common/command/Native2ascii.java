package com.easycodebox.common.command;

/**
 * @author WangXiaoJin
 * 
 */
public class Native2ascii {
	
	public static String basePath = "D:/develop/git/com/easycodebox/common/src/main/resources/";
	
	public static String inFile = "test.properties";
	public static String outFile = "test_out.properties";
	
	
	public static void main(String[] args) {
		String in = basePath + inFile;
		String out = basePath + outFile;
		try {
			RuntimeUtils.exec("native2ascii -encoding UTF-8 " + in + " " + out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
