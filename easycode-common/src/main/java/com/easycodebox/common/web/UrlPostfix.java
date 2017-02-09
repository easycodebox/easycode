package com.easycodebox.common.web;

import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.validate.Assert;

/**
 * @author WangXiaoJin
 *
 */
public class UrlPostfix {

	public static String[] actionPostfixes = {
		
		Symbol.EMPTY, "do", "html", "action", "jsp", "php"
		
	};
	
	public static boolean isAction(String name) {
		Assert.notBlank(name, "name can't be blank.");
		int index = name.lastIndexOf(Symbol.PERIOD);
		name = index > -1 ? name.substring(index + 1) : Symbol.EMPTY;
		name = name.toLowerCase();
		for (String actionPostfixe : actionPostfixes) {
			if (actionPostfixe.equals(name))
				return true;
		}
		return false;
	}
	
}
