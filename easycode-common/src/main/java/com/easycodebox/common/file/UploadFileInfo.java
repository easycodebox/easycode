package com.easycodebox.common.file;

import java.io.InputStream;

/**
 * @author WangXiaoJin
 *
 */
public class UploadFileInfo extends FileInfo {
	
	/**
	 * 上传图片时用到的参数名
	 */
	private String paramKey;
	
	private InputStream inputStream;
	
	public UploadFileInfo() {
		super();
	}
	
	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getParamKey() {
		return paramKey;
	}

	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}
	
}
