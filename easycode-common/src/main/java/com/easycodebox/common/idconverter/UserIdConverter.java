package com.easycodebox.common.idconverter;

/**
 * @author WangXiaoJin
 */
public interface UserIdConverter extends IdConverter {
	
	/**
	 * @param id
	 * @return 返回realname
	 */
	String idToRealname(Object id);
	
	/**
	 * @param id
	 * @return 返回nickname
	 */
	String idToNickname(Object id);
	
	/**
	 * @param id
	 * @return 优先返回realname，realname为null则返回nickname
	 */
	String idToRealOrNickname(Object id);
	
}
