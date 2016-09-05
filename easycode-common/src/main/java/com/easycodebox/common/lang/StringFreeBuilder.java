package com.easycodebox.common.lang;

import org.apache.commons.lang.text.StrBuilder;

/**
 * @author WangXiaoJin
 *
 */
public class StringFreeBuilder extends StrBuilder {
	
	public StringFreeBuilder() {
        super();
    }

    public StringFreeBuilder(int initialCapacity) {
        super(initialCapacity);
    }
    
    public StringFreeBuilder(String str) {
        super(str);
    }
	
	/**
	 * 返回自身的数据。
	 * 当buffer是大数据量时，调用toCharArray()方法会复制一份数据，当数据量达到虚拟机上限时导致内存溢出，
	 * 获取原有数据能尽小避免问题
	 * @return
	 */
	public char[] getSelfChars() {
		return buffer;
	}
	
	public StringFreeBuilder append(String str) {
		super.append(str);
		return this;
	}
	
}
