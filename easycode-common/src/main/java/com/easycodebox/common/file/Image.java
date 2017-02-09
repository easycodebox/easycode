package com.easycodebox.common.file;


/**
 * @author WangXiaoJin
 * 
 */
public class Image extends FileInfo {
	
	private Integer height;
	
	private Integer width;
	
	/**
	 * 此方法预留，为了兼容老版本。后期会删除此方法
	 * @return
	 */
	@Deprecated
	public String getImgPath() {
		return getPath();
	}
	
	/**
	 * 此方法预留，为了兼容老版本。后期会删除此方法
	 * @return
	 */
	@Deprecated
	public String getImgName() {
		return getName();
	}
	
	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

}
