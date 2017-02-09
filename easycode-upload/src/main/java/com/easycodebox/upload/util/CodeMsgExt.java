package com.easycodebox.upload.util;

import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.file.PropertiesPool;
import com.easycodebox.common.lang.Symbol;

import java.io.File;

/**
 * @author WangXiaoJin
 *
 */
public class CodeMsgExt extends CodeMsg {
	
	public static CodeMsg PARAM_ERROR_TYPE 	= new CodeMsgExt("1001", "文件类型参数错误");
	public static CodeMsg FORM_ERROR 		= new CodeMsgExt("1002", "表单格式错误");
	public static CodeMsg MAX_UPLOAD_ERROR 	= new CodeMsgExt("1003", "上传文件应小于{0}M");
	public static CodeMsg NO_FILE 			= new CodeMsgExt("1004", "请选择您要上传的文件");
	public static CodeMsg NO_PATH 			= new CodeMsgExt("1005", "类型{0}没有对应的路径值");
	
	public static CodeMsg NO_FILES_PARAM 	= new CodeMsgExt("2001", "缺少files参数");
	public static CodeMsg NO_FILE_TYPE_PARAM 	= new CodeMsgExt("2002", "缺少fileType参数");
	public static CodeMsg FILE_TYPE_PARAM_ERROR = new CodeMsgExt("2003", "fileType参数错误");
	
	
	private static final String FILE_PATH = "/code-msg.properties";
	
	static {
		PropertiesPool.loadPropertiesFile(FILE_PATH);
	}
	
	protected CodeMsgExt(String code, String msg) {
		super(code, msg);
	}
    
	public static void main(String[] args) throws Exception {
		
		/************* 生成properties文件 ************************/
		File file = new File("src/main/resources" + (FILE_PATH.startsWith(Symbol.SLASH) ? "" : Symbol.SLASH) + FILE_PATH);
		CodeMsgs.storePropertiesFile(CodeMsgExt.class, file);
		/************* 生成properties文件 ************************/
	}
	
}
