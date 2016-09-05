package com.easycodebox.common.algorithm;

import java.util.List;
import java.util.zip.CRC32;

import org.apache.commons.lang.text.StrBuilder;

import com.easycodebox.common.validate.Assert;

/**
 * @author WangXiaoJin
 *
 */
public class CRCUtils {
	
	public static long crc32Value(CharSequence cs) {
		Assert.notNull(cs, "CharSequence can not be null.");
		CRC32 crc = new CRC32();
		for(int i = 0; i < cs.length(); i++) {
			crc.update(cs.charAt(i));
		}
		return crc.getValue();
	}
	
	public static long crc32Value(StrBuilder str) {
		Assert.notNull(str, "str can not be null.");
		CRC32 crc = new CRC32();
		for(int i = 0; i < str.length(); i++) {
			crc.update(str.charAt(i));
		}
		return crc.getValue();
	}
	
	public static long crc32Value(List<? extends StrBuilder> strs) {
		Assert.notNull(strs, "str can not be null.");
		CRC32 crc = new CRC32();
		for(StrBuilder str : strs) {
			for(int i = 0; i < str.length(); i++) {
				crc.update(str.charAt(i));
			}
		}
		return crc.getValue();
	}
	
}
