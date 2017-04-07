package com.easycodebox.upload.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 在此类中添加本项目私有配置
 * @author WangXiaoJin
 */
@ConfigurationProperties(prefix = "upload")
public class UploadProperties {
	
	/**
	 * 文件上传根路径
	 */
	private String path;
	
	/**
	 * 配置文件路径
	 */
	private String configFile;
	
	/**
	 * 删除的文件存放哪个文件夹下
	 */
	private String deleteFilename;
	
	/**
	 * 生成的文件名最小值
	 */
	private String filenameInitVal;
	
	/**
	 * 生成的文件名当前值
	 */
	private String filenameCurVal;
	
	/**
	 * 生成的文件名最大值
	 */
	private String filenameMaxVal;
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getConfigFile() {
		return configFile;
	}
	
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
	
	public String getDeleteFilename() {
		return deleteFilename;
	}
	
	public void setDeleteFilename(String deleteFilename) {
		this.deleteFilename = deleteFilename;
	}
	
	public String getFilenameInitVal() {
		return filenameInitVal;
	}
	
	public void setFilenameInitVal(String filenameInitVal) {
		this.filenameInitVal = filenameInitVal;
	}
	
	public String getFilenameCurVal() {
		return filenameCurVal;
	}
	
	public void setFilenameCurVal(String filenameCurVal) {
		this.filenameCurVal = filenameCurVal;
	}
	
	public String getFilenameMaxVal() {
		return filenameMaxVal;
	}
	
	public void setFilenameMaxVal(String filenameMaxVal) {
		this.filenameMaxVal = filenameMaxVal;
	}
}
