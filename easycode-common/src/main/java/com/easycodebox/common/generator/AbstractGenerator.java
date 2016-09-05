package com.easycodebox.common.generator;

import java.util.concurrent.atomic.AtomicBoolean;

import com.easycodebox.common.enums.entity.YesNo;

/**
 * @author WangXiaoJin
 * 
 */
public abstract class AbstractGenerator<T> {
	
	protected AtomicBoolean hadUsedBeginVal = new AtomicBoolean(false);
	
	/**
	 * 生成器本批次生成的数量
	 */
	protected volatile int genNum;

	/**
	 * 值每次增加的跨度
	 */
	protected int increment;
	
	/**
	 * 每次抓取的个数
	 */
	protected int fetchSize;
	
	/**
	 * 初始化值
	 */
	protected T initialVal;
	
	/**
	 * 最大值，该属性可为null
	 */
	protected T maxVal;
	
	/**
	 * 此生成器值是否到达最大值后循环生成
	 */
	protected YesNo isCycle;
	
	protected AbstractGenerator(int increment, int fetchSize
			, String initialVal, String currentVal, 
			String maxVal, YesNo isCycle) {
		this.increment = increment;
		this.fetchSize = fetchSize;
		this.isCycle = isCycle;
	};
	
	/**
	 * 获取生成器的下一个值
	 * @return return null 说明数据需要重新reload
	 */
	public abstract T nextVal();
	
	/**
	 * 获取当前值
	 * @return
	 */
	public abstract T currentVal();
	
	/**
	 * 批量加载数据，返回下一次加载时的起始值
	 * 并且设置当前值段为 新传入的curVal~ 新nextStepVal
	 * 设置genNum=0
	 * @return return null说明不需要批量加载数据，nextVal只依据自己的规则，不需要依据其他字段
	 */
	public abstract T nextStepVal(String curVal);

	public int getIncrement() {
		return increment;
	}

	public int getFetchSize() {
		return fetchSize;
	}

	public T getInitialVal() {
		return initialVal;
	}

	public T getMaxVal() {
		return maxVal;
	}

	public YesNo getIsCycle() {
		return isCycle;
	}

	public int getGenNum() {
		return genNum;
	}

}
