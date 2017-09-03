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
	
	public static CodeMsg PARAM_BLANK 	= new CodeMsgExt("1001", "参数{0}不能为空");
	public static CodeMsg PARAM_ILLEGAL 	= new CodeMsgExt("1002", "参数{0}:{1}非法");
	
	public static CodeMsg FORM_ERROR 		= new CodeMsgExt("2001", "表单格式错误");
	public static CodeMsg MAX_UPLOAD_ERROR 	= new CodeMsgExt("2002", "上传文件应小于{0}M");
	public static CodeMsg NO_FILE 			= new CodeMsgExt("2003", "请选择您要上传的文件");
	
	private static final String FILE_PATH = "code-msg.properties";
	
	static {
		PropertiesPool.loadPropertiesFile(FILE_PATH);
	}
	
	protected CodeMsgExt(String code, String msg) {
		super(code, msg);
	}
    
	public static void main(String[] args) throws Exception {
		/* ----------- 生成properties文件 ------------ */
		File file = new File("src/main/resources" + (FILE_PATH.startsWith(Symbol.SLASH) ? "" : Symbol.SLASH) + FILE_PATH);
		CodeMsgs.storePropertiesFile(CodeMsgExt.class, file);
		/* ----------- 生成properties文件 ----------- */
	}
	
}
