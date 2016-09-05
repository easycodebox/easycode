package com.easycodebox.common.file;

import java.io.File;

import com.easycodebox.common.lang.dto.AbstractBo;

public class FileInfo extends AbstractBo {
	
	private static final long serialVersionUID = -5758401252814280071L;

	private String name;
	
	/**
	 * 原件原名
	 */
	private String oldName;
	
	/**
	 * 文件的路径
	 */
	private String path;
	
	/**
	 * 文件类型，可以简单理解为文件后缀
	 */
	private String type;
	
	private File file;
	
	private Double size;
	
	/**
	 * 错误信息
	 */
	private String error;
	
	public FileInfo() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Double getSize() {
		return size;
	}

	public void setSize(Double size) {
		this.size = size;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
