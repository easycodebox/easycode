package com.easycodebox.common.idconverter;

/**
 * 通过ID来获取对象
 * @author WangXiaoJin
 *
 */
public interface IdConverter {
	
	/**
	 * 
	 * @param id 
	 * @param prop （可选） 某些情况下需要提供对象的属性名，特别是提供不同的属性名显示不同值的场景
	 * @return
	 */
	Object convert(Object id, String prop);
	
}
