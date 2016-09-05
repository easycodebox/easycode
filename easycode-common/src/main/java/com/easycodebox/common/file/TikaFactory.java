package com.easycodebox.common.file;

import org.apache.tika.Tika;

/**
 * @author WangXiaoJin
 * 
 */
public class TikaFactory {

	public static Tika getInstance() {
		return TikaSingleton.INSTANCE;
	}
	
	private static final class TikaSingleton {
		
		static final Tika INSTANCE = new Tika();
		
	}
	
}
