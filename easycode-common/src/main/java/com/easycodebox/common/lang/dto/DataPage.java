package com.easycodebox.common.lang.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WangXiaoJin
 * 
 */
public class DataPage<T> extends AbstractBo {

	private Integer pageNo;

	private Integer pageSize; // 每页的记录数

	private Integer partIndex;// 分流式分页的当前分流索引值

	private Integer partSize;// 分流式分页一次分流几条数据

	private long totalCount;// 总记录数

	private List<T> data; // 当前页中存放的记录,类型一般为List

	private Integer nextPage; // 下一页

	private Integer prePage; // 上一页

	/**
	 * pageNo 默认为1
	 * pageSize 默认为20
	 */
	public DataPage() {
		this(1, 20, 0L, new ArrayList<T>());
	}

	/**
	 * @param pageNo 当前页数
	 * @param pageSize 本页容量
	 * @param totalCount 数据库中总记录条数
	 * @param data 本页包含的数据
	 */
	public DataPage(Integer pageNo, Integer pageSize, long totalCount, List<T> data) {
		this(pageNo, pageSize, null, null, totalCount, data);
	}

	public DataPage(Integer pageNo, Integer pageSize, Integer partIndex,
			Integer partSize, long totalCount, List<T> data) {
		if (partIndex != null && partSize != null) {
			this.partIndex = partIndex;
			this.partSize = partSize;
		}
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.totalCount = totalCount;
		this.data = data;
		if (hasNextPage())
			this.nextPage = this.pageNo + 1;
		else
			this.nextPage = this.getTotalPage();
		if(hasPreviousPage())
			this.prePage = this.pageNo - 1;
		else
			this.prePage = 1;
	}

	/**
	 * 取总页数.
	 */
	public int getTotalPage() {
		if(pageSize == null)
			return 1;	//没有分页则返回1
		else if (totalCount % pageSize == 0)
			return (int) (totalCount / pageSize);
		else
			return (int) (totalCount / pageSize + 1);
	}

	/**
	 * 该页是否有下一页.
	 */
	public boolean hasNextPage() {
		return this.pageNo != null && this.pageNo < this.getTotalPage();
	}

	/**
	 * 该页是否有上一页.
	 */
	public boolean hasPreviousPage() {
		return this.pageNo != null && this.pageNo > 1;
	}

	/**
	 * 获取当前分页 在数据库中的起始索引
	 * 
	 * @return
	 */
	public int getStart() {
		if(pageNo == null || pageSize == null)
			return 0;	//没有分页则返回1
		else if (partIndex != null && partSize != null) {
			return getStartOfPage(pageNo, pageSize, partIndex, partSize);
		} else
			return (pageNo - 1) * pageSize;
	}
	
	public static int getPageNo(int limitStart, int pageSize) {
		return limitStart/pageSize + 1;
	}

	/**
	 * 获取任一页任意分流索引第一条数据在数据集的位置.
	 * 
	 * @param pageNo 从1开始的页号
	 * @param pageSize 每页记录条数
	 * @param partIndex 索引从1开始
	 * @param partSize 分流一次数据个数
	 * @return 该页第一条数据
	 */
	public static int getStartOfPage(int pageNo, int pageSize,
			Integer partIndex, Integer partSize) {
		if (partIndex != null && partSize != null) {
			int fullStart = (pageNo - 1) * pageSize;
			return fullStart + (partIndex - 1) * partSize;
		} else
			return (pageNo - 1) * pageSize;

	}

	/**
	 * 获取任一页第一条数据在数据集的位置.
	 * 
	 * @param pageNo
	 *            从1开始的页号
	 * @param pageSize
	 *            每页记录条数
	 * @return 该页第一条数据
	 */
	public static int getStartOfPage(Integer pageNo, Integer pageSize) {
		if(pageNo == null || pageSize == null)
			return 0;
		else
			return getStartOfPage(pageNo, pageSize, null, null);
	}

	/**
	 * 分流分页获取指定分流能获取到的数据个数
	 * 
	 * @param pageNo 从1开始的页号
	 * @param pageSize 每页记录条数
	 * @param partIndex 索引从1开始
	 * @param partSize 分流一次数据个数
	 * @return 分流分页获取指定分流能获取到的数据个数
	 */
	public static int getObtainSize(int pageNo, int pageSize,
			Integer partIndex, Integer partSize) {
		if (partIndex == null || partSize == null)
			return pageSize;
		int nextPageStart = getStartOfPage(pageNo + 1, pageSize), partStart = getStartOfPage(
				pageNo, pageSize, partIndex, partSize);
		if (partStart + partSize <= nextPageStart)
			return partSize;
		else
			return nextPageStart - partStart;
	}

	public Integer getNextPage() {
		return nextPage;
	}

	public Integer getPrePage() {
		return prePage;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPartIndex() {
		return partIndex;
	}

	public void setPartIndex(Integer partIndex) {
		this.partIndex = partIndex;
	}

	public Integer getPartSize() {
		return partSize;
	}

	public void setPartSize(Integer partSize) {
		this.partSize = partSize;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public static void main(String[] args) {
		System.out.println(getStartOfPage(4, 20, 2, 5));
		System.out.println(getPageNo(25, 20));
	}

}