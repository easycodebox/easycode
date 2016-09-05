package com.easycodebox.upload.util;

import java.io.File;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.file.PropertiesPool;

/**
 * @author WangXiaoJin
 * 
 */
public class Constants extends BaseConstants {
	
	static {
		
		/*************** properties ******************/
		PropertiesPool.loadPropertiesFile("/upload.properties");
		
	}
	
	public static void main(String[] args) {
		File f = new File("f:/data/d/a.xml");
		System.out.println(f.exists());
		f.mkdirs();
	}
	
	/**
	 * 上传多个文件时，是否事务控制。即当一个文件上传失败，所有的文件都上传失败 <br>
	 * 当没用事务时，上传文件失败的错误信息会存放在fileInfo中error属性中，否则错误信息存放于CodeMsg的msg属性。<br>
	 * transaction 默认值: false。
	 */
	public static final String TRANSACTION_KEY = "transaction";
	
	/**
	 * 上传文件保存路径
	 */
	public static final String UPLOAD_PATH = PropertiesPool.getProperty("upload_path");
	
	/**
	 * 配置文件名的属性文件路径
	 */
	public static final String FILENAME_PROPS_PATH = PropertiesPool.getProperty("filename_props_path");
	
	/**
	 * 能接受的最大上传文件。小于等于0 时上传文件不受限制
	 */
	public static final Integer MAX_UPLOAD_FILE = Integer.parseInt(PropertiesPool.getProperty("max_upload_file"));
	
	/**
	 * 上传文件的临时文件存放地址
	 */
	public static final String TMP_FILE = PropertiesPool.getProperty("tmp_file");
	
	/**
	 * 删除后的文件存放地址
	 */
	public static final String DELETE_FILE_PATH = PropertiesPool.getProperty("delete_file_path");
	
}
