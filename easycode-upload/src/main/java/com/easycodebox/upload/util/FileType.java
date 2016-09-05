package com.easycodebox.upload.util;

import java.util.Map;

import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.file.PropertiesPool;
import com.easycodebox.common.lang.Symbol;

/**
 * @author WangXiaoJin
 * 
 */
public enum FileType implements DetailEnum<String> {
	
	PIC_TYPE("PIC_TYPE", "图片类型", "imgs"),
	MIX_TYPE("MIX_TYPE", "混合类型", "mix");
	
	static {
		PropertiesPool.loadXMLFile("/file.xml");
	}

	private final String value;
	private final String desc;
	private final String fileDir;
	//上传文件的路径
	private Map<String, String> uploadPaths;
	
	private FileType(String value, String desc, String fileDir) {
		this.value = value;
		this.desc = desc;
		this.fileDir = fileDir;
	}
	
	/**
	 * 获取对应type的上传路径
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getUploadPath(String type) {
		if(uploadPaths == null) {
			uploadPaths = (Map<String, String>)PropertiesPool.get(getValue());
		}
		return uploadPaths == null ? null : uploadPaths.get(type);
	}
	
	/**
	 * 获取文件类型的上传根目录
	 * @return
	 */
	public String getRoot() {
		return Constants.UPLOAD_PATH + Symbol.SLASH + fileDir;
	}
	
	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public String getDesc() {
		return this.desc;
	}

	@Override
	public String toString() {
		return "{desc : '" + desc + "', value : " + value + "}";
	}
	
	@Override
	public String getClassName() {
		return this.name();
	}
	
}
