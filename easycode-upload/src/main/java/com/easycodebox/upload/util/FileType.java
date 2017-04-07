package com.easycodebox.upload.util;

import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.file.PropertiesPool;

import java.util.Map;

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
	 * 存储文件的文件夹名
	 * @return
	 */
	public String getFileDir() {
		return fileDir;
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
	public String getClassName() {
		return this.name();
	}
	
}
